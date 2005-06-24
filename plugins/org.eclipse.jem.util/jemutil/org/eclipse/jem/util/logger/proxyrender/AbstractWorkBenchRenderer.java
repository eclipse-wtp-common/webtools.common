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
 *  $RCSfile: AbstractWorkBenchRenderer.java,v $
 *  $Revision: 1.4 $  $Date: 2005/06/24 21:22:25 $ 
 */
package org.eclipse.jem.util.logger.proxyrender;

import java.lang.reflect.Field;
import java.util.logging.Level;

import org.eclipse.core.runtime.*;
import org.osgi.framework.Bundle;

import org.eclipse.jem.util.logger.proxy.*;


/**
 * Base log renderer that logs to the workbench.
 * 
 * @since 1.1.0
 */
public abstract class AbstractWorkBenchRenderer implements ILogRenderer2 {

	private boolean fTraceMode = false; // will we actually punch trace messaged or not

	private boolean fSettingTrace = false;

	protected Bundle fMyBundle = null;

	protected Logger fMyLogger = null;

	protected ILog fWorkBenchLogger = null;

	/**
	 * Constructer taking a logger.
	 * 
	 * @param logger
	 * 
	 * @since 1.1.0
	 */
	public AbstractWorkBenchRenderer(Logger logger) {
		super();
		fMyLogger = logger;
		fTraceMode = fMyLogger.getTraceMode();

		String pluginID = fMyLogger.getPluginID();
		fMyBundle = Platform.getBundle(pluginID);
		if (fMyBundle == null)
			throw new RuntimeException("Invalid Plugin ID"); //$NON-NLS-1$

		fWorkBenchLogger = Platform.getLog(fMyBundle);
		setTraceMode(fMyLogger.getTraceMode() || isDebugging(fMyBundle));
		fMyLogger.setRenderer(this);
	}

	/*
	 * This used to come from the Plugin instance. But in new OSGi, there is not necessarily a Plugin instance. So use the same logic they use.
	 */
	private boolean isDebugging(Bundle bundle) {
		String symbolicName = bundle.getSymbolicName();
		if (symbolicName != null) {
			String key = symbolicName + "/debug"; //$NON-NLS-1$
			String value = Platform.getDebugOption(key);
			return value == null ? false : value.equalsIgnoreCase("true"); //$NON-NLS-1$
		}
		return false;
	}

