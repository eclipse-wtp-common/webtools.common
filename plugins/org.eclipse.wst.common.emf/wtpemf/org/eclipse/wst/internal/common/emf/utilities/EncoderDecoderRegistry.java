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


import java.util.HashMap;
import java.util.Map;

/**
 * Registry for storing and retrieving value decoders and encoders. Usage Example:
 * 
 * <pre>
 * 
 * 
 * // Use the standard WebSphere password value encoder/decoder.
 * EncoderDecoderRegistry.getDefaultRegistry().setDefaultEncoderDecoder(new com.ibm.ejs.security.util.WASEncoderDecoder());
 * // Begin tracking changes...
 * WriteBackHelper.begin();
 * // Load a resource which may have un-encoded values...
 * // Note: The WCCM will attempt to detect un-encoded values.  If unencoded values
 * // are found, the value will be encoded, and the resource will be added to the
 * // WriteBackHelper.
 * Resource res = resourceSet.load(&quot;myResource&quot;);
 * // Ensure that any changes due to encoding are written back out.
 * WriteBackHelper.end();
 * </pre>
 */
public class EncoderDecoderRegistry {
	protected static EncoderDecoderRegistry _defaultInstance;
	public static final EncoderDecoder INITIAL_DEFAULT_ENCODER = PassthruEncoderDecoder.INSTANCE;
	protected Map encoders = new HashMap();
	protected Object defaultEncoderKey;

	/**
	 * EncoderDecoderRegistry constructor comment.
	 */
	public EncoderDecoderRegistry() {
		super();
		initializeDefaultEncoders();
	}

	/**
	 * Insert the method's description here. Creation date: (2/2/2001 12:43:31 AM)
	 */
	public void addEncoderDecoder(EncoderDecoder encoderDecoder) {
		encoders.put(encoderDecoder.getClass().getName(), encoderDecoder);
		if (defaultEncoderKey == null)
			defaultEncoderKey = encoderDecoder.getClass().getName();
	}

	/**
	 * Returns an encoder/decoder by key.
	 */
	public EncoderDecoder getDefaultEncoderDecoder() {
		if (defaultEncoderKey == null)
			return null;
		return (EncoderDecoder) encoders.get(defaultEncoderKey);
	}

	/**
	 * Returns the default registry to use for retrieving value encoders and decoders
	 */
	public static EncoderDecoderRegistry getDefaultRegistry() {
		if (_defaultInstance == null) {
			_defaultInstance = new EncoderDecoderRegistry();
		}
		return _defaultInstance;
	}

	/**
	 * Returns an encoder/decoder by key.
	 */
	public EncoderDecoder getEncoderDecoder(Object key) {
		return (EncoderDecoder) encoders.get(key);
	}

	/**
	 * Initializes a standard set of encoder/decoders.
	 */
	public void initializeDefaultEncoders() {
		setDefaultEncoderDecoder(INITIAL_DEFAULT_ENCODER);
		addEncoderDecoder(new XMLValueEncoderDecoder());
	}

	/**
	 * Removes the encoder/decoder with the specified key.
	 */
	public void removeEncoderDecoder(Object key) {
		if (encoders.containsKey(key)) {
			encoders.remove(key);
		}
	}

	/**
	 * Returns an encoder/decoder by key.
	 */
	public void setDefaultEncoderDecoder(EncoderDecoder encoder) {
		defaultEncoderKey = encoder.getClass().getName();
		if (!encoders.containsKey(defaultEncoderKey)) {
			addEncoderDecoder(encoder);
		}
	}
}