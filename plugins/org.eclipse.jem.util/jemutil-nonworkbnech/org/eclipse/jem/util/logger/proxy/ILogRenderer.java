/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*


 */
package org.eclipse.jem.util.logger.proxy;

/**
 * @deprecated Plugin error logging should be used instead
 * Basic log renderer interface. It is replaced by the extension <code>ILogRenderer2.</code>
 * 
 * @since 1.0.0
 */
public interface ILogRenderer {

	/**
	 * Logged to console.
	 */
	final public static String CONSOLE_DESCRIPTION = "console"; //$NON-NLS-1$

	/**
	 * Logged to workbench.
	 */
	final public static String WORKBENCH_DESCRIPTION = "workbench log"; //$NON-NLS-1$

	/**
	 * Not logged.
	 */
	final public static String NOLOG_DESCRIPTION = ""; //$NON-NLS-1$

	/**
	 * Log levels. These are deprecated, use <code>java.util.logging.Level</code> codes instead.
	 */
	final public static int LOG_ERROR = 0;

	final public static int LOG_TRACE = 1;

	final public static int LOG_WARNING = 2;

	final public static int LOG_INFO = 3;

	final public static String DefaultPluginID = "org.eclipse.jem.util"; //$NON-NLS-1$

	/**
	 * Log the string at the specified type.
	 * 
	 * @param msg
	 * @param type
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(String msg, int type);

	/**
	 * Start or stop the tracing.
	 * 
	 * @param Flag
	 *            <code>true</code> to start the tracing.
	 * 
	 * @since 1.0.0
	 */
	public void setTraceMode(boolean Flag);
}