/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * 
 * Derived from org.eclipse.search.internal.ui.Messages
 *******************************************************************************/
package org.eclipse.wst.common.ui.internal.search.basecode;

import com.ibm.icu.text.MessageFormat;

/**
 * Helper class to format message strings.
 * 
 * @since 3.1
 */
public class Messages {

	public static String format(String message, Object object) {
		return MessageFormat.format(message, new Object[] { object});
	}

	public static String format(String message, Object[] objects) {
		return MessageFormat.format(message, objects);
	}

	private Messages() {
		// Not for instantiation
	}
}
