/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Aug 5, 2004
 */
package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * @author dfholt
 */
public class ListMessageDialog extends MessageDialog {
	protected String[] listItems;
	protected List list;

	/**
	 * EJBSelectiveImportDialog constructor comment.
	 * 
	 * @param parentShell
	 *            org.eclipse.swt.widgets.Shell
	 * @param dialogTitle
	 *            java.lang.String
	 * @param dialogTitleImage
	 *            org.eclipse.swt.graphics.Image
	 * @param dialogMessage
	 *            java.lang.String
	 * @param dialogImageType
	 *            int
	 * @param dialogButtonLabels
	 *            java.lang.String[]
	 * @param defaultIndex
	 *            int
	 */
	public ListMessageDialog(org.eclipse.swt.widgets.Shell parentShell, String dialogTitle, org.eclipse.swt.graphics.Image dialogTitleImage, String dialogMessage, int dialogImageType, java.lang.String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
	}

	/**
	 * ListMessageDialog constructor comment.
	 * 
	 * @param parentShell
	 *            org.eclipse.swt.widgets.Shell
	 * @param dialogTitle
	 *            java.lang.String
	 * @param dialogTitleImage
	 *            org.eclipse.swt.graphics.Image
	 * @param dialogMessage
	 *            java.lang.String
	 * @param dialogImageType
	 *            int
	 * @param dialogButtonLabels
	 *            java.lang.String[]
	 * @param defaultIndex
	 *            int
	 */
	public ListMessageDialog(org.eclipse.swt.widgets.Shell parentShell, String dialogTitle, org.eclipse.swt.graphics.Image dialogTitleImage, String dialogMessage, int dialogImageType, java.lang.String[] dialogButtonLabels, int defaultIndex, String[] names) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
		listItems = names;
	}

	/**
	 * Creates and returns the contents of an area of the dialog which appears below the message and
	 * above the button bar.
	 * <p>
	 * The default implementation of this framework method returns <code>null</code>. Subclasses
	 * may override.
	 * </p>
	 * 
	 * @param the
	 *            parent composite to contain the custom area
	 * @return the custom area control, or <code>null</code>
	 */
	@Override
	protected Control createCustomArea(Composite parent) {

		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (listItems != null) {
			list = new List(composite, SWT.BORDER);
			GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
			list.setLayoutData(data);
			list.setItems(listItems);
		}

		return composite;

	}

	/**
	 * Convenience method to open a simple confirm (OK/Cancel) dialog.
	 * 
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param title
	 *            the dialog's title, or <code>null</code> if none
	 * @param message
	 *            the message
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openConfirm(Shell parent, String title, String message, String[] items) {
		ListMessageDialog dialog = new ListMessageDialog(parent, title, null, // accept the default
					// window icon
					message, QUESTION, new String[]{IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, 0, items); // OK
		// is
		// the
		// default
		return dialog.open() == 0;
	}

	/**
	 * Convenience method to open a standard error dialog.
	 * 
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param title
	 *            the dialog's title, or <code>null</code> if none
	 * @param message
	 *            the message
	 */
	public static void openError(Shell parent, String title, String message, String[] items) {
		ListMessageDialog dialog = new ListMessageDialog(parent, title, null, // accept the default
					// window icon
					message, ERROR, new String[]{IDialogConstants.OK_LABEL}, 0, items); // ok is the
		// default
		dialog.open();
		return;
	}

	/**
	 * Convenience method to open a standard information dialog.
	 * 
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param title
	 *            the dialog's title, or <code>null</code> if none
	 * @param message
	 *            the message
	 */
	public static void openInformation(Shell parent, String title, String message, String[] items) {
		ListMessageDialog dialog = new ListMessageDialog(parent, title, null, // accept the default
					// window icon
					message, INFORMATION, new String[]{IDialogConstants.OK_LABEL}, 0, items);
		// ok is the default
		dialog.open();
		return;
	}

	/**
	 * Convenience method to open a simple Yes/No question dialog.
	 * 
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param title
	 *            the dialog's title, or <code>null</code> if none
	 * @param message
	 *            the message
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openQuestion(Shell parent, String title, String message, String[] items) {
		ListMessageDialog dialog = new ListMessageDialog(parent, title, null, // accept the default
					// window icon
					message, QUESTION, new String[]{IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 0, items); // yes
		// is
		// the
		// default
		return dialog.open() == 0;
	}

	/**
	 * Convenience method to open a standard warning dialog.
	 * 
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param title
	 *            the dialog's title, or <code>null</code> if none
	 * @param message
	 *            the message
	 */
	public static void openWarning(Shell parent, String title, String message, String[] items) {
		ListMessageDialog dialog = new ListMessageDialog(parent, title, null, // accept the default
					// window icon
					message, WARNING, new String[]{IDialogConstants.OK_LABEL}, 0, items); // ok is
		// the
		// default
		dialog.open();
		return;
	}
}