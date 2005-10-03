/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.tests.ui.wizard;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

public class TestDataModelProvider extends AbstractDataModelProvider {
	public String getID() {
		return "TestWizardID";
	}

	public Set getPropertyNames() {
		HashSet result = new HashSet();

		result.add("prop1");
		result.add("prop2");

		return result;
	}

	public IDataModelOperation getDefaultOperation() {
		return new TestOperation();
	}
}
