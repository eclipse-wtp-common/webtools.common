/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.datamodel.provisional;

import org.eclipse.wst.common.frameworks.internal.datamodel.DataModelFactoryImpl;

public interface DataModelFactory {

	public static final DataModelFactory INSTANCE = new DataModelFactoryImpl();

	/**
	 * Looks up the appropriate IDataModelProvider by the specified id and constructs a new
	 * IDataModel. If the IDataModelProvider is not found then a RuntimeException is thrown.
	 * 
	 * @param dataModelProviderID
	 *            the id of the IDataModelProvider
	 * @return a new IDataModel
	 */
	public IDataModel createDataModel(String dataModelProviderID);

	/**
	 * Looks up the appropriate IDataModelProvider using the name of the specified class. This
	 * method is equavalent to
	 * <code>createDataModel(dataModelProviderClassID.getName())</code>.
	 * 
	 * @param dataModelProviderClass
	 *            the class whose name is the id of the IDataModelProvider
	 * @return a new IDataModel
	 */
	public IDataModel createDataModel(Class dataModelProviderClassID);

	/**
	 * Creates a new IDataModel using the the specified instance of an IDataModelProvider
	 * 
	 * @param dataModelProviderInstance
	 * @return a new IDataModel
	 */
	public IDataModel createDataModel(IDataModelProvider dataModelProviderInstance);

}
