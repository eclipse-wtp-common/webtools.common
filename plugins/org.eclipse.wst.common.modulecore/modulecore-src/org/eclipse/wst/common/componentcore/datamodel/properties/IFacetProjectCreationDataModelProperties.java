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
package org.eclipse.wst.common.componentcore.datamodel.properties;

import java.util.Map;

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties;

public interface IFacetProjectCreationDataModelProperties extends IDataModelProperties {

	/**
	 * A String
	 */
	public static final String FACET_PROJECT_NAME = IFacetDataModelProperties.FACET_PROJECT_NAME;

	/**
	 * A Nested IDataModel of type IProjectCreationDataModelProperties
	 */
	public static final String NESTED_PROJECT_DM = "IFacetProjectCreationDataModelProperties.NESTED_PROJECT_DM"; //$NON-NLS-1$";

	/**
	 * An instanceof FacetDataModelMap
	 */
	public static final String FACET_DM_MAP = "IFacetProjectCreationDataModelProperties.FACET_DM_MAP"; //$NON-NLS-1$

	/**
	 * An instanceof of IRuntime
	 */
	public static final String FACET_RUNTIME = "IFacetProjectCreationDataModelProperties.FACET_RUNTIME"; //$NON-NLS-1$

	public interface FacetDataModelMap extends Map {
		public void add(IDataModel facetDataModel);
	}
}
