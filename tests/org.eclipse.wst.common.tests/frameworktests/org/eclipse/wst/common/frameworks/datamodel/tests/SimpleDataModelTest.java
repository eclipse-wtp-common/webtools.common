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

import org.eclipse.wst.common.frameworks.datamodel.provisional.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.provisional.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.provisional.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.provisional.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.provisional.IDataModel;
import org.eclipse.wst.common.frameworks.internal.WTPResourceHandler;

import junit.framework.Assert;
import junit.framework.TestCase;


public class SimpleDataModelTest extends TestCase {

	private class DMProvider extends AbstractDataModelProvider {
		public static final String INT_PROP = "INT_PROP";
		public static final String INT_PROP2 = "INT_PROP2";
		public static final String INT_PROP3 = "INT_PROP3";
		public static final String INT_PROP4 = "INT_PROP4";
		public static final String BOOLEAN_PROP = "BOOLEAN_PROP";
		public static final String BOOLEAN_PROP2 = "BOOLEAN_PROP2";
		public static final String STRING_PROP = "STRING_PROP";

		public String[] getPropertyNames() {
			return new String[]{INT_PROP, INT_PROP2, INT_PROP3, INT_PROP4, BOOLEAN_PROP, BOOLEAN_PROP2, STRING_PROP};
		}

		public Object getDefaultProperty(String propertyName) {
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

		public boolean isPropertyEnabled(String propertyName) {
			if (propertyName.equals(BOOLEAN_PROP2)) {
				return getBooleanProperty(BOOLEAN_PROP);
			}
			return true;
		}

		public boolean setProperty(String propertyName, Object propertyValue) {
			if (propertyName.equals(INT_PROP)) {
				model.notifyPropertyChange(INT_PROP2, IDataModel.VALUE_CHG);
				model.notifyPropertyChange(INT_PROP2, IDataModel.VALID_VALUES_CHG);
				model.notifyPropertyChange(STRING_PROP, IDataModel.DEFAULT_CHG);
			}
			if (propertyName.equals(BOOLEAN_PROP)) {
				model.notifyPropertyChange(BOOLEAN_PROP2, IDataModel.ENABLE_CHG);
				model.notifyPropertyChange(STRING_PROP, IDataModel.DEFAULT_CHG);
			}
			return true;
		}

		public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
			if (INT_PROP2.equals(propertyName)) {
				int range = getIntProperty(INT_PROP);
				Integer[] ints = new Integer[range];
				for (int i = 0; i < ints.length; i++) {
					ints[i] = new Integer(i + 1);
				}
				return DataModelPropertyDescriptor.createDescriptors(ints);
			}
			if (INT_PROP3.equals(propertyName)) {
				int range = 3;
				Integer[] ints = new Integer[range];
				for (int i = 0; i < ints.length; i++) {
					ints[i] = new Integer(i + 1);
				}
				String[] descriptions = new String[]{"one", "two", "three"};
				return DataModelPropertyDescriptor.createDescriptors(ints, descriptions);
			}
			if (INT_PROP4.equals(propertyName)) {
				DataModelPropertyDescriptor[] descriptors = new DataModelPropertyDescriptor[3];
				String[] descriptions = new String[]{"one", "two", "three"};
				for (int i = 0; i < descriptors.length; i++) {
					descriptors[i] = new DataModelPropertyDescriptor(new Integer(i + 1), descriptions[i]);
				}
				return descriptors;
			}
			return null;
		}
	};

	private IDataModel dm;
	private TestListener dmL;

	protected void setUp() throws Exception {
		super.setUp();
		dm = DataModelFactory.INSTANCE.createDataModel(new DMProvider());
		dmL = new TestListener();
		dm.addListener(dmL);
	}

	public void testInvalidProperty() {
		String PROPERTY_NOT_LOCATED_ = WTPResourceHandler.getString("20"); //$NON-NLS-1$
		Exception ex = null;
		try {
			dm.getProperty("foo");
		} catch (RuntimeException e) {
			ex = e;
		}
		Assert.assertNotNull(ex);
		Assert.assertTrue(ex.getMessage().startsWith(PROPERTY_NOT_LOCATED_));
		ex = null;
		try {
			dm.getIntProperty("foo");
		} catch (RuntimeException e) {
			ex = e;
		}
		Assert.assertNotNull(ex);
		Assert.assertTrue(ex.getMessage().startsWith(PROPERTY_NOT_LOCATED_));
		ex = null;
		ex = null;
		try {
			dm.getBooleanProperty("foo");
		} catch (RuntimeException e) {
			ex = e;
		}
		Assert.assertNotNull(ex);
		Assert.assertTrue(ex.getMessage().startsWith(PROPERTY_NOT_LOCATED_));
		ex = null;
		ex = null;
		try {
			dm.getStringProperty("foo");
		} catch (RuntimeException e) {
			ex = e;
		}
		Assert.assertNotNull(ex);
		Assert.assertTrue(ex.getMessage().startsWith(PROPERTY_NOT_LOCATED_));
		ex = null;
		try {
			dm.getStringProperty(null);
		} catch (RuntimeException e) {
			ex = e;
		}
		Assert.assertNotNull(ex);
		Assert.assertTrue(ex.getMessage().startsWith(PROPERTY_NOT_LOCATED_));
		
	}

