package org.eclipse.wst.common.frameworks.internal;

/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */



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