/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.ui;


import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * Display both progress status and cumulative status (text) to the user.
 */
public class ProgressAndTextDialog extends ProgressMonitorDialog {
	protected static final String NEWLINE = System.getProperty("line.separator"); //$NON-NLS-1$
	protected Text text = null;
	protected boolean cancelPressed = false; // Has the "Cancel" button been clicked?
	protected boolean closePressed = false; // Has the "Close" button been clicked?
	protected boolean operationDone = false; // Has the operation completed?
	protected Cursor arrowCursor; // The cursor used in the cancel button.


	public ProgressAndTextDialog(Shell parent) {
		super(parent);
		setShellStyle(SWT.BORDER | SWT.TITLE | SWT.RESIZE); // do not make this dialog modal
		setBlockOnOpen(false); // do not force the user to close this dialog before using eclipse
	}

	public Control createDialogArea(Composite parent) {
		super.createDialogArea(parent);

		final int hHint = 100;
		final int wHint = 250;

		Composite composite = new Composite(parent, SWT.NONE);
		Label textLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
		textLabel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_STATUS));
		GridData labelData = new GridData();
		labelData.horizontalSpan = 2;
		textLabel.setLayoutData(labelData);

		composite.setLayout(new GridLayout()); // use the layout's default preferences
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = hHint;
		data.widthHint = wHint;
		data.horizontalSpan = 2;
		composite.setLayoutData(data);

		text = new Text(composite, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY | SWT.BORDER);
		GridData d = new GridData(GridData.FILL_BOTH);
		text.setLayoutData(d);

		return composite;
	}

	public void addText(final String messageString) {
		if (getShell() != null) {
			getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					text.append(messageString);
					text.append(NEWLINE);
				}
			});
		}
	}

	/**
	 * Do not dismiss the dialog until the user presses the Close button.
	 */
	public boolean close() {
		if (closePressed) {
			// Cancel button converted to "Close" button, and user clicked the "Close" button
			return super.close();
		}

		// Turn the "cancel" button into a "Close" button and keep the window open until
		// the user clicks the "Close" button.
		progressIndicator.setVisible(false);
		if (arrowCursor == null)
			arrowCursor = new Cursor(cancel.getDisplay(), SWT.CURSOR_ARROW);
		getShell().setCursor(arrowCursor);
		if (cancelPressed) {
			messageLabel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_CANCELLED));
		} else {
			messageLabel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_COMPLETE));
		}
		cancel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_CLOSE));
		cancel.setEnabled(true);
		return false;
	}

	protected void internalCancelPressed() {
		// Has this method been called because "Cancel" has been clicked or
		// because "Close" has been clicked?
		if (operationDone) {
			// "Close" was clicked.
			closePressed = true;
			close();
		} else {
			// "Cancel" was clicked.
			cancelPressed = true;
		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);

		// Cannot override cancelPressed() because of the way that
		// ProgressMonitorDialog is coded.
		// The ProgressMonitorDialog parent, instead of overriding the
		// cancelPressed() method, added an anonymous Listener to the
		// cancel button. When the button's Listener is invoked, if
		// close() has been called by cancelPressed(), then an
		// exception is thrown (SWTException - "Widget is disposed').
		// Work around this by adding an anonymous
		// cancel listener, which will be called after the parent's
		// cancel listener, and this class' listener will call close().
		cancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				internalCancelPressed();
			}
		});
	}

	/**
	 * @see org.eclipse.jface.operation.IRunnableContext#run(boolean, boolean,
	 *      IRunnableWithProgress)
	 */
	public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InvocationTargetException, InterruptedException {
		try {
			super.run(fork, cancelable, runnable);
		} finally {
			operationDone = true;
		}
	}

}