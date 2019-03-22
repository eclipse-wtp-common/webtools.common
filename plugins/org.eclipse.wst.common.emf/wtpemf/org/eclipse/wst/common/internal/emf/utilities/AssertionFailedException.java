/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;


/**
 * <code>AssertionFailedException</code> is a runtime exception thrown by some of the methods in
 * <code>Assert</code>.
 * <p>
 * This class is not declared public to prevent some misuses; programs that catch or otherwise
 * depend on assertion failures are susceptible to unexpected breakage when assertions in the code
 * are added or removed.
 * </p>
 */
// This class was, originally, copied directly from com.ibm.itp.common.
// It was copied to our own package just to minimize minor dependencies
// on other packages and internal APIs.
class AssertionFailedException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3408284008342683621L;

	/**
	 * Constructs a new exception.
	 */
	public AssertionFailedException() {
	}

	/**
	 * Constructs a new exception with the given message.
	 */
	public AssertionFailedException(String detail) {
		super(detail);
	}
}