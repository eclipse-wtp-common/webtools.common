package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wst.common.frameworks.internal.FlexibleJavaProjectPreferenceUtil;


public class FlexibleJavaProjectPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, SelectionListener{

	Button alloMultipleButton = null;
	
	protected Control createContents(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);	
		RowLayout rowLayout = new RowLayout();
		rowLayout.justify = false;
		rowLayout.marginLeft = 5;
		rowLayout.marginRight = 5;
		rowLayout.spacing = 5;
		composite.setLayout(rowLayout);

		alloMultipleButton = new Button(composite, SWT.CHECK);
		alloMultipleButton.setText("Allow Multiple modules per project");
		alloMultipleButton.setEnabled(true);
		
		boolean val = FlexibleJavaProjectPreferenceUtil.getMultipleModulesPerProjectProp();
		
		alloMultipleButton.setSelection(val);

		alloMultipleButton.addSelectionListener(this);
		return composite;
	}
	
	public void init(IWorkbench workbench) {
	}

	public void widgetSelected(SelectionEvent e) {
		FlexibleJavaProjectPreferenceUtil.setMultipleModulesPerProjectProp( alloMultipleButton.getSelection() );
	}

	public void widgetDefaultSelected(SelectionEvent e) {

	}
    protected void performDefaults() {
		FlexibleJavaProjectPreferenceUtil.setMultipleModulesPerProjectProp( false );
		alloMultipleButton.setSelection( false );
    }	

}
