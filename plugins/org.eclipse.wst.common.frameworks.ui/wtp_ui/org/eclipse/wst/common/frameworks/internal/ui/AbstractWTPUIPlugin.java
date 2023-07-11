/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
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

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public abstract class AbstractWTPUIPlugin extends AbstractUIPlugin {
	public ResourceBundle resourceBundle;
	protected static AbstractWTPUIPlugin instance = null; 

	/**
	 * @param descriptor
	 */
	public AbstractWTPUIPlugin() {
		super();
		instance = this;
	}

	public abstract String getPluginID();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#startup()
	 */
	@Override
	public void start(BundleContext context) throws Exception  {
		super.start(context);
	}
}
