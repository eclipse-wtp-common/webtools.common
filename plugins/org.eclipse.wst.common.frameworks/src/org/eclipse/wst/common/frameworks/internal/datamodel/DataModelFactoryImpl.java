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

	private DataModelExtensionReader reader;

	public IDataModel createDataModel(String dataModelProviderID) {
		return createDataModel(loadProvider(dataModelProviderID));
	}

	private IDataModelProvider loadProvider(String id) {
		if (null == reader) {
			reader = new DataModelExtensionReader();
		}
		return reader.getProvider(id);
	}

	public IDataModel createDataModel(Class dataModelProviderID) {
		return createDataModel(dataModelProviderID.getName());
	}

	public IDataModel createDataModel(IDataModelProvider provider) {
		DataModelImpl dataModel = new DataModelImpl(provider);
		return dataModel;
	}

}
