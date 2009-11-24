/******************************************************************************
 * Copyright (c) 2009 Red Hat
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rob Stryker - initial implementation and ongoing maintenance
 ******************************************************************************/
package org.eclipse.wst.common.componentcore.ui.internal.propertypage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.internal.IModuleHandler;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.ui.Messages;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.IWizardHandle;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.WizardFragment;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

public class ProjectReferenceWizardFragment extends WizardFragment {
	private LabelProvider labelProvider = null;
	private ITreeContentProvider contentProvider = null;
	private TreeViewer viewer;
	private IProject selected;
	
	public boolean hasComposite() {
		return true;
	}

	public Composite createComposite(Composite parent, IWizardHandle handle) {
		handle.setTitle(Messages.ProjectReferenceTitle);
		handle.setDescription(Messages.ProjectReferenceDescription);
		
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new FillLayout());
		viewer = new TreeViewer(c, SWT.SINGLE | SWT.BORDER);
		viewer.setContentProvider(getContentProvider());
		viewer.setLabelProvider(getLabelProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				selChanged();
			}
		});
		viewer.setInput(ResourcesPlugin.getWorkspace());
		return c;
	}
	
	private void selChanged() {
		if( viewer != null ) {
			IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
			if( sel != null ) 
				selected = (IProject)sel.getFirstElement();
		}
	}
	
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		if( !ModuleCoreNature.isFlexibleProject(selected)) {
			try {
				ProjectFacetsManager.create(selected.getProject(), true, monitor);
			} catch( CoreException ee) {
				// TODO something
			}
		}
		String path = null;
		IVirtualComponent comp = ComponentCore.createComponent(selected, false);
		path = getArchiveName(selected,comp);
		

		getTaskModel().putObject(NewReferenceWizard.COMPONENT, comp);
		getTaskModel().putObject(NewReferenceWizard.COMPONENT_PATH, path);
	}

	protected String getArchiveName(IProject proj, IVirtualComponent comp) {
		return getModuleHandler().getArchiveName(proj,comp);
	}

	
	protected IModuleHandler getModuleHandler() {
		return (IModuleHandler)getTaskModel().getObject(NewReferenceWizard.MODULEHANDLER);
	}

	protected LabelProvider getLabelProvider() {
		if( labelProvider == null ) {
			labelProvider = new LabelProvider() {
				public Image getImage(Object element) {
					return PlatformUI.getWorkbench().getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
				}
				public String getText(Object element) {
					if( element instanceof IProject )
						return ((IProject)element).getName();
					return element == null ? "" : element.toString();//$NON-NLS-1$
				}
			};
		}
		return labelProvider;
	}
	
	protected ITreeContentProvider getContentProvider() {
		if( contentProvider == null ) {
			contentProvider = new ITreeContentProvider() {
				public Object[] getElements(Object inputElement) {
					IProject root = (IProject)getTaskModel().getObject(NewReferenceWizard.PROJECT);
					IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
					ArrayList<IProject> list = new ArrayList<IProject>(Arrays.asList(projects));
					IVirtualComponent comp = (IVirtualComponent)getTaskModel().getObject(NewReferenceWizard.ROOT_COMPONENT);
					List filtered = getModuleHandler().getFilteredProjectListForAdd(comp, list);
					return filtered.toArray(new IProject[filtered.size()]);
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
}
