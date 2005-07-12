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
import org.eclipse.wst.common.componentcore.internal.builder.ReferencedComponentBuilderDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties;

/**
 * <p>
 * IWorkbenchComponentBuilderDataModelProperties supplies the properties to the IDataModel and 
 * associated DataModelProvider WorkbenchComponentBuilderDataModelProvider used by the 
 * ComponentStructuralBuilder.
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
 * @plannedfor 1.0
 */
public interface IWorkbenchComponentBuilderDataModelProperties extends IDataModelProperties{
    /**
     * Required, type project relative IFolder.  This represents the Folder output container defined for the given 
     * WORKBENCH_COMPONENT.
     */
	public static final String OUTPUT_CONTAINER = "IWorkbenchComponentBuilderDataModelProperties.OUTPUT_CONTAINER"; //$NON-NLS-1$
    /**
     * Required, type IVirtualComponent. The initializing builder will set this field to the WorkbenchComponent value of the 
     * Component to be built.
     */
	public static final String VIRTUAL_COMPONENT = "IWorkbenchComponentBuilderDataModelProperties.VIRTUAL_COMPONENT"; //$NON-NLS-1$
	/**
	 * Required, type List of DependentDeployableModuleDataModel  The initializing builder will set this field to a List of initialized DataModels for 
     * the ReferencedComponentBuilderDataModelProvider.  
     * This field represents builders (IDataModel( for all Referenced Components present in the current WorkbenchComponent.
     * 
     * @see org.eclipse.wst.common.frameworks.datamodel.IDataModel
     * @see ReferencedComponentBuilderDataModelProvider
     */
	public static final String DEPENDENT_COMPONENT_DM_LIST = "IWorkbenchComponentBuilderDataModelProperties.DEPENDENT_COMPONENT_DM_LIST"; //$NON-NLS-1$
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
    public static final String BUILD_KIND_FOR_DEP = "IWorkbenchComponentBuilderDataModelProperties.BUILD_KIND_FOR_DEP"; //$NON-NLS-1$
    
}
