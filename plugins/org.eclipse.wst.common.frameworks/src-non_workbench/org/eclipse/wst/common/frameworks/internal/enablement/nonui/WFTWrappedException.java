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
package org.eclipse.wst.common.frameworks.internal.enablement.nonui;

//import com.ibm.etools.wft.nls.WFTUtilsResourceHandler;



/**
 * Insert the type's description here. Creation date: (04/03/01 11:12:51 AM)
 * 
 * @author: Administrator
 */
public class WFTWrappedException extends java.lang.reflect.InvocationTargetException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -6885044277377784429L;

	/**
	 * WFTWrappedException constructor comment.
	 */
	protected WFTWrappedException() {
		super();
	}

	/**
	 * WFTWrappedException constructor comment.
	 * 
	 * @param target
	 *            java.lang.Throwable
	 */
	public WFTWrappedException(Throwable target) {
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
	public WFTWrappedException(Throwable target, String s) {
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
			//TODO add this back in
			// s.println(WFTUtilsResourceHandler.getString("Stack_trace_of_nested_exce_ERROR_"));
			// //$NON-NLS-1$ = "Stack trace of nested exception:"
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
			//TODO add this back in
			// s.println(WFTUtilsResourceHandler.getString("Stack_trace_of_nested_exce_ERROR_"));
			// //$NON-NLS-1$ = "Stack trace of nested exception:"
			getTargetException().printStackTrace(s);
		} else {
			super.printStackTrace(s);
		}
	}
}