/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.frameworks.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.frameworks.internal.enablement.nonui.IWFTWrappedException;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

/**
 * Insert the type's description here. Creation date: (10/19/2001 11:40:59 AM)
 * 
 * @author: Administrator
 */
public class SaveHandlerHeadless implements ISaveHandler {
	/**
	 * HeadlessSaveHandler constructor comment.
	 */
	public SaveHandlerHeadless() {
		super();
	}

	/**
	 * access method comment.
	 */
	public void access() {
	}

	/**
	 * handleSaveFailed method comment.
	 */
	public void handleSaveFailed(SaveFailedException ex, org.eclipse.core.runtime.IProgressMonitor monitor) {
		throw ex;
	}

	public static boolean isFailedWriteFileFailure(IWFTWrappedException ex) {
		Exception nested = ex.getInnerMostNestedException();
		if (nested == null)
			return false;

		return isFailedWriteFileFailure(nested);
	}

	public static boolean isFailedWriteFileFailure(Exception ex) {
		if (ex instanceof IWFTWrappedException)
			return isFailedWriteFileFailure((IWFTWrappedException) ex);
		else if (ex instanceof CoreException)
			return isFailedWriteFileFailure((CoreException) ex);
		return false;
	}

	public static boolean isFailedWriteFileFailure(CoreException ex) {
		org.eclipse.core.runtime.IStatus status = ex.getStatus();
		if (status == null)
			return false;
		Throwable nested = status.getException();
		if (nested instanceof CoreException)
			return isFailedWriteFileFailure((CoreException) nested);
		return status.getCode() == org.eclipse.core.resources.IResourceStatus.FAILED_WRITE_LOCAL;
	}

	/**
	 * release method comment.
	 */
	public void release() {
	}

	/**
	 * shouldContinueAndMakeFileEditable method comment.
	 */
	public boolean shouldContinueAndMakeFileEditable(org.eclipse.core.resources.IFile aFile) {
		if (aFile == null)
			return false;
		String error = WTPResourceHandler.getString("Unable_to_save_read-only_f_ERROR_", new Object[]{aFile.getFullPath()}); //$NON-NLS-1$ = "Unable to save read-only file: "
		WTPCommonPlugin.logError(error);
		return false;
	}
}