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

import junit.framework.TestCase;

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

	private DM dm;
	private TestListener dmL;

	protected void setUp() throws Exception {
		super.setUp();
		dm = new DM();
		dmL = new TestListener();
		dm.addListener(dmL);
	}

	public void testPropertyDescriptors() {
		WTPPropertyDescriptor[] descriptors = dm.getValidPropertyDescriptors(DM.INT_PROP2);
		for (int i = 0; i < descriptors.length; i++) {
			int value = i + 1;
			assertEquals(value, ((Integer) descriptors[i].getPropertyValue()).intValue());
			assertEquals("" + value, descriptors[i].getPropertyDescription());
		}
		descriptors = dm.getValidPropertyDescriptors(DM.INT_PROP3);
		String[] descriptions = new String[]{"one", "two", "three"};
		for (int i = 0; i < descriptors.length; i++) {
			int value = i + 1;
			assertEquals(value, ((Integer) descriptors[i].getPropertyValue()).intValue());
			assertEquals(descriptions[i], descriptors[i].getPropertyDescription());
		}
		descriptors = dm.getValidPropertyDescriptors(DM.INT_PROP4);
		for (int i = 0; i < descriptors.length; i++) {
			int value = i + 1;
			assertEquals(value, ((Integer) descriptors[i].getPropertyValue()).intValue());
			assertEquals(descriptions[i], descriptors[i].getPropertyDescription());
		}
	}


	public void testDefaults() {
		assertEquals(true, dm.getBooleanProperty(DM.BOOLEAN_PROP));
		assertEquals(true, ((Boolean) dm.getProperty(DM.BOOLEAN_PROP)).booleanValue());
		assertEquals(10, dm.getIntProperty(DM.INT_PROP));
		assertEquals(10, ((Integer) dm.getProperty(DM.INT_PROP)).intValue());
		assertEquals("foo10true", (String) dm.getProperty(DM.STRING_PROP));
		assertEquals("foo10true", dm.getStringProperty(DM.STRING_PROP));
	}

	public void testFiringEvents() {
		dmL.clearEvents();
		dm.notifyDefaultChange(DM.INT_PROP2);
		List events = dmL.getEvents();
		assertEquals(1, events.size());
		WTPOperationDataModelEvent event = (WTPOperationDataModelEvent) events.get(0);
		assertEquals(DM.INT_PROP2, event.getPropertyName());
		assertEquals(WTPOperationDataModelEvent.PROPERTY_CHG, event.getFlag());

		dmL.clearEvents();
		dm.notifyValidValuesChange(DM.INT_PROP2);
		events = dmL.getEvents();
		assertEquals(1, events.size());
		event = (WTPOperationDataModelEvent) events.get(0);
		assertEquals(DM.INT_PROP2, event.getPropertyName());
		assertEquals(WTPOperationDataModelEvent.VALID_VALUES_CHG, event.getFlag());
	}

	public void testSimpleSetEvents() {
		dmL.clearEvents();
		dm.setIntProperty(DM.INT_PROP2, 100);
		List events = dmL.getEvents();
		assertEquals(1, events.size());
		WTPOperationDataModelEvent event = (WTPOperationDataModelEvent) events.get(0);
		assertEquals(DM.INT_PROP2, event.getPropertyName());
		assertEquals(WTPOperationDataModelEvent.PROPERTY_CHG, event.getFlag());
		assertEquals(100, dm.getIntProperty(DM.INT_PROP2));

		dmL.clearEvents();
		dm.setIntProperty(DM.INT_PROP2, 100);
		events = dmL.getEvents();
		assertEquals(0, events.size());

		dmL.clearEvents();
		dm.setIntProperty(DM.INT_PROP2, 101);
		events = dmL.getEvents();
		assertEquals(1, events.size());
		event = (WTPOperationDataModelEvent) events.get(0);
		assertEquals(DM.INT_PROP2, event.getPropertyName());
	}

	public void testComplexEvents() {
		dmL.clearEvents();
		dm.setIntProperty(DM.INT_PROP, 11);
		List events = dmL.getEvents();
		assertEquals(4, events.size());

		WTPOperationDataModelEvent event = (WTPOperationDataModelEvent) events.get(0);
		assertEquals(DM.INT_PROP2, event.getPropertyName());
		assertEquals(WTPOperationDataModelEvent.PROPERTY_CHG, event.getFlag());
		assertEquals(11, ((Integer) dm.getProperty(DM.INT_PROP2)).intValue());

		event = (WTPOperationDataModelEvent) events.get(1);
		assertEquals(DM.INT_PROP2, event.getPropertyName());
		assertEquals(WTPOperationDataModelEvent.VALID_VALUES_CHG, event.getFlag());
		WTPPropertyDescriptor[] descriptors = event.getValidPropertyDescriptors();
		WTPPropertyDescriptor[] descriptors2 = dm.getValidPropertyDescriptors(DM.INT_PROP2);
		assertEquals(11, descriptors.length);
		assertEquals(11, descriptors2.length);

		event = (WTPOperationDataModelEvent) events.get(2);
		assertEquals(DM.STRING_PROP, event.getPropertyName());
		assertEquals(WTPOperationDataModelEvent.PROPERTY_CHG, event.getFlag());
		assertEquals("foo11true", event.getProperty());

		event = (WTPOperationDataModelEvent) events.get(3);
		assertEquals(DM.INT_PROP, event.getPropertyName());
		assertEquals(WTPOperationDataModelEvent.PROPERTY_CHG, event.getFlag());
		assertEquals(11, ((Integer) dm.getProperty(DM.INT_PROP)).intValue());

		dmL.clearEvents();
		dm.setBooleanProperty(DM.BOOLEAN_PROP, false);
		events = dmL.getEvents();
		assertEquals(3, events.size());
		event = (WTPOperationDataModelEvent) events.get(0);
		assertEquals(DM.BOOLEAN_PROP2, event.getPropertyName());
		assertEquals(WTPOperationDataModelEvent.ENABLE_CHG, event.getFlag());
		assertFalse(dm.isEnabled(DM.BOOLEAN_PROP2).booleanValue());

		event = (WTPOperationDataModelEvent) events.get(1);
		assertEquals(DM.STRING_PROP, event.getPropertyName());
		assertEquals(WTPOperationDataModelEvent.PROPERTY_CHG, event.getFlag());
		assertEquals("foo11false", event.getProperty());

		event = (WTPOperationDataModelEvent) events.get(2);
		assertEquals(DM.BOOLEAN_PROP, event.getPropertyName());
		assertEquals(WTPOperationDataModelEvent.PROPERTY_CHG, event.getFlag());
		assertEquals(false, dm.getBooleanProperty(DM.BOOLEAN_PROP));

		dm.setProperty(DM.STRING_PROP, "bar");
		assertEquals("bar", dm.getStringProperty(DM.STRING_PROP));
		dmL.clearEvents();
		dm.setBooleanProperty(DM.BOOLEAN_PROP, true);
		events = dmL.getEvents();
		assertEquals(2, events.size());
		event = (WTPOperationDataModelEvent) events.get(0);
		assertEquals(DM.BOOLEAN_PROP2, event.getPropertyName());
		event = (WTPOperationDataModelEvent) events.get(1);
		assertEquals(DM.BOOLEAN_PROP, event.getPropertyName());

		assertEquals("bar", dm.getStringProperty(DM.STRING_PROP));
		dm.setProperty(DM.STRING_PROP, null);
		assertEquals("foo11true", dm.getStringProperty(DM.STRING_PROP));
		dmL.clearEvents();
		dm.setBooleanProperty(DM.BOOLEAN_PROP, false);
		events = dmL.getEvents();
		assertEquals(3, events.size());
		event = (WTPOperationDataModelEvent) events.get(0);
		assertEquals(DM.BOOLEAN_PROP2, event.getPropertyName());
		assertEquals(WTPOperationDataModelEvent.ENABLE_CHG, event.getFlag());
		assertFalse(dm.isEnabled(DM.BOOLEAN_PROP2).booleanValue());

		event = (WTPOperationDataModelEvent) events.get(1);
		assertEquals(DM.STRING_PROP, event.getPropertyName());
		assertEquals(WTPOperationDataModelEvent.PROPERTY_CHG, event.getFlag());
		assertEquals("foo11false", event.getProperty());

		event = (WTPOperationDataModelEvent) events.get(2);
		assertEquals(DM.BOOLEAN_PROP, event.getPropertyName());
		assertEquals(WTPOperationDataModelEvent.PROPERTY_CHG, event.getFlag());
		assertEquals(false, dm.getBooleanProperty(DM.BOOLEAN_PROP));


	}

}
