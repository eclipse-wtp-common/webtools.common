/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 * yyyymmdd bug      Email and other contact information
 * -------- -------- -----------------------------------------------------------
 * 20060221   100364 pmoogk@ca.ibm.com - Peter Moogk
 *******************************************************************************/
package org.eclipse.wst.common.internal.environment.plugin;


import org.eclipse.core.runtime.Plugin;

/**
* This is the plugin class for the org.eclipse.wst.common.internal.environment.plugin.EnvironmentPlugin.
*/
public class EnvironmentPlugin extends Plugin
{	
	/**
	 * The instance of this plugin.
	 */
	private static EnvironmentPlugin instance;

	/**
	 * Constructor for use by the Eclipse platform only.
	 */
	public EnvironmentPlugin()
	{
		super();
		instance = this;
	}
	
	/**
	 * Returns the instance of this plugin.
	 */
	static public EnvironmentPlugin getInstance ()
	{
		return instance;
	}
}
