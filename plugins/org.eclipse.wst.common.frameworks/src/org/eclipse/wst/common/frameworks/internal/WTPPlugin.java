/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
/*
 * Created on Dec 10, 2003
 * 
 * To change the template for this generated file go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.common.frameworks.internal.enablement.nonui.WorkbenchUtil;
import org.osgi.framework.BundleContext;

import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jem.util.logger.proxyrender.DefaultPluginTraceRenderer;
import org.eclipse.jem.util.logger.proxyrender.IMsgLogger;

public abstract class WTPPlugin extends Plugin implements IMsgLogger {
	protected static Logger logger = null;
	protected static WTPPlugin instance = null; 
	public ResourceBundle resourceBundle;

	/**
	 * @param descriptor
	 */
	public WTPPlugin() {
		super();
		instance = this;
	}

	public Logger getMsgLogger() {
		if (logger == null) {
			logger = Logger.getLogger(getPluginID());
			setRenderer(logger);
		}
		return logger;
	}

	/**
	 * @param aLogger
	 */
	protected void setRenderer(Logger aLogger) {
		new DefaultPluginTraceRenderer(aLogger);
	}

	public Logger getLogger() {
		return getMsgLogger();
	}

	public abstract String getPluginID();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#startup()
	 */
	public void start(BundleContext context) throws Exception  {
		super.start(context);
		WorkbenchUtil.setWorkbenchIsRunning(true);
	}
	
}