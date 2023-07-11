/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.common.frameworks.internal.operations.IHeadlessRunnableWithProgress;
import org.eclipse.core.runtime.Platform;
import java.lang.Throwable;
import org.eclipse.core.runtime.CoreException;

/**
 * The main plugin class to be used in the desktop.
 */
public class WTPUIPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.wst.common.frameworks.ui"; //$NON-NLS-1$
	public static final String EXTENDED_VIEWER_REGISTRY_EXTENSION_POINT = "extendedViewer"; //$NON-NLS-1$
	// The shared instance.
	private static WTPUIPlugin plugin;
	// Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public WTPUIPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.eclipse.wst.common.frameworks.internal.ui.WTPUIPluginResources"); //$NON-NLS-1$
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

	public static RunnableOperationWrapper getRunnableWithProgress(final IUndoableOperation operation) {
		return new RunnableOperationWrapper(operation);
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
    
    public static void log( final Throwable e )
    {
        final String msg = e.getMessage() + ""; //$NON-NLS-1$
        log( new Status( IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, e ) );
    }

    public static void log( final IStatus status )
    {
        getDefault().getLog().log( status );
    }
    
    public static void log( final String msg )
    {
        log( new Status( IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, null ) );
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
