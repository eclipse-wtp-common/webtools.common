/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.plugin;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.internal.WTPPlugin;
import org.eclipse.core.runtime.Platform;
import java.lang.Throwable;
import org.eclipse.core.runtime.CoreException;

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
	@Override
	public String getPluginID() {
		return PLUGIN_ID;
	}

	public static IStatus createStatus(int severity, String message, Throwable exception) {
		return new Status(severity, PLUGIN_ID, message, exception);
	}

	public static IStatus createStatus(int severity, String message) {
		return createStatus(severity, message, null);
	}

	public static void logError(Throwable exception) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( createStatus(IStatus.ERROR, exception.getMessage(), exception));
	}

	public static void logError(CoreException exception) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( exception.getStatus() );
	}

	public static void logError(String message) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( createStatus(IStatus.ERROR, message));
	}
}
