/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.tests.ui.wizard;

import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.AddablePageGroup;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizard;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.SimplePageGroup;
import org.eclipse.wst.common.frameworks.internal.ui.PageGroupManager;

public class TestDataWizard extends DataModelWizard {
	private SimplePageGroup root;

	public TestDataWizard() {
		setForcePreviousAndNextButtons(true);
		setNeedsProgressMonitor(true);
	}

	protected void doAddPages() {
		PageGroupManager pageManager = getPageGroupManager();
		pageManager.addGroupAfter(root.getPageGroupID(), new TestPageGroup2(getDataModel()));

		super.doAddPages();
	}

	protected IDataModelProvider getDefaultProvider() {
		return new TestDataModelProvider();
	}

	protected AddablePageGroup createRootPageGroup() {
		root = new TestRootPageGroup(getDataModel());

		return root;
	}
}
