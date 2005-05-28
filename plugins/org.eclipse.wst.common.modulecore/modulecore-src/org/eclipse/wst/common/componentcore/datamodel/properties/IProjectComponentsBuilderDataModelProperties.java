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
package org.eclipse.wst.common.componentcore.datamodel.properties;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.wst.common.componentcore.internal.builder.WorkbenchComponentBuilderDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties;
/**
 * <p>
 * IProjectComponentsBuilderDataModelProperties supplies the properties to the IDataModel and 
 * associated DataModelProvider ProjectComponentsBuilderDataModelProvider used for 
 * the Project Component section of the ComponentStructuralBuilder. 
 * @see org.eclipse.wst.common.componentcore.internal.builder.ComponentStructuralBuilder
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider
 * @see org.eclipse.wst.common.frameworks.datamodel.DataModelFactory
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties
 * 
 * @since 1.0
 */
public interface IProjectComponentsBuilderDataModelProperties extends IDataModelProperties{
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
     * Populatd, type List. The initializing builder will set this field to a List of changed resources as calculated via a resource walker of 
     * IResouceDelta provided on the project via the project builder framework.  This list will be a list of IResources.
     */
    public static final String CHANGED_RESOURCES_DELTA = "IProjectComponentsBuilderDataModelProperties.CHANGED_RESOURCES_DELTA"; //$NON-NLS-1$

	/**
	 * Required, type ComponentCore. The initializing builder will set this field to the ModuleCore associated
	 * with the project which is currently being built.  This field can be used to retrieve information about components and their associated 
	 * dependent components present in the current project.
	 * 
	 * @see org.eclipse.wst.common.componentcore.ComponentCore
	 */
	public static final String COMPONENT_CORE = "IProjectComponentsBuilderDataModelProperties.COMPONENT_CORE";

    /**
     * Populatd, type List. The initializing builder will set this field to a List of initialized ComponentStructuralBuilders.  
     * This field represents builder IDataModel for all components present in the current project.
     * @see org.eclipse.wst.common.frameworks.datamodel.IDataModel
     * @see WorkbenchComponentBuilderDataModelProvider
     */
	public static final String COMPONENT_BUILDER_DM_LIST = "IProjectComponentsBuilderDataModelProperties.COMPONENT_BUILDER_DM_LIST"; //$NON-NLS-1$
    /**
     * Populatd, type List. The provider will populate this list of additional IDataModels for ReferencedComponentBjuilderDataModelProvider.  This list
     * will handle all rebuilding that needs to be done during an incremental build. 
     */
    public static final String ADDITIONAL_REFERENCED_BUILDER_DM_LIST = "IProjectComponentsBuilderDataModelProperties.ADDITIONAL_DEPENDENT_BUILDER_DM_LIST"; //$NON-NLS-1$

}
