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
package org.eclipse.wst.common.modulecore.internal.builder;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;

public abstract class ComponentStructuralBuilderDataModel extends WTPOperationDataModel{
	/**
	 * Required, type IProject
	 */
	public static final String PROJECT = "DeployableModuleProjectBuilderDataModel.PROJECT"; //$NON-NLS-1$

	/**
	 * Required, type Integer default to FULL
	 */
	public static final String BUILD_KIND = "DeployableModuleProjectBuilderDataModel.BUILD_KIND"; //$NON-NLS-1$

	/**
	 * Required, type IResourceDelta
	 */
	public static final String PROJECT_DETLA = "DeployableModuleProjectBuilderDataModel.PROJECT_DETLA"; //$NON-NLS-1$

	/**
	 * Required, type ModuleBuilderDataModel
	 */
	public static final String MODULE_BUILDER_DM_LIST = "DeployableModuleProjectBuilderDataModel.MODULE_BUILDER_DM_LIST"; //$NON-NLS-1$


	public static final String MODULE_CORE = "DeployableModuleProjectBuilderDataModel.MODULE_CORE";

    /**
     * 
     */
    public ComponentStructuralBuilderDataModel() {
        super();
    }
	protected void init() {
		super.init();
	}

	protected void initValidBaseProperties() {
		addValidBaseProperty(PROJECT);
		addValidBaseProperty(BUILD_KIND);
		addValidBaseProperty(PROJECT_DETLA);
		addValidBaseProperty(MODULE_BUILDER_DM_LIST);
		addValidBaseProperty(MODULE_CORE);
		super.initValidBaseProperties();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#getDefaultProperty(java.lang.String)
	 */
	protected Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(BUILD_KIND))
			return new Integer(IncrementalProjectBuilder.FULL_BUILD);
		return super.getDefaultProperty(propertyName);
	}
	
}
