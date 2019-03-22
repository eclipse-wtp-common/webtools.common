/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*


 */
package org.eclipse.jem.util.logger.proxy;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.logging.Level;

/**
 * @deprecated Plugin error logging should be used instead
 * This is a base, UI independent logger.   It will
 * construct a consistent msg. body, and call an enfironment specific ILogRenderer.
 * By default, this logger will use a console based ILogRenderer,
 * and a J2EE Plugin identification.
 * 
 * <p>
 * When running outside of Eclipse, the trace and logging level come from the system properties
 * <ul>
 * 		<li>"debug" (="true") - The default is <code>false</code>. 
 * 		<li>"logLevel" (="level" where "level" is a level string, e.g. SEVERE, WARNING, etc. from the <code>java.util.logging.Level</code> class).
 * 			The default is "WARNING".
 * </ul>
 * 
 * 
 * @since 1.0.0
 */
public class Logger {
	
	// This is used by ILogRenderer2 to define the default level.
	static class LocalLevel extends Level {
		/**
		 * Comment for <code>serialVersionUID</code>
		 * 
		 * @since 1.1.0
		 */
		private static final long serialVersionUID = -6273357074767854883L;

		public LocalLevel(String name, int level) {
			super(name, level);
		}
	}	
	
	private boolean fTraceMode = false; // will we actually punch trace messaged or not              
	private String fPluginID;
	private ILogRenderer fRenderer = null;
	private ILogRenderer2 renderer2 = null;
	public String fLineSeperator;
	private Level level;
	private Level defaultLevel = Level.SEVERE;	// By default only severe or greater are logged.
	private String logFileName;
	private final static String DefaultLoggerPlugin = ILogRenderer.DefaultPluginID;
	static private Hashtable Loggers = new Hashtable(); // Keep track of all the Loggers
	final protected static String[] LogMark = { "*** ERROR *** ", //$NON-NLS-1$
		"[Trace] ", //$NON-NLS-1$
		"+++ Warning +++ ", //$NON-NLS-1$
		"Info " }; //$NON-NLS-1$

	final protected static String Filler = "    "; // Use this to indent msg. body //$NON-NLS-1$
	
	protected Logger() {
		this(ILogRenderer.DefaultPluginID);
	}
	
	protected Logger(String pluginID) {
		fPluginID = pluginID;
		setRenderer(new JDKConsoleRenderer(this));	// Set up default to this. Someone can change it later.
	}

	/**
	 * Return the stacktrace as a print formatted string.
	 * @param e
	 * @return the stacktrace as a string.
	 * 
	 * @since 1.0.0
	 */
	public String exceptionToString(Throwable e) {
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}
	
	/**
	 * Get the system default logger. This is used for clients that don't know if they
	 * are running in Eclipse or outside of it. This way they have a common logger format
	 * which switch correctly.
	 * @return default logger.
	 * 
	 * @since 1.0.0
	 */
	static public Logger getLogger() {
		Logger defaultLogger = (Logger) Loggers.get(DefaultLoggerPlugin);
		if (defaultLogger == null) {
			defaultLogger = new Logger();
			defaultLogger.init();
			Loggers.put(DefaultLoggerPlugin, defaultLogger);
		}
		return defaultLogger;
	}
	
	/**
	 * Get the logger for a specific plugin.
	 * @param pluginId
	 * @return logger for a specific pluggin.
	 * 
	 * @since 1.0.0
	 */
	static public Logger getLogger(String pluginId) {
		if (pluginId == null)
			return Logger.getLogger();
		Logger Logger = (Logger) Loggers.get(pluginId);
		if (Logger == null) {
			Logger = new Logger(pluginId);
			Logger.init();
			Loggers.put(pluginId, Logger);
		}
		return Logger;
	}
	
	/**
	 * Used by subclass to get a logger if it exists, but not create one.
	 * @param pluginId
	 * @return logger.
	 * 
	 * @since 1.0.0
	 */
	static protected Logger getLoggerIfExists(String pluginId) {
		if (pluginId == null)
			return Logger.getLogger();
		else
			return (Logger) Loggers.get(pluginId);
	}

