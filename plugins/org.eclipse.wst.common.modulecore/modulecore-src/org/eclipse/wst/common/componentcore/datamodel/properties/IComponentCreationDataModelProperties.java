/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.componentcore.datamodel.properties;

import org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties;

/**
 * <p>
 * IComponentCreationDataModelProperties provides properties to the DataModel associated with the
 * ComponentCreationDataModelProvider as well as all extending interfaces extending
 * IComponentCreationDataModelProperties specifically, but not limited to all J2EE component related
 * creation.
 * 
 * @see org.eclipse.wst.common.componentcore.internal.operation.ComponentCreationDataModelProvider
 *      </p>
 *      <p>
 *      This interface is not intended to be implemented by clients.
 *      </p>
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider
 * @see org.eclipse.wst.common.frameworks.datamodel.DataModelFactory
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties
 * 
 * @since 1.0
 */
public interface IComponentCreationDataModelProperties extends IDataModelProperties {

	/**
	 * Required, type String. The user defined name of the target project for the component to be
	 * created.
	 */
	public static final String PROJECT_NAME = "IComponentCreationDataModelProperties.PROJECT_NAME"; //$NON-NLS-1$
	/**
	 * Required, type String. The user defined name of the component to be created.
	 */
	public static final String COMPONENT_NAME = "IComponentCreationDataModelProperties.COMPONENT_NAME"; //$NON-NLS-1$
    /**
     * Required, type String. The user defined location on disk of the target project for the component to be created.
     * Defaulted to default eclipse workspace location
     */
    public static final String LOCATION = "IFlexibleProjectCreationDataModelProperties.LOCATION"; //$NON-NLS-1$
 

	/**
	 * Required, type String. The user defined deploy name of the component to be created. The
	 * DataModelProvider will default the name to the COMPONENT_NAME.
	 */
	public static final String COMPONENT_DEPLOY_NAME = "IComponentCreationDataModelProperties.COMPONENT_DEPLOY_NAME"; //$NON-NLS-1$
    /**
     * Required, type String. Nested operation to create a new project.
     */
    public static final String NESTED_PROJECT_CREATION_DM = "IComponentCreationDataModelProperties.NESTED_PROJECT_CREATION_DM"; //$NON-NLS-1$
	// TODO delete this
	/**
	 * Optional, type Boolean The default value is <code>Boolean.TRUE</code>. If this property is
	 * set to <code>Boolean.TRUE</code> then a default deployment descriptor and supporting
	 * bindings files will be generated.
	 */
	public static final String CREATE_DEFAULT_FILES = "IComponentCreationDataModelProperties.CREATE_DEFAULT_FILES"; //$NON-NLS-1$

}
