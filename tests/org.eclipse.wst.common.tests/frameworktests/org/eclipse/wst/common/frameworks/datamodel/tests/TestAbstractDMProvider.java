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

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;

public class TestAbstractDMProvider extends TestCase {

	private static final String INTEGER = "INTEGER";
	private static final String BOOLEAN = "BOOLEAN";
	private static final String STRING = "STRING";
	private static final String OBJECT = "OBJECT";

	private class DMProvider extends AbstractDataModelProvider {

		public String[] getPropertyNames() {
			return combineProperties(new String[]{INTEGER, BOOLEAN}, new String[]{STRING, OBJECT});
		}

		public void init() {
			super.init();
		}

		public Object getDefaultProperty(String propertyName) {
			return super.getDefaultProperty(propertyName);
		}

		public boolean isPropertyEnabled(String propertyName) {
			return super.isPropertyEnabled(propertyName);
		}

		public IStatus validate(String name) {
			return super.validate(name);
		}

		public boolean propertySet(String propertyName, Object propertyValue) {
			return super.propertySet(propertyName, propertyValue);
		}

		public DataModelPropertyDescriptor getPropertyDescriptor(String propertyName) {
			return super.getPropertyDescriptor(propertyName);
		}

		public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
			return super.getValidPropertyDescriptors(propertyName);
		}

		public List getExtendedContext() {
			return super.getExtendedContext();
		}

		public IDataModelOperation getDefaultOperation() {
			return super.getDefaultOperation();
		}

		public String getID() {
			return super.getID();
		}

		public void dispose() {
			super.dispose();
		}

		public void protectedTest() {
			getProperty(INTEGER);
			setProperty(INTEGER, new Integer(1));
			getBooleanProperty(BOOLEAN);
			setBooleanProperty(BOOLEAN, true);
			getIntProperty(INTEGER);
			setIntProperty(INTEGER, 1);
			getStringProperty(STRING);
			isPropertySet(INTEGER);
		}
	}

	IDataModelProvider idmp = null;
	AbstractDataModelProvider dmp = null;
	IDataModel dm = null;

	protected void setUp() throws Exception {
		super.setUp();
		dmp = new DMProvider();
		idmp = dmp;
		dm = DataModelFactory.createDataModel(dmp);
	}

	public void testAbstractDataModelProvider() {
		dmp.setDataModel(dm);
		assertTrue(dm == dmp.getDataModel());
		assertNotNull(dmp.getPropertyNames());
		dmp.init();
		assertNull(dmp.getDefaultProperty(INTEGER));
		assertTrue(dmp.isPropertyEnabled(INTEGER));
		assertNull(dmp.validate(INTEGER));
		assertTrue(dmp.propertySet(INTEGER, new Integer(1)));
		assertNull(dmp.getPropertyDescriptor(INTEGER));
		assertNull(dmp.getValidPropertyDescriptors(INTEGER));
		assertNull(dmp.getExtendedContext());
		assertNull(dmp.getDefaultOperation());
		assertTrue(dmp.getID().equals(dmp.getClass().getName()));
		((DMProvider) dmp).protectedTest();
		dmp.dispose();
	}

	public void testIDataModelProvider() {
		idmp.setDataModel(dm);
		assertTrue(dm == idmp.getDataModel());
		assertNotNull(idmp.getPropertyNames());
		idmp.init();
		assertNull(idmp.getDefaultProperty(INTEGER));
		assertTrue(idmp.isPropertyEnabled(INTEGER));
		assertNull(idmp.validate(INTEGER));
		assertTrue(idmp.propertySet(INTEGER, new Integer(1)));
		assertNull(idmp.getPropertyDescriptor(INTEGER));
		assertNull(idmp.getValidPropertyDescriptors(INTEGER));
		assertNull(idmp.getExtendedContext());
		assertNull(idmp.getDefaultOperation());
		assertTrue(idmp.getID().equals(idmp.getClass().getName()));
		idmp.dispose();
	}
	
}