	/**
	 * Get the plugin id for this logger.
	 * @return pluginid
	 * 
	 * @since 1.0.0
	 */
	public String getPluginID() {
		return fPluginID;
	}
	
	/**
	 * Get the trace mode for this logger
	 * @return <code>true</code> if tracing is going on.
	 * 
	 * @since 1.0.0
	 */
	public boolean getTraceMode() {
		return fTraceMode;
	}
	
	/*
	 * Indent the Msg. Body to make it easier to read the log
	 */
	private void indentMsg(String msg, StringBuffer logMsg) {
		// Line seperator is different on different platform, unix = \n, windows \r\n and mac \r
		String sep = fLineSeperator;
		if (msg.indexOf("\r\n") != -1) //$NON-NLS-1$
			sep = "\r\n"; //$NON-NLS-1$
		else if (msg.indexOf("\n") != -1) //$NON-NLS-1$
			sep = "\n"; //$NON-NLS-1$
		else if (msg.indexOf("\r") != -1) //$NON-NLS-1$
			sep = "\r"; //$NON-NLS-1$
		StringTokenizer tokenizer = new StringTokenizer(msg, sep);
		boolean first = true;
		while (tokenizer.hasMoreTokens()) {
			if (first) {
				first = false;
				logMsg.append(Filler + tokenizer.nextToken());
			} else
				logMsg.append(fLineSeperator + Filler + tokenizer.nextToken());
		}
	}
	/*
	 * If Eclipse is started with the -XDebug or -debug turn traces on for this Logger
	 * Creation date: (8/23/2001 7:37:04 PM)
	 */
	private void init() {
		if (System.getProperty("debug") != null) //$NON-NLS-1$
			fTraceMode = true;
		level = defaultLevel = Level.parse(System.getProperty("logLevel", Level.WARNING.getName())); //$NON-NLS-1$
		
		try {
			fLineSeperator = System.getProperty("line.separator"); // Diff on Win/Unix/Mac //$NON-NLS-1$
		} catch (Exception e) {
			fLineSeperator = "\n"; //$NON-NLS-1$
		}
	}
	/*
	 * Generic log.
	 * Creation date: (8/24/2001 1:55:34 PM)
	 * @return java.lang.String
	 * @param msg java.lang.String
	 * @param type int
	 */
	private String logAny(String msg, int type) {
		StringBuffer logMsg = new StringBuffer();
		logMsg.append(fLineSeperator);
		logMsg.append(LogMark[type]);
		return punchLog(logRest(msg, logMsg), type);
	}
	
	/**
	 * This is to be used by renderers that want to put a msg out
	 * in a generic format. This just returns the string that
	 * should be logged. It puts things like headers on it.
	 * 
	 * @param msg
	 * @param aLevel
	 * @return The generic message for the string and level.
	 * 
	 * @since 1.0.0
	 */
	public String getGenericMsg(String msg, Level aLevel) {
		StringBuffer genMsg = new StringBuffer(msg.length()+16);
		genMsg.append(fLineSeperator);
		genMsg.append(getLevelHeader(aLevel));
		genMsg.append(": "); //$NON-NLS-1$
		genMsg.append(new Date());
		indentMsg(msg, genMsg);
		return genMsg.toString();
	}
	
	private static final Level[] LEVEL_SEARCH = new Level[] {
		Level.SEVERE,
		Level.WARNING,
		Level.INFO,
		ILogRenderer2.TRACE
	};
	
	private static final String[] LEVEL_MARK = new String[] {
		"*** ERROR ***", //$NON-NLS-1$
		"+++ Warning +++", //$NON-NLS-1$
		"Info", //$NON-NLS-1$
		"[Trace]" //$NON-NLS-1$
	};
	
	private String getLevelHeader(Level aLevel) {
		for (int i=0; i<LEVEL_SEARCH.length; i++)
			if (LEVEL_SEARCH[i] == aLevel)
				return LEVEL_MARK[i];
		return aLevel.getName();	// Not found, just use level string.
	}
	
	// The write's are here for history. Will implement using log(obj, Level) for all of the types.


