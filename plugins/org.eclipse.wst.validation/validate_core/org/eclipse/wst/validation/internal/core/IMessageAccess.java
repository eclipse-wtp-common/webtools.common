/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.core;


import java.util.List;
import java.util.Locale;


/**
 * <p>
 * Interface which can be used to access messages which are stored within a reporter which retains
 * its message history.
 * </p>
 * 
 * [issue: CS - This interface seems like overkill.  I'd think that an IReport should simply have a getMessages() method.
 * I think MessageFiltering should take place at the 'display' level and not at this level of the API.]
 */
public interface IMessageAccess {
	/**
	 * @param filter
	 *            A filter which specifies a subset of messages to retrieve. null specifies, "all
	 *            messages".
	 * @return List list of messages related to the target object
	 */
	abstract List getMessages(MessageFilter filter);

	/**
	 * @param filter
	 *            A filter which specifies a subset of messages to retrieve. null specifies, "all
	 *            messages".
	 * @param locale
	 *            The target local to translate the messages into. null specifies, "use default
	 *            locale".
	 * @return List a list of messages related to the target object, translated into the specified
	 * locale.
	 */
	abstract List getMessagesAsText(MessageFilter filter, Locale targetLocale);
}
