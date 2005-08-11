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
package org.eclipse.wst.common.frameworks.operations.tests;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.operations.WTPDataModelBridgeProvider;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModelEvent;
import org.eclipse.wst.common.frameworks.internal.operations.WTPPropertyDescriptor;

public class SimpleDataModelTest extends TestCase {

	private class DM extends WTPOperationDataModel {
		public static final String INT_PROP = "INT_PROP";
		public static final String INT_PROP2 = "INT_PROP2";
		public static final String INT_PROP3 = "INT_PROP3";
		public static final String INT_PROP4 = "INT_PROP4";
		public static final String BOOLEAN_PROP = "BOOLEAN_PROP";
		public static final String BOOLEAN_PROP2 = "BOOLEAN_PROP2";
		public static final String STRING_PROP = "STRING_PROP";

		protected void initValidBaseProperties() {
			super.initValidBaseProperties();
			addValidBaseProperty(INT_PROP);
			addValidBaseProperty(INT_PROP2);
			addValidBaseProperty(INT_PROP3);
			addValidBaseProperty(INT_PROP4);
			addValidBaseProperty(BOOLEAN_PROP);
			addValidBaseProperty(BOOLEAN_PROP2);
			addValidBaseProperty(STRING_PROP);
		}

		protected Object getDefaultProperty(String propertyName) {
			if (propertyName.equals(INT_PROP)) {
				return new Integer(10);
			} else if (propertyName.equals(INT_PROP2)) {
				return getProperty(INT_PROP);
			} else if (propertyName.equals(BOOLEAN_PROP)) {
				return Boolean.TRUE;
			} else if (propertyName.equals(STRING_PROP)) {
				return "foo" + getProperty(INT_PROP) + getProperty(BOOLEAN_PROP);
			}
			return super.getDefaultProperty(propertyName);
		}

		protected Boolean basicIsEnabled(String propertyName) {
			if (propertyName.equals(BOOLEAN_PROP2)) {
				return (Boolean) getProperty(BOOLEAN_PROP);
			}
			return super.basicIsEnabled(propertyName);
		}

		protected boolean doSetProperty(String propertyName, Object propertyValue) {
			boolean success = super.doSetProperty(propertyName, propertyValue);
			if (propertyName.equals(INT_PROP)) {
				notifyDefaultChange(INT_PROP2);
				notifyValidValuesChange(INT_PROP2);
				notifyDefaultChange(STRING_PROP);
			}
			if (propertyName.equals(BOOLEAN_PROP)) {
				notifyEnablementChange(BOOLEAN_PROP2);
				notifyDefaultChange(STRING_PROP);
			}
			return success;
		}

		protected WTPPropertyDescriptor[] doGetValidPropertyDescriptors(String propertyName) {
			if (INT_PROP2.equals(propertyName)) {
				int range = getIntProperty(INT_PROP);
				Integer[] ints = new Integer[range];
				for (int i = 0; i < ints.length; i++) {
					ints[i] = new Integer(i + 1);
				}
				return WTPPropertyDescriptor.createDescriptors(ints);
			}
			if (INT_PROP3.equals(propertyName)) {
				int range = 3;
				Integer[] ints = new Integer[range];
				for (int i = 0; i < ints.length; i++) {
					ints[i] = new Integer(i + 1);
				}
				String[] descriptions = new String[]{"one", "two", "three"};
				return WTPPropertyDescriptor.createDescriptors(ints, descriptions);
			}
			if (INT_PROP4.equals(propertyName)) {
				WTPPropertyDescriptor[] descriptors = new WTPPropertyDescriptor[3];
				String[] descriptions = new String[]{"one", "two", "three"};
				for (int i = 0; i < descriptors.length; i++) {
					descriptors[i] = new WTPPropertyDescriptor(new Integer(i + 1), descriptions[i]);
				}
				return descriptors;
			}
			return super.doGetValidPropertyDescriptors(propertyName);
		}

		public WTPOperation getDefaultOperation() {
			return null;
		}
	};

	private class DM_Provider extends WTPDataModelBridgeProvider {
		protected WTPOperationDataModel initWTPDataModel() {
			return wtpDM;
		}
	}

	private DM wtpDM;
	private IDataModel dm;
	private WTPTestListener wtpDML;
	private TestListener dmL;

	protected void setUp() throws Exception {
		super.setUp();
		wtpDM = new DM();
		wtpDML = new WTPTestListener();
		wtpDM.addListener(wtpDML);
		dm = DataModelFactory.createDataModel(new DM_Provider());
		dmL = new TestListener();
		dm.addListener(dmL);
	}