	/**
	 * Is the console log for eclipse turned on to sysout. If true, then we shouldn't log to console anything already logged because Eclipse would of
	 * logged it for us. This comes from the -Declipse.consoleLog="true" which is the default when starting eclipse from PDE.
	 */
	protected static final boolean consoleLogOn;
	static {
		String consologPropertyName = null;
		try {
			// Accessing an internal field, so using reflection. This way if changes in future we won't crash.
			Class eclipseStarter = Class.forName("org.eclipse.core.runtime.adaptor.EclipseStarter");	//$NON-NLS-1$
			Field consolelog = eclipseStarter.getDeclaredField("PROP_CONSOLE_LOG");	//$NON-NLS-1$
			consologPropertyName = (String) consolelog.get(null);
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} catch (ClassNotFoundException e) {
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		}
		consoleLogOn = consologPropertyName != null && "true".equals(System.getProperty(consologPropertyName)) ; 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer#setTraceMode(boolean)
	 */
	public void setTraceMode(boolean flag) {
		if (fSettingTrace)
			return; // Do not allow cycles

		fSettingTrace = true;
		fTraceMode = flag;
		fMyLogger.setTraceMode(flag);
		fSettingTrace = false;
	}

	// The following methods are for historical renderers in case this has been subclassed outside
	// of util.

	/**
	 * Log a string to the trace.
	 * 
	 * @param param
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public abstract String log(String param);

	/**
	 * Default one that log a string to the trace given a level. Default simply passes it to log(String) so that we don't break old subclasses.
	 * <p>
	 * If loggedToWorkbench is true, then it shouldn't be logged to console if consoleLogOn is true because workbench already logged to console.
	 * 
	 * @param msg
	 * @param l
	 * 
	 * @since 1.0.0
	 */
	protected void log(String msg, Level l, boolean loggedToWorkbench) {
		log(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer#log(java.lang.String, int)
	 */
	public String log(String msg, int type) {

		String target = logWorkBench(msg, type);
		if (fTraceMode || target.equals(NOLOG_DESCRIPTION))
			return log(msg);
		else
			return target;
	}

	/**
	 * Log to workbench, a string of the given level <code>ILogRenderer.LOG_</code>. levels.
	 * 
	 * @param msg
	 * @param type
	 * @return description of the log's destination e.g., <code>CONSOLE_DESCRIPTION</code>
	 * 
	 * @see ILogRenderer#LOG_ERROR and all of the other log types.
	 * @see ILogRenderer#CONSOLE_DESCRIPTION
	 * @since 1.0.0
	 */
	public String logWorkBench(String msg, int type) {

		try {
			int ErrCode;
			if (fWorkBenchLogger != null) {
				switch (type) {
					case (ILogRenderer.LOG_ERROR):
						ErrCode = IStatus.ERROR;
						break;
					case (ILogRenderer.LOG_WARNING):
						ErrCode = IStatus.WARNING;
						break;
					case (ILogRenderer.LOG_INFO):
						ErrCode = IStatus.INFO;
						break;
					case (ILogRenderer.LOG_TRACE):
						ErrCode = IStatus.OK;
						break;
					default:
						throw new RuntimeException("Invalid Log Type"); //$NON-NLS-1$
				}
				Status status = new Status(ErrCode, fMyBundle.getSymbolicName(), IStatus.OK, msg, null);
				fWorkBenchLogger.log(status);
				return WORKBENCH_DESCRIPTION;
			} else
				return NOLOG_DESCRIPTION;
		} catch (Throwable t) {
			return NOLOG_DESCRIPTION;
		}
	}

	// Default implentation of the ILogRenderer2 interface.
	protected boolean isLogging(Level level) {
		return fTraceMode || fMyLogger.isLoggingLevel(level);
	}

	private static final int[] STATUS_LEVEL;

	private static final Level[] STATUS_LEVEL_LOOKUP;

	private static final Level[] LEVEL_STATUS;

	static {
		// Status levels that correspond to the log levels, from finest to none, same indexes as from STATUS_LEVEL_LOOKUP.
		STATUS_LEVEL_LOOKUP = new Level[] { Level.INFO, Level.WARNING, Level.SEVERE};
		STATUS_LEVEL = new int[] { IStatus.INFO, IStatus.WARNING, IStatus.ERROR};

		// Levels that correspond to the IStatus levels.
		int maxID = Math.max(IStatus.OK, Math.max(IStatus.INFO, Math.max(IStatus.WARNING, IStatus.ERROR)));
		LEVEL_STATUS = new Level[maxID + 1];
		LEVEL_STATUS[IStatus.OK] = Level.FINE;
		LEVEL_STATUS[IStatus.INFO] = Level.INFO;
		LEVEL_STATUS[IStatus.WARNING] = Level.WARNING;
		LEVEL_STATUS[IStatus.ERROR] = Level.SEVERE;
	}

	/**
	 * Return the Java Level for the status code from the given IStatus.
	 * 
	 * @param status
	 * @return the Java Level
	 * 
	 * @since 1.0.0
	 */
	protected Level getLogLevel(IStatus status) {
		return LEVEL_STATUS[status.getSeverity()];
	}

	/**
	 * Return the IStatus status code for the given Java Level.
	 * 
	 * @param logLevel
	 * @return the IStatus status code.
	 * 
	 * @since 1.0.0
	 */
	protected int getStatusSeverity(Level logLevel) {
		for (int i = 0; i < STATUS_LEVEL_LOOKUP.length; i++) {
			if (STATUS_LEVEL_LOOKUP[i] == logLevel)
				return STATUS_LEVEL[i];
		}
		return IStatus.OK; // Default to ok.
	}

	/**
	 * Log the string to the workbench for the given level
	 * 
	 * @param msg
	 * @param level
	 * @return description of the log's destination e.g., <code>CONSOLE_DESCRIPTION</code>
	 * 
	 * @since 1.1.0
	 */
	protected String logWorkbench(String msg, Level level) {
		String result = NOLOG_DESCRIPTION;
		// Test again because we could be here simply due to trace mode, in which case we
		// don't want to workbench log it.
		if (fMyLogger.isLoggingLevel(level)) {
			Platform.getLog(fMyBundle).log(new Status(getStatusSeverity(level), fMyBundle.getSymbolicName(), 0, msg, null));
			result = WORKBENCH_DESCRIPTION;
			if (fTraceMode)
				log(msg, level, true);
		} else if (fTraceMode)
			log(msg, level, false);
		return result;
	}

	private String getStatusMsg(IStatus s, Level l) {
		if (s.getException() != null)
			return fMyLogger.getGenericMsg(s.toString() + fMyLogger.fLineSeperator + fMyLogger.exceptionToString(s.getException()), l);
		else
			return fMyLogger.getGenericMsg(s.toString(), l);
	}

	/**
	 * Log the IStatus to the workbench at the given level.
	 * 
	 * @param s
	 * @param level
	 * @return description of the log's destination e.g., <code>CONSOLE_DESCRIPTION</code>
	 * 
	 * @since 1.0.0
	 */
	protected String logWorkbench(IStatus s, Level level) {
		if (level == DEFAULT)
			level = getLogLevel(s);
		String result = NOLOG_DESCRIPTION;
		// Test again because we could be here simply due to trace mode, in which case we
		// don't want to workbench log it.
		if (fMyLogger.isLoggingLevel(level)) {
			Platform.getLog(fMyBundle).log(s);
			result = WORKBENCH_DESCRIPTION;
			if (fTraceMode)
				log(getStatusMsg(s, level), level, true);
		} else if (fTraceMode)
			log(getStatusMsg(s, level), level, false);
		return result;
	}

	/**
	 * Log to the workbench the Throwable at the given level.
	 * 
	 * @param t
	 * @param level
	 * @return description of the log's destination e.g., <code>CONSOLE_DESCRIPTION</code>
	 * 
	 * @since 1.0.0
	 */
	protected String logWorkbench(Throwable t, Level level) {
		String result = NOLOG_DESCRIPTION;
		// Test again because we could be here simply due to trace mode, in which case we
		// don't want to workbench log it.
		if (fMyLogger.isLoggingLevel(level)) {
			Platform.getLog(fMyBundle).log(new Status(getStatusSeverity(level), fMyBundle.getSymbolicName(), 0, "Exception thrown.", t)); //$NON-NLS-1$
			result = WORKBENCH_DESCRIPTION;
			if (fTraceMode)
				log(fMyLogger.getGenericMsg(fMyLogger.exceptionToString(t), level), level, true);
		} else if (fTraceMode)
			log(fMyLogger.getGenericMsg(fMyLogger.exceptionToString(t), level), level, false);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer2#log(byte, java.util.logging.Level)
	 */
	public String log(boolean b, Level level) {
		if (level == DEFAULT)
			level = Level.FINEST;
		if (isLogging(level))
			return logWorkbench(fMyLogger.getGenericMsg(String.valueOf(b), level), level);
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
			return logWorkbench(fMyLogger.getGenericMsg(String.valueOf(b), level), level);
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
			return logWorkbench(fMyLogger.getGenericMsg(String.valueOf(c), level), level);
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
			return logWorkbench(fMyLogger.getGenericMsg(String.valueOf(d), level), level);
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
			return logWorkbench(fMyLogger.getGenericMsg(String.valueOf(f), level), level);
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
			return logWorkbench(fMyLogger.getGenericMsg(String.valueOf(i), level), level);
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
			return logWorkbench(fMyLogger.getGenericMsg(String.valueOf(l), level), level);
		else
			return NOLOG_DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer2#log(java.lang.Object, java.util.logging.Level)
	 */
	public String log(Object o, Level level) {
		if (o instanceof IStatus)
			return logWorkbench((IStatus) o, level);
		if (level == DEFAULT)
			level = Level.FINEST;
		if (isLogging(level))
			return logWorkbench(fMyLogger.getGenericMsg(String.valueOf(o), level), level);
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
			return logWorkbench(fMyLogger.getGenericMsg(String.valueOf(s), level), level);
		else
			return NOLOG_DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxy.ILogRenderer2#log(java.lang.Throwable, java.util.logging.Level)
	 */
	public String log(Throwable t, Level level) {
		if (t instanceof CoreException)
			return logWorkbench(((CoreException) t).getStatus(), level);
		if (level == DEFAULT)
			level = Level.SEVERE;
		if (isLogging(level)) {
			return logWorkbench(t, level);
		} else
			return NOLOG_DESCRIPTION;
	}

}