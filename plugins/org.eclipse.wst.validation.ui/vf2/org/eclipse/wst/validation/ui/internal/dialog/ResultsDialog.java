/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.validation.ui.internal.dialog;


import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.validation.internal.Misc;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValPrefManagerGlobal;
import org.eclipse.wst.validation.internal.ValidationResultSummary;
import org.eclipse.wst.validation.internal.model.GlobalPreferences;
import org.eclipse.wst.validation.internal.ui.ValidationUIMessages;
import org.eclipse.wst.validation.ui.internal.ValUIMessages;

/**
 * A dialog for displaying the results of a manual validation.
 * @author karasiuk
 *
 */
public class ResultsDialog extends IconAndMessageDialog {
	
	private ValidationResultSummary 	_result;
	private long				_time;
	private int					_resourceCount;
	private Button _hideButton;

	/**
	 * Create a dialog for displaying validation results.
	 * 
	 * @param parentShell this can be null
	 * @param results the results of the validation
	 * @param time the time that the validation took in milliseconds
	 * @param resourceCount the number of resources that were validated
	 */
	public ResultsDialog(Shell parentShell, ValidationResultSummary results, long time, int resourceCount) {
		super(parentShell);
		_result = results;
		_time = time;
		_resourceCount = resourceCount;
	}
	
	@Override
	protected void okPressed() {
		if(_hideButton!=null) {
			GlobalPreferences gp = ValManager.getDefault().getGlobalPreferences();
			gp.setConfirmDialog(!_hideButton.getSelection());
			ValPrefManagerGlobal.getDefault().savePreferences(gp);
		}
		super.okPressed();
	}
	
	@Override
	public int open() {
		if (!ErrorDialog.AUTOMATED_MODE) {
			return super.open();
		}
		setReturnCode(OK);
		return OK;
	}
	
	@Override
	protected Image getImage() {
		if (_result.getSeverityError() > 0)return getErrorImage();
		if (_result.getSeverityWarning() > 0)return getWarningImage();
		return getInfoImage();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		message = getMessage();
		createMessageArea(parent);
				
		new Label(parent, SWT.NONE).setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());		
		addHideCheckbox(parent);

		Control c = super.createDialogArea(parent);
		return c;
	}
	
	private void addHideCheckbox(Composite parent) {
		_hideButton = new Button(parent, SWT.CHECK);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).span(2, 1).applyTo(_hideButton);
		_hideButton.setText(ValUIMessages.DoNotShowResults);
		if (ValManager.getDefault().getGlobalPreferences().getConfirmDialog() && _hideButton.getSelection()) {
			// tell the user where to re-enable it?
		}
		_hideButton.setSelection(!ValManager.getDefault().getGlobalPreferences().getConfirmDialog());
	}

	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(ValidationUIMessages.ValResults);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button ok = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
		ok.setFocus();
	}

	private String getMessage(){
		if (_resourceCount > 1) {
			if (_result.getSeverityError() > 1)
				return NLS.bind(ValidationUIMessages.ValErrorsResources, new Object[]{_result.getSeverityError(), _resourceCount, Misc.getTimeMS(_time)});
			if (_result.getSeverityError() == 1)
				return NLS.bind(ValidationUIMessages.ValError1Resources, _resourceCount, Misc.getTimeMS(_time));

			if (_result.getSeverityWarning() > 1)
				return NLS.bind(ValidationUIMessages.ValWarnResources, new Object[]{_result.getSeverityWarning(), _resourceCount, Misc.getTimeMS(_time)});
			if (_result.getSeverityWarning() == 1)
				return NLS.bind(ValidationUIMessages.ValWarn1Resources, _resourceCount, Misc.getTimeMS(_time));

			if (_result.getSeverityInfo() > 1)
				return NLS.bind(ValidationUIMessages.ValInfoResources, new Object[]{_result.getSeverityInfo(), _resourceCount, Misc.getTimeMS(_time)});
			if (_result.getSeverityInfo() == 1)
				return NLS.bind(ValidationUIMessages.ValInfo1Resources, _resourceCount, Misc.getTimeMS(_time));
		}
		else if (_resourceCount == 1) {
			if (_result.getSeverityError() > 1)
				return NLS.bind(ValidationUIMessages.ValErrorsResource1, _result.getSeverityError(), Misc.getTimeMS(_time));
			if (_result.getSeverityError() == 1)
				return NLS.bind(ValidationUIMessages.ValError1Resource1, Misc.getTimeMS(_time));

			if (_result.getSeverityWarning() > 1)
				return NLS.bind(ValidationUIMessages.ValWarnResource1, _result.getSeverityWarning(), Misc.getTimeMS(_time));
			if (_result.getSeverityWarning() == 1)
				return NLS.bind(ValidationUIMessages.ValWarn1Resource1, Misc.getTimeMS(_time));

			if (_result.getSeverityInfo() > 1)
				return NLS.bind(ValidationUIMessages.ValInfoResource1, _result.getSeverityInfo(), Misc.getTimeMS(_time));
			if (_result.getSeverityInfo() == 1)
				return NLS.bind(ValidationUIMessages.ValInfo1Resource1, Misc.getTimeMS(_time));
		}
		return ValidationUIMessages.ValSuccess;
	}
}
