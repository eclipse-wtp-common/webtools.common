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

import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;

public class TestDataModelProvider extends AbstractDataModelProvider implements ITestDataModel {
	
	private static int instanceCount = 0;
	
	public static int getInstanceCount() {
		return instanceCount;
	}
	
	
	public TestDataModelProvider(){
		super();
		instanceCount++;
	}

	public String[] getPropertyNames() {
		return new String[]{ITestDataModel.FOO};
	}

	public String getID() {
		return ITestDataModel.class.getName();
	}

}
