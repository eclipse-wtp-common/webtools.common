/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.common.frameworks.internal.enablement.nonui.WorkbenchUtil;
import org.osgi.framework.BundleContext;

public abstract class WTPPlugin extends Plugin {
	protected static WTPPlugin instance = null; 
	public ResourceBundle resourceBundle;

	/**
	 * @param descriptor
	 */
	public WTPPlugin() {
		super();
		instance = this;
	}

	public static boolean isPlatformCaseSensitive() {
		return Platform.OS_MACOSX.equals(Platform.getOS()) ? false : new
				java.io.File("a").compareTo(new java.io.File("A")) != 0;  //$NON-NLS-1$//$NON-NLS-2$
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
		WorkbenchUtil.setWorkbenchIsRunning(true);
	}
	
}
