/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.validateedit;


import java.util.List;

import org.eclipse.core.runtime.IStatus;

public interface ResourceStateValidatorPresenter {
	/**
	 * Present to the user that the <code>inconsistentFiles</code> will need to be refreshed in
	 * the workbench. Return true if the refresh should occur. The List will be a list of IFiles.
	 * 
	 * @param inconsistentFiles
	 * @return boolean
	 */
	boolean promptForInconsistentFileRefresh(List inconsistentFiles);

	/**
	 * Return the context (Shell) that would be passed to the validateEdit method. If this method
	 * returns null, a prompt to check out code will not be presented to the user.
	 * 
	 * @see org.eclipse.core.resources.IWorkspace#validateEdit(org.eclipse.core.resources.IFile[],
	 *      java.lang.Object)
	 */
	Object getValidateEditContext();

	/**
	 * Present a dialog to the user that indicates that the user is about to save and overwrite the
	 * list of <code>inconsitentFiles</codes>.  Return true if
	 * the overwrite should proceed.  The list of <code>inconsitentFiles</codes> will
	 * be a list of IFiles. 
	 * @param inconsistentFiles
	 * @return boolean
	 */
	boolean promptForInconsistentFileOverwrite(List inconsistentFiles);

	/**
	 * This method should be called by any action that is about to edit any contents of any IFile.
	 */
	public IStatus validateState();
}

