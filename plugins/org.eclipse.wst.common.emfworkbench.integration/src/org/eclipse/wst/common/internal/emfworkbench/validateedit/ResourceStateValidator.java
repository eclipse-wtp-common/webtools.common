/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.validateedit;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.jem.util.plugin.JEMUtilPlugin;

public interface ResourceStateValidator {
	static final IStatus OK_STATUS = new Status(IStatus.OK, JEMUtilPlugin.ID, 0, "", null); //$NON-NLS-1$

	/**
	 * This method should be called whenever a <code>presenter</code> is activated (becomes
	 * active). This will check the timestamps of the underlying files to see if they are different
	 * from the last cached modified value. The <code>presenter</code> should be prepared to
	 * prompt the user if they would like to refresh with the contents on disk if we are dirty.
	 */
	void checkActivation(ResourceStateValidatorPresenter presenter) throws CoreException;

	/**
	 * This method should be called whenever a <code>presenter</code> looses activation. This will
	 * check the timestamps of the underlying files to see if they are different from the last
	 * cached modified value. The <code>presenter</code> should be prepared to prompt the user if
	 * they would like to refresh with the contents on disk if we are dirty.
	 */
	void lostActivation(ResourceStateValidatorPresenter presenter) throws CoreException;

	/**
	 * This method should be called the first time the files are about to be modified after a
	 * <code>presenter</code> becomes active. The returned IStatus may have an ERROR status which
	 * should be presented to the user.
	 */
	IStatus validateState(ResourceStateValidatorPresenter presenter) throws CoreException;

	/**
	 * This method should be called prior to the <code>presenter</code> saving the modified
	 * contents. This will check the consistency of the underlying files to ensure that they are
	 * synchronized. If true is returned, the save can proceed.
	 */
	boolean checkSave(ResourceStateValidatorPresenter presenter) throws CoreException;

	/**
	 * Return true if there are any read only files.
	 */
	boolean checkReadOnly();
}

