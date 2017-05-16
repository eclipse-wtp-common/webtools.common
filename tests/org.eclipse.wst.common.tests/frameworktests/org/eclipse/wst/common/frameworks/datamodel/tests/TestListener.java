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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;

public class TestListener implements IDataModelListener {

	private ArrayList events = new ArrayList();

	public void clearEvents() {
		events.clear();
	}

	public List getEvents() {
		return events;
	}

	public void propertyChanged(DataModelEvent event) {
		events.add(event);
	}
}
