/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.frameworks.internal;

/**
 * Runtime exception that could get thrown during save of an edit model; clients should use
 * {@link #getConcatenatedMessages}to get all the messages of this and all nested exceptions to
 * report the failure.
 */
public class SaveFailedException extends WrappedRuntimeException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -4640018901910731240L;

	/**
	 * SaveFailedException constructor comment.
	 */
	public SaveFailedException() {
		super();
	}

	/**
	 * SaveFailedException constructor comment.
	 * 
	 * @param e
	 *            java.lang.Exception
	 */
	public SaveFailedException(Exception e) {
		super(e);
	}

	/**
	 * SaveFailedException constructor comment.
	 * 
	 * @param s
	 *            java.lang.String
	 */
	public SaveFailedException(String s) {
		super(s);
	}

	/**
	 * SaveFailedException constructor comment.
	 * 
	 * @param s
	 *            java.lang.String
	 * @param e
	 *            java.lang.Exception
	 */
	public SaveFailedException(String s, Exception e) {
		super(s, e);
	}
}