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
package org.eclipse.wst.internal.common.emf.utilities;


/**
 * A passthru EncoderDecoder implementation
 */
public class PassthruEncoderDecoder extends EncoderDecoderAdapter implements EncoderDecoder {
	public static final PassthruEncoderDecoder INSTANCE = new PassthruEncoderDecoder();
	public static final String KEY = PassthruEncoderDecoder.class.getName();

	/**
	 * EncoderDecoderAdapter constructor comment.
	 */
	private PassthruEncoderDecoder() {
		super();
	}

	/**
	 * Returns a decoded version of the value.
	 */
	public String decode(String value) {
		return value;
	}

	/**
	 * Returns an encoded version of the value.
	 */
	public String encode(String value) {
		return value;
	}
}