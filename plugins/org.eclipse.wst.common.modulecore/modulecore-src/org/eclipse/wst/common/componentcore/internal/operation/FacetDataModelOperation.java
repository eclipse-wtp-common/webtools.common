/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationPropertiesNew;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action.Type;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

public class FacetDataModelOperation extends AbstractDataModelOperation {

	public FacetDataModelOperation(IDataModel model) {
		super(model);
	}
	
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		IFacetedProject facetProj;
		try {
			facetProj = createProject(monitor);
			Set actions = new HashSet();
			actions.add(new IFacetedProject.Action((Type) model.getProperty(IFacetDataModelProperties.FACET_TYPE), (IProjectFacetVersion) model.getProperty(IFacetDataModelProperties.FACET_VERSION), model));
			facetProj.modify(actions, monitor);
		} catch (CoreException e) {
			throw new ExecutionException(e.getMessage(), e);
		}
		return OK_STATUS;
	}

	public IFacetedProject createProject(IProgressMonitor monitor) throws CoreException {
		IProject project = ProjectUtilities.getProject((String) model.getProperty(IFacetProjectCreationDataModelProperties.FACET_PROJECT_NAME));
		IFacetedProject facetProj = null;

		if (project.exists()) {
			facetProj = ProjectFacetsManager.create(project, true, monitor);
		} else {
			String location = null;
			if (model.isProperty(IProjectCreationPropertiesNew.PROJECT_LOCATION))
			{
				location = (String) model.getProperty(IProjectCreationPropertiesNew.PROJECT_LOCATION);
			}
			IPath locationPath = null == location ? null : new Path(location);
			facetProj = ProjectFacetsManager.create(model.getStringProperty(IFacetProjectCreationDataModelProperties.FACET_PROJECT_NAME), locationPath, monitor);
		}
		if (model.isProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME))
		{
			IRuntime runtime = (IRuntime) model.getProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME);
			IRuntime existingRuntime = facetProj.getPrimaryRuntime();
			if (runtime != null && (existingRuntime == null || !runtime.equals(existingRuntime))) {
				facetProj.setTargetedRuntimes(Collections.singleton(runtime), null);
			}
		}
		return facetProj;
	}
}
