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

import org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties;

/**
 * <p>
 * IReferencedComponentBuilderDataModelProperties supplies the properties to the IDataModel and 
 * associated DataModelProvider ReferencedComponentBuilderDataModelProvider used by the 
 * ComponentStructuralBuilderDependencyResolver 
 * @see org.eclipse.wst.common.componentcore.internal.builder.ComponentStructuralBuilderDependencyResolver
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
public interface IReferencedComponentBuilderDataModelProperties extends IDataModelProperties{
    /**
     * Required, type WorkbenchComponent. The initializing builder will set this field to the WorkbenchComponent value of the 
     * Component which is referencing the DEPENDENT_COMPONENT
     */
    public static final String CONTAINING_WB_COMPONENT = "IReferencedComponentBuilderDataModelProperties.CONTAINING_WB_COMPONENT"; //$NON-NLS-1$

    /**
     * Required, type ReferencedComponent. This field represents a ReferencedComponent which has another 
     * Component (namely the CONTAINING_WB_COMPONENT field) depends on.
     * 
     * @see org.eclipse.wst.common.componentcore.internal.ReferencedComponent
     */
    public static final String DEPENDENT_COMPONENT = "IReferencedComponentBuilderDataModelProperties.DEPENDENT_COMPONENT"; //$NON-NLS-1$
    /**
     * Calculated, type project relative IPath.  This represents the folder output container defined for the given CONTAINING_WB_COMPONENT.
     */
    public static final String OUTPUT_CONTAINER = "IReferencedComponentBuilderDataModelProperties.OUTPUT_CONTAINER"; //$NON-NLS-1$

    /**
     * Required, type WorkbenchComponent. This field represents a the actual WorkbenchComponent defined by
     * the DEPENDENT_COMPONENT
     * 
     * @see org.eclipse.wst.common.componentcore.internal.WorkbenchComponent
     */
    public static final String DEPENDENT_WB_COMPONENT = "IReferencedComponentBuilderDataModelProperties.DEPENDENT_WB_COMPONENT"; //$NON-NLS-1$

    /**
     * Calc, type Boolean.  Represents property defined by the defining Containing CONTAINING_WB_COMPONENT.  
     * Indicates the referenced component is either CONSUMED or USES by the referencing Component.
     * @see DependencyType.CONSUMES
     * @see DependencyType.USES
     */
    public static final String DOES_CONSUME = "IReferencedComponentBuilderDataModelProperties.DOES_CONSUME"; //$NON-NLS-1$
    /**
     * Required, type ComponentCore. The initializing builder will set this field to the ModuleCore associated
     * with the project which is currently being built.  This field can be used to retrieve information about components and their associated 
     * dependent components present in the current project.
     * 
     * @see org.eclipse.wst.common.componentcore.ComponentCore
     */
    public static final String COMPONENT_CORE = "IReferencedComponentBuilderDataModelProperties.COMPONENT_CORE"; //$NON-NLS-1$

}
