/*******************************************************************************
 * Copyright (c) 2003, 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.builder;

public interface IWorkbenchComponentBuilderDataModelProperties {
	/**
	 * Required, type IProject
	 */
	public static final String PROJECT = "IWorkbenchComponentBuilderDataModelProperties.PROJECT"; //$NON-NLS-1$
	/**
	 * Required, type project relative URI
	 */
	public static final String OUTPUT_CONTAINER = "IWorkbenchComponentBuilderDataModelProperties.OUTPUT_CONTAINER"; //$NON-NLS-1$
	/**
	 * Required, type WorkbenchComponent
	 */
	public static final String WORKBENCH_MODULE = "IWorkbenchComponentBuilderDataModelProperties.WORKBENCH_MODULE_RESOURCES"; //$NON-NLS-1$
	/**
	 * Required, type List of DependentDeployableModuleDataModel
	 */
	public static final String DEPENDENT_MODULES_DM_LIST = "IWorkbenchComponentBuilderDataModelProperties.DEPENDENT_MODULES_DM_LIST"; //$NON-NLS-1$

	public static final String MODULE_CORE = "IWorkbenchComponentBuilderDataModelProperties.MODULE_CORE";
}
