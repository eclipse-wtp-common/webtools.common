/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.common.framework.operation.IHeadlessRunnableWithProgress;

import com.ibm.wtp.common.logger.proxy.Logger;

/**
 * The main plugin class to be used in the desktop.
 */
public class WTPUIPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.wst.common.framework.ui"; //$NON-NLS-1$
	public static final String EXTENDED_VIEWER_REGISTRY_EXTENSION_POINT = "extendedViewer"; //$NON-NLS-1$
	//The shared instance.
	private static WTPUIPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public WTPUIPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.eclipse.wst.common.framework.ui.WTPUIPluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
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
	public static IStatus createErrorStatus(String message, Throwable exception) {
		return new Status(IStatus.ERROR, PLUGIN_ID, -1, message, exception);
	}

	/**
	 * Returns the shared instance.
	 */
	public static WTPUIPlugin getDefault() {
		return plugin;
	}

	public static Logger getLogger() {
		return Logger.getLogger(PLUGIN_ID);
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Return the workbench
	 * 
	 * This method is internal to the j2ee plugin and must not be called by any other plugins.
	 */

	public static IWorkbench getPluginWorkbench() {
		return getDefault().getWorkbench();

	}

	public static IRunnableWithProgress getRunnableWithProgress(IHeadlessRunnableWithProgress aHeadlessRunnableWithProgress) {
		return new RunnableWithProgressWrapper(aHeadlessRunnableWithProgress);
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = WTPUIPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null ? bundle.getString(key) : key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}