	public void testPropertyDescriptors() {
		WTPPropertyDescriptor[] wtpDescriptors = wtpDM.getValidPropertyDescriptors(DM.INT_PROP2);
		DataModelPropertyDescriptor[] descriptors = dm.getValidPropertyDescriptors(DM.INT_PROP2);
		for (int i = 0; i < wtpDescriptors.length; i++) {
			int value = i + 1;
			assertEquals(value, ((Integer) wtpDescriptors[i].getPropertyValue()).intValue());
			assertEquals("" + value, wtpDescriptors[i].getPropertyDescription());
			assertEquals(value, ((Integer) descriptors[i].getPropertyValue()).intValue());
			assertEquals("" + value, descriptors[i].getPropertyDescription());
		}
		wtpDescriptors = wtpDM.getValidPropertyDescriptors(DM.INT_PROP3);
		descriptors = dm.getValidPropertyDescriptors(DM.INT_PROP3);
		String[] descriptions = new String[]{"one", "two", "three"};
		for (int i = 0; i < wtpDescriptors.length; i++) {
			int value = i + 1;
			assertEquals(value, ((Integer) wtpDescriptors[i].getPropertyValue()).intValue());
			assertEquals(descriptions[i], wtpDescriptors[i].getPropertyDescription());
			assertEquals(value, ((Integer) descriptors[i].getPropertyValue()).intValue());
			assertEquals(descriptions[i], descriptors[i].getPropertyDescription());
		}
		wtpDescriptors = wtpDM.getValidPropertyDescriptors(DM.INT_PROP4);
		descriptors = dm.getValidPropertyDescriptors(DM.INT_PROP4);
		for (int i = 0; i < wtpDescriptors.length; i++) {
			int value = i + 1;
			assertEquals(value, ((Integer) wtpDescriptors[i].getPropertyValue()).intValue());
			assertEquals(descriptions[i], wtpDescriptors[i].getPropertyDescription());
			assertEquals(value, ((Integer) descriptors[i].getPropertyValue()).intValue());
			assertEquals(descriptions[i], descriptors[i].getPropertyDescription());
		}
	}


	public void testDefaults() {
		assertEquals(true, wtpDM.getBooleanProperty(DM.BOOLEAN_PROP));
		assertEquals(true, dm.getBooleanProperty(DM.BOOLEAN_PROP));
		assertEquals(true, ((Boolean) wtpDM.getProperty(DM.BOOLEAN_PROP)).booleanValue());
		assertEquals(true, ((Boolean) dm.getProperty(DM.BOOLEAN_PROP)).booleanValue());
		assertEquals(10, wtpDM.getIntProperty(DM.INT_PROP));
		assertEquals(10, dm.getIntProperty(DM.INT_PROP));
		assertEquals(10, ((Integer) wtpDM.getProperty(DM.INT_PROP)).intValue());
		assertEquals(10, ((Integer) dm.getProperty(DM.INT_PROP)).intValue());
		assertEquals("foo10true", (String) wtpDM.getProperty(DM.STRING_PROP));
		assertEquals("foo10true", (String) dm.getProperty(DM.STRING_PROP));
		assertEquals("foo10true", wtpDM.getStringProperty(DM.STRING_PROP));
		assertEquals("foo10true", dm.getStringProperty(DM.STRING_PROP));
	}

	private Object verifyEventOccurred(String propertyName, int type, Object value, List eventList) {
		for (int i = 0; i < eventList.size(); i++) {
			Object obj = eventList.get(i);
			if (obj instanceof WTPOperationDataModelEvent) {
				WTPOperationDataModelEvent eventWTP = (WTPOperationDataModelEvent) obj;
				if (eventWTP.getPropertyName().equals(propertyName) && eventWTP.getFlag() == (type)) {
					if (null == value || value.equals(eventWTP.getProperty())) {
						eventList.remove(obj);
						return obj;
					}
				}
			} else {
				DataModelEvent event = (DataModelEvent) obj;
				if (event.getPropertyName().equals(propertyName) && event.getFlag() == (type)) {
					if (null == value || value.equals(event.getProperty())) {
						eventList.remove(obj);
						return obj;
					}
				}
			}
		}
		Assert.fail("Event not found");
		return null;
	}

	public void testFiringEvents() {
		wtpDML.clearEvents();
		dmL.clearEvents();
		wtpDM.notifyDefaultChange(DM.INT_PROP2);
		List eventsWTP = wtpDML.getEvents();
		List events = dmL.getEvents();
		verifyEventOccurred(DM.INT_PROP2, WTPOperationDataModelEvent.PROPERTY_CHG, null, eventsWTP);
		verifyEventOccurred(DM.INT_PROP2, DataModelEvent.VALUE_CHG, null, events);

		wtpDML.clearEvents();
		dmL.clearEvents();
		wtpDM.notifyValidValuesChange(DM.INT_PROP2);
		eventsWTP = wtpDML.getEvents();
		events = dmL.getEvents();
		assertEquals(1, eventsWTP.size());
		assertEquals(1, events.size());

		verifyEventOccurred(DM.INT_PROP2, WTPOperationDataModelEvent.VALID_VALUES_CHG, null, eventsWTP);
		verifyEventOccurred(DM.INT_PROP2, DataModelEvent.VALID_VALUES_CHG, null, events);
	}

	public void testSimpleSetEvents() throws Exception {
		for (int i = 0; i < 2; i++) {
			if (i != 0) {
				setUp();
			}

			wtpDML.clearEvents();
			dmL.clearEvents();
			if (i == 0) {
				wtpDM.setIntProperty(DM.INT_PROP2, 100);
			} else {
				dm.setIntProperty(DM.INT_PROP2, 100);
			}
			List eventsWTP = wtpDML.getEvents();
			List events = dmL.getEvents();

			verifyEventOccurred(DM.INT_PROP2, WTPOperationDataModelEvent.PROPERTY_CHG, new Integer(100), eventsWTP);
			verifyEventOccurred(DM.INT_PROP2, DataModelEvent.VALUE_CHG, new Integer(100), events);

			wtpDML.clearEvents();
			dmL.clearEvents();

			if (i == 0) {
				wtpDM.setIntProperty(DM.INT_PROP2, 100);
			} else {
				dm.setIntProperty(DM.INT_PROP2, 100);
			}
			eventsWTP = wtpDML.getEvents();
			events = dmL.getEvents();
			assertEquals(0, eventsWTP.size());
			assertEquals(0, events.size());

			wtpDML.clearEvents();
			dmL.clearEvents();
			if (i == 0) {
				wtpDM.setIntProperty(DM.INT_PROP2, 101);
			} else {
				dm.setIntProperty(DM.INT_PROP2, 101);
			}
			eventsWTP = wtpDML.getEvents();
			events = dmL.getEvents();
			assertEquals(1, eventsWTP.size());

			verifyEventOccurred(DM.INT_PROP2, WTPOperationDataModelEvent.PROPERTY_CHG, new Integer(101), eventsWTP);
			verifyEventOccurred(DM.INT_PROP2, DataModelEvent.VALUE_CHG, new Integer(101), events);
		}
	}

