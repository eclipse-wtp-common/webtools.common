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
package org.eclipse.wst.common.internal.emf.utilities;


/**
 * An adapter for EncoderDecoder implementations
 */
public abstract class EncoderDecoderAdapter implements EncoderDecoder {

	/**
	 * EncoderDecoderAdapter constructor comment.
	 */
	public EncoderDecoderAdapter() {
		super();
	}

	/**
	 * Returns a decoded version of the value.
	 */
	public abstract String decode(String value);

	/**
	 * Returns an encoded version of the value.
	 */
	public abstract String encode(String value);
}