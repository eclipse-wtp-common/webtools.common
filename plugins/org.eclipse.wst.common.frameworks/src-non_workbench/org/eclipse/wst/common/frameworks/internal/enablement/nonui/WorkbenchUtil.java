/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.enablement.nonui;

public class WorkbenchUtil {

	protected static boolean WorkbenchRunning = false;

	private WorkbenchUtil() {
		super();
	}

	/**
	 * workbenchIsRunning() - test whether or not we are running in the workbench environment.
	 * 
	 * @see JavaPlugin.startup()
	 */
	public static boolean workbenchIsRunning() {
		return WorkbenchRunning;
	}

	/**
	 * Set to true if you are running in a Workbench environment.
	 * 
	 * @see JavaPlugin.startup()
	 */
	public static void setWorkbenchIsRunning(boolean aBoolean) {
		WorkbenchRunning = aBoolean;
	}

}