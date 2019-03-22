/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.tests.ui.wizard;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import junit.framework.TestCase;

public class TestWizardTestcase extends TestCase {
	private TestDataWizard wizard_;

	protected void setUp() throws Exception {
		super.setUp();

		wizard_ = new TestDataWizard();
	}

	public void testSimpleWizard() throws Exception {
		WizardDialog dialog = new WizardDialog(null, wizard_);

		dialog.open();
	}
	
	public void test2DataModelWizard() throws Exception {
		Display.getDefault().syncExec( new Runnable(){
			public void run() {
				WizardDialog dialog = new WizardDialog(null, new Test2DataModelWizard());
				dialog.open();
			}
		});
	}
}
