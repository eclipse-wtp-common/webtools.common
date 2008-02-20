package org.eclipse.wst.common.tests.ui.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.operation.extensionui.DMWizardPageExtensionFactory;

public class Test2PageWizardExtensionFactory extends
		DMWizardPageExtensionFactory {

	public void createAdditionalControls(Composite parent, IDataModel model,
			String pageName) {
		String labelName;
		if ("A Better Page".equals(pageName)) {
			labelName = "An additonal text box for the better page";
		} else if ("Another Page, but not better".equals(pageName)) {
			labelName = "An additonal text box for the NOT better page";
		} else {
			return;
		}
		
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelName);
	}

}
