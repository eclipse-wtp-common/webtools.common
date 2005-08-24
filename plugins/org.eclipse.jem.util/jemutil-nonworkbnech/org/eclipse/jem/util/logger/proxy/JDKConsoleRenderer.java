/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $RCSfile: JDKConsoleRenderer.java,v $
 *  $Revision: 1.3 $  $Date: 2005/08/24 21:10:34 $ 
 */
package org.eclipse.jem.util.logger.proxy;

import java.util.logging.Level;

/**
 * Default log renderer to use when not running under Eclipse. It logs to sysout and syserr.
 * 
 * @since 1.1.0
 */

public class JDKConsoleRenderer implements ILogRenderer2 {

	private boolean fTraceMode = false; // will we actually punch trace messaged or not

	private boolean fSettingTrace = false;

	private Logger fMyLogger = null;

	/**
	 * Constructer taking a logger.
	 * 
	 * @param logger
	 * 
	 * @since 1.1.0
	 */
	public JDKConsoleRenderer(Logger logger) {
		super();
		fMyLogger = logger;
		fTraceMode = fMyLogger.getTraceMode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer#log(java.lang.String, int)
	 */
	public String log(String msg, int type) {

		if (type == ILogRenderer.LOG_TRACE && !fTraceMode)
			return null;

		if (type == ILogRenderer.LOG_ERROR)
			System.err.println(msg);
		else
			System.out.println(msg);
		return ILogRenderer.CONSOLE_DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer#setTraceMode(boolean)
	 */
	public void setTraceMode(boolean flag) {

		if (fSettingTrace)
			return;
		fSettingTrace = true;
		fTraceMode = flag;
		fMyLogger.setTraceMode(flag);
		fSettingTrace = false;
	}

	/**
	 * Log the string at the given level.
	 * 
	 * @param msg
	 * @param level
	 * @return <code>CONSOLE_DESCRIPTION</code>
	 * 
	 * @since 1.1.0
	 */
	protected String log(String msg, Level level) {
		if (level == Level.SEVERE)
			System.err.println(msg);
		else
			System.out.println(msg);
		return ILogRenderer.CONSOLE_DESCRIPTION;
	}

	/**
	 * Answer if logging at the given level
	 * 
	 * @param logLevel
	 * @return <code>true</code> if logging at the given level.
	 * 
	 * @since 1.1.0
	 */
	protected boolean isLogging(Level logLevel) {
		return fTraceMode || fMyLogger.isLoggingLevel(logLevel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer2#log(boolean, java.util.logging.Level)
	 */
	public String log(boolean b, Level level) {
		if (level == DEFAULT)
			level = Level.FINEST;
		if (isLogging(level))
			return log(fMyLogger.getGenericMsg(String.valueOf(b), level), level);
		else
			return NOLOG_DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer2#log(byte, java.util.logging.Level)
	 */
	public String log(byte b, Level level) {
		if (level == DEFAULT)
			level = Level.FINEST;
		if (isLogging(level))
			return log(fMyLogger.getGenericMsg(String.valueOf(b), level), level);
		else
			return NOLOG_DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer2#log(char, java.util.logging.Level)
	 */
	public String log(char c, Level level) {
		if (level == DEFAULT)
			level = Level.FINEST;
		if (isLogging(level))
			return log(fMyLogger.getGenericMsg(String.valueOf(c), level), level);
		else
			return NOLOG_DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer2#log(double, java.util.logging.Level)
	 */
	public String log(double d, Level level) {
		if (level == DEFAULT)
			level = Level.FINEST;
		if (isLogging(level))
			return log(fMyLogger.getGenericMsg(String.valueOf(d), level), level);
		else
			return NOLOG_DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer2#log(float, java.util.logging.Level)
	 */
	public String log(float f, Level level) {
		if (level == DEFAULT)
			level = Level.FINEST;
		if (isLogging(level))
			return log(fMyLogger.getGenericMsg(String.valueOf(f), level), level);
		else
			return NOLOG_DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer2#log(int, java.util.logging.Level)
	 */
	public String log(int i, Level level) {
		if (level == DEFAULT)
			level = Level.FINEST;
		if (isLogging(level))
			return log(fMyLogger.getGenericMsg(String.valueOf(i), level), level);
		else
			return NOLOG_DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer2#log(long, java.util.logging.Level)
	 */
	public String log(long l, Level level) {
		if (level == DEFAULT)
			level = Level.FINEST;
		if (isLogging(level))
			return log(fMyLogger.getGenericMsg(String.valueOf(l), level), level);
		else
			return NOLOG_DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer2#log(java.lang.Object, java.util.logging.Level)
	 */
	public String log(Object o, Level level) {
		if (level == DEFAULT)
			level = Level.FINEST;
		if (isLogging(level))
			return log(fMyLogger.getGenericMsg(String.valueOf(o), level), level);
		else
			return NOLOG_DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer2#log(short, java.util.logging.Level)
	 */
	public String log(short s, Level level) {
		if (level == DEFAULT)
			level = Level.FINEST;
		if (isLogging(level))
			return log(fMyLogger.getGenericMsg(String.valueOf(s), level), level);
		else
			return NOLOG_DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer2#log(java.lang.Throwable, java.util.logging.Level)
	 */
	public String log(Throwable t, Level level) {
		if (level == DEFAULT)
			level = Level.SEVERE;
		if (isLogging(level))
			return log(fMyLogger.getGenericMsg(fMyLogger.exceptionToString(t), level), level);
		else
			return NOLOG_DESCRIPTION;
	}

}
