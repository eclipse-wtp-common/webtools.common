/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.tests.ui.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;

public class TestPage1 extends DataModelWizardPage {
	public TestPage1(IDataModel model) {
		super(model, "Page1"); //$NON-NLS-1$
		setTitle("Title for test page1"); //$NON-NLS-1$
		setDescription("Description for test page 1"); //$NON-NLS-1$
	}

	public boolean canPageFinish() {
		return true;
	}

	public IDataModelOperation createOperation() {
		return null;
	}

	protected Composite createTopLevelComposite(Composite parent) {
		Composite group = new Composite(parent, SWT.NONE);
		Button button1 = new Button(group, SWT.PUSH);
		Button button2 = new Button(group, SWT.PUSH);

		button1.setText("Button1"); //$NON-NLS-1$
		button2.setText("Button2"); //$NON-NLS-1$
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_BOTH));

		return group;
	}

	protected String[] getValidationPropertyNames() {
		return new String[0];
	}
}
