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
 * @plannedfor 1.0
 */
public interface IReferencedComponentBuilderDataModelProperties extends IDataModelProperties{
    /**
     * Required, type IVirtualComponent. 
     */
    public static final String VIRTUAL_REFERENCE = "IReferencedComponentBuilderDataModelProperties.VIRTUAL_REFERENCE"; //$NON-NLS-1$
}
