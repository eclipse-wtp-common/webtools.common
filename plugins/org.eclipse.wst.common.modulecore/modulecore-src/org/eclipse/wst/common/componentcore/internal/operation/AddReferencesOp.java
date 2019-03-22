/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.datamodel.properties.IAddReferenceDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.core.util.UIContextDetermination;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.internal.emfworkbench.validateedit.IValidateEditContext;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

public class AddReferencesOp extends AbstractDataModelOperation {


	public AddReferencesOp(IDataModel model) {
		super(model);
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (!validateEdit().isOK())
			return Status.CANCEL_STATUS;
		addReferencedComponents(monitor);
		addProjectReferences(monitor);
		return OK_STATUS;
	}
	
	/**
	 * Validate edit for resource state
	 */
	protected IStatus validateEdit() {
		IStatus status = OK_STATUS;
		IValidateEditContext validator = (IValidateEditContext) UIContextDetermination.createInstance(IValidateEditContext.CLASS_KEY);
		IVirtualComponent sourceComp = (IVirtualComponent) model.getProperty(IAddReferenceDataModelProperties.SOURCE_COMPONENT);
		IProject project = sourceComp.getProject();

		if (status.isOK()) {
			StructureEdit sEdit = null;
			try {
				sEdit = StructureEdit.getStructureEditForWrite(project);
				status = validator.validateState(sEdit.getModuleStructuralModel());
			} finally {
				if (sEdit !=null)
					sEdit.dispose();
			}
		}
		
		IFile [] files = new IFile[1];
		files[0] = project.getFile(ProjectUtilities.DOT_PROJECT);
		status = ResourcesPlugin.getWorkspace().validateEdit(files, null);
		return status;
	}
	
	public static List<IVirtualReference> getListFromModel(IDataModel model) {
		Object value = model.getProperty(IAddReferenceDataModelProperties.TARGET_REFERENCE_LIST);
		List<IVirtualReference> modList = value instanceof List ?  (List<IVirtualReference>)value : Arrays.asList(new IVirtualReference[]{(IVirtualReference)value});
		return modList;
	}
	
	protected void addProjectReferences() {
		addProjectReferences(null);
	}

	protected void addProjectReferences(IProgressMonitor monitor) {
		IVirtualComponent sourceComp = (IVirtualComponent) model.getProperty(IAddReferenceDataModelProperties.SOURCE_COMPONENT);
		List modList = getListFromModel(model);
		List targetprojectList = new ArrayList();
		for (int i = 0; i < modList.size(); i++) {
			IVirtualComponent virtualComponent = ((IVirtualReference) modList.get(i)).getReferencedComponent();
			if(virtualComponent.isBinary()){
				continue;
			}
			IProject targetProject = virtualComponent.getProject();
			targetprojectList.add(targetProject);
		}
		try {
			ProjectUtilities.addReferenceProjects(sourceComp.getProject(), targetprojectList);

			if (model.getBooleanProperty(IAddReferenceDataModelProperties.SYNC_PRIMARY_RUNTIME))
			{
				// set the primary runtime for the child modules to be the parent module's primary runtime, if necessary
	            IFacetedProject sourceProject = ProjectFacetsManager.create( sourceComp.getProject() );
	            IRuntime sourceRuntime = sourceProject.getPrimaryRuntime();
	            for (Iterator tplItr = targetprojectList.iterator(); tplItr.hasNext();)
	            {
		            IFacetedProject moduleFacetedProject = ProjectFacetsManager.create( ((IProject)tplItr.next()) );
		
		            if( moduleFacetedProject != null && ! equals( sourceRuntime, moduleFacetedProject.getPrimaryRuntime() ) )
		            {
		            	boolean supports = true;
		
		            	if( sourceRuntime != null )
		            	{
		            		for( Iterator itr = moduleFacetedProject.getProjectFacets().iterator(); itr.hasNext(); )
		            		{
		            			IProjectFacetVersion fver = (IProjectFacetVersion) itr.next();
		            			if( ! sourceRuntime.supports( fver ) )
		            			{
		            				supports = false;
		            				break;
		            			}
		            		}
		            	}
			            if( supports )
			            {
			                moduleFacetedProject.setRuntime( sourceRuntime, submon( monitor, 1 ) );
			            }
			        }
	            }
            }
		} catch (CoreException e) {
			ModulecorePlugin.logError(e);
		}

	}

	protected void addReferencedComponents(IProgressMonitor monitor) {
		IVirtualComponent sourceComp = (IVirtualComponent) model.getProperty(IAddReferenceDataModelProperties.SOURCE_COMPONENT);
		List<IVirtualReference> refList = getListFromModel(model);
		IVirtualReference[] refs = (IVirtualReference[]) refList.toArray(new IVirtualReference[refList.size()]);
		sourceComp.addReferences(refs);
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return null;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return null;
	}

	private static IProgressMonitor submon(final IProgressMonitor parent, final int ticks) {
		return (parent == null ? null : new SubProgressMonitor(parent, ticks));
	}

	private static boolean equals( final Object obj1, final Object obj2 )
	{
		if( obj1 == obj2 )
		{
			return true;
		}
		else if( obj1 == null || obj2 == null )
		{
			return false;
		}
		else
		{
			return obj1.equals( obj2 );
		}
	}
}
