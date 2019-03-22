/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on May 12, 2003
 *
 */
package org.eclipse.wst.common.internal.emf.utilities;

/**
 * Exception thrown when a proxy can not be resolved
 */
public class DanglingHREFException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3369128742899263327L;

	public DanglingHREFException() {
		super();
	}

	/**
	 * @param s
	 */
	public DanglingHREFException(String s) {
		super(s);
	}


}
