/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.frameworks.internal;

public class WrappedException extends java.lang.reflect.InvocationTargetException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -9221925581603648538L;

	/**
	 * WFTWrappedException constructor comment.
	 */
	protected WrappedException() {
		super();
	}

	/**
	 * WFTWrappedException constructor comment.
	 * 
	 * @param target
	 *            java.lang.Throwable
	 */
	public WrappedException(Throwable target) {
		super(target);
	}

	/**
	 * WFTWrappedException constructor comment.
	 * 
	 * @param target
	 *            java.lang.Throwable
	 * @param s
	 *            java.lang.String
	 */
	public WrappedException(Throwable target, String s) {
		super(target, s);
	}

	/**
	 * Print out a stack trace to the system err.
	 */
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	/**
	 * Prints the exception to System.err. If we have a nested exception, print its stack.
	 */
	public void printStackTrace(java.io.PrintStream s) {
		if (getTargetException() != null) {
			s.println(this);
			s.println("Stack trace of nested exception:"); //$NON-NLS-1$
			getTargetException().printStackTrace(s);
		} else {
			super.printStackTrace(s);
		}
	}

	/**
	 * Prints the exception to System.err. If we have a nested exception, print its stack.
	 */
	public void printStackTrace(java.io.PrintWriter s) {
		if (getTargetException() != null) {
			s.println(this);
			s.println("Stack trace of nested exception:"); //$NON-NLS-1$
			getTargetException().printStackTrace(s);
		} else {
			super.printStackTrace(s);
		}
	}
}