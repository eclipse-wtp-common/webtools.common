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
package org.eclipse.wst.common.frameworks.datamodel.tests;

import org.eclipse.wst.common.frameworks.datamodel.provisional.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.provisional.IDataModel;

import junit.framework.TestCase;

public class DataModelFactoryTest extends TestCase {


	public void testBogusExtension() {
		Exception exception = null;
		try {
			IDataModel dataModel = DataModelFactory.INSTANCE.createDataModel("bogus");
		} catch (Exception e) {
			exception = e;
		}
		assertNotNull(exception);
	}

	public void testInvalidExtensionID() {
		Exception exception = null;
		try {
			IDataModel dataModel = DataModelFactory.INSTANCE.createDataModel("badID");
		} catch (Exception e) {
			exception = e;
		}
		assertNotNull(exception);
	}

	public void testInvalidExtensionClass() {
		Exception exception = null;
		try {
			IDataModel dataModel = DataModelFactory.INSTANCE.createDataModel(Object.class);
		} catch (Exception e) {
			exception = e;
		}
		assertNotNull(exception);
	}

	public void testValidExtensionID() {
		IDataModel dataModel = DataModelFactory.INSTANCE.createDataModel("org.eclipse.wst.common.frameworks.datamodel.tests.ITestDataModel");
		assertTrue(dataModel.isProperty(ITestDataModel.FOO));
	}

	public void testValidExtensionClass() {
		IDataModel dataModel = DataModelFactory.INSTANCE.createDataModel(ITestDataModel.class);
		assertTrue(dataModel.isProperty(ITestDataModel.FOO));
	}

	public void testValidExtensionInstance() {
		IDataModel dataModel = DataModelFactory.INSTANCE.createDataModel(new TestDataModelProvider());
		assertTrue(dataModel.isProperty(ITestDataModel.FOO));
	}

}
