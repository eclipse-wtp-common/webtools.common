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

public interface IProjectComponentsBuilderDataModelProperties {
	/**
	 * Required, type String. The initializing builder will set this field to the name value of the 
	 * project which is currently being built.
	 */
	public static final String PROJECT = "IProjectComponentsBuilderDataModelProperties.PROJECT"; //$NON-NLS-1$

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
	public static final String BUILD_KIND = "IProjectComponentsBuilderDataModelProperties.BUILD_KIND"; //$NON-NLS-1$
	/**
	 * Required, type Integer. The initializing builder will set this field to the IResourceDelta value based on the delta
	 * passed to the IncrementalProjectBuilder during a build call.  This field can be used along with the BUILD_KIND to 
	 * create a more efficient builder
	 * 
	 * @see org.eclipse.core.resources.IResourceDelta
	 */
	public static final String PROJECT_DETLA = "IProjectComponentsBuilderDataModelProperties.PROJECT_DETLA"; //$NON-NLS-1$

	/**
	 * Required, type org.eclipse.wst.common.modulecore.ModuleCore. The initializing builder will set this field to the ModuleCore associated
	 * with the project which is currently being built.  This field can be used to retrieve information about components and their associated 
	 * dependent components present in the current project.
	 * 
	 * @see org.eclipse.wst.common.componentcore.StructureEdit
	 */
	public static final String MODULE_CORE = "IProjectComponentsBuilderDataModelProperties.MODULE_CORE";
	
	public static final String MODULE_BUILDER_DM_LIST = "IProjectComponentsBuilderDataModelProperties.MODULE_BUILDER_DM_LIST"; //$NON-NLS-1$

}
