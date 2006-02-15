/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Oct 29, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.plugin;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.internal.WTPPlugin;

/**
 * @author jsholl
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WTPCommonPlugin extends WTPPlugin {

	public static final String PLUGIN_ID = "org.eclipse.wst.common.frameworks"; //$NON-NLS-1$
	public static final String GROUP_REGISTRY_EXTENSION_POINT = "functionGroup"; //$NON-NLS-1$
	public static final IStatus OK_STATUS = new Status(IStatus.OK, PLUGIN_ID, 0, "OK", null); //$NON-NLS-1$

	//	The shared instance.
	private static WTPCommonPlugin plugin;

	public WTPCommonPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("wtp_common"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	public static WTPCommonPlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * @param string
	 * @return
	 */
	public static IStatus createErrorStatus(String message) {
		return createErrorStatus(message, null);
	}

	/**
	 * @param string
	 * @return
	 */
	public static IStatus createWarningStatus(String message) {
		return createWarningStatus(message, null);
	}
	
	/**
	 * @param string
	 * @return
	 */
	public static IStatus createCancelStatus(String message) {
		return createCancelStatus(message, null);
	}	

	/**
	 * @param string
	 * @return
	 */
	public static IStatus createWarningStatus(String message, Throwable exception) {
		return new Status(IStatus.WARNING, PLUGIN_ID, -1, message, exception);
	}

	/**
	 * @param string
	 * @return
	 */
	public static IStatus createErrorStatus(String message, Throwable exception) {
		return new Status(IStatus.ERROR, PLUGIN_ID, -1, message, exception);
	}
	
	/**
	 * @param string
	 * @return
	 */
	public static IStatus createCancelStatus(String message, Throwable exception) {
		return new Status(IStatus.CANCEL, PLUGIN_ID, -1, message, exception);
	}	

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = WTPCommonPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null ? bundle.getString(key) : key);
		} catch (MissingResourceException e) {
			return "!" + key + "!"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public static String getResourceString(String key, Object[] args) {
		String pattern = getResourceString(key);
		if (pattern != null)
			return MessageFormat.format(pattern, args);
		return null;
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.WTPPlugin#getPluginID()
	 */
	public String getPluginID() {
		return PLUGIN_ID;
	}
}
