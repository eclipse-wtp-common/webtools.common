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

public interface IReferencedComponentBuilderDataModelProperties {
    /**
     * Required, type IProject
     */
    public static final String CONTAINING_WBMODULE = "IReferencedComponentBuilderDataModelProperties.CONTAINING_WBMODULE"; //$NON-NLS-1$

    /**
     * Required, type ReferencedComponent
     */
    public static final String DEPENDENT_MODULE = "IReferencedComponentBuilderDataModelProperties.DEPENDENT_MODULE"; //$NON-NLS-1$

    /**
     * Calc, type project relative URI
     */
    public static final String HANDLE = "IReferencedComponentBuilderDataModelProperties.HANDLE"; //$NON-NLS-1$

    /**
     * Calc, type project relative IPath
     */
    public static final String OUTPUT_CONTAINER = "IReferencedComponentBuilderDataModelProperties.OUTPUT_CONTAINER"; //$NON-NLS-1$

    /**
     * Calc, type WorkbenchComponent
     */
    public static final String DEPENDENT_WBMODULE = "IReferencedComponentBuilderDataModelProperties.WORKBENCH_MODULE_RESOURCES"; //$NON-NLS-1$

    /**
     * Calc, type boolean
     */
    public static final String DOES_CONSUME = "IReferencedComponentBuilderDataModelProperties.DOES_CONSUME"; //$NON-NLS-1$

    public static final String MODULE_CORE = "IReferencedComponentBuilderDataModelProperties.MODULE_CORE"; //$NON-NLS-1$

}
