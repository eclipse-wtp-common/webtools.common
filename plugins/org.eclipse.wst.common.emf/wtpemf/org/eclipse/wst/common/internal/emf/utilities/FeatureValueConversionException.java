/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;

public class FeatureValueConversionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8190891648333814201L;

	/**
	 * Constructor for FeatureValueConversionException.
	 */
	public FeatureValueConversionException() {
		super();
	}

	/**
	 * Constructor for FeatureValueConversionException.
	 * 
	 * @param s
	 */
	public FeatureValueConversionException(String s) {
		super(s);
	}

}