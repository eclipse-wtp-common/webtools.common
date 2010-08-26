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

package org.eclipse.wst.common.componentcore.ui.internal.propertypage;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.wst.common.componentcore.ui.IModuleCoreUIContextIds;
import org.eclipse.wst.common.componentcore.ui.Messages;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.IWizardHandle;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.WizardFragment;
import org.eclipse.wst.common.componentcore.ui.propertypage.AddModuleDependenciesPropertiesPage.ComponentResourceProxy;
import org.eclipse.wst.common.componentcore.ui.propertypage.IReferenceWizardConstants;

public class FolderMappingWizardFragment extends WizardFragment {
	private IProject project;
	private TreeViewer viewer;
	private IContainer selected = null;
	protected IWizardHandle handle;

	boolean isComplete = false;

	public boolean isComplete() {
		return isComplete;
	}
	
	public boolean hasComposite() {
		return true;
	}

	public Composite createComposite(Composite parent, IWizardHandle handle) {
		this.handle = handle;
		handle.setTitle(Messages.AddFolder);
		handle.setDescription(Messages.AddFolderMappings);
		handle.setImageDescriptor(IDEWorkbenchPlugin.getIDEImageDescriptor("wizban/newfolder_wiz.png"));
		project = (IProject)getTaskModel().getObject(IReferenceWizardConstants.PROJECT);		
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		c.setLayout(layout);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(c, IModuleCoreUIContextIds.DEPLOYMENT_ASSEMBLY_PREFERENCE_PAGE_ADD_NEW_FOLDER_MAPPING_P1);
		this.viewer = new TreeViewer(c, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.setContentProvider(getContentProvider());
		viewer.setLabelProvider(getLabelProvider());
		viewer.addFilter(getFilter());
		viewer.setInput(project);
		viewer.addSelectionChangedListener(getListener());
		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 390;
		data.heightHint = 185;
		viewer.getTree().setLayoutData(data);
		return c;
	}
	
	protected ISelectionChangedListener getListener() {
		return new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
				Object first = sel.getFirstElement();
				if( first instanceof IContainer) {
					selected = (IContainer)first;
					if(!isComplete) {
						isComplete = true;
						handle.update();
					}
				}
			}
		};
	}
	
	public IContainer getSelected() {
		return selected;
	}
	
	protected ViewerFilter getFilter() {
		return new ViewerFilter() {			
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if(element instanceof IContainer) {
					IContainer container = (IContainer) element;
					IPath path = container.getProjectRelativePath();
					if(!ResourceMappingFilterExtensionRegistry.shouldFilter(path)){
						return true;
					}
				}
				return false;
			}
		};
	}
	
	protected ITreeContentProvider getContentProvider() {
		return new ITreeContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			
			public void dispose() {
			}
			
			public Object[] getElements(Object inputElement) {
				try {
					return project.members();
				} catch( CoreException ce ) {
					return new Object[]{};
				}
			}
			
			public boolean hasChildren(Object element) {
				if( element instanceof IContainer) {
					try {
						return ((IContainer)element).members().length > 0;
					} catch( CoreException ce ) {
					}
				}
				return false;
			}
			
			public Object getParent(Object element) {
				if( element instanceof IResource)
					return ((IResource)element).getParent();
				return null;
			}
			
			public Object[] getChildren(Object parentElement) {
				if( parentElement instanceof IContainer) {
					try {
						return ((IContainer)parentElement).members();
					} catch( CoreException ce ) {
					}
				}
				return new Object[]{};
			}
		};
	}

	protected LabelProvider getLabelProvider() {
		return new LabelProvider() {
			public Image getImage(Object element) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
			}
			public String getText(Object element) {
				if( element instanceof IResource)
					return ((IResource)element).getName();
				return element.toString();
			}
		};
	}


	public void performFinish(IProgressMonitor monitor) throws CoreException {
		IContainer c = getSelected();
		if( c != null ) {
			IPath p = c.getProjectRelativePath().makeAbsolute();
			ComponentResourceProxy proxy = new ComponentResourceProxy(p, new Path("/")); //$NON-NLS-1$
			getTaskModel().putObject(IReferenceWizardConstants.FOLDER_MAPPING, proxy);
		}
	}	
}
