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
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.ui.DataModelWizard;
import org.eclipse.wst.common.frameworks.datamodel.ui.DataModelWizardFactory;

public class DataModelWizardFactoryImpl implements DataModelWizardFactory {

	private DataModelWizardExtensionReader reader;

	public DataModelWizard createWizard(IDataModel dataModel) {
		return loadWizard(dataModel);
	}

	public DataModelWizard createWizard(String dataModelID) {
		return createWizard(DataModelFactory.INSTANCE.createDataModel(dataModelID));
	}

	private DataModelWizard loadWizard(IDataModel dataModel) {
		if (null == reader) {
			reader = new DataModelWizardExtensionReader();
		}
		return reader.getWizard(dataModel);
	}

	public DataModelWizard createWizard(Class dataModelProviderID) {
		return createWizard(dataModelProviderID.getName());
	}
}
