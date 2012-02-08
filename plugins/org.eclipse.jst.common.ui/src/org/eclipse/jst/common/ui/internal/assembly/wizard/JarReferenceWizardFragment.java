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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.viewsupport.FilteredElementTreeSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.common.ui.internal.IJstCommonUIContextIds;
import org.eclipse.jst.common.ui.internal.JstCommonUIPlugin;
import org.eclipse.jst.common.ui.internal.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualArchiveComponent;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.ui.ModuleCoreUIPlugin;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.IWizardHandle;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.WizardFragment;
import org.eclipse.wst.common.componentcore.ui.propertypage.IReferenceWizardConstants;

public class JarReferenceWizardFragment extends WizardFragment {
	protected LabelProvider labelProvider = null;
	protected ITreeContentProvider contentProvider = null;
	protected TreeViewer viewer;
	protected Button add, remove;
	protected IPath[] paths;
	protected IWizardHandle handle;
	protected IPath[] selected = new IPath[]{};
	protected Map <IPath, IPath> archives = new HashMap<IPath, IPath>();
	boolean isComplete = false;

	public boolean isComplete() {
		return isComplete;
	}
	
	public boolean hasComposite() {
		return true;
	}

	public Composite createComposite(Composite parent, IWizardHandle handle) {
		this.handle = handle;
		handle.setTitle(Messages.ArchiveTitle);
		handle.setDescription(Messages.ArchiveDescription);
		handle.setImageDescriptor(JavaPluginImages.DESC_WIZBAN_ADD_LIBRARY);
				
		Composite c = new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(c, IJstCommonUIContextIds.DEPLOYMENT_ASSEMBLY_NEW_ARCHIVE_REFERENCE_P1);
		c.setLayout(new GridLayout(2, false));
		viewer = new TreeViewer(c, SWT.MULTI | SWT.BORDER);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(getContentProvider());
		viewer.setLabelProvider(getLabelProvider());
		viewer.setInput(ResourcesPlugin.getWorkspace());

		Composite buttonColumn = new Composite(c, SWT.NONE);
		buttonColumn.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		final GridLayout gl = new GridLayout();
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		
		buttonColumn.setLayout( gl );
		
		add = new Button(buttonColumn, SWT.NONE);
		add.setText(Messages.Add);
		GridDataFactory.defaultsFor(add).applyTo(add);
		
		remove = new Button(buttonColumn, SWT.NONE);
		remove.setText(Messages.Remove);
		GridDataFactory.defaultsFor(remove).applyTo(remove);

		add.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				buttonPressed();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		remove.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				removeButtonPressed();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		return c;
	}

	protected void buttonPressed() {
		IProject project = (IProject)getTaskModel().getObject(IReferenceWizardConstants.PROJECT);
		selected = chooseEntries(
				add.getShell(), 
				project.getFullPath());

		if(selected != null) {
			removeInvalidArchiveFiles();
		
			for(IPath path: selected) {
				if(!archives.containsKey(path)) {
					archives.put(path, path);
				}
			}
			
			viewer.refresh();
			if(archives != null && archives.size() > 0) {
				isComplete = true;
			} else {
				isComplete = false;
			}
			handle.update();
		}
	}
	
	protected void removeButtonPressed() {
		TreeItem[] toRemove = viewer.getTree().getSelection();
		
		if(toRemove != null && toRemove.length > 0) {
			for(int i = 0; i < toRemove.length; i++) {
				Path path = (Path) toRemove[i].getData();
				if(archives.containsKey(path)) {
					archives.remove(path);
				}
			}
			
			viewer.refresh();
			if(archives != null && archives.size() > 0) {
				isComplete = true;
			} else {
				isComplete = false;
			}
			handle.update();
		}
	}

	public void performFinish(IProgressMonitor monitor) throws CoreException {
		IVirtualComponent rootComponent = (IVirtualComponent)getTaskModel().getObject(IReferenceWizardConstants.ROOT_COMPONENT);
		String runtimeLoc = (String)getTaskModel().getObject(IReferenceWizardConstants.DEFAULT_LIBRARY_LOCATION);
		if (archives != null && archives.size() > 0) {
			ArrayList<IVirtualReference> refList = new ArrayList<IVirtualReference>();
			Iterator iterator = archives.values().iterator();
			while(iterator.hasNext()) {
	    		IPath path = (Path)iterator.next();
				// IPath fullPath = project.getFile(selected[i]).getFullPath();
				String type = VirtualArchiveComponent.LIBARCHIVETYPE
						+ IPath.SEPARATOR;
				IVirtualComponent archive = ComponentCore
						.createArchiveComponent(rootComponent.getProject(),
								type + path.makeRelative().toString());
				VirtualReference ref = new VirtualReference(rootComponent, archive);
				ref.setArchiveName(path.lastSegment());
				if (runtimeLoc != null) {
					ref.setRuntimePath(new Path(runtimeLoc).makeAbsolute());
				}
				refList.add(ref);
			}
			IVirtualReference[] finalRefs = refList.toArray(new IVirtualReference[refList.size()]);
			getTaskModel().putObject(IReferenceWizardConstants.FINAL_REFERENCE, finalRefs);
		}
	}

	protected LabelProvider getLabelProvider() {
		if (labelProvider == null) {
			labelProvider = new LabelProvider() {
				public Image getImage(Object element) {
					return ModuleCoreUIPlugin.getInstance().getImage("jar_obj");
				}

				public String getText(Object element) {
					return element == null ? "" : element.toString();//$NON-NLS-1$
				}
			};
		}
		return labelProvider;
	}

