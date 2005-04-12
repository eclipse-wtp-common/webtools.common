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
package org.eclipse.wst.common.frameworks.datamodel;
/**
 * <p>
 * IDataModelProperties provides the base interface for all Data Model Properties interfaces.
 * The interface itself can be used to access an instance of the IDataModel and IDataModelProvider which will be registered
 * against with the interface.  IDataModels are not meant to be instantiated directly, rather they are built from an
 * IDataModelProvider. In this way the user will call to the DataModelFactory passing in the interface,
 * which will return the correct IDataModel. 
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider
 * @see org.eclipse.wst.common.frameworks.datamodel.DataModelFactory
 * @see 
 * 
 * @since 1.0
 */
public interface IDataModelProperties {

}