	/**
	 * deprecated use log(Level, Exception)
	 * @param aLevel
	 * @param ex
	 * @return
	 * 
	 * @since 1.0.0
	 * 
	 */
	public String write(Level aLevel, Exception ex) {
		return log(aLevel, ex);
	}
	
	/**
	 * deprecated use log(Throwable)
	 * @param ex
	 * @return
	 * 
	 * @since 1.0.0
	 * 
	 */
	public String write(Throwable ex) {
		return log(ex);
	}
	
	/**
	 * deprecated use log(Object, Level)
	 * @param aLevel
	 * @param logEntry
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public String write(Level aLevel, Object logEntry) {
		return log(logEntry, aLevel);
	}
	
	/**
	 * deprecated use log(String, Level)
	 * @param aLevel
	 * @param string
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public String write(Level aLevel, String string) {
		return log(string, aLevel);
	}
	/**
	 * deprecated use log(Throwable, Level)
	 * @param aLevel
	 * @param ex
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public String write(Level aLevel, Throwable ex) {
		return log(ex, aLevel);
	}
	/**
	 * deprecated use log(Throwable, Level)
	 * @param aLevel
	 * @param ex
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public String log(Level aLevel, Exception ex) {
		return log(ex, aLevel);
	}
	/**
	 * deprecated use log(Throwable, Level)
	 * @param aLevel
	 * @param ex
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public String log(Level aLevel, Throwable ex) {
		return log(ex, aLevel);
	}
	
	/**
	 * Get the logging level
	 * @return logging level
	 * 
	 * @since 1.0.0
	 */
	public Level getLevel() {
		return level;
	}
	
	/**
	 * Check if the requested level is being logged. (e.g. if current level is SEVERE, then FINE will not be logged).
	 * @param requestlevel
	 * @return <code>true</code> if the level will be logged.
	 * 
	 * @since 1.0.0
	 */
	public boolean isLoggingLevel(Level requestlevel) {
		if (requestlevel == ILogRenderer2.TRACE && !getTraceMode())
			return false;	// We aren't tracing but requested trace.
		
		return !(requestlevel.intValue() < getLevel().intValue() || getLevel() == Level.OFF);
	}

	/**
	 * Log an error string.
	 * @param msg
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String logError(String msg) {
		return log(msg, Level.SEVERE);
	}

	/**
	 * Log an error throwable
	 * @param e
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String logError(Throwable e) {
		return log(e, Level.SEVERE);
	}

	/**
	 * Log an info message.
	 * @param msg
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String logInfo(String msg) {
		return log(msg, Level.INFO);
	}
	
/**
 * Log a throwable as a warning.
 * @param e
 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
 * 
 * @since 1.0.0
 */
	public String logInfo(Throwable e) {
		return log(e, Level.INFO);
	}

	/**
	 * Append the string to logMsg buffer passed in. Append the date and format the
	 * string with nice indentation.
	 * 
	 * @param msg
	 * @param logMsg
	 * @return the string from the logMsg after logging the rest.
	 * 
	 * @since 1.0.0
	 */
	protected String logRest(String msg, StringBuffer logMsg) {
		logMsg.append(new Date());
		indentMsg(msg, logMsg);
		return logMsg.toString();
	}

	/**
	 * Log the msg as trace only.
	 * @param msg
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String logTrace(String msg) {
		if (fTraceMode)
			return log(msg, ILogRenderer2.TRACE);
		else
			return ILogRenderer.NOLOG_DESCRIPTION;
	}

	/**
	 * Log the throwable as trace only.
	 * @param e
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String logTrace(Throwable e) {
		return log(e, ILogRenderer2.TRACE);
	}

	/**
	 * Log the message as warning.
	 * @param msg
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String logWarning(String msg) {
		return log(msg, Level.WARNING);
	}
	/**
	 * Log the throwable as a warning.
	 * @param e
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String logWarning(Throwable e) {
		return log(e, Level.WARNING);
	}
	
	/**
	 * Ask the Renderer to punch the msg. in the log.. one
	 * caller at the time
	 * Creation date: (8/24/2001 9:19:17 AM)
	 * @return java.lang.String
	 * @param msg java.lang.String
	 * @param type int
	 */
	protected synchronized String punchLog(String msg, int type) {
		return fRenderer.log(msg, type);
	}
	
