/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.tests.ui.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.SimplePageGroup;

public class TestRootPageGroup extends SimplePageGroup {
	private IDataModel dataModel;

	public TestRootPageGroup(IDataModel dataModel) {
		super(dataModel.getID(), dataModel.getID());
		this.dataModel = dataModel;

		addPages(new IWizardPage[]{new TestPage1(this.dataModel)});
	}
}
