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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.internal.IModuleHandler;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.ui.Messages;
import org.eclipse.wst.common.componentcore.ui.ModuleCoreUIPlugin;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.IWizardHandle;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.TaskWizardPage;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.WizardFragment;
import org.eclipse.wst.common.componentcore.ui.propertypage.IReferenceWizardConstants;
import org.eclipse.wst.common.componentcore.ui.propertypage.IReferenceWizardConstants.ProjectConverterOperationProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

public class ProjectReferenceWizardFragment extends WizardFragment {
	protected LabelProvider labelProvider = null;
	protected ITreeContentProvider contentProvider = null;
	protected TreeViewer viewer;
	protected IProject[] selected;
	
	public ProjectReferenceWizardFragment() {
		super();
		setComplete(false);
	}
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
				if (selected.length != 0) setComplete(true);
				else setComplete(false);
			} else setComplete(false);
		}
		((TaskWizardPage)getPage()).update();
	}
	
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		VirtualReference[] refs = new VirtualReference[selected.length];
		String runtimeLoc = (String) getTaskModel().getObject(IReferenceWizardConstants.DEFAULT_LIBRARY_LOCATION);
		ProjectConverterOperationProvider converter = (ProjectConverterOperationProvider)getTaskModel().getObject(IReferenceWizardConstants.PROJECT_CONVERTER_OPERATION_PROVIDER);
		runtimeLoc = runtimeLoc != null ? runtimeLoc : "/"; //$NON-NLS-1$
		for (int i = 0; i < selected.length; i++) {
			IProject proj = selected[i];
			
			if( !ModuleCoreNature.isFlexibleProject(proj)) {
				convertProject(selected[i], converter, monitor);
			}
			String path = null;
			refs[i] = new VirtualReference(
					(IVirtualComponent)getTaskModel().getObject(IReferenceWizardConstants.ROOT_COMPONENT), 
					ComponentCore.createComponent(proj, false));
			refs[i].setArchiveName(getArchiveName(proj, refs[i].getReferencedComponent()));
			refs[i].setRuntimePath(new Path(runtimeLoc).makeAbsolute());
		}
		getTaskModel().putObject(IReferenceWizardConstants.FINAL_REFERENCE, refs);
	}

	protected void convertProject(IProject proj, ProjectConverterOperationProvider converter, IProgressMonitor monitor) {
		if( converter != null ) {
			IDataModelOperation operation = converter.getConversionOperation(proj);
			if( operation != null ) {
				try {
					operation.execute(monitor, null);
					return;
				} catch( ExecutionException ee ) {
					ModuleCoreUIPlugin.log(NLS.bind(Messages.ProjectConversionError, proj.getName()), ee);
				}
			}
		} 
		try {
			if( !ModuleCoreNature.isFlexibleProject(proj)) {
				ModuleCoreNature.addModuleCoreNatureIfNecessary(proj, monitor);
				ProjectFacetsManager.create(proj, true, monitor);
			}
		} catch( CoreException ce) {
			ModuleCoreUIPlugin.log(NLS.bind(Messages.ProjectConversionError, proj.getName()), ce);
		}
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
					ArrayList<IVirtualReference> currentRefs = (ArrayList<IVirtualReference>)getTaskModel().getObject(IReferenceWizardConstants.CURRENT_REFS);
					ArrayList<IProject> availableList = getAvailableProjects(projects,currentRefs);
					IVirtualComponent comp = (IVirtualComponent)getTaskModel().getObject(IReferenceWizardConstants.ROOT_COMPONENT);
					List filtered = getModuleHandler().getFilteredProjectListForAdd(comp, availableList);
					return filtered.toArray(new IProject[filtered.size()]);
				}
				private ArrayList<IProject> getAvailableProjects(IProject[] projects, ArrayList<IVirtualReference> currentRefs) {
					if (currentRefs.isEmpty())
						return new ArrayList<IProject>(Arrays.asList(projects));
					ArrayList availProjects = new ArrayList();
					for (int i = 0; i < projects.length; i++) {
						IProject proj = projects[i];
						boolean matches = false;
						for (int j = 0; j < currentRefs.size() && !matches; j++) {
							IVirtualReference ref = currentRefs.get(j);
							if ((ref.getReferencedComponent().getProject().equals(proj)) || availProjects.contains(proj))
								matches = true;
						}
						if( !matches )
							availProjects.add(proj);
					}
					return availProjects;
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
