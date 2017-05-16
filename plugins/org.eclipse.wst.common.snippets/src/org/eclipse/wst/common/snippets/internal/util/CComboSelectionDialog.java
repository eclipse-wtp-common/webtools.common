/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal.util;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Based on org.eclipse.jdt.internal.ui.refactoring.ComboSelectionDialog
 */

public class CComboSelectionDialog extends Dialog {
	CCombo combo;
	private final String[] fAllowedStrings;
	private final int fInitialSelectionIndex;
	private final String fLabelText;

	int fSelectionIndex = -1;
	private final String fShellTitle;
	String fStringValue = null;
	IInputValidator fValidator;

	public CComboSelectionDialog(Shell parentShell, String shellTitle, String labelText, String[] comboStrings, int initialSelectionIndex, IInputValidator validator) {
		super(parentShell);
		Assert.isNotNull(shellTitle);
		Assert.isNotNull(labelText);
		Assert.isTrue(initialSelectionIndex >= -1 && initialSelectionIndex <= comboStrings.length);
		fShellTitle = shellTitle;
		fLabelText = labelText;
		fAllowedStrings = comboStrings;
		fInitialSelectionIndex = initialSelectionIndex;
		fValidator = validator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createButtonBar(Composite parent) {
		Control result = super.createButtonBar(parent);
		if (combo != null && result != null) {
			getButton(IDialogConstants.CANCEL_ID).addTraverseListener(new TraverseListener() {
				public void keyTraversed(TraverseEvent e) {
					if (e.character == SWT.TAB && e.stateMask == SWT.NONE)
						combo.setFocus();
				}
			});
		}
		return result;
	}

	/*
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		getShell().setText(fShellTitle);
		setShellStyle(getShellStyle() | SWT.RESIZE);

		Composite composite = (Composite) super.createDialogArea(parent);
		Composite innerComposite = new Composite(composite, SWT.NONE);
		innerComposite.setLayoutData(new GridData());
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		innerComposite.setLayout(gl);

		Label label = new Label(innerComposite, SWT.NONE);
		label.setText(fLabelText);
		label.setLayoutData(new GridData());

		combo = new CCombo(innerComposite, SWT.BORDER);
		combo.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR && e.stateMask == SWT.NONE) {
					fStringValue = combo.getText();
					setReturnCode(Window.OK);
					updateSelectionIndex();
					close();
				}
				else if (e.character == SWT.TAB) {
					if (e.stateMask == SWT.SHIFT)
						combo.traverse(SWT.TRAVERSE_TAB_PREVIOUS);
					else
						combo.traverse(SWT.TRAVERSE_TAB_NEXT);
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});
		combo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fStringValue = combo.getText();
				if (getButton(IDialogConstants.OK_ID) != null) {
					String errorMessage = null;
					if (fValidator != null) {
						errorMessage = fValidator.isValid(combo.getText());
					}
					getButton(IDialogConstants.OK_ID).setEnabled(errorMessage == null);
				}
			}
		});
		for (int i = 0; i < fAllowedStrings.length; i++) {
			combo.add(fAllowedStrings[i]);
		}
		combo.select(fInitialSelectionIndex);
		fSelectionIndex = combo.getSelectionIndex();
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(getMaxStringLength() + 10);
		combo.setLayoutData(gd);
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fSelectionIndex = combo.getSelectionIndex();
				fStringValue = combo.getText();
			}
		});
		applyDialogFont(composite);
		combo.setFocus();
		return composite;
	}

	private int getMaxStringLength() {
		int max = 0;
		for (int i = 0; i < fAllowedStrings.length; i++) {
			max = Math.max(max, fAllowedStrings[i].length());
		}
		return max;
	}

	/**
	 * @return
	 */
	public int getSelectionIndex() {
		return fSelectionIndex;
	}

	/**
	 * @return
	 */
	public String getStringValue() {
		return fStringValue;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		updateSelectionIndex();
		super.okPressed();
	}

	/**
	 * @param i
	 */
	public void setSelectionIndex(int i) {
		fSelectionIndex = i;
	}

	/**
	 * @param string
	 */
	public void setStringValue(String string) {
		fStringValue = string;
	}

	void updateSelectionIndex() {
		if (fSelectionIndex >= 0) {
			// patch the selection index and String
			boolean selectedString = false;
			for (int i = 0; i < fAllowedStrings.length; i++)
				selectedString = selectedString || fAllowedStrings[i].equals(fStringValue);
			if (!selectedString)
				fSelectionIndex = -1;
		}
	}
}
