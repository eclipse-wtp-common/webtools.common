/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operations;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Simialar to org.eclipse.jface.operation.IRunnableWithProgress, however without UI dependencies.
 * 
 * This class is EXPERIMENTAL and is subject to substantial changes.
 */
public interface IHeadlessRunnableWithProgress {
	/**
	 * Runs this operation without forcing a UI dependency.
	 * 
	 * @param monitor
	 *            the progress monitor to use to display progress and receive requests for
	 *            cancelation
	 * @exception InvocationTargetException
	 *                if the run method must propagate a checked exception, it should wrap it inside
	 *                an <code>InvocationTargetException</code>; runtime exceptions are
	 *                automatically wrapped in an <code>InvocationTargetException</code> by the
	 *                calling context
	 * @exception InterruptedException
	 *                if the operation detects a request to cancel, using
	 *                <code>IProgressMonitor.isCanceled()</code>, it should exit by throwing
	 *                <code>InterruptedException</code>
	 * 
	 * @see IRunnableWithProgress
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException;
}