	public void testComplexEvents() {
		wtpDML.clearEvents();
		wtpDM.setIntProperty(DM.INT_PROP, 11);
		List eventsWTP = wtpDML.getEvents();
		List events = dmL.getEvents();

		verifyEventOccurred(DM.INT_PROP2, WTPOperationDataModelEvent.PROPERTY_CHG, new Integer(11), eventsWTP);
		verifyEventOccurred(DM.INT_PROP2, DataModelEvent.VALUE_CHG, new Integer(11), events);

		WTPOperationDataModelEvent eventWTP = (WTPOperationDataModelEvent) verifyEventOccurred(DM.INT_PROP2, WTPOperationDataModelEvent.VALID_VALUES_CHG, null, eventsWTP);
		DataModelEvent event = (DataModelEvent) verifyEventOccurred(DM.INT_PROP2, DataModelEvent.VALID_VALUES_CHG, null, events);

		WTPPropertyDescriptor[] descriptorsWTP = eventWTP.getValidPropertyDescriptors();
		DataModelPropertyDescriptor[] descriptors = event.getValidPropertyDescriptors();
		WTPPropertyDescriptor[] descriptorsWTP2 = wtpDM.getValidPropertyDescriptors(DM.INT_PROP2);
		DataModelPropertyDescriptor[] descriptors2 = dm.getValidPropertyDescriptors(DM.INT_PROP2);
		assertEquals(11, descriptorsWTP.length);
		assertEquals(11, descriptors.length);
		assertEquals(11, descriptorsWTP2.length);
		assertEquals(11, descriptors2.length);

		verifyEventOccurred(DM.STRING_PROP, WTPOperationDataModelEvent.PROPERTY_CHG, "foo11true", eventsWTP);
		verifyEventOccurred(DM.STRING_PROP, DataModelEvent.VALUE_CHG, "foo11true", events);

		verifyEventOccurred(DM.INT_PROP, WTPOperationDataModelEvent.PROPERTY_CHG, new Integer(11), eventsWTP);
		verifyEventOccurred(DM.INT_PROP, DataModelEvent.VALUE_CHG, new Integer(11), events);

		wtpDML.clearEvents();
		dmL.clearEvents();
		wtpDM.setBooleanProperty(DM.BOOLEAN_PROP, false);
		eventsWTP = wtpDML.getEvents();
		events = dmL.getEvents();

		verifyEventOccurred(DM.BOOLEAN_PROP2, WTPOperationDataModelEvent.ENABLE_CHG, null, eventsWTP);
		verifyEventOccurred(DM.BOOLEAN_PROP2, DataModelEvent.ENABLE_CHG, null, events);
		assertFalse(wtpDM.isEnabled(DM.BOOLEAN_PROP2).booleanValue());
		assertFalse(dm.isPropertyEnabled(DM.BOOLEAN_PROP2));


		verifyEventOccurred(DM.STRING_PROP, WTPOperationDataModelEvent.PROPERTY_CHG, "foo11false", eventsWTP);
		verifyEventOccurred(DM.STRING_PROP, DataModelEvent.VALUE_CHG, "foo11false", events);

		verifyEventOccurred(DM.BOOLEAN_PROP, WTPOperationDataModelEvent.PROPERTY_CHG, new Boolean(false), eventsWTP);
		verifyEventOccurred(DM.BOOLEAN_PROP, DataModelEvent.VALUE_CHG, new Boolean(false), events);


		wtpDM.setProperty(DM.STRING_PROP, "bar");
		assertEquals("bar", wtpDM.getStringProperty(DM.STRING_PROP));
		assertEquals("bar", dm.getStringProperty(DM.STRING_PROP));

		wtpDML.clearEvents();
		dmL.clearEvents();
		wtpDM.setBooleanProperty(DM.BOOLEAN_PROP, true);
		eventsWTP = wtpDML.getEvents();
		events = dmL.getEvents();

		verifyEventOccurred(DM.BOOLEAN_PROP, WTPOperationDataModelEvent.PROPERTY_CHG, null, eventsWTP);
		verifyEventOccurred(DM.BOOLEAN_PROP, DataModelEvent.VALUE_CHG, null, events);

		verifyEventOccurred(DM.BOOLEAN_PROP2, WTPOperationDataModelEvent.ENABLE_CHG, null, eventsWTP);
		verifyEventOccurred(DM.BOOLEAN_PROP2, DataModelEvent.ENABLE_CHG, null, events);

		assertEquals("bar", wtpDM.getStringProperty(DM.STRING_PROP));
		assertEquals("bar", dm.getStringProperty(DM.STRING_PROP));

		wtpDM.setProperty(DM.STRING_PROP, null);
		assertEquals("foo11true", wtpDM.getStringProperty(DM.STRING_PROP));
		assertEquals("foo11true", dm.getStringProperty(DM.STRING_PROP));

		wtpDML.clearEvents();
		dmL.clearEvents();
		wtpDM.setBooleanProperty(DM.BOOLEAN_PROP, false);
		eventsWTP = wtpDML.getEvents();
		events = dmL.getEvents();

		verifyEventOccurred(DM.BOOLEAN_PROP2, WTPOperationDataModelEvent.ENABLE_CHG, null, eventsWTP);
		verifyEventOccurred(DM.BOOLEAN_PROP2, DataModelEvent.ENABLE_CHG, null, events);
		assertFalse(wtpDM.isEnabled(DM.BOOLEAN_PROP2).booleanValue());
		assertFalse(dm.isPropertyEnabled(DM.BOOLEAN_PROP2));

		verifyEventOccurred(DM.STRING_PROP, WTPOperationDataModelEvent.PROPERTY_CHG, "foo11false", eventsWTP);
		verifyEventOccurred(DM.STRING_PROP, DataModelEvent.VALUE_CHG, "foo11false", events);

		verifyEventOccurred(DM.BOOLEAN_PROP, WTPOperationDataModelEvent.PROPERTY_CHG, new Boolean(false), eventsWTP);
		verifyEventOccurred(DM.BOOLEAN_PROP, DataModelEvent.VALUE_CHG, new Boolean(false), events);

	}

}
