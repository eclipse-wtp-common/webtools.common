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
/*
 * Created on Aug 5, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.framework.operation.IOperationHandler;


/**
 * @author dfholt
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Generation - Code and Comments
 */
public class UIOperationHandler implements IOperationHandler {
	protected Shell parentShell;
	final public static String DEFAULT_INFORMATION = "Information";//EMFWorkbenchUIResourceHandler.getString("Information_UI__UI_");
																   // //$NON-NLS-1$
	// //$NON-NLS-1$ = "Information"
	final public static String DEFAULT_ERROR = "Error";// EMFWorkbenchUIResourceHandler.getString("Error_UI_");
													   // //$NON-NLS-1$
	// //$NON-NLS-1$ = "Error"
	final public static String DEFAULT_CONFIRM = "Confirm";//EMFWorkbenchUIResourceHandler.getString("Confirm_UI_");
														   // //$NON-NLS-1$
	// //$NON-NLS-1$ = "Confirm"
	protected String informationTitle = DEFAULT_INFORMATION;
	protected String confirmTitle = DEFAULT_CONFIRM;
	protected String errorTitle = DEFAULT_ERROR;

	/**
	 * UIOperationHandler constructor comment.
	 */
	public UIOperationHandler() {
		super();
	}

	/**
	 * UIOperationHandler constructor comment.
	 */
	public UIOperationHandler(Shell parent) {
		super();
		parentShell = parent;
	}

	/**
	 * A decision needs to made as to whether an action/operation can continue
	 */
	public boolean canContinue(String message) {
		return MessageDialog.openQuestion(getParentShell(), getConfirmTitle(), message);
	}

	/**
	 * A decision needs to made as to whether an action/operation can continue
	 */
	public boolean canContinue(String message, String[] items) {
		return ListMessageDialog.openQuestion(getParentShell(), getConfirmTitle(), message, items);
	}

	/**
	 * A decision needs to made as to whether an action/operation can continue. The boolean array
	 * will return two booleans. The first indicates their response to the original question and the
	 * second indicates if they selected the apply to all check box.
	 * 
	 * Return the return code for the dialog. 0 = Yes, 1 = Yes to all, 2 = No
	 */
	public int canContinueWithAllCheck(String message) {
		MessageDialog dialog = new MessageDialog(getParentShell(), getConfirmTitle(), null, // accept
					// the
					// default
					// window
					// icon
					message, MessageDialog.QUESTION, new String[]{IDialogConstants.YES_LABEL, IDialogConstants.YES_TO_ALL_LABEL, IDialogConstants.NO_LABEL}, 1); // yes
		// is
		// the
		// default
		return dialog.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.j2ee.operations.IOperationHandler#canContinueWithAllCheckAllowCancel(java.lang.String)
	 */
	public int canContinueWithAllCheckAllowCancel(String message) {
		MessageDialog dialog = new MessageDialog(getParentShell(), getConfirmTitle(), null, // accept
					// the
					// default
					// window
					// icon
					message, MessageDialog.QUESTION, new String[]{IDialogConstants.YES_LABEL, IDialogConstants.YES_TO_ALL_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL}, 1); // yes
		// is
		// the
		// default
		return dialog.open();
	}

	/**
	 * An error has occurred
	 */
	public void error(String message) {
		MessageDialog.openError(getParentShell(), getErrorTitle(), message);
	}

	/**
	 * The dialog title to be used for confirmations
	 */
	public java.lang.String getConfirmTitle() {
		return confirmTitle;
	}

	/**
	 * The dialog title to be used for errors
	 */
	public java.lang.String getErrorTitle() {
		return errorTitle;
	}

	/**
	 * The dialog title to be used for information
	 */
	public java.lang.String getInformationTitle() {
		return informationTitle;
	}

	public Shell getParentShell() {
		if (parentShell == null)
			parentShell = WTPUIPlugin.getPluginWorkbench().getActiveWorkbenchWindow().getShell();

		return parentShell;
	}

	/**
	 * An informational message needs to be presented
	 */
	public void inform(String message) {
		MessageDialog.openInformation(getParentShell(), getInformationTitle(), message);
	}

	/**
	 * Insert the method's description here. Creation date: (8/9/2001 11:51:36 AM)
	 * 
	 * @param newConfirmTitle
	 *            java.lang.String
	 */
	public void setConfirmTitle(java.lang.String newConfirmTitle) {
		confirmTitle = newConfirmTitle;
	}

	/**
	 * Insert the method's description here. Creation date: (8/9/2001 11:51:36 AM)
	 * 
	 * @param newErrorTitle
	 *            java.lang.String
	 */
	public void setErrorTitle(java.lang.String newErrorTitle) {
		errorTitle = newErrorTitle;
	}

	/**
	 * Insert the method's description here. Creation date: (8/9/2001 11:51:36 AM)
	 * 
	 * @param newInformationTitle
	 *            java.lang.String
	 */
	public void setInformationTitle(java.lang.String newInformationTitle) {
		informationTitle = newInformationTitle;
	}

	/**
	 * @see com.ibm.etools.j2ee.operations.IOperationHandler#getContext()
	 */
	public Object getContext() {
		return getParentShell();
	}


}