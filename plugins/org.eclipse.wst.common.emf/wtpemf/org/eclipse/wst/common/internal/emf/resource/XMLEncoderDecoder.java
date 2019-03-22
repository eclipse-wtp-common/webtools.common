/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on May 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.internal.emf.resource;

/**
 * @author mdelder
 *  
 */
public class XMLEncoderDecoder {

	/**
	 *  
	 */
	public XMLEncoderDecoder() {
		super();
	}

	/**
	 * Identifies the last printable character in the Unicode range that is supported by the
	 * encoding used with this serializer. For 8-bit encodings this will be either 0x7E or 0xFF. For
	 * 16-bit encodings this will be 0xFFFF. Characters that are not printable will be escaped using
	 * character references.
	 */
	private int _lastPrintable = 0xFFFE;

	protected static XMLEncoderDecoder _singleton;


	/**
	 * Returns a decoded version of the value.
	 */
	public String decode(String value) {
		// NOT_IMPLEMENTED
		return value;
	}


	/**
	 * Escapes a string so it may be printed as text content or attribute value. Non printable
	 * characters are escaped using character references. Where the format specifies a deault entity
	 * reference, that reference is used (e.g. <tt>&amp;lt;</tt>).
	 * 
	 * @param source
	 *            The string to escape
	 */
	public char[] encode(char[] value) {
		boolean unmodified = true;
		StringBuffer sbuf = new StringBuffer(value.length);
		String charRef = null;
		char ch;
		for (int i = 0; i < value.length; ++i) {
			ch = value[i];
			// If there is a suitable entity reference for this
			// character, print it. The list of available entity
			// references is almost but not identical between
			// XML and HTML.
			charRef = getEntityRef(ch);
			if (charRef != null) {
				sbuf.append('&');
				sbuf.append(charRef);
				sbuf.append(';');
				unmodified = false;
			} else if ((ch >= ' ' && ch <= _lastPrintable && ch != 0xF7) || ch == '\n' || ch == '\r' || ch == '\t') {
				// If the character is not printable, print as character
				// reference.
				// Non printables are below ASCII space but not tab or line
				// terminator, ASCII delete, or above a certain Unicode
				// threshold.
				sbuf.append(ch);
			} else {
				sbuf.append("&#");//$NON-NLS-1$
				sbuf.append(Integer.toString(ch));
				sbuf.append(';');
				unmodified = false;
			}
		}
		if (unmodified)
			return value;
		char[] result = new char[sbuf.length()];
		sbuf.getChars(0, sbuf.length(), result, 0);
		return result;
	}

	/**
	 * Escapes a string so it may be printed as text content or attribute value. Non printable
	 * characters are escaped using character references. Where the format specifies a deault entity
	 * reference, that reference is used (e.g. <tt>&amp;lt;</tt>).
	 * 
	 * @param source
	 *            The string to escape
	 */
	public String encode(String value) {
		StringBuffer sbuf = new StringBuffer(value.length());
		String charRef = null;
		char ch;
		for (int i = 0; i < value.length(); ++i) {
			ch = value.charAt(i);
			// If there is a suitable entity reference for this
			// character, print it. The list of available entity
			// references is almost but not identical between
			// XML and HTML.
			charRef = getEntityRef(ch);
			if (charRef != null) {
				sbuf.append('&');
				sbuf.append(charRef);
				sbuf.append(';');
			} else if ((ch >= ' ' && ch <= _lastPrintable && ch != 0xF7) || ch == '\n' || ch == '\r' || ch == '\t') {
				// If the character is not printable, print as character
				// reference.
				// Non printables are below ASCII space but not tab or line
				// terminator, ASCII delete, or above a certain Unicode
				// threshold.
				sbuf.append(ch);
			} else {
				sbuf.append("&#");//$NON-NLS-1$
				sbuf.append(Integer.toString(ch));
				sbuf.append(';');
			}
		}
		return sbuf.toString();
	}

	public static String escape(String value) {
		if (_singleton == null) {
			_singleton = new XMLEncoderDecoder();
		}
		return _singleton.encode(value);
	}

	/**
	 * Returns the suitable entity reference for this character value, or null if no such entity
	 * exists. Calling this method with <tt>'&amp;'</tt> will return <tt>"&amp;amp;"</tt>.
	 * 
	 * @param ch
	 *            Character value
	 * @return Character entity name, or null
	 */
	protected String getEntityRef(char ch) {
		// Encode special XML characters into the equivalent character
		// references.
		// These five are defined by default for all XML documents.
		switch (ch) {
			case '<' :
				return "lt";//$NON-NLS-1$
			case '>' :
				return "gt";//$NON-NLS-1$
			case '"' :
				return "quot";//$NON-NLS-1$
			case '\'' :
				return "apos";//$NON-NLS-1$
			case '&' :
				return "amp";//$NON-NLS-1$
		}
		return null;
	}
}
