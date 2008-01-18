package org.eclipse.wst.validation.ui.internal.dialog;

import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.internal.ui.ValidationUIMessages;

/**
 * A dialog for displaying the results of a manual validation.
 * @author karasiuk
 *
 */
public class ResultsDialog extends IconAndMessageDialog {
	
	private ValidationResult 	_result;
	private long				_time;
	private int					_resourceCount;

	/**
	 * Create a dialog for displaying validation results.
	 * 
	 * @param parentShell this can be null
	 * @param results the results of the validation
	 * @param time the time that the validation took in milliseconds
	 * @param resourceCount the number of resources that were validated
	 */
	public ResultsDialog(Shell parentShell, ValidationResult results, long time, int resourceCount) {
		super(parentShell);
		_result = results;
		_time = time;
		_resourceCount = resourceCount;
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
		
		StringBuffer b = new StringBuffer(200);
		String infoMessage = getInfoMessage();
		if (infoMessage != null){
			b.append(infoMessage);
			b.append(' ');
		}
		
		if (_resourceCount > 0){
			if (_resourceCount == 1)b.append(NLS.bind(ValidationUIMessages.ValTime1, getTime(_time)));
			else b.append(NLS.bind(ValidationUIMessages.ValTime, _resourceCount, getTime(_time)));
		}
				
		Label msg = new Label(parent, SWT.NONE);
		msg.setText(b.toString());
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		msg.setLayoutData(gd);
		Control c = super.createDialogArea(parent);
		return c;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(ValidationUIMessages.ValResultsStatus);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	private String getTime(long time) {
		if (_time <= 1000)return ValidationUIMessages.TimeUnder;
		if (_time <= 60000)return NLS.bind(ValidationUIMessages.TimeSec, _time/1000);
		return NLS.bind(ValidationUIMessages.TimeMin, _time/60000);
	}

	private String getInfoMessage() {
		int n = _result.getSeverityInfo();
		if (n == 0)return null;
		if (n == 1)return ValidationUIMessages.ValInfo1;
		return NLS.bind(ValidationUIMessages.ValInfo, n);
	}

	private String getMessage(){
		if (_result.getSeverityError() > 1)return NLS.bind(ValidationUIMessages.ValErrors, _result.getSeverityError());
		if (_result.getSeverityError() == 1)return ValidationUIMessages.ValErrors1;
		if (_result.getSeverityWarning() > 1)return NLS.bind(ValidationUIMessages.ValWarn, _result.getSeverityWarning());
		if (_result.getSeverityWarning() == 1)return ValidationUIMessages.ValWarn1;
		return ValidationUIMessages.ValSuccess;
	}
}
