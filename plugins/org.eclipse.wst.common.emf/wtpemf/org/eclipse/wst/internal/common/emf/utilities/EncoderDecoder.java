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
 * Represents an interface to an object which can encode and decode values. This typically involves
 * cryptography algorithms. This interface, along with the supplied adapters provide an extension
 * mechanism for pluggable crytography that can be used when storing and retrieving attribute
 * values, and is used prevalently for encoding and decoding password values of mof objects.
 */
public interface EncoderDecoder {
	/**
	 * Returns a decoded version of the value.
	 */
	public String decode(String value);

	/**
	 * Returns an encoded version of the value.
	 */
	public String encode(String value);
}