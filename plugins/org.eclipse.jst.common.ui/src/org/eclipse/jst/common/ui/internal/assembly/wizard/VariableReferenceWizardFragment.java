/******************************************************************************
 * Copyright (c) 2010 Red Hat and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rob Stryker - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - misc. UI cleanup
 ******************************************************************************/

package org.eclipse.jst.common.ui.internal.assembly.wizard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.CPVariableElement;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.CPVariableElementLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jst.common.ui.internal.IJstCommonUIContextIds;
import org.eclipse.jst.common.ui.internal.JstCommonUIPlugin;
import org.eclipse.jst.common.ui.internal.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualArchiveComponent;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.IWizardHandle;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.WizardFragment;
import org.eclipse.wst.common.componentcore.ui.propertypage.IReferenceWizardConstants;

public class VariableReferenceWizardFragment extends WizardFragment {
	protected LabelProvider labelProvider = null;
	protected ITreeContentProvider contentProvider = null;
	protected TreeViewer viewer;
	protected IPath[] paths;
	protected IWizardHandle handle;
	protected Object selected = null;
	boolean isComplete = false;

	public boolean isComplete() {
		return isComplete;
	}
	
	public boolean hasComposite() {
		return true;
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		this.handle = handle;
		handle.setTitle(Messages.VariableReferenceTitle);
		handle.setDescription(Messages.VariableReferenceDescription);
		handle.setImageDescriptor(JavaPluginImages.DESC_WIZBAN_ADD_LIBRARY);
				
		Composite c = new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(c, IJstCommonUIContextIds.DEPLOYMENT_ASSEMBLY_NEW_VARIABLE_REFERENCE_P1);
		c.setLayout(new FormLayout());
		viewer = new TreeViewer(c, SWT.SINGLE | SWT.BORDER);
		viewer.setContentProvider(getContentProvider());
		viewer.setLabelProvider(getLabelProvider());
		viewer.setInput(ResourcesPlugin.getWorkspace());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleNewSelection();
			}
		});
		FormData fd = new FormData();
		fd.width = 390;
		fd.height = 185;
		fd.left = new FormAttachment(0, 5);
		fd.top = new FormAttachment(0, 5);
		fd.right = new FormAttachment(100, -5);
		fd.bottom = new FormAttachment(100, -5);
		viewer.getTree().setLayoutData(fd);
		return c;
	}
	
	protected void handleNewSelection() {
		isComplete = true;
		IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
		Object o = sel.getFirstElement();
		if( o == null ) {
			isComplete = false;
			selected = null;
		} else {
			selected = o;
			IPath p = null;
			if( o instanceof CPVariableElement) {
				p = ((CPVariableElement)o).getPath();
			} else if( o instanceof ExtendedVariable) {
				p = ((ExtendedVariable)o).element.getPath().append(((ExtendedVariable)o).pathAfterElement);
			}
			if(p == null || p.isEmpty() || p.toFile().isDirectory() || !isValidArchive(p)) {
				isComplete = false;
			}
		}
		handle.update();
	}
	
	protected LabelProvider getLabelProvider() {
		if (labelProvider == null) {
			labelProvider= new VariablesLabelProvider();
		}
		return labelProvider;
	}
	
	public static class VariablesLabelProvider extends LabelProvider {
		private CPVariableElementLabelProvider delegate = new CPVariableElementLabelProvider(false);
		public Image getImage(Object element) {
			if( element instanceof CPVariableElement)
				return delegate.getImage(element);
			if( element instanceof ExtendedVariable) {
				if(((ExtendedVariable)element).isFolder()) 
					return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
			}
			return null;
		}
		public String getText(Object element) {
			if( element instanceof CPVariableElement)
				return delegate.getText(element);
			if( element instanceof ExtendedVariable) 
				return ((ExtendedVariable)element).pathAfterElement.lastSegment();
			return element == null ? "" : element.toString();//$NON-NLS-1$
		}	
	}
	
	public static class ExtendedVariable {
		public CPVariableElement element;
		public IPath pathAfterElement;
		public ExtendedVariable(CPVariableElement e, IPath p) {
			element = e;
			pathAfterElement = p;
		}
		public boolean isFolder() {
			return element.getPath().append(pathAfterElement).toFile().isDirectory();
		}
	}
	
	private CPVariableElement[] elements;
	private CPVariableElement[] initializeElements() {
		String[] entries= JavaCore.getClasspathVariableNames();
		ArrayList elements= new ArrayList(entries.length);
		for (int i= 0; i < entries.length; i++) {
			String name= entries[i];
			IPath entryPath= JavaCore.getClasspathVariable(name);
			if (entryPath != null) {
				elements.add(new CPVariableElement(name, entryPath));
			}
		}
		return (CPVariableElement[]) elements.toArray(new CPVariableElement[elements.size()]);
	}

	
	protected ITreeContentProvider getContentProvider() {
		if (contentProvider == null) {
			contentProvider = new ITreeContentProvider() {
				public Object[] getElements(Object inputElement) {
					if( elements == null )
						elements = initializeElements();
					return elements;
				}
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}
				public void dispose() {
				}
				public boolean hasChildren(Object element) {
					return getChildren(element).length > 0;
				}
				public Object getParent(Object element) {
					return null;
				}
				public Object[] getChildren(Object parentElement) {
					if( parentElement instanceof CPVariableElement) {
						if(((CPVariableElement)parentElement).getPath().toFile().isDirectory()) {
							String[] names = ((CPVariableElement)parentElement).getPath().toFile().list();
							ExtendedVariable[] extensions = new ExtendedVariable[names.length];
							for( int i = 0; i < extensions.length; i++ )
								extensions[i] = new ExtendedVariable((CPVariableElement)parentElement, new Path(names[i]));
							return extensions;
						}
					}
					if( parentElement instanceof ExtendedVariable) {
						ExtendedVariable p1 = (ExtendedVariable)parentElement;
						IPath parentLoc = p1.element.getPath();
						parentLoc = parentLoc.append(p1.pathAfterElement);
						String[] names = parentLoc.toFile().list();
						if( names != null ) {
							ExtendedVariable[] extensions = new ExtendedVariable[names.length];
							for( int i = 0; i < extensions.length; i++ )
								extensions[i] = new ExtendedVariable(p1.element, p1.pathAfterElement.append(names[i]));
							return extensions;
						}
					}
					return new Object[]{};
				}
			};
		}
		return contentProvider;
	}

	public void performFinish(IProgressMonitor monitor) throws CoreException {
		IVirtualComponent rootComponent = (IVirtualComponent)getTaskModel().getObject(IReferenceWizardConstants.ROOT_COMPONENT);
		String runtimeLoc = (String)getTaskModel().getObject(IReferenceWizardConstants.DEFAULT_LIBRARY_LOCATION);
		if (selected != null ) {
			ArrayList<IVirtualReference> refList = new ArrayList<IVirtualReference>();
//			for (int i = 0; i < selected.length; i++) {
				IPath variablePath = getVariablePath(selected);
				IPath resolvedPath = JavaCore.getResolvedVariablePath(variablePath);
				java.io.File file = new java.io.File(resolvedPath.toOSString());
				if (file.isFile() && file.exists()) {
					String type = VirtualArchiveComponent.VARARCHIVETYPE
							+ IPath.SEPARATOR;
					IVirtualComponent archive = ComponentCore
							.createArchiveComponent(rootComponent.getProject(),
									type + variablePath.toString());
					VirtualReference ref = new VirtualReference(rootComponent, archive);
					ref.setArchiveName(resolvedPath.lastSegment());
					if (runtimeLoc != null) {
						ref.setRuntimePath(new Path(runtimeLoc).makeAbsolute());
					}
					refList.add(ref);
				}
//			}
			IVirtualReference[] finalRefs = refList.toArray(new IVirtualReference[refList.size()]);
			getTaskModel().putObject(IReferenceWizardConstants.FINAL_REFERENCE, finalRefs);
		}
	}
	
	private IPath getVariablePath(Object selected) {
		if( selected instanceof ExtendedVariable) {
			ExtendedVariable s1 = (ExtendedVariable)selected;
			return new Path(s1.element.getName()).append(s1.pathAfterElement);
		}
		return new Path(((CPVariableElement)selected).getName());
	}
	
	private boolean isValidArchive(IPath path) {
		boolean valid = true;
		ZipFile zipFile = null;
		try {
			String osPath = null;
			if(path.segmentCount() > 1){
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				if(file.exists()) {
					IPath loc = file.getLocation();
					if(loc != null) { 
						osPath = loc.toOSString();
					}
				}
			}
			if(osPath == null){
				osPath = path.toOSString();
			}
			zipFile = new ZipFile(new File(osPath));
		} catch (ZipException e1){
			valid = false;
		} catch (IOException e2){
			valid = false;
		}finally {
			if (zipFile != null){
				try {
					zipFile.close();
				} catch (IOException e) {
					JstCommonUIPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, JstCommonUIPlugin.PLUGIN_ID, e.getMessage(), e));
				}
			}
		}
		return valid;
	}
}
