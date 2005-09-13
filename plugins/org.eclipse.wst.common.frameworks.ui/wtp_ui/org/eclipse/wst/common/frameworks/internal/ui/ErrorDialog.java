package org.eclipse.wst.common.frameworks.internal.ui;

/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Added a Details button to the MessageDialog to show the exception stack trace.
 * 
 * Borrowed from an eclipse InternalErrorDialog
 */
public class ErrorDialog extends MessageDialog {
	protected static final String[] LABELS_OK = {IDialogConstants.OK_LABEL};
	protected static final String[] LABELS_OK_CANCEL = {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL};
	protected static final String[] LABELS_OK_DETAILS = {IDialogConstants.OK_LABEL, IDialogConstants.SHOW_DETAILS_LABEL};
	protected static final String[] LABELS_OK_CANCEL_DETAILS = {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL, IDialogConstants.SHOW_DETAILS_LABEL};
	private Throwable detail;
	private int detailButtonID = -1;
	private Text text;
	private String message;
	//Workaround. SWT does not seem to set the default button if
	//there is not control with focus. Bug: 14668
	private int defaultButtonIndex = 0;
	/**
	 * Size of the text in lines.
	 */
	private static final int TEXT_LINE_COUNT = 15;

	public ErrorDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, Throwable detail, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
		defaultButtonIndex = defaultIndex;
		this.detail = detail;
		message = dialogMessage;
		setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.RESIZE);
	}

	//Workaround. SWT does not seem to set rigth the default button if
	//there is not control with focus. Bug: 14668
	public int open() {
		create();
		Button b = getButton(defaultButtonIndex);
		b.setFocus();
		b.getShell().setDefaultButton(b);
		return super.open();
	}

	/**
	 * Set the detail button;
	 */
	public void setDetailButton(int index) {
		detailButtonID = index;
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == detailButtonID) {
			toggleDetailsArea();
		} else {
			setReturnCode(buttonId);
			close();
		}
	}

	/**
	 * Toggles the unfolding of the details area. This is triggered by the user pressing the details
	 * button.
	 */
	private void toggleDetailsArea() {
		Point windowSize = getShell().getSize();
		Point oldSize = getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		if (text != null) {
			text.dispose();
			text = null;
			getButton(detailButtonID).setText(IDialogConstants.SHOW_DETAILS_LABEL);
		} else {
			createDropDownText((Composite) getContents());
			getButton(detailButtonID).setText(IDialogConstants.HIDE_DETAILS_LABEL);
		}
		Point newSize = getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		getShell().setSize(new Point(windowSize.x, windowSize.y + (newSize.y - oldSize.y)));
	}

	/**
	 * Create this dialog's drop-down list component.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return the drop-down list component
	 */
	protected void createDropDownText(Composite parent) {
		// create the list
		text = new Text(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		// print the stacktrace in the text field
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			detail.printStackTrace(ps);
			if ((detail instanceof SWTError) && (((SWTError) detail).throwable != null)) {
				ps.println("\n*** Stack trace of contained exception ***"); //$NON-NLS-1$
				((SWTError) detail).throwable.printStackTrace(ps);
			} else if ((detail instanceof SWTException) && (((SWTException) detail).throwable != null)) {
				ps.println("\n*** Stack trace of contained exception ***"); //$NON-NLS-1$
				((SWTException) detail).throwable.printStackTrace(ps);
			}
			ps.flush();
			baos.flush();
			text.setText(baos.toString());
		} catch (IOException e) {
		}
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
		data.heightHint = text.getLineHeight() * TEXT_LINE_COUNT;
		text.setLayoutData(data);
	}

	public static boolean openError(Shell parent, String title, String message, Throwable detail, int defaultIndex, boolean showCancel) {
		String[] labels;
		if (detail == null)
			labels = showCancel ? LABELS_OK_CANCEL : LABELS_OK;
		else
			labels = showCancel ? LABELS_OK_CANCEL_DETAILS : LABELS_OK_DETAILS;
		ErrorDialog dialog = new ErrorDialog(parent, title, null, // accept
					// the
					// default
					// window
					// icon
					message, detail, ERROR, labels, defaultIndex);
		if (detail != null)
			dialog.setDetailButton(labels.length - 1);
		return dialog.open() == 0;
	}

	protected Control createDialogArea(Composite parent) {
		// create a composite with standard margins and spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		((GridLayout) composite.getLayout()).numColumns = 2;
		// create image
		Image image = composite.getDisplay().getSystemImage(SWT.ICON_ERROR);
		if (image != null) {
			Label label = new Label(composite, 0);
			image.setBackground(label.getBackground());
			label.setImage(image);
			label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_BEGINNING));
		}
		// create message
		if (message != null) {
			Label label = new Label(composite, SWT.WRAP);
			label.setText(message);
			GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
			data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
			label.setLayoutData(data);
			label.setFont(parent.getFont());
		}
		return composite;
	}
}