	protected ITreeContentProvider getContentProvider() {
		if (contentProvider == null) {
			contentProvider = new ITreeContentProvider() {
				public Object[] getElements(Object inputElement) {
					return archives == null ? new Object[]{} : archives.values().toArray();
				}
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}
				public void dispose() {
				}
				public boolean hasChildren(Object element) {
					return false;
				}
				public Object getParent(Object element) {
					return null;
				}
				public Object[] getChildren(Object parentElement) {
					return null;
				}
			};
		}
		return contentProvider;
	}
	
	private static IPath[] chooseEntries(Shell shell, IPath initialSelection) {
		Class[] acceptedClasses= new Class[] { IFile.class };
		TypedElementSelectionValidator validator= new TypedElementSelectionValidator(acceptedClasses, true);
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();

		FilteredElementTreeSelectionDialog dialog =  new FilteredElementTreeSelectionDialog(shell, new WorkbenchLabelProvider(), new WorkbenchContentProvider());
		dialog.setHelpAvailable(false);
		dialog.setValidator(validator);
		dialog.setTitle(Messages.ArchiveDialogNewTitle);
		dialog.setMessage(Messages.ArchiveDialogNewDescription);
		dialog.addFilter(getDialogViewerFilter());
		dialog.setInput(root);
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.setInitialFilter("*.jar,*.war,*.rar,*.zip"); //$NON-NLS-1$
		dialog.create();		

		if (dialog.open() == Window.OK) {
			Object[] elements= dialog.getResult();
			IPath[] res= new IPath[elements.length];
			for (int i= 0; i < res.length; i++) {
				IResource elem= (IResource)elements[i];
				res[i]= elem.getFullPath();
			}
			return res;
		}
		return null;
	}
	
	protected void removeInvalidArchiveFiles(){
		// Tries to open archive to verify it's valid
		// If it is not a valid archive, a dialog is shown informing the user of the invalid archives
		ArrayList<IPath> invalidArchiveFiles = new ArrayList<IPath>();
		ArrayList<IPath> validArchiveFiles = new ArrayList<IPath>();
		ZipFile zipFile = null;
		for(IPath path:selected){
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
				validArchiveFiles.add(path);
			} catch (ZipException e1){
				invalidArchiveFiles.add(path);
			} catch (IOException e2){
				invalidArchiveFiles.add(path);
			}finally {
				if (zipFile != null){
					try {
						zipFile.close();
					} catch (IOException e) {
						JstCommonUIPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, JstCommonUIPlugin.PLUGIN_ID, e.getMessage(), e));
					}
				}
			}
		}
		if(invalidArchiveFiles.size() > 0) {
			selected = validArchiveFiles.toArray(new IPath[validArchiveFiles.size()]);
			showInvalidArchiveFilesAsWarning(invalidArchiveFiles);
		}
	}
	
	private void showInvalidArchiveFilesAsWarning(ArrayList<IPath>invalidArchiveFiles) {
		String message = Messages.InvalidArchivesWarning;
		boolean first = true;
		for(IPath path: invalidArchiveFiles) {
			if(!first) {
				message += ", \'"; //$NON-NLS-1$
			} else {
				message += "\'"; //$NON-NLS-1$
				first = false;
			}
			message += path.lastSegment() + "\'"; //$NON-NLS-1$
		}
		MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Warning", message); //$NON-NLS-1$
	}
	
	private static ViewerFilter getDialogViewerFilter() {
		return new ViewerFilter() {
			private final String[] excludedExtensions= { "txt", "css", "dll", "htm", "html", "class", "java", "classpath",      //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$//$NON-NLS-5$ //$NON-NLS-6$//$NON-NLS-7$ //$NON-NLS-8$
				"compatibility", "component", "container", "cvsignore", "del", "dnx", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$//$NON-NLS-5$ //$NON-NLS-6$
				"dtd", "mf", "project", "properties", "psf", "rc", "runtime", "sh", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$//$NON-NLS-5$ //$NON-NLS-6$//$NON-NLS-7$ //$NON-NLS-8$
				"spec", "sql", "tld", "xmi", "xml", "xsd", "gif", "jpg", "js", "vsd", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$//$NON-NLS-5$ //$NON-NLS-6$//$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
				"png", "bat", "xsl", "factorypath"}; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$ 

			private HashMap<String,String> excludedExtensionsMap = null;

			@Override
			public boolean select(Viewer viewer, Object parent, Object element) {
				if(excludedExtensionsMap == null) {
					initializeExludeMap();
				}
				if (element instanceof IFile) {
					IFile file = (IFile) element;
					String ext = file.getFileExtension();
					if(ext != null) {
						ext = ext.toLowerCase();
						if(excludedExtensionsMap.get(ext) != null) {
							return false;
						}
					}
					return true;
				} else if (element instanceof IContainer) { // IProject, IFolder
					// ignore closed projects
					if (element instanceof IProject && !((IProject)element).isOpen())
						return false;
					// ignore .settings folder
					if (element instanceof IFolder) {
						IFolder folder = (IFolder) element;
						if (folder.getName().equals(".settings"))
							return false;
					}
					try {
						IResource[] resources= ((IContainer)element).members();
						for (int i= 0; i < resources.length; i++) {
							// Only show containers that contain an archive
							if (select(viewer, parent, resources[i])) {
								return true;
							}
						}
					} catch (CoreException e) {
						JavaPlugin.log(e.getStatus());
					}
				}
				return false;
			}
			
			private void initializeExludeMap() {
				excludedExtensionsMap = new HashMap<String, String>();
				for(int i = 0; i < excludedExtensions.length; i++)
				excludedExtensionsMap.put(excludedExtensions[i], excludedExtensions[i]);
			}
		};
	}
}
