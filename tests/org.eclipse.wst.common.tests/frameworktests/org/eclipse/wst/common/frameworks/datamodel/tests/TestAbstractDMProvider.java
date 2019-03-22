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

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
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

	private class DMOp extends AbstractDataModelOperation {

		public DMOp() {
			super();
		}

		public DMOp(IDataModel model) {
			super(model);
		}

		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return null;
		}

		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return null;
		}

		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return null;
		}

		public void setID(String id) {
			super.setID(id);
		}

		public String getID() {
			return super.getID();
		}

		public void setDataModel(IDataModel model) {
			super.setDataModel(model);
		}

		public IDataModel getDataModel() {
			return super.getDataModel();
		}
	}

	private class DMProvider extends AbstractDataModelProvider {

		public Set getPropertyNames(){
			Set propertyNames = super.getPropertyNames();
			propertyNames.add(INTEGER);
			propertyNames.add(BOOLEAN);
			propertyNames.add(STRING);
			propertyNames.add(OBJECT);
			return propertyNames;
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
			super.getProperty(INTEGER);
			super.setProperty(INTEGER, new Integer(1));
			super.getBooleanProperty(BOOLEAN);
			super.setBooleanProperty(BOOLEAN, true);
			super.getIntProperty(INTEGER);
			super.setIntProperty(INTEGER, 1);
			super.getStringProperty(STRING);
			super.isPropertySet(INTEGER);
		}
	}

	IDataModelProvider idmp = null;
	AbstractDataModelProvider dmp = null;
	IDataModel dm = null;

	IDataModelOperation idmo = null;
	AbstractDataModelOperation dmo = null;

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

	public void testAbstractDataModelOperation() {
		dmo = new DMOp();
		dmo = new DMOp(dm);
		idmo = dmo;
		dmo.setID("foo");
		assertTrue(dmo.getID().equals("foo"));
		dmo.setDataModel(dm);
		assertTrue(dm == dmo.getDataModel());
	}
	
	public void testIDataModelOperation() {
		dmo = new DMOp();
		idmo = dmo;
		idmo.setID("foo");
		assertTrue(idmo.getID().equals("foo"));
		idmo.setDataModel(dm);
		assertTrue(dm == idmo.getDataModel());
	}

}