	/**
	 * Set the renderer to use.
	 * @param renderer
	 * 
	 * @since 1.0.0
	 */
	public void setRenderer(ILogRenderer renderer) {
		fRenderer = renderer;
		renderer2 = (renderer instanceof ILogRenderer2) ? (ILogRenderer2) renderer : null;
		renderer.setTraceMode(getTraceMode());
	}
	
	/**
	 * Set the trace mode.
	 * @param flag <code>true</code> to turn on tracing.
	 * 
	 * @since 1.0.0
	 */
	public void setTraceMode(boolean flag) {
		fTraceMode = flag;
		if (fRenderer != null)
			fRenderer.setTraceMode(flag);
	}
	
	/**
	 * Set the level cutoff for logging. Anything below this level will not log.
	 * Do not set level to <code>ILogRenderer2.TRACE</code>. It doesn't make sense.
	 * 
	 * @param level (Use <code>ILogRenderer2.DEFAULT</code> to restore to default for this logger.
	 * 
	 * @since 1.0.0
	 */
	public void setLevel(Level level) {
		this.level = level != ILogRenderer2.DEFAULT ? level : defaultLevel;
	}
	
	/**
	 * Set the default level for this logger. It won't touch the current level.
	 * 
	 * @param level
	 * 
	 * @since 1.0.0
	 */
	public void setDefaultLevel(Level level) {
		this.defaultLevel = level;
	}
	
	/**
	 * Get the log file name.
	 * @return Returns the logFileName.
	 */
	public String getLogFileName() {
		return logFileName;
	}

