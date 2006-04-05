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

import org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;

public interface IFacetDataModelProperties extends IDataModelProperties {

	public static final Object FACET_TYPE_INSTALL = Action.Type.INSTALL;
	public static final Object FACET_TYPE_UNINSTALL = Action.Type.UNINSTALL;
	public static final Object FACET_TYPE_VERSION_CHANGE = Action.Type.VERSION_CHANGE;

	public static final String FACET_PROJECT_NAME = "IFacetDataModelProperties.FACET_PROJECT_NAME"; //$NON-NLS-1$

	public static final String FACET_TYPE = "IFacetDataModelProperties.FACET_TYPE"; //$NON-NLS-1$

	public static final String FACET_ID = "IFacetDataModelProperties.FACET_ID"; //$NON-NLS-1$

	public static final String FACET_VERSION_STR = "IFacetDataModelProperties.FACET_VERSION_STR"; //$NON-NLS-1$

	/**
	 * an IProjectFacetVersion
	 */
	public static final String FACET_VERSION = "IFacetDataModelPropeties.FACET_VERSION"; //$NON-NLS-1$
	
	/**
	 * an IFacetedProject.Action
	 */
	public static final String FACET_ACTION = "IFacetDataModelProperties.FACET_ACTION"; //$NON-NLS-1$

	public static final String SHOULD_EXECUTE = "IFacetDataModelProperties.SHOULD_EXECUTE"; //$NON-NLS-1$
}
