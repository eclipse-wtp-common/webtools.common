package org.eclipse.wst.common.tests.ui.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.operation.extensionui.DMWizardPageExtensionFactory;

public class Test2PageWizardExtensionFactory2 extends
		DMWizardPageExtensionFactory {

	public void createAdditionalControls(Composite parent, IDataModel model,
			String pageName) {
		if ("A Better Page".equals(pageName)) {
			Button checkbox = new Button(parent, SWT.CHECK);
			checkbox.setText("Additional checkbox for a better page");
		}
	}

}
