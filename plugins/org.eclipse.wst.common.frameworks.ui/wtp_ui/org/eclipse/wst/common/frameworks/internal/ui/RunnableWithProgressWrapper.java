package org.eclipse.wst.common.frameworks.internal.ui;

/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */


import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.wst.common.frameworks.operations.IHeadlessRunnableWithProgress;


/**
 * This is a wrapper for our IHeadlessRunnableWithProgress to the IRunnableWithProgress. This class
 * needs to be used when running the operation from a IRunnableContext.
 * 
 * @see IRunnableContext
 * @see JavaUIPlugin#getRunnableWithProgress Creation date: (5/8/2001 1:28:45 PM)
 * @author: Administrator
 */
public class RunnableWithProgressWrapper implements org.eclipse.jface.operation.IRunnableWithProgress {
	// //$NON-NLS-1$
	private IHeadlessRunnableWithProgress headlessRunnable;

	/**
	 * RunnableWithProgressWrapper constructor comment.
	 */
	public RunnableWithProgressWrapper(IHeadlessRunnableWithProgress aHeadlessRunnableWithProgress) {
		setHeadlessRunnable(aHeadlessRunnableWithProgress);
	}

	/**
	 * Insert the method's description here. Creation date: (5/8/2001 1:29:52 PM)
	 * 
	 * @return com.ibm.etools.j2ee.operations.IHeadlessRunnableWithProgress
	 */
	protected org.eclipse.wst.common.frameworks.operations.IHeadlessRunnableWithProgress getHeadlessRunnable() {
		return headlessRunnable;
	}

	/**
	 * Runs this operation. Progress should be reported to the given progress monitor. This method
	 * is usually invoked by an <code>IRunnableContext</code>'s<code>run</code> method, which
	 * supplies the progress monitor. A request to cancel the operation should be honored and
	 * acknowledged by throwing <code>InterruptedException</code>.
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
	 * @see IRunnableContext#run
	 */
	public void run(org.eclipse.core.runtime.IProgressMonitor monitor) throws java.lang.reflect.InvocationTargetException, java.lang.InterruptedException {
		getHeadlessRunnable().run(monitor);
	}

	/**
	 * Insert the method's description here. Creation date: (5/8/2001 1:29:52 PM)
	 * 
	 * @param newHeadlessRunnable
	 *            com.ibm.etools.j2ee.operations.IHeadlessRunnableWithProgress
	 */
	protected void setHeadlessRunnable(org.eclipse.wst.common.frameworks.operations.IHeadlessRunnableWithProgress newHeadlessRunnable) {
		headlessRunnable = newHeadlessRunnable;
	}
}