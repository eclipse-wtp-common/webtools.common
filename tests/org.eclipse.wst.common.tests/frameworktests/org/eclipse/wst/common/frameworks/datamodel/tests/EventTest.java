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

import junit.framework.TestCase;

import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class EventTest extends TestCase {

	public void testEventCreation() {
		IDataModel dm = DataModelFactory.createDataModel(new A());
		dm.setProperty(A.P, "aaa");
		DataModelEvent event = new DataModelEvent(dm, A.P, DataModelEvent.VALUE_CHG);
		assertEquals(dm, event.getDataModel());
		assertEquals(A.P, event.getPropertyName());
		assertEquals("aaa", event.getProperty());
		assertEquals(DataModelEvent.VALUE_CHG, event.getFlag());
		dm.setProperty(A.P, "bbb");
		assertEquals("bbb", event.getProperty());
		event = new DataModelEvent(dm, A.P, DataModelEvent.ENABLE_CHG);
		assertEquals(DataModelEvent.ENABLE_CHG, event.getFlag());
		event = new DataModelEvent(dm, A.P, DataModelEvent.VALID_VALUES_CHG);
		assertEquals(DataModelEvent.VALID_VALUES_CHG, event.getFlag());
	}
}