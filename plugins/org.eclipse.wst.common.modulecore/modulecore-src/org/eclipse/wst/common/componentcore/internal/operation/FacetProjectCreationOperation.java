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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

public class FacetProjectCreationOperation extends AbstractDataModelOperation {

	public FacetProjectCreationOperation() {
		super();
	}

	public FacetProjectCreationOperation(IDataModel model) {
		super(model);
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			IFacetedProject facetProj = ProjectFacetsManager.create(model.getStringProperty(IFacetProjectCreationDataModelProperties.FACET_PROJECT_NAME), null, monitor);
			List dmList = (List) model.getProperty(IFacetProjectCreationDataModelProperties.FACET_DM_LIST);
			Set actions = new HashSet();
			IDataModel facetDM = null;
			for (Iterator iterator = dmList.iterator(); iterator.hasNext();) {
				facetDM = (IDataModel) iterator.next();
				actions.add(facetDM.getProperty(IFacetDataModelProperties.FACET_ACTION));
			}
			facetProj.modify(actions, monitor);
			Set fixedFacets = new HashSet(), newFacetVersions = facetProj.getProjectFacets();
			for (Iterator iter = newFacetVersions.iterator(); iter.hasNext();) {
				IProjectFacetVersion facetVersion = (IProjectFacetVersion) iter.next();
				fixedFacets.add(facetVersion.getProjectFacet());
			}
			facetProj.setFixedProjectFacets(fixedFacets);

		} catch (CoreException e) {
			throw new ExecutionException(e.getMessage(), e);
		}
		return OK_STATUS;
	}
}