	public void testPropertyDescriptors() {
		DataModelPropertyDescriptor[] descriptors = dm.getValidPropertyDescriptors(DMProvider.INT_PROP2);
		for (int i = 0; i < descriptors.length; i++) {
			int value = i + 1;
			assertEquals(value, ((Integer) descriptors[i].getPropertyValue()).intValue());
			assertEquals("" + value, descriptors[i].getPropertyDescription());
		}
		descriptors = dm.getValidPropertyDescriptors(DMProvider.INT_PROP3);
		String[] descriptions = new String[]{"one", "two", "three"};
		for (int i = 0; i < descriptors.length; i++) {
			int value = i + 1;
			assertEquals(value, ((Integer) descriptors[i].getPropertyValue()).intValue());
			assertEquals(descriptions[i], descriptors[i].getPropertyDescription());
		}
		descriptors = dm.getValidPropertyDescriptors(DMProvider.INT_PROP4);
		for (int i = 0; i < descriptors.length; i++) {
			int value = i + 1;
			assertEquals(value, ((Integer) descriptors[i].getPropertyValue()).intValue());
			assertEquals(descriptions[i], descriptors[i].getPropertyDescription());
		}
	}


	public void testDefaults() {
		assertEquals(true, dm.getBooleanProperty(DMProvider.BOOLEAN_PROP));
		assertEquals(true, ((Boolean) dm.getProperty(DMProvider.BOOLEAN_PROP)).booleanValue());
		assertEquals(10, dm.getIntProperty(DMProvider.INT_PROP));
		assertEquals(10, ((Integer) dm.getProperty(DMProvider.INT_PROP)).intValue());
		assertEquals("foo10true", (String) dm.getProperty(DMProvider.STRING_PROP));
		assertEquals("foo10true", dm.getStringProperty(DMProvider.STRING_PROP));
	}

	public void testFiringEvents() {
		dmL.clearEvents();
		dm.notifyPropertyChange(DMProvider.INT_PROP2, IDataModel.DEFAULT_CHG);
		List events = dmL.getEvents();
		assertEquals(1, events.size());
		DataModelEvent event = (DataModelEvent) events.get(0);
		assertEquals(DMProvider.INT_PROP2, event.getPropertyName());
		assertEquals(DataModelEvent.VALUE_CHG, event.getFlag());

		dmL.clearEvents();
		dm.notifyPropertyChange(DMProvider.INT_PROP2, IDataModel.VALID_VALUES_CHG);
		events = dmL.getEvents();
		assertEquals(1, events.size());
		event = (DataModelEvent) events.get(0);
		assertEquals(DMProvider.INT_PROP2, event.getPropertyName());
		assertEquals(DataModelEvent.VALID_VALUES_CHG, event.getFlag());
	}

	public void testSimpleSetEvents() {
		dmL.clearEvents();
		dm.setIntProperty(DMProvider.INT_PROP2, 100);
		List events = dmL.getEvents();
		assertEquals(1, events.size());
		DataModelEvent event = (DataModelEvent) events.get(0);
		assertEquals(DMProvider.INT_PROP2, event.getPropertyName());
		assertEquals(DataModelEvent.VALUE_CHG, event.getFlag());
		assertEquals(100, dm.getIntProperty(DMProvider.INT_PROP2));

		dmL.clearEvents();
		dm.setIntProperty(DMProvider.INT_PROP2, 100);
		events = dmL.getEvents();
		assertEquals(0, events.size());

		dmL.clearEvents();
		dm.setIntProperty(DMProvider.INT_PROP2, 101);
		events = dmL.getEvents();
		assertEquals(1, events.size());
		event = (DataModelEvent) events.get(0);
		assertEquals(DMProvider.INT_PROP2, event.getPropertyName());
	}

