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

/**
 * Common interface for ArchiveRuntime and ArchiveWrapped exceptions, which can contain nested
 * exceptions
 */
public interface IWrappedException {
	/**
	 * Return the messages from this and all nested exceptions, in order from outermost to innermost
	 */
	public String[] getAllMessages();

	/**
	 * Return the messages from this and all nested exceptions, in order from outermost to
	 * innermost, concatenated as one
	 */
	public String getConcatenatedMessages();

	public Exception getInnerMostNestedException();

	public String getMessage();

	public java.lang.Exception getNestedException();

	public void printStackTrace();

	public void printStackTrace(java.io.PrintStream s);

	public void printStackTrace(java.io.PrintWriter s);
}