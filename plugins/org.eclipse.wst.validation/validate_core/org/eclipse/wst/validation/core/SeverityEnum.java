/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.core;


/**
 * Enumeration values used to specify severity of a reported message.
 * 
 * [issue: CS - I'd suggest defining these enums directly on 'IMessage' (ala IMarker)]
 */
public interface SeverityEnum {
	/**
	 * Typically used to specify error messages.
	 */
	public static final int HIGH_SEVERITY = 0x0001;

	/**
	 * Typically used to specify warning messages.
	 */
	public static final int NORMAL_SEVERITY = 0x0002;

	/**
	 * Typically used to specify information messages.
	 */
	public static final int LOW_SEVERITY = 0x0004;

	/**
	 * Specify high (error) and normal (warning) messages. Typically used with a MessageFilter, to
	 * filter out information messages.
	 */
	public static final int ERROR_AND_WARNING = HIGH_SEVERITY | NORMAL_SEVERITY;

	/**
	 * Specify all types of messages. Typically used with a MessageFilter.
	 */
	public static final int ALL_MESSAGES = ERROR_AND_WARNING | LOW_SEVERITY;
}