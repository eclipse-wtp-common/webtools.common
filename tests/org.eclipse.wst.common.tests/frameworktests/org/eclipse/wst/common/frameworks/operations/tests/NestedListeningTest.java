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

import org.eclipse.wst.common.frameworks.operations.WTPOperationDataModel;
import org.eclipse.wst.common.frameworks.operations.WTPOperationDataModelEvent;

public class NestedListeningTest extends TestCase {

	public void testListeners1() {
		TestListener aL = new TestListener();
		TestListener bL = new TestListener();
		TestListener cL = new TestListener();

		A a = new A();
		B b = new B();
		C c = new C();
		a.addListener(aL);
		b.addListener(bL);
		c.addListener(cL);

		// cylical
		a.addNestedModel("b", b);
		b.addNestedModel("c", c);
		c.addNestedModel("a", a);
		aL.clearEvents();
		bL.clearEvents();
		cL.clearEvents();
		a.setProperty(A.P, "a");
		b.setProperty(B.P, "b");
		c.setProperty(C.P, "c");
		List aEvents = aL.getEvents();
		List bEvents = bL.getEvents();
		List cEvents = cL.getEvents();
		assertEquals(3, aEvents.size());
		assertEquals(3, bEvents.size());
		assertEquals(3, cEvents.size());
		for (int i = 0; i < 3; i++) {
			WTPOperationDataModelEvent aEvent = (WTPOperationDataModelEvent) aEvents.get(i);
			WTPOperationDataModelEvent bEvent = (WTPOperationDataModelEvent) bEvents.get(i);
			WTPOperationDataModelEvent cEvent = (WTPOperationDataModelEvent) cEvents.get(i);

			WTPOperationDataModel dataModel = aEvent.getDataModel();
			assertEquals(bEvent.getDataModel(), dataModel);
			assertEquals(cEvent.getDataModel(), dataModel);

			String propertyName = aEvent.getPropertyName();
			assertEquals(bEvent.getPropertyName(), propertyName);
			assertEquals(cEvent.getPropertyName(), propertyName);

			int flag = aEvent.getFlag();
			assertEquals(bEvent.getFlag(), flag);
			assertEquals(cEvent.getFlag(), flag);

			Object property = aEvent.getProperty();
			assertEquals(bEvent.getProperty(), property);
			assertEquals(cEvent.getProperty(), property);
			switch (i) {
				case 0 :
					assertEquals(a, dataModel);
					assertEquals(flag, WTPOperationDataModelEvent.PROPERTY_CHG);
					assertEquals(propertyName, A.P);
					assertEquals(property, "a");
					assertTrue(dataModel.isSet(propertyName));
					break;
				case 1 :
					assertEquals(b, dataModel);
					assertEquals(flag, WTPOperationDataModelEvent.PROPERTY_CHG);
					assertEquals(propertyName, B.P);
					assertEquals(property, "b");
					assertTrue(dataModel.isSet(propertyName));
					break;
				case 2 :
					assertEquals(c, dataModel);
					assertEquals(flag, WTPOperationDataModelEvent.PROPERTY_CHG);
					assertEquals(propertyName, C.P);
					assertEquals(property, "c");
					assertTrue(dataModel.isSet(propertyName));
					break;
			}
		}

		aL.clearEvents();
		bL.clearEvents();
		cL.clearEvents();
		a.setProperty(A.P, null);
		b.setProperty(B.P, null);
		c.setProperty(C.P, null);
		aEvents = aL.getEvents();
		bEvents = bL.getEvents();
		cEvents = cL.getEvents();
		assertEquals(3, aEvents.size());
		assertEquals(3, bEvents.size());
		assertEquals(3, cEvents.size());
		for (int i = 0; i < 3; i++) {
			WTPOperationDataModelEvent aEvent = (WTPOperationDataModelEvent) aEvents.get(i);
			WTPOperationDataModelEvent bEvent = (WTPOperationDataModelEvent) bEvents.get(i);
			WTPOperationDataModelEvent cEvent = (WTPOperationDataModelEvent) cEvents.get(i);

			WTPOperationDataModel dataModel = aEvent.getDataModel();
			assertEquals(bEvent.getDataModel(), dataModel);
			assertEquals(cEvent.getDataModel(), dataModel);

			String propertyName = aEvent.getPropertyName();
			assertEquals(bEvent.getPropertyName(), propertyName);
			assertEquals(cEvent.getPropertyName(), propertyName);

			int flag = aEvent.getFlag();
			assertEquals(bEvent.getFlag(), flag);
			assertEquals(cEvent.getFlag(), flag);

			Object property = aEvent.getProperty();
			assertEquals(bEvent.getProperty(), property);
			assertEquals(cEvent.getProperty(), property);
			switch (i) {
				case 0 :
					assertEquals(a, dataModel);
					assertEquals(flag, WTPOperationDataModelEvent.PROPERTY_CHG);
					assertEquals(propertyName, A.P);
					assertTrue(!dataModel.isSet(propertyName));
					break;
				case 1 :
					assertEquals(b, dataModel);
					assertEquals(flag, WTPOperationDataModelEvent.PROPERTY_CHG);
					assertEquals(propertyName, B.P);
					assertTrue(!dataModel.isSet(propertyName));
					break;
				case 2 :
					assertEquals(c, dataModel);
					assertEquals(flag, WTPOperationDataModelEvent.PROPERTY_CHG);
					assertEquals(propertyName, C.P);
					assertTrue(!dataModel.isSet(propertyName));
					break;
			}
		}
	}
}
