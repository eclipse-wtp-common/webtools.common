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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModelEvent;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModelListener;

public class TestListener implements WTPOperationDataModelListener {

	private ArrayList events = new ArrayList();

	public void clearEvents() {
		events.clear();
	}

	public List getEvents() {
		return events;
	}

	public void propertyChanged(WTPOperationDataModelEvent event) {
		events.add(event);
	}
}
