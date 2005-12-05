/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.frameworks.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to factor common code for implementers of IArchiveWrappedException
 */
public class ExceptionHelper {
	/**
	 * Utility class; cannot be instantiated
	 */
	private ExceptionHelper() {
		super();
	}

	/**
	 * Return the messages from
	 * 
	 * @exception and
	 *                all nested exceptions, in order from outermost to innermost
	 */
	public static java.lang.String[] getAllMessages(IWrappedException exception) {
		List messages = new ArrayList(4);
		messages.add(exception.getMessage());
		Exception nested = exception.getNestedException();
		while (nested != null) {
			messages.add(nested.getMessage());
			if (nested instanceof IWrappedException)
				nested = ((IWrappedException) nested).getNestedException();
			else
				nested = null;
		}
		return (String[]) messages.toArray(new String[messages.size()]);
	}

	/**
	 * Return the messages from
	 * 
	 * @exception and
	 *                all nested exceptions, in order from outermost to innermost, concatenated as
	 *                one
	 */
	public static String getConcatenatedMessages(IWrappedException exception) {
		String[] messages = getAllMessages(exception);
		StringBuffer sb = new StringBuffer(256);
		for (int i = 0; i < messages.length; i++) {
			sb.append(messages[i]);
			if (i < messages.length - 1)
				sb.append('\n');
		}
		return sb.toString();
	}
}