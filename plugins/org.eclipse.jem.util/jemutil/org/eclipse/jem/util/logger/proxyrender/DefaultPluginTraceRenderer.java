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
package org.eclipse.jem.util.logger.proxyrender;

import java.io.*;
import java.util.logging.Level;

import org.eclipse.core.runtime.Platform;

import org.eclipse.jem.util.logger.proxy.Logger;


/**
 * Logger that also logs to a trace file in the plugin's metadata area.
 * 
 * @since 1.0.0
 */
public class DefaultPluginTraceRenderer extends AbstractWorkBenchRenderer {

	/**
	 * Name of the trace file in the metadata area.
	 * 
	 * @since 1.0.0
	 */
	public static final String PluginTraceFileName = "/.log"; //$NON-NLS-1$

	private String fTraceFile = null;

	private PrintWriter traceFile = null;

	/**
	 * DefaultUILogRenderer constructor.
	 * 
	 * @param logger
	 */
	public DefaultPluginTraceRenderer(Logger logger) {
		super(logger);

		fTraceFile = Platform.getStateLocation(fMyBundle).toString() + PluginTraceFileName;
		(new File(fTraceFile)).delete(); // Start from fresh ... do not want to leak on disk space

	}

	private void closeTraceFile() {
		if (traceFile == null)
			return;
		try {
			traceFile.flush();
			traceFile.close();
		} finally {
			traceFile = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxyrender.AbstractWorkBenchRenderer#log(java.lang.String)
	 */
	public String log(String msg) {

		System.out.println(msg);

		openTraceFile();
		if (traceFile != null) {
			traceFile.println(msg);
			closeTraceFile();
		}
		return fTraceFile;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxyrender.AbstractWorkBenchRenderer#log(java.lang.String, java.util.logging.Level, boolean)
	 */
	protected void log(String msg, Level l, boolean loggedToWorkbench) {
		if (!loggedToWorkbench || !consoleLogOn) {
			if (l == Level.SEVERE)
				System.err.println(msg);
			else
				System.out.println(msg);
		}

		openTraceFile();
		if (traceFile != null) {
			traceFile.println(msg);
			closeTraceFile();
		}
	}

	private void openTraceFile() {
		try {
			traceFile = new PrintWriter(new FileOutputStream(fTraceFile, true));
		} catch (IOException e) {
			// there was a problem opening the log file so log to the console
			traceFile = null;
		}
	}
}