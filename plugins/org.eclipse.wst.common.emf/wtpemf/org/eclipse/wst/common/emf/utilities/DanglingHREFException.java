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
/*
 * Created on May 12, 2003
 *
 */
package org.eclipse.wst.common.emf.utilities;

/**
 * Exception thrown when a proxy can not be resolved
 */
public class DanglingHREFException extends Exception {


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