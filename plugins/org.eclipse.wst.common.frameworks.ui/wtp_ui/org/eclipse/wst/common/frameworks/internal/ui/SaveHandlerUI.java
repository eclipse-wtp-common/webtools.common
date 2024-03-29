/*******************************************************************************
 * Copyright (c) 2001, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.frameworks.internal.ISaveHandler;
import org.eclipse.wst.common.frameworks.internal.SaveFailedException;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;


public class SaveHandlerUI implements ISaveHandler {
	public static final String SAVE_FAILED = WTPCommonPlugin.getResourceString("16"); //$NON-NLS-1$
	public static final String BEGINNING_MESSAGE = WTPCommonPlugin.getResourceString("17"); //$NON-NLS-1$
	protected boolean isYesToAll = false;
	protected int referenceCount = 0;

	/**
	 * SaveHandlerUI constructor comment.
	 */
	public SaveHandlerUI() {
		super();
	}

	/**
	 * access method comment.
	 */
	@Override
	public void access() {
		referenceCount++;
	}

	protected Shell getParentShellForDialog() {
		if (Display.getCurrent() != null)
			return Display.getCurrent().getActiveShell();

		return null;
	}

	protected Display getDisplay() {
		Display result = Display.getCurrent();
		return result == null ? Display.getDefault() : result;
	}

	@Override
	public void handleSaveFailed(SaveFailedException ex, IProgressMonitor monitor) {
		if (referenceCount > 1)
			//Let the outermost reference handle it
			throw ex;
		String exMsg = ex.getInnerMostNestedException() == null ? ex.getMessage() : ex.getInnerMostNestedException().getMessage();
		final String message = BEGINNING_MESSAGE + ":\n" + exMsg;//$NON-NLS-1$
		getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openError(getParentShellForDialog(), SAVE_FAILED, message);
			}
		});
		if (monitor != null)
			monitor.setCanceled(true);
		else
			throw ex;
	}

	protected boolean promptUserToSaveReadOnly(IFile aFile) {

		String[] buttonStrings = {WTPCommonPlugin.getResourceString("Yes_UI_"), WTPCommonPlugin.getResourceString("Yes_To_All_UI_"), WTPCommonPlugin.getResourceString("No_UI_")}; //$NON-NLS-3$ = "No" //$NON-NLS-2$ = "Yes To All" //$NON-NLS-1$ = "Yes"
		String title = WTPCommonPlugin.getResourceString("Saving_Read-Only_File_UI_"); //$NON-NLS-1$ = "Saving Read-Only File"
		String message = WTPCommonPlugin.getResourceString("2concat_INFO_", (new Object[]{aFile.getFullPath()})); //$NON-NLS-1$ = "The file {0} is read-only and cannot be saved.  Would you like to make it editable and save anyway?"

		final MessageDialog dialog = new MessageDialog(getParentShellForDialog(), title, null, // accept
					// the
					// default
					// window
					// icon
					message, MessageDialog.QUESTION, buttonStrings, 0); // Yes is the default

		final int[] ret = new int[1];
		getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				dialog.setBlockOnOpen(true);
				ret[0] = dialog.open();
			}
		});

		switch (ret[0]) {
			case 0 : {
				return true;
			}
			case 1 : {
				isYesToAll = true;
				return true;
			}
			case 2 : {
				return false;
			}
		}
		return false;
	}

	/**
	 * release method comment.
	 */
	@Override
	public void release() {
		referenceCount--;
		if (referenceCount == 0)
			isYesToAll = false;

	}

	/**
	 * shouldContinueAndMakeFileEditable method comment.
	 */
	@Override
	public boolean shouldContinueAndMakeFileEditable(IFile aFile) {
		boolean yes = isYesToAll || promptUserToSaveReadOnly(aFile);
		if (yes)
			aFile.getResourceAttributes().setReadOnly(false);
		return yes;
	}
}