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
import java.util.Iterator;
import java.util.Map;
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
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationPropertiesNew;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

public class FacetProjectCreationOperation extends AbstractDataModelOperation {

	protected boolean runtimeAdded = false;

	public FacetProjectCreationOperation() {
		super();
	}

	public FacetProjectCreationOperation(IDataModel model) {
		super(model);
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			IFacetedProject facetProj = createProject(monitor);
			Set existingFacets = facetProj.getProjectFacets();

			Map dmMap = (Map) model.getProperty(IFacetProjectCreationDataModelProperties.FACET_DM_MAP);
			Set actions = new HashSet();
			IDataModel facetDM = null;
			for (Iterator iterator = dmMap.values().iterator(); iterator.hasNext();) {
				facetDM = (IDataModel) iterator.next();
				if (facetDM.getBooleanProperty(IFacetDataModelProperties.SHOULD_EXECUTE)) {
					String facetID = facetDM.getStringProperty(IFacetDataModelProperties.FACET_ID);
					boolean shouldInstallFacet = true;
					for (Iterator existingFacetsIterator = existingFacets.iterator(); shouldInstallFacet && existingFacetsIterator.hasNext();) {
						IProjectFacetVersion version = (IProjectFacetVersion) existingFacetsIterator.next();
						if (version.getProjectFacet().getId().equals(facetID)) {
							shouldInstallFacet = false;
						}
					}
					if (shouldInstallFacet) {
						actions.add(facetDM.getProperty(IFacetDataModelProperties.FACET_ACTION));
					}
				}
			}
			Map actionsMap = (Map) model.getProperty(IFacetProjectCreationDataModelProperties.FACET_ACTION_MAP);
			for (Iterator iterator = actionsMap.values().iterator(); iterator.hasNext();) {
				actions.add(iterator.next());
			}
			if (!actions.isEmpty()) {
				facetProj.modify(actions, monitor);
			}
			Set fixedFacets = new HashSet(), newFacetVersions = facetProj.getProjectFacets(), existingFixedFacets = facetProj.getFixedProjectFacets();
			for (Iterator iter = newFacetVersions.iterator(); iter.hasNext();) {
				IProjectFacetVersion facetVersion = (IProjectFacetVersion) iter.next();
				String facetID = facetVersion.getProjectFacet().getId();
				boolean shouldInstallFacet = true;
				for (Iterator existingFacetsIterator = existingFixedFacets.iterator(); shouldInstallFacet && existingFacetsIterator.hasNext();) {
					IProjectFacet facet = (IProjectFacet) existingFacetsIterator.next();
					if (facet.getId().equals(facetID)) {
						shouldInstallFacet = false;
					}
				}
				if (shouldInstallFacet) {
					fixedFacets.add(facetVersion.getProjectFacet());
				}
			}
			if (!fixedFacets.isEmpty()) {
				facetProj.setFixedProjectFacets(fixedFacets);
			}
			if (runtimeAdded) {
				IRuntime runtime = (IRuntime) model.getProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME);
				addDefaultFacets(facetProj, runtime.getDefaultFacets(fixedFacets));
			}

		} catch (CoreException e) {
			Logger.getLogger().logError(e);
			throw new ExecutionException(e.getMessage(), e);
		} catch (Exception e) {
			Logger.getLogger().logError(e);
		}
		return OK_STATUS;
	}

	private static void addDefaultFacets(IFacetedProject facetProj, Set defaultFacets) {
		Set actions = new HashSet();
		for (Iterator iter = defaultFacets.iterator(); iter.hasNext();) {
			IProjectFacetVersion facetVersion = (IProjectFacetVersion) iter.next();
			if (!facetProj.hasProjectFacet(facetVersion.getProjectFacet())) {
				actions.add(new IFacetedProject.Action(Action.Type.INSTALL, facetVersion, null));
			}
		}

		try {
			if (!actions.isEmpty())
				facetProj.modify(actions, null);
		} catch (CoreException e) {
			Logger.getLogger().logError(e);
		}
	}

	public IFacetedProject createProject(IProgressMonitor monitor) throws CoreException {
		IProject project = ProjectUtilities.getProject((String) model.getProperty(IFacetProjectCreationDataModelProperties.FACET_PROJECT_NAME));
		IFacetedProject facetProj = null;
		if (project.exists()) {
			facetProj = ProjectFacetsManager.create(project, true, monitor);
		} else {
			String location = (String) model.getProperty(IProjectCreationPropertiesNew.PROJECT_LOCATION);
			IPath locationPath = null == location ? null : new Path(location);
			facetProj = ProjectFacetsManager.create(model.getStringProperty(IFacetProjectCreationDataModelProperties.FACET_PROJECT_NAME), locationPath, monitor);
		}
		IRuntime runtime = (IRuntime) model.getProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME);
		IRuntime existingRuntime = facetProj.getPrimaryRuntime();
		if (runtime != null && (existingRuntime == null || !runtime.equals(existingRuntime))) {
			facetProj.setTargetedRuntimes(Collections.singleton(runtime), null);
			runtimeAdded = true;
		}
		return facetProj;
	}
	
	public static void addDefaultFactets(IFacetedProject facetProj, IRuntime runtime) throws ExecutionException {
		try {
			if (runtime != null) {
				addDefaultFacets(facetProj, runtime.getDefaultFacets(facetProj.getFixedProjectFacets()));
			}
		} catch (CoreException e) {
			Logger.getLogger().logError(e);
			throw new ExecutionException(e.getMessage(), e);
		}
	}

}
