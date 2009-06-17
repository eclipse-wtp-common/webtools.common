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

import java.util.logging.Level;

import org.eclipse.jem.util.logger.proxy.ILogRenderer;
import org.eclipse.jem.util.logger.proxy.Logger;


/**
 * @deprecated Plugin error logging should be used instead
 * Log renderer to the console.
 * 
 * @since 1.0.0
 */
public class ConsoleLogRenderer extends AbstractWorkBenchRenderer {

	/**
	 * Constructor taking a logger.
	 * 
	 * @param logger
	 * 
	 * @since 1.0.0
	 */
	public ConsoleLogRenderer(Logger logger) {
		super(logger);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.logger.proxyrender.AbstractWorkBenchRenderer#log(java.lang.String)
	 */
	public String log(String msg) {

		System.out.println(msg);
		return ILogRenderer.CONSOLE_DESCRIPTION;

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
	}

}