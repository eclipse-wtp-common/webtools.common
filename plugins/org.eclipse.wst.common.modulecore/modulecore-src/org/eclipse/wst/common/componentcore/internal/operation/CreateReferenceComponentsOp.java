/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.ICreateReferenceComponentsDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

public class CreateReferenceComponentsOp extends AbstractDataModelOperation {


	public CreateReferenceComponentsOp(IDataModel model) {
		super(model);
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		addReferencedComponents(monitor);
		addProjectReferences();
		return OK_STATUS;
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
		for (int i = 0; i < modList.size(); i++) {
			IVirtualComponent comp = (IVirtualComponent) modList.get(i);
			if (!srcComponentContainsReference(sourceComp, comp)) {
				IVirtualReference ref = ComponentCore.createReference(sourceComp, comp);
				String deployPath = model.getStringProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_DEPLOY_PATH);
				if (deployPath != null && deployPath.length() > 0)
					ref.setRuntimePath(new Path(deployPath));

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
		boolean useArchiveURI = true;
		IFacetedProject facetedProject = null;
		try {
			facetedProject = ProjectFacetsManager.create(comp.getProject());
		} catch (CoreException e) {
			useArchiveURI = false;
		}

		if (useArchiveURI && facetedProject != null && ProjectFacetsManager.isProjectFacetDefined(IModuleConstants.JST_UTILITY_MODULE)) {
			IProjectFacet projectFacet = ProjectFacetsManager.getProjectFacet(IModuleConstants.JST_UTILITY_MODULE);
			useArchiveURI = projectFacet != null && facetedProject.hasProjectFacet(projectFacet);
		}
		if (useArchiveURI) {
			Map map = (Map) model.getProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_TO_URI_MAP);
			String uri = (String) map.get(comp);
			return uri == null ? "" : uri;
		} else {
			return model.getStringProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_ARCHIVE_NAME);
		}
	}


	private boolean srcComponentContainsReference(IVirtualComponent sourceComp, IVirtualComponent comp) {
		if (sourceComp == null || comp == null)
			return false;
		IVirtualReference[] existingReferences = sourceComp.getReferences();
		if (existingReferences != null) {
			for (int i = 0; i < existingReferences.length; i++) {
				if (existingReferences[i] != null && existingReferences[i].getReferencedComponent() != null) {
					if (existingReferences[i].getReferencedComponent().getProject() != null && comp.getProject() != null) {
						if (existingReferences[i].getReferencedComponent().getProject().equals(comp.getProject()))
							return true;
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
