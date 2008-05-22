/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jem.util.UIContextDetermination;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.ICreateReferenceComponentsDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.internal.emfworkbench.validateedit.IValidateEditContext;

public class CreateReferenceComponentsOp extends AbstractDataModelOperation {


	public CreateReferenceComponentsOp(IDataModel model) {
		super(model);
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (!validateEdit().isOK())
			return Status.CANCEL_STATUS;
		addReferencedComponents(monitor);
		addProjectReferences();
		return OK_STATUS;
	}
	
	/**
	 * Validate edit for resource state
	 */
	protected IStatus validateEdit() {
		IStatus status = OK_STATUS;
		IValidateEditContext validator = (IValidateEditContext) UIContextDetermination.createInstance(IValidateEditContext.CLASS_KEY);
		IVirtualComponent sourceComp = (IVirtualComponent) model.getProperty(ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT);
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

	protected void addProjectReferences() {

		IVirtualComponent sourceComp = (IVirtualComponent) model.getProperty(ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT);
		List modList = (List) model.getProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_LIST);
		List targetprojectList = new ArrayList();
		for (int i = 0; i < modList.size(); i++) {
			IVirtualComponent IVirtualComponent = (IVirtualComponent) modList.get(i);
			IProject targetProject = IVirtualComponent.getProject();
			targetprojectList.add(targetProject);
		}
		try {
			ProjectUtilities.addReferenceProjects(sourceComp.getProject(), targetprojectList);
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	protected void addReferencedComponents(IProgressMonitor monitor) {
		IVirtualComponent sourceComp = (IVirtualComponent) model.getProperty(ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT);
		List vlist = new ArrayList();
		List modList = (List) model.getProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_LIST);
		Map modDeployPathMap = (Map) model.getProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_DEPLOY_PATH_MAP);
		String deployPathStr = model.getStringProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_DEPLOY_PATH);
		IPath deployPath = null;
		IPath archiveDeployPath = null;
		if (deployPathStr != null && deployPathStr.length() > 0){
			deployPath = new Path(deployPathStr);
		}
		for (int i = 0; i < modList.size(); i++) {
			IVirtualComponent comp = (IVirtualComponent) modList.get(i);
			if (!srcComponentContainsReference(sourceComp, comp, deployPath)) {
				IVirtualReference ref = ComponentCore.createReference(sourceComp, comp);
				deployPathStr = (String)modDeployPathMap.get(comp);
				if (deployPathStr != null)
				{
					archiveDeployPath = new Path(deployPathStr);
				}
				else
				{
					archiveDeployPath = deployPath;
				}
				if(archiveDeployPath != null){
					ref.setRuntimePath(archiveDeployPath);
				}

				String archiveName = getArchiveName(comp);
				if (archiveName.length() > 0) {
					ref.setArchiveName(archiveName);
				}
				vlist.add(ref);
			}
		}

		IVirtualReference[] refs = (IVirtualReference[]) vlist.toArray(new IVirtualReference[vlist.size()]);
		sourceComp.addReferences(refs);
	}

	protected String getArchiveName(IVirtualComponent comp) {
		Map map = (Map) model.getProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_TO_URI_MAP);
		String uri = (String) map.get(comp);
		return uri == null ? "" : uri; //$NON-NLS-1$
	}


	private boolean srcComponentContainsReference(IVirtualComponent sourceComp, IVirtualComponent comp, IPath deployPath) {
		if ((sourceComp != null && sourceComp.getProject() != null) && (comp != null && comp.getProject() != null)) {
			IVirtualReference[] existingReferences = sourceComp.getReferences();
			IVirtualComponent referencedComponent = null;
			if (existingReferences != null) {
				for (int i = 0; i < existingReferences.length; i++) {
					IVirtualReference ref = existingReferences[i];
					if(ref != null){
						//also check to see if the deploy path is the same (remember that it can be null)
						if((ref.getRuntimePath() == null && deployPath != null) || ref.getRuntimePath().equals(deployPath)){
							referencedComponent = ref.getReferencedComponent();	
							if (referencedComponent != null && referencedComponent.equals(comp) ){ 
								return true;
							}	
						}
					}
				}
			}
		}
		return false;
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return null;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return null;
	}

}
