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
package org.eclipse.wst.common.tests.ui;

import junit.framework.TestCase;

import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.tests.ITestDataModel;
import org.eclipse.wst.common.frameworks.datamodel.tests.TestDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.ui.DataModelWizard;
import org.eclipse.wst.common.frameworks.datamodel.ui.DataModelWizardFactory;

public class DataModelUIFactoryTest extends TestCase {

	public void testValidExtensionID() {
		IDataModel dataModel = DataModelFactory.INSTANCE.createDataModel("org.eclipse.wst.common.frameworks.datamodel.tests.ITestDataModel");
		assertTrue(dataModel.isProperty(ITestDataModel.FOO));
		DataModelWizard wizard = DataModelWizardFactory.INSTANCE.createWizard("org.eclipse.wst.common.frameworks.datamodel.tests.ITestDataModel");
		assertNotNull(wizard);
	}


	public void testValidExtensionClass() {
		IDataModel dataModel = DataModelFactory.INSTANCE.createDataModel(ITestDataModel.class);
		assertTrue(dataModel.isProperty(ITestDataModel.FOO));
		DataModelWizard wizard = DataModelWizardFactory.INSTANCE.createWizard(ITestDataModel.class);
		assertNotNull(wizard);
	}

	public void testValidExtensionInstance() {
		IDataModel dataModel = DataModelFactory.INSTANCE.createDataModel(new TestDataModelProvider());
		assertTrue(dataModel.isProperty(ITestDataModel.FOO));
		DataModelWizard wizard = DataModelWizardFactory.INSTANCE.createWizard(dataModel);
		assertNotNull(wizard);
		assertTrue(dataModel == ((TestDataModelWizard) wizard).getDataModel());
	}

}
