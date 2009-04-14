/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Dec 10, 2003
 * 
 * To change the template for this generated file go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.ui;

import java.util.ResourceBundle;

import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jem.util.logger.proxyrender.DefaultPluginTraceRenderer;
import org.eclipse.jem.util.logger.proxyrender.IMsgLogger;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public abstract class AbstractWTPUIPlugin extends AbstractUIPlugin implements IMsgLogger {
	/**
	 * @deprecated
	 */
	protected static Logger logger = null;
	public ResourceBundle resourceBundle;
	protected static AbstractWTPUIPlugin instance = null; 

	/**
	 * @param descriptor
	 */
	public AbstractWTPUIPlugin() {
		super();
		instance = this;
	}

	/**
	 * @deprecated
	 * @return
	 */
	public Logger getMsgLogger() {
		if (logger == null) {
			logger = Logger.getLogger(getPluginID());
			setRenderer(logger);
		}
		return logger;
	}

	public abstract String getPluginID();

	/**
	 * @deprecated
	 * @param aLogger
	 */
	protected void setRenderer(Logger aLogger) {
		new DefaultPluginTraceRenderer(aLogger);
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#startup()
	 */
	public void start(BundleContext context) throws Exception  {
		super.start(context);
	}
}
