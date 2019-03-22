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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.frameworks.internal.WTPResourceHandler;


public class SimpleDataModelTest extends TestCase {

	private class DMProvider extends AbstractDataModelProvider {
		public static final String INT_PROP = "INT_PROP";
		public static final String INT_PROP2 = "INT_PROP2";
		public static final String INT_PROP3 = "INT_PROP3";
		public static final String INT_PROP4 = "INT_PROP4";
		public static final String BOOLEAN_PROP = "BOOLEAN_PROP";
		public static final String BOOLEAN_PROP2 = "BOOLEAN_PROP2";
		public static final String STRING_PROP = "STRING_PROP";

		public Set getPropertyNames() {
			Set propertyNames = super.getPropertyNames();
			propertyNames.add(INT_PROP);
			propertyNames.add(INT_PROP2);
			propertyNames.add(INT_PROP3);
			propertyNames.add(INT_PROP4);
			propertyNames.add(BOOLEAN_PROP);
			propertyNames.add(BOOLEAN_PROP2);
			propertyNames.add(STRING_PROP);
			return propertyNames;
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

		public boolean propertySet(String propertyName, Object propertyValue) {
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

		public DataModelPropertyDescriptor getPropertyDescriptor(String propertyName) {
			Object property = getProperty(propertyName);
			if (INT_PROP2.equals(propertyName)) {
				return new DataModelPropertyDescriptor(property);
			} else if (INT_PROP3.equals(propertyName) || INT_PROP4.equals(propertyName)) {
				String[] descriptions = new String[]{"one", "two", "three"};
				int value = ((Integer) property).intValue();
				return new DataModelPropertyDescriptor(property, descriptions[value - 1]);
			}
			return null;
		}


		public String getID() {
			return id;
		}

		public List getExtendedContext() {
			return extendedContext;
		}

	}

	private String id;
	private List extendedContext;

	private IDataModel dm;
	private TestListener dmL;

	protected void setUp() throws Exception {
		super.setUp();
		dm = DataModelFactory.createDataModel(new DMProvider());
		dmL = new TestListener();
		dm.addListener(dmL);
	}

	public void testBasics() {
		id = null;
		assertEquals("", dm.getID());
		id = "foo";
		assertEquals("foo", dm.getID());
		extendedContext = null;
		assertNotNull(dm.getExtendedContext());
		extendedContext = new ArrayList();
		assertTrue(dm.getExtendedContext() == extendedContext);
		extendedContext.add("foo");
		assertTrue(dm.getExtendedContext() == extendedContext);
		assertNotNull(dm.getDefaultOperation());
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
		for (int i = 1; i < 4; i++) {
			dm.setIntProperty(DMProvider.INT_PROP2, i);
			DataModelPropertyDescriptor descriptor = dm.getPropertyDescriptor(DMProvider.INT_PROP2);
			assertEquals(descriptor.getPropertyValue(), dm.getProperty(DMProvider.INT_PROP2));
			assertEquals(((Integer) descriptor.getPropertyValue()).intValue(), dm.getIntProperty(DMProvider.INT_PROP2));
			assertTrue(descriptor.getPropertyDescription().equals(Integer.toString(i)));

			dm.setIntProperty(DMProvider.INT_PROP3, i);
			descriptor = dm.getPropertyDescriptor(DMProvider.INT_PROP3);
			assertEquals(descriptor.getPropertyValue(), dm.getProperty(DMProvider.INT_PROP3));
			assertEquals(((Integer) descriptor.getPropertyValue()).intValue(), dm.getIntProperty(DMProvider.INT_PROP3));
			assertTrue(descriptor.getPropertyDescription().equals(descriptions[i - 1]));

			dm.setIntProperty(DMProvider.INT_PROP4, i);
			descriptor = dm.getPropertyDescriptor(DMProvider.INT_PROP4);
			assertEquals(descriptor.getPropertyValue(), dm.getProperty(DMProvider.INT_PROP4));
			assertEquals(((Integer) descriptor.getPropertyValue()).intValue(), dm.getIntProperty(DMProvider.INT_PROP4));
			assertTrue(descriptor.getPropertyDescription().equals(descriptions[i - 1]));
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

	public void testPropertyChangedOnListener() {
		dmL.clearEvents();
		DataModelEvent event = new DataModelEvent(dm, A.P, DataModelEvent.VALUE_CHG);
		dmL.propertyChanged(event);
		List events = dmL.getEvents();
		assertEquals(1, events.size());
		assertTrue(events.contains(event));

		dmL.clearEvents();
		IDataModelListener idml = dmL;
		idml.propertyChanged(event);
		events = dmL.getEvents();
		assertEquals(1, events.size());
		assertTrue(events.contains(event));

	}

	public void testAddRemoveListener() {
		dmL.clearEvents();
		dm.notifyPropertyChange(DMProvider.INT_PROP2, IDataModel.DEFAULT_CHG);
		List events = dmL.getEvents();
		assertEquals(1, events.size());

		dmL.clearEvents();
		dm.removeListener(dmL);
		dm.notifyPropertyChange(DMProvider.INT_PROP2, IDataModel.DEFAULT_CHG);
		events = dmL.getEvents();
		assertEquals(0, events.size());

		dmL.clearEvents();
		dm.addListener(dmL);
		dm.addListener(dmL);
		dm.addListener(dmL);
		dm.addListener(dmL);
		dm.addListener(dmL);
		dm.notifyPropertyChange(DMProvider.INT_PROP2, IDataModel.DEFAULT_CHG);
		events = dmL.getEvents();
		assertEquals(1, events.size());

		dmL.clearEvents();
		dm.removeListener(dmL);
		dm.notifyPropertyChange(DMProvider.INT_PROP2, IDataModel.DEFAULT_CHG);
		events = dmL.getEvents();
		assertEquals(0, events.size());
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
		assertEquals("foo11true", dm.getDefaultProperty(DMProvider.STRING_PROP));
		assertEquals("foo11true", dm.getProperty(DMProvider.STRING_PROP));

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
		assertFalse(event.isPropertyEnabled());

		event = (DataModelEvent) events.get(1);
		assertEquals(DMProvider.STRING_PROP, event.getPropertyName());
		assertEquals(DataModelEvent.VALUE_CHG, event.getFlag());
		assertEquals("foo11false", event.getProperty());
		assertEquals("foo11false", dm.getDefaultProperty(DMProvider.STRING_PROP));
		assertEquals("foo11false", dm.getProperty(DMProvider.STRING_PROP));

		event = (DataModelEvent) events.get(2);
		assertEquals(DMProvider.BOOLEAN_PROP, event.getPropertyName());
		assertEquals(DataModelEvent.VALUE_CHG, event.getFlag());
		assertEquals(false, dm.getBooleanProperty(DMProvider.BOOLEAN_PROP));

		dm.setStringProperty(DMProvider.STRING_PROP, "bar");
		assertEquals("bar", dm.getStringProperty(DMProvider.STRING_PROP));
		assertEquals("foo11false", dm.getDefaultProperty(DMProvider.STRING_PROP));
		dmL.clearEvents();
		dm.setBooleanProperty(DMProvider.BOOLEAN_PROP, true);
		events = dmL.getEvents();
		assertEquals(2, events.size());
		event = (DataModelEvent) events.get(0);
		assertEquals(DMProvider.BOOLEAN_PROP2, event.getPropertyName());
		event = (DataModelEvent) events.get(1);
		assertEquals(DMProvider.BOOLEAN_PROP, event.getPropertyName());

		assertEquals("bar", dm.getStringProperty(DMProvider.STRING_PROP));
		dm.setStringProperty(DMProvider.STRING_PROP, null);
		assertEquals("foo11true", dm.getStringProperty(DMProvider.STRING_PROP));
		dmL.clearEvents();
		dm.setBooleanProperty(DMProvider.BOOLEAN_PROP, false);
		events = dmL.getEvents();
		assertEquals(3, events.size());
		event = (DataModelEvent) events.get(0);
		assertEquals(DMProvider.BOOLEAN_PROP2, event.getPropertyName());
		assertEquals(DataModelEvent.ENABLE_CHG, event.getFlag());
		assertFalse(dm.isPropertyEnabled(DMProvider.BOOLEAN_PROP2));
		assertFalse(event.isPropertyEnabled());

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
