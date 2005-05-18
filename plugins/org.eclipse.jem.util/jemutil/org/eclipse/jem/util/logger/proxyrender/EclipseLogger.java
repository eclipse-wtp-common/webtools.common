/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $RCSfile: EclipseLogger.java,v $
 *  $Revision: 1.3 $  $Date: 2005/05/18 21:58:34 $ 
 */
package org.eclipse.jem.util.logger.proxyrender;

import java.util.logging.Level;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;

import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jem.util.plugin.JEMUtilPlugin;

 
/**
 * Get an Eclipse logger. This gets a logger that knows it is for a plugin. 
 * 
 * For each plugin, the following can be specified in the .option file:
 * 		{plugin-id}/debug/logTrace
 * 			The values valid for this are "true", "false", or "default". If true then loggers will be set into
 * 			trace mode. This means everything logged (even those that are filtered out due to not meeting log level)
 * 			will be traced out. If the value is "true" they will be traced to the system console.  
 * 			If it is "default" or not set at all, then it will be the value in the "org.eclipse.jem.util/debug/logTrace" .options setting.
 * 			If not set there, then default value will be false.
 * 
 * 		{plugin-id}/debug/logTraceFile
 * 			The values valid for this are "true", "false", or "default". If true then loggers will trace to 
 * 			the system console AND to the ".log" file in the plugins work area in the metadata section of the workspace.
 * 			If "false" then not traced to a trace file. 
 * 			If it is "default" or not set at all, then it will be the value in the "org.eclipse.jem.util/debug/logTraceFile" .options setting.
 * 			If not set there, then default value will be false.
 * 
 * 		{plugin-id}.debug/logLevel
 * 			The values valid for this are the names from <code>java.util.logging.Level</code>, and "default". These
 * 			are the trace cutoff levels to use. For instance, if the level was SEVERE, then only level SEVERE msgs
 * 			are logged. The rest are skipped. Or if the level was WARNING, then only level SEVERE and WARNING are
 * 			logged.
 * 			If it is "default" or not set at all, then it will be the value in the "org.eclipse.jem.util/debug/logLevel" .options setting.
 * 			If not set there, then default value will be WARNING.
 * 
 * 
 * @since 1.0.0
 */
public class EclipseLogger extends Logger {

	public static final String DEBUG_TRACE_OPTION = "/debug/logtrace"; //$NON-NLS-1$
	public static final String DEBUG_TRACE_FILE_OPTION = "/debug/logtracefile"; //$NON-NLS-1$
	public static final String DEBUG_LOG_LEVEL_OPTION = "/debug/loglevel"; // The logging level to use when no Hyaedes. (which we don't support at this time). //$NON-NLS-1$
	public static final String DEFAULT_OPTION = "default";	// If option value is this, then the value from WTP Common plugin options will be used for both logTrace and logLevel.	 //$NON-NLS-1$
	
	/**
	 * Return a logger based upon the Plugin. 
	 * @param plugin
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public static Logger getEclipseLogger(Plugin plugin) {
		return getEclipseLogger(plugin.getBundle());
	}
	
	/**
	 * Return a logger based upon the bundle.
	 * 
	 * @param bundle
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public static Logger getEclipseLogger(Bundle bundle) {
		String id = bundle.getSymbolicName();
		Logger logger = getLoggerIfExists(id);
		if (logger != null)
			return logger;
		
		logger = getLogger(id);	// Create one, we will now customize it.
		
		String pluginOption = Platform.getDebugOption(id + DEBUG_TRACE_OPTION);
		if (pluginOption == null || "default".equalsIgnoreCase(pluginOption)) //$NON-NLS-1$
			pluginOption = Platform.getDebugOption(JEMUtilPlugin.getDefault().getBundle().getSymbolicName() + DEBUG_TRACE_OPTION);
		boolean logTrace = "true".equalsIgnoreCase(pluginOption); //$NON-NLS-1$

		pluginOption = Platform.getDebugOption(id + DEBUG_TRACE_FILE_OPTION);
		if (pluginOption == null || "default".equalsIgnoreCase(pluginOption)) //$NON-NLS-1$
			pluginOption = Platform.getDebugOption(JEMUtilPlugin.getDefault().getBundle().getSymbolicName() + DEBUG_TRACE_FILE_OPTION);
		boolean logTraceFile = "true".equalsIgnoreCase(pluginOption); //$NON-NLS-1$
		
		pluginOption = Platform.getDebugOption(id + DEBUG_LOG_LEVEL_OPTION);
		if (pluginOption == null || "default".equalsIgnoreCase(pluginOption)) //$NON-NLS-1$
			pluginOption = Platform.getDebugOption(JEMUtilPlugin.getDefault().getBundle().getSymbolicName() + DEBUG_LOG_LEVEL_OPTION);
		
		Level logLevel = Level.WARNING;
		if (pluginOption != null) {
			try {
				logLevel = Level.parse(pluginOption);
			} catch (IllegalArgumentException e) {
			}
		}
		
		if (logTrace)
			logger.setTraceMode(true);
		logger.setLevel(logLevel);
		if (!logTraceFile)
			logger.setRenderer(new ConsoleLogRenderer(logger));
		else
			logger.setRenderer(new DefaultPluginTraceRenderer(logger));
		
		return logger;
	}
	
}
