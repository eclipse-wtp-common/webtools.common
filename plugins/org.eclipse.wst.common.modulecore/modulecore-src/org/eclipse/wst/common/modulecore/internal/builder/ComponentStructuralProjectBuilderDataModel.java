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
/**
 * Provides common set of preregisterd data model properties related to build status information 
 * present in IncrementalProjectBuilder (@see org.eclipse.core.resources.IncrementalProjectBuilder) as well as a ModuleCore instance for the current project which is being
 * built.
 * <p>
 * The data model should be subclassed by any vendor which aims to override the default ComponentStructuralBuilder.
 * Subclasses should implement all required methods from the super class including but not limited to getDefaultOperation.
 * which should return a WTPOperation associated with the data model.  The ComponentStructuralBuilder extension point should 
 * be used to register the overriding builder.
 * </p>
 * 
 * This class is experimental until fully documented.
 * </p>
 * 
 */
public abstract class ComponentStructuralProjectBuilderDataModel extends WTPOperationDataModel{
	/**
	 * Required, type String. The initializing builder will set this field to the name value of the 
	 * project which is currently being built.
	 */
	public static final String PROJECT = "ComponentStructuralProjectBuilderDataModel.PROJECT"; //$NON-NLS-1$

	/**
	 * Required, type Integer. The initializing builder will set this field to the int value based on the build
	 * kind passed to the IncrementalProjectBuilder
	 * 
	 * @see IncrementalProjectBuilder.FULL_BUILD
	 * <li><code>FULL_BUILD</code>- indicates a full build.</li>
	 * 
	 * @see IncrementalProjectBuilder.INCREMENTAL_BUILD
	 * <li><code>INCREMENTAL_BUILD</code>- indicates an incremental build.</li>
	 * 
	 * @see IncrementalProjectBuilder.AUTO_BUILD
	 * <li><code>AUTO_BUILD</code>- indicates an automatically triggered
	 */
	public static final String BUILD_KIND = "ComponentStructuralProjectBuilderDataModel.BUILD_KIND"; //$NON-NLS-1$
	/**
	 * Required, type Integer. The initializing builder will set this field to the IResourceDelta value based on the delta
	 * passed to the IncrementalProjectBuilder during a build call.  This field can be used along with the BUILD_KIND to 
	 * create a more efficient builder
	 * 
	 * @see org.eclipse.core.resources.IResourceDelta
	 */
	public static final String PROJECT_DETLA = "ComponentStructuralProjectBuilderDataModel.PROJECT_DETLA"; //$NON-NLS-1$

	/**
	 * Required, type org.eclipse.wst.common.modulecore.ModuleCore. The initializing builder will set this field to the ModuleCore associated
	 * with the project which is currently being built.  This field can be used to retrieve information about components and their associated 
	 * dependent components present in the current project.
	 * 
	 * @see org.eclipse.wst.common.modulecore.ModuleCore
	 */
	public static final String MODULE_CORE = "ComponentStructuralProjectBuilderDataModel.MODULE_CORE";

	/**
	 * <p>
	 * The ComponentStructuralBuilderDataModel constructor. This constructor will first add the base
	 * ComponentStructuralBuilderDataModel properties (PROJECT, BUILD_KIND, PROJECT_DETLA and
	 * MODULE_CORE). 
	 * 
	 * @see #initValidBaseProperties()
	 * 
	 * It then invokes the base WTPOperationDataModel.
	 * 
	 * @see WTPOperationDataModel
	 * 
	 * 
	 */
    public ComponentStructuralProjectBuilderDataModel() {
        super();
    }
	/**
	 * Subclasses should use this method within <code>initValidBaseProperties()</code> to add
	 * properties.
	 * 
	 * @param propertyName
	 *            The property name to be added.
	 * @see #initValidBaseProperties()
	 */
	protected void initValidBaseProperties() {
		addValidBaseProperty(PROJECT);
		addValidBaseProperty(BUILD_KIND);
		addValidBaseProperty(PROJECT_DETLA);
		addValidBaseProperty(MODULE_CORE);
		super.initValidBaseProperties();
	}
	/**
	 * Override this method to compute default property values.
	 * 
	 * @param propertyName
	 * @return
	 */
	protected Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(BUILD_KIND))
			return new Integer(IncrementalProjectBuilder.FULL_BUILD);
		return super.getDefaultProperty(propertyName);
	}
	
}
