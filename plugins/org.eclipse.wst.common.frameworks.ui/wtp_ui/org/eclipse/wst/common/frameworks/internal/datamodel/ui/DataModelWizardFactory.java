/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel.ui;

import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.operation.extensionui.DataModelWizardExtensionReader;

public class DataModelWizardFactory {

	private static DataModelWizardExtensionReader reader;

	/**
	 * Looks up the appropriate DataModelWizard by the specified id and
	 * constructs a new DataModelWizard using a new instance of the IDataModel
	 * looked up with the same id. If the DataModelWizard is not found then a
	 * RuntimeException is thrown.
	 * 
	 * @param id
	 *            the id of the DataModelWizard
	 * @return a new DataModelWizard
	 */
	public static DataModelWizard createWizard(String id) {
		return createWizard(DataModelFactory.createDataModel(id));
	}

	/**
	 * Looks up the appropriate DataModelWizard using the id retured from
	 * <code>dataModel.getID()</code>.
	 * 
	 * @param dataModel
	 * @return a new DataModelWizard
	 */
	public static DataModelWizard createWizard(IDataModel dataModel) {
		return loadWizard(dataModel);
	}

	private static DataModelWizard loadWizard(IDataModel dataModel) {
		if (null == reader) {
			reader = new DataModelWizardExtensionReader();
		}
		return reader.getWizard(dataModel);
	}

	/**
	 * Looks up the appropriate DataModelWizard using the name of the specified
	 * class as the id. This method is equavalent to
	 * <code>createWizard(classID.getName())</code>.
	 * 
	 * @param classID
	 *            the class whose name is the id of the DataModelWizard
	 * @return a new DataModelWizard
	 */
	public static DataModelWizard createWizard(Class dataModelProviderID) {
		return createWizard(dataModelProviderID.getName());
	}
}
