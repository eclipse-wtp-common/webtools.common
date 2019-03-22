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
package org.eclipse.wst.common.frameworks.datamodel.tests;

import junit.framework.TestCase;

import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class DataModelFactoryTest extends TestCase {


	public void testBogusExtension() {
		Exception exception = null;
		IDataModel dataModel = null;
		try {
			dataModel  = DataModelFactory.createDataModel("bogus");
		} catch (Exception e) {
			exception = e;
		}
		assertNull(dataModel);
	}

	public void testInvalidExtensionID() {
		Exception exception = null;
		IDataModel dataModel = null;
		try {
			dataModel = DataModelFactory.createDataModel("badID");
		} catch (Exception e) {
			exception = e;
		}
		assertNull(dataModel);
	}

	public void testInvalidExtensionClass() {
		Exception exception = null;
		IDataModel dataModel = null;
		try {
			 dataModel = DataModelFactory.createDataModel(Object.class);
		} catch (Exception e) {
			exception = e;
		}
		assertNull(dataModel);
	}
    
    public void testValidExtensionIDAndProviderType() {
        String[] descs = DataModelFactory.getDataModelProviderIDsForKind("testProviderBase");
        IDataModel dataModel = DataModelFactory.createDataModel(descs[0]);
        assertTrue(dataModel.isProperty(ITestDataModel.FOO));
    }
    
	public void testValidExtensionID() {
		IDataModel dataModel = DataModelFactory.createDataModel("org.eclipse.wst.common.frameworks.datamodel.tests.ITestDataModel");
		assertTrue(dataModel.isProperty(ITestDataModel.FOO));
	}
    
	public void testValidExtensionClass() {
		IDataModel dataModel = DataModelFactory.createDataModel(ITestDataModel.class);
		assertTrue(dataModel.isProperty(ITestDataModel.FOO));
	}

	public void testValidExtensionInstance() {
		IDataModel dataModel = DataModelFactory.createDataModel(new TestDataModelProvider());
		assertTrue(dataModel.isProperty(ITestDataModel.FOO));
	}



}
