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

public class TestPage2 extends DataModelWizardPage {
	public TestPage2(IDataModel model) {
		super(model, "Page2"); //$NON-NLS-1$
		setTitle("Title for test page2"); //$NON-NLS-1$
		setDescription("Description for test page 2"); //$NON-NLS-1$
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

		button1.setText("Page 2 button1"); //$NON-NLS-1$
		button2.setText("Page 2 button2"); //$NON-NLS-1$
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_BOTH));

		return group;
	}

	protected String[] getValidationPropertyNames() {
		return new String[0];
	}
}
