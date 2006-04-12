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
package org.eclipse.wst.common.componentcore.datamodel.properties;

public interface ICreateReferenceComponentsDataModelProperties {
	
    /**
     * Required, type IVirtualComponent
     */	
	public static final String SOURCE_COMPONENT = "ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT"; //$NON-NLS-1$
	
    /**
     * Required, type List, List  should contain list of IVirtualComponent
     */
	public static final String TARGET_COMPONENT_LIST = "ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT"; //$NON-NLS-1$
	
	/**
     * Optional, deploy path for the dependent component, default is "/"
     */
	public static final String TARGET_COMPONENTS_DEPLOY_PATH = "ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_DEPLOY_PATH"; //$NON-NLS-1$
	
	/**
     * Optional, archive name for the dependent component, default is ""
     */
	public static final String TARGET_COMPONENT_ARCHIVE_NAME = "ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_ARCHIVE_NAME"; //$NON-NLS-1$	
	
}