	/**
	 * Set the log file name.
	 * @param logFileName The logFileName to set.
	 */
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}
	
	// Now all of the log() types that use a Level.

	/**
	 * Log the throwable at the default level for a throwable. 
	 * @param e
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(Throwable e) {
		return log(e, ILogRenderer2.DEFAULT);
	}
	
	/**
	 * Log the throwable at the given level.
	 * @param e
	 * @param logLevel
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(Throwable e, Level logLevel) {
		if (renderer2 != null) {
			return renderer2.log(e, logLevel);
		} else {
			// Do it the old way.
			String stackTrace = exceptionToString(e);
			return logAny(stackTrace, getOldType(logLevel != ILogRenderer2.DEFAULT ? level : Level.SEVERE));
		}
	}
	
	public String log(Object o) {
		return log(o, ILogRenderer2.DEFAULT);
	}
	
	/**
	 * Log the object at the given level.
	 * @param o
	 * @param logLevel
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(Object o, Level logLevel) {
		if (renderer2 != null) {
			return renderer2.log(o, logLevel);
		} else {
			// Do it the old way.
			return logAny(String.valueOf(o), getOldType(logLevel != ILogRenderer2.DEFAULT ? level : Level.FINEST));
		}
	}	
	
	// The following are added to match up with Hyades so that primitives can be logged too.
	
	/**
	 * Log a boolean at the default level. 
	 * @param b
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(boolean b) {
		return log(b, ILogRenderer2.DEFAULT);
	}
	
	/**
	 * Log a boolean at the given level.
	 * @param b
	 * @param logLevel
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(boolean b, Level logLevel) {
		if (renderer2 != null) {
			return renderer2.log(b, logLevel);
		} else {
			// Do it the old way.
			return logAny(String.valueOf(b), getOldType(logLevel != ILogRenderer2.DEFAULT ? level : Level.FINEST));
		}
	}	
	
	/**
	 * Log the character at the default level.
	 * @param c
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(char c) {
		return log(c, ILogRenderer2.DEFAULT);
	}
	
	/**
	 * Log the character at the given level.
	 * @param c
	 * @param logLevel
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(char c, Level logLevel) {
		if (renderer2 != null) {
			return renderer2.log(c, logLevel);
		} else {
			// Do it the old way.
			return logAny(String.valueOf(c), getOldType(logLevel != ILogRenderer2.DEFAULT ? level : Level.FINEST));
		}
	}
	
	/**
	 * Log the byte at the default level.
	 * @param b
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(byte b) {
		return log(b, ILogRenderer2.DEFAULT);
	}
	
	/**
	 * Log the byte at the given level.
	 * @param b
	 * @param logLevel
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(byte b, Level logLevel) {
		if (renderer2 != null) {
			return renderer2.log(b, logLevel);
		} else {
			// Do it the old way.
			return logAny(String.valueOf(b), getOldType(logLevel != ILogRenderer2.DEFAULT ? level : Level.FINEST));
		}
	}	
	
	/**
	 * Log the short at the default level.
	 * @param s
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(short s) {
		return log(s, ILogRenderer2.DEFAULT);
	}
	
	/**
	 * Log the short at the given level.
	 * @param s
	 * @param logLevel
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(short s, Level logLevel) {
		if (renderer2 != null) {
			return renderer2.log(s, logLevel);
		} else {
			// Do it the old way.
			return logAny(String.valueOf(s), getOldType(logLevel != ILogRenderer2.DEFAULT ? level : Level.FINEST));
		}
	}
	
	/**
	 * Log the int at the default level.
	 * @param i
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(int i) {
		return log(i, ILogRenderer2.DEFAULT);
	}
	
	/**
	 * Log the int at the default level.
	 * @param i
	 * @param logLevel
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(int i, Level logLevel) {
		if (renderer2 != null) {
			return renderer2.log(i, logLevel);
		} else {
			// Do it the old way.
			return logAny(String.valueOf(i), getOldType(logLevel != ILogRenderer2.DEFAULT ? level : Level.FINEST));
		}
	}
	
	/**
	 * Log the long at the default level.
	 * @param l
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(long l) {
		return log(l, ILogRenderer2.DEFAULT);
	}
	
	/**
	 * Log the long at the given level.
	 * @param l
	 * @param logLevel
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(long l, Level logLevel) {
		if (renderer2 != null) {
			return renderer2.log(l, logLevel);
		} else {
			// Do it the old way.
			return logAny(String.valueOf(l), getOldType(logLevel != ILogRenderer2.DEFAULT ? level : Level.FINEST));
		}
	}	
	
	/**
	 * Log the float at the default level.
	 * @param f
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(float f) {
		return log(f, ILogRenderer2.DEFAULT);
	}
	
	/**
	 * Log the float at the given level.
	 * @param f
	 * @param logLevel
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(float f, Level logLevel) {
		if (renderer2 != null) {
			return renderer2.log(f, logLevel);
		} else {
			// Do it the old way.
			return logAny(String.valueOf(f), getOldType(logLevel != ILogRenderer2.DEFAULT ? level : Level.FINEST));
		}
	}
	
	/**
	 * Log the double at the default level
	 * @param d
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(double d) {
		return log(d, ILogRenderer2.DEFAULT);
	}
	
	/**
	 * Log the double at the given level
	 * 
	 * @param d
	 * @param logLevel
	 * @return how it was logged. See <code>CONSOLE_DESCRIPTION.</code>
	 * 
	 * @since 1.0.0
	 */
	public String log(double d, Level logLevel) {
		if (renderer2 != null) {
			return renderer2.log(d, logLevel);
		} else {
			// Do it the old way.
			return logAny(String.valueOf(d), getOldType(logLevel != ILogRenderer2.DEFAULT ? level : Level.FINEST));
		}
	}	
	
	/*
	 * Turn new type into old type. The defaultLevel is the
	 * level to use if the incoming level is marked as default.
	 */
	private int getOldType(Level aLevel) {
		if (aLevel == Level.SEVERE)
			return ILogRenderer.LOG_ERROR;
		else if (aLevel == Level.WARNING)
			return ILogRenderer.LOG_WARNING;
		else if (aLevel == Level.INFO)
			return ILogRenderer.LOG_INFO;
		else if (aLevel == ILogRenderer2.TRACE)
			return ILogRenderer.LOG_TRACE;
		else
			return ILogRenderer.LOG_INFO;		
	}
}
