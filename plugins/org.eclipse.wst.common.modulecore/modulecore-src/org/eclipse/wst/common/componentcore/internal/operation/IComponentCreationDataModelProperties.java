/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.componentcore.internal.operation;


public interface IComponentCreationDataModelProperties {

	/**
     * Required
     */
    public static final String PROJECT_NAME = "ComponentCreationDataModel.PROJECT_NAME"; //$NON-NLS-1$

    /**
     * Required
     */
    public static final String COMPONENT_NAME = "ComponentCreationDataModel.COMPONENT_NAME"; //$NON-NLS-1$
	/**
	 * Required
	 */	
	
	public static final String COMPONENT_DEPLOY_NAME = "ComponentCreationDataModel.MODULE_DEPLOY_NAME"; //$NON-NLS-1$
	
    /**
     * An optional dataModel propertyName for a <code>Boolean</code> type. The
     * default value is <code>Boolean.TRUE</code>. If this property is set to
     * <code>Boolean.TRUE</code> then a default deployment descriptor and
     * supporting bindings files will be generated.
     */
    public static final String CREATE_DEFAULT_FILES = "ComponentCreationDataModel.CREATE_DEFAULT_FILES"; //$NON-NLS-1$

    /**
     * An optional dataModel propertyName for a <code>Boolean</code> type. The
     * default value is <code>Boolean.TRUE</code>. If this property is set to
     * <code>Boolean.TRUE</code> then a default deployment descriptor and
     * supporting bindings files will be generated.
     */
    public static final String SHOULD_CREATE_PROJECT = "ComponentCreationDataModel.SHOULD_CREATE_PROJECT"; //$NON-NLS-1$

    /**
     * Optional, type String
     */
    public static final String FINAL_PERSPECTIVE = "ComponentCreationDataModel.FINAL_PERSPECTIVE"; //$NON-NLS-1$

	/**
	 * type Integer
	 */
	public static final String COMPONENT_VERSION = "ComponentCreationDataModel.COMPONENT_VERSION"; //$NON-NLS-1$
	
	/**
	 * type Integer
	 */
	public static final String VALID_MODULE_VERSIONS_FOR_PROJECT_RUNTIME = "ComponentCreationDataModel.VALID_MODULE_VERSIONS_FOR_PROJECT_RUNTIME"; //$NON-NLS-1$

	
}
