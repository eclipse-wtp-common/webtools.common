/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.datamodel.properties;

import java.util.Map;

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;

public interface IFacetProjectCreationDataModelProperties extends IDataModelProperties {
    
    public static final String FACETED_PROJECT_WORKING_COPY 
        = "IFacetProjectCreationDataModelProperties.FACETED_PROJECT_WORKING_COPY"; //$NON-NLS-1$";

	/**
	 * A String
	 */
	public static final String FACET_PROJECT_NAME = IFacetDataModelProperties.FACET_PROJECT_NAME;

	/**
	 * A Nested IDataModel of type IProjectCreationDataModelProperties
	 */
	public static final String NESTED_PROJECT_DM = "IFacetProjectCreationDataModelProperties.NESTED_PROJECT_DM"; //$NON-NLS-1$";

	/**
	 * An instanceof FacetDataModelMap.
	 */
	public static final String FACET_DM_MAP = "IFacetProjectCreationDataModelProperties.FACET_DM_MAP"; //$NON-NLS-1$

	/**
	 * An instance of FacetActionMap
	 */
	public static final String FACET_ACTION_MAP = "IFacetProjectCreationDataModelProperties.FACET_ACTION_MAP"; //$NON-NLS-1$

	
	/**
	 * An instanceof of IRuntime
	 */
	public static final String FACET_RUNTIME = "IFacetProjectCreationDataModelProperties.FACET_RUNTIME"; //$NON-NLS-1$

	
	/**
	 * This map is used for tracking individual IDataModels implementing IFacetDataModelProperties.
	 * The facet ids are the keys for retieving the specific IFacetataModelProperties IDataModels
	 */
	public interface FacetDataModelMap extends Map {
		public void add(IDataModel facetDataModel);
		public IDataModel getFacetDataModel(String facetID);
	}

	/**
	 * This map is used for tracing indivdual IFacetedProject.Actions for facets that either do not
	 * have any config data, or facets whose config data is not an IDataModel.
	 */
	public interface FacetActionMap extends Map {
		public void add(IFacetedProject.Action action);
		public IFacetedProject.Action getAction(String facetID);
	}
	
}
