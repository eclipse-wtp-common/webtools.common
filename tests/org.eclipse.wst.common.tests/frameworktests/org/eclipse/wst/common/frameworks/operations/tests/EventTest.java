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

import junit.framework.TestCase;

import org.eclipse.wst.common.frameworks.operations.WTPOperationDataModel;
import org.eclipse.wst.common.frameworks.operations.WTPOperationDataModelEvent;

public class EventTest extends TestCase {

	public void testEventCreation() {
		WTPOperationDataModel dm = new A();
		dm.setProperty(A.P, "aaa");
		WTPOperationDataModelEvent event = new WTPOperationDataModelEvent(dm, A.P, WTPOperationDataModelEvent.PROPERTY_CHG);
		assertEquals(dm, event.getDataModel());
		assertEquals(A.P, event.getPropertyName());
		assertEquals("aaa", event.getProperty());
		assertEquals(WTPOperationDataModelEvent.PROPERTY_CHG, event.getFlag());
		dm.setProperty(A.P, "bbb");
		assertEquals("bbb", event.getProperty());
		event = new WTPOperationDataModelEvent(dm, A.P, WTPOperationDataModelEvent.ENABLE_CHG);
		assertEquals(WTPOperationDataModelEvent.ENABLE_CHG, event.getFlag());
		event = new WTPOperationDataModelEvent(dm, A.P, WTPOperationDataModelEvent.VALID_VALUES_CHG);
		assertEquals(WTPOperationDataModelEvent.VALID_VALUES_CHG, event.getFlag());
	}
}
