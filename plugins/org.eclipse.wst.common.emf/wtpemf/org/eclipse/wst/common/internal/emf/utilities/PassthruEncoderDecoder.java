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
 * A passthru EncoderDecoder implementation
 */
public class PassthruEncoderDecoder extends EncoderDecoderAdapter {
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
	@Override
	public String decode(String value) {
		return value;
	}

	/**
	 * Returns an encoded version of the value.
	 */
	@Override
	public String encode(String value) {
		return value;
	}
}