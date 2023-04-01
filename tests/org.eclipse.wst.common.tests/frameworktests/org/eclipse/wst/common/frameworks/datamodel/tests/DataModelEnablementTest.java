/*******************************************************************************
 * Copyright (c) 2003, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.datamodel.tests;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.enablement.DataModelEnablementFactory;

public class DataModelEnablementTest extends TestCase {

	public void testValidExtensionIDAndProviderType() throws Exception {
		IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject("temp");
		if (!proj.exists()) {
			proj.create(null);
		}
		IDataModel dataModel = DataModelEnablementFactory.createDataModel("testProviderBase", proj);
		assertTrue(dataModel.isProperty(ITestDataModel.FOO));
	}

	public void testValidExtensionIDImplementorForProviderType() throws Exception{
		IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject("temp");
		if (!proj.exists()) {
			proj.create(null);
		}
		IDataModel dataModel = DataModelEnablementFactory.createDataModel("testProviderBogus", proj);
		assertTrue(dataModel == null);
	}

}
