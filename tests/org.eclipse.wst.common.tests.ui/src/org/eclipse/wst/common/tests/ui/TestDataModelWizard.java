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
package org.eclipse.wst.common.tests.ui;

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.tests.TestDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.ui.DataModelWizard;

public class TestDataModelWizard extends DataModelWizard {

	public TestDataModelWizard() {
		super();
	}

	public TestDataModelWizard(IDataModel dataModel) {
		super(dataModel);
	}

	protected IDataModelProvider getDefaultProvider() {
		return new TestDataModelProvider();
	}

}
