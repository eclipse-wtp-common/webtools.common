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
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
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
import org.eclipse.wst.common.componentcore.ui.propertypage.IReferenceWizardConstants;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

public class ProjectReferenceWizardFragment extends WizardFragment {
	protected LabelProvider labelProvider = null;
	protected ITreeContentProvider contentProvider = null;
	protected TreeViewer viewer;
	protected IProject[] selected;
	
	public boolean hasComposite() {
		return true;
	}

	public Composite createComposite(Composite parent, IWizardHandle handle) {
		handle.setTitle(Messages.ProjectReferenceTitle);
		handle.setDescription(Messages.ProjectReferenceDescription);
		
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new FillLayout());
		viewer = new TreeViewer(c, SWT.MULTI | SWT.BORDER);
		viewer.setContentProvider(getContentProvider());
		viewer.setLabelProvider(getLabelProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				selChanged();
			}
		});
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				advanceToNextPageOrFinish();
			}
		});
		viewer.setInput(ResourcesPlugin.getWorkspace());
		return c;
	}
	
	protected void selChanged() {
		if( viewer != null ) {
			IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
			if( sel != null ) {
				List selectionList = sel.toList();
				selected = (IProject[])selectionList.toArray(new IProject[selectionList.size()]);
			}
		}
	}
	
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		IVirtualComponent[] comps = new IVirtualComponent[selected.length];
		String[] paths = new String[selected.length];
		for (int i = 0; i < selected.length; i++) {
			IProject proj = selected[i];
			
			if( !ModuleCoreNature.isFlexibleProject(proj)) {
				try {
					ModuleCoreNature.addModuleCoreNatureIfNecessary(proj, monitor);
					ProjectFacetsManager.create(proj, true, monitor);
				} catch( CoreException ee) {
					// TODO something
				}
			}
			String path = null;
			comps[i] = ComponentCore.createComponent(proj, false);
			paths[i] = getArchiveName(proj,comps[i]);
		
		}
		getTaskModel().putObject(IReferenceWizardConstants.COMPONENT, comps);
		getTaskModel().putObject(IReferenceWizardConstants.COMPONENT_PATH, paths);
	}

	protected String getArchiveName(IProject proj, IVirtualComponent comp) {
		return getModuleHandler().getArchiveName(proj,comp);
	}

	
	protected IModuleHandler getModuleHandler() {
		return (IModuleHandler)getTaskModel().getObject(IReferenceWizardConstants.MODULEHANDLER);
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
					IProject root = (IProject)getTaskModel().getObject(IReferenceWizardConstants.PROJECT);
					IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
					ArrayList<IProject> list = new ArrayList<IProject>(Arrays.asList(projects));
					IVirtualComponent comp = (IVirtualComponent)getTaskModel().getObject(IReferenceWizardConstants.ROOT_COMPONENT);
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
