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
package org.eclipse.wst.common.frameworks.internal.datamodel;

import org.eclipse.wst.common.frameworks.datamodel.provisional.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.provisional.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.provisional.IDataModelProvider;

public class DataModelFactoryImpl implements DataModelFactory {

	public IDataModel createDataModel(String dataModelProviderID) {
		return createDataModel(lookupProviderClass(dataModelProviderID));
	}

	private Class lookupProviderClass(String dataModelProviderID) {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataModel createDataModel(Class dataModelProviderClass) {
		IDataModelProvider provider;
		try {
			provider = (IDataModelProvider) dataModelProviderClass.newInstance();
			return createDataModel(provider);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public IDataModel createDataModel(IDataModelProvider provider) {
		DataModelImpl dataModel = new DataModelImpl(provider);
		return dataModel;
	}

}
