package org.eclipse.wst.common.frameworks.internal.operations;

/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */


import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Insert the type's description here. Creation date: (5/8/2001 1:14:41 PM)
 * 
 * @author: Administrator
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