	public void testComplexEvents() {
		dmL.clearEvents();
		dm.setIntProperty(DMProvider.INT_PROP, 11);
		List events = dmL.getEvents();
		assertEquals(4, events.size());

		DataModelEvent event = (DataModelEvent) events.get(0);
		assertEquals(DMProvider.INT_PROP2, event.getPropertyName());
		assertEquals(DataModelEvent.VALUE_CHG, event.getFlag());
		assertEquals(11, ((Integer) dm.getProperty(DMProvider.INT_PROP2)).intValue());

		event = (DataModelEvent) events.get(1);
		assertEquals(DMProvider.INT_PROP2, event.getPropertyName());
		assertEquals(DataModelEvent.VALID_VALUES_CHG, event.getFlag());
		DataModelPropertyDescriptor[] descriptors = event.getValidPropertyDescriptors();
		DataModelPropertyDescriptor[] descriptors2 = dm.getValidPropertyDescriptors(DMProvider.INT_PROP2);
		assertEquals(11, descriptors.length);
		assertEquals(11, descriptors2.length);

		event = (DataModelEvent) events.get(2);
		assertEquals(DMProvider.STRING_PROP, event.getPropertyName());
		assertEquals(DataModelEvent.VALUE_CHG, event.getFlag());
		assertEquals("foo11true", event.getProperty());

		event = (DataModelEvent) events.get(3);
		assertEquals(DMProvider.INT_PROP, event.getPropertyName());
		assertEquals(DataModelEvent.VALUE_CHG, event.getFlag());
		assertEquals(11, ((Integer) dm.getProperty(DMProvider.INT_PROP)).intValue());

		dmL.clearEvents();
		dm.setBooleanProperty(DMProvider.BOOLEAN_PROP, false);
		events = dmL.getEvents();
		assertEquals(3, events.size());
		event = (DataModelEvent) events.get(0);
		assertEquals(DMProvider.BOOLEAN_PROP2, event.getPropertyName());
		assertEquals(DataModelEvent.ENABLE_CHG, event.getFlag());
		assertFalse(dm.isPropertyEnabled(DMProvider.BOOLEAN_PROP2));

		event = (DataModelEvent) events.get(1);
		assertEquals(DMProvider.STRING_PROP, event.getPropertyName());
		assertEquals(DataModelEvent.VALUE_CHG, event.getFlag());
		assertEquals("foo11false", event.getProperty());

		event = (DataModelEvent) events.get(2);
		assertEquals(DMProvider.BOOLEAN_PROP, event.getPropertyName());
		assertEquals(DataModelEvent.VALUE_CHG, event.getFlag());
		assertEquals(false, dm.getBooleanProperty(DMProvider.BOOLEAN_PROP));

		dm.setProperty(DMProvider.STRING_PROP, "bar");
		assertEquals("bar", dm.getStringProperty(DMProvider.STRING_PROP));
		dmL.clearEvents();
		dm.setBooleanProperty(DMProvider.BOOLEAN_PROP, true);
		events = dmL.getEvents();
		assertEquals(2, events.size());
		event = (DataModelEvent) events.get(0);
		assertEquals(DMProvider.BOOLEAN_PROP2, event.getPropertyName());
		event = (DataModelEvent) events.get(1);
		assertEquals(DMProvider.BOOLEAN_PROP, event.getPropertyName());

		assertEquals("bar", dm.getStringProperty(DMProvider.STRING_PROP));
		dm.setProperty(DMProvider.STRING_PROP, null);
		assertEquals("foo11true", dm.getStringProperty(DMProvider.STRING_PROP));
		dmL.clearEvents();
		dm.setBooleanProperty(DMProvider.BOOLEAN_PROP, false);
		events = dmL.getEvents();
		assertEquals(3, events.size());
		event = (DataModelEvent) events.get(0);
		assertEquals(DMProvider.BOOLEAN_PROP2, event.getPropertyName());
		assertEquals(DataModelEvent.ENABLE_CHG, event.getFlag());
		assertFalse(dm.isPropertyEnabled(DMProvider.BOOLEAN_PROP2));

		event = (DataModelEvent) events.get(1);
		assertEquals(DMProvider.STRING_PROP, event.getPropertyName());
		assertEquals(DataModelEvent.VALUE_CHG, event.getFlag());
		assertEquals("foo11false", event.getProperty());

		event = (DataModelEvent) events.get(2);
		assertEquals(DMProvider.BOOLEAN_PROP, event.getPropertyName());
		assertEquals(DataModelEvent.VALUE_CHG, event.getFlag());
		assertEquals(false, dm.getBooleanProperty(DMProvider.BOOLEAN_PROP));


	}

}
