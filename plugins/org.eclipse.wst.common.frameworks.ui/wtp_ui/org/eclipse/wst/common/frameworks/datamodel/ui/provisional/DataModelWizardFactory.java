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
package org.eclipse.wst.common.frameworks.datamodel.ui.provisional;

import org.eclipse.wst.common.frameworks.datamodel.provisional.IDataModel;
import org.eclipse.wst.common.frameworks.internal.operation.extensionui.DataModelWizardFactoryImpl;

public interface DataModelWizardFactory {

	public static final DataModelWizardFactory INSTANCE = new DataModelWizardFactoryImpl();

	/**
	 * Looks up the appropriate DataModelWizard by the specified id and constructs a new
	 * DataModelWizard using a new instance of the IDataModel looked up with the same id. If the
	 * DataModelWizard is not found then a RuntimeException is thrown.
	 * 
	 * @param id
	 *            the id of the DataModelWizard
	 * @return a new DataModelWizard
	 */
	public DataModelWizard createWizard(String id);

	/**
	 * Looks up the appropriate DataModelWizard using the name of the specified class as the id.
	 * This method is equavalent to <code>createWizard(classID.getName())</code>.
	 * 
	 * @param classID
	 *            the class whose name is the id of the DataModelWizard
	 * @return a new DataModelWizard
	 */
	public DataModelWizard createWizard(Class classID);

	/**
	 * Looks up the appropriate DataModelWizard using the id retured from
	 * <code>dataModel.getID()</code>.
	 * 
	 * @param dataModel
	 * @return a new DataModelWizard
	 */
	public DataModelWizard createWizard(IDataModel dataModel);

}
