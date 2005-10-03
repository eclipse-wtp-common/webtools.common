/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.tests.ui.wizard;

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.SimplePageGroup;

public class TestRootPageGroup extends SimplePageGroup {
	private IDataModel dataModel;

	public TestRootPageGroup(IDataModel dataModel) {
		super(dataModel.getID(), dataModel.getID());
		this.dataModel = dataModel;

		addPages(new DataModelWizardPage[]{new TestPage1(this.dataModel)});
	}
}
