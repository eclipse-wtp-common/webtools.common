/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: JEMUtilPlugin.java,v $$
 *  $$Revision: 1.2 $$  $$Date: 2005/02/15 23:04:14 $$ 
 */
package org.eclipse.jem.util.plugin;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.Bundle;

import org.eclipse.jem.internal.util.emf.workbench.ProjectResourceSetImpl;
import org.eclipse.jem.internal.util.emf.workbench.WorkspaceResourceNotifier;
import org.eclipse.jem.util.emf.workbench.ProjectResourceSet;
import org.eclipse.jem.util.emf.workbench.ResourceHandler;
import org.eclipse.jem.util.logger.proxy.Logger;


/**
 * Plugin for EMFWorkbench utils.
 * 
 * @since 1.0.0
 */
public class JEMUtilPlugin extends Plugin {

	public static final String ID = "org.eclipse.jem.util"; //$NON-NLS-1$
	
	/**
	 * Plugin id of this plugin.
	 * 
	 * @since 1.0.0
	 */
	public static final String PLUGIN_ID = ID;	

	/**
	 * UI Context extension point.
	 * 
	 * @since 1.0.0
	 */
	public static final String UI_CONTEXT_EXTENSION_POINT = "uiContextSensitiveClass"; //$NON-NLS-1$

	/**
	 * UITester element name.
	 * 
	 * @since 1.0.0
	 */
	public static final String UI_TESTER_EXTENSION_POINT = "uiTester"; //$NON-NLS-1$
	
	/**
	 * Protocol for workspace
	 * 
	 * @since 1.0.0
	 */
	public static final String WORKSPACE_PROTOCOL = "workspace"; //$NON-NLS-1$

	/**
	 * Protocol for platform uri's. i.e. "platform:/..."
	 * 
	 * @since 1.0.0
	 *  
	 */
	public static final String PLATFORM_PROTOCOL = "platform"; //$NON-NLS-1$

	/**
	 * Resource indication in platform protocol. Indicates url is for a resource in the workspace. i.e. "platform:/resource/projectname/..."
	 * 
	 * @since 1.0.0
	 */
	public static final String PLATFORM_RESOURCE = "resource"; //$NON-NLS-1$

	/**
	 * Plugin indication in platform protocol. Indicates url is for a file/directory in the plugins area. i.e. "platform:/plugin/pluginid/..."
	 * 
	 * @since 1.0.0
	 */
	public static final String PLATFORM_PLUGIN = "plugin"; //$NON-NLS-1$

	private static WorkspaceResourceNotifier sharedCache;

	private static ResourceSet pluginResourceSet;

	private static String[] GLOBAL_LOADING_PLUGIN_NAMES;

	private static JEMUtilPlugin DEFAULT;

	public JEMUtilPlugin() {
		super();
		DEFAULT = this;
	}

	/**
	 * Get the workspace. Just use ResourcePlugin.getWorkspace() instead.
	 * 
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Get the plugin instance.
	 * 
	 * @return plugin instance.
	 * 
	 * @since 1.0.0
	 */
	public static JEMUtilPlugin getDefault() {
		return DEFAULT;
	}

	/**
	 * Gets the sharedCache.
	 * <p>
	 * This is not meant to be called by clients.
	 * </p>
	 * 
	 * @return a WorkspaceResourceNotifier
	 * @since 1.0.0
	 */
	public static WorkspaceResourceNotifier getSharedCache() {
		if (sharedCache == null)
			sharedCache = new WorkspaceResourceNotifier();
		return sharedCache;
	}

	/**
	 * Sets the sharedCache.
	 * <p>
	 * This is not meant to be called by clients.
	 * </p>
	 * 
	 * @param sharedCache
	 *            The sharedCache to set
	 * @since 1.0.0
	 */
	public static void setSharedCache(WorkspaceResourceNotifier aSharedCache) {
		sharedCache = aSharedCache;
	}

	/**
	 * @deprecated use createIsolatedResourceSet(IProject)
	 */
	public static ResourceSet createIsolatedResourceSet() {
		return null;
	}

	/**
	 * Add an Adapter. You can use this api to listen for any shared resource being loaded or removed from any ProjectResourceSet in the Workbench
	 * instead of trying to listen to each individual ProjectResourceSet.
	 * 
	 * @param adapter
	 * 
	 * @since 1.0.0
	 */
	public static void addWorkspaceEMFResourceListener(Adapter adapter) {
		if (adapter != null && !getSharedCache().eAdapters().contains(adapter))
			getSharedCache().eAdapters().add(adapter);
	}

	/**
	 * Removes the adapter.
	 * 
	 * @param adapter
	 * 
	 * @see #addWorkspaceEMFResourceListener(Adapter)
	 * @since 1.0.0
	 */
	public static void removeWorkspaceEMFResourceListener(Adapter adapter) {
		if (adapter != null)
			getSharedCache().eAdapters().remove(adapter);
	}

	/**
	 * Is this plugin active.
	 * 
	 * @return <code>true</code> if active
	 * 
	 * @since 1.0.0
	 */
	public static boolean isActivated() {
		Bundle bundle = Platform.getBundle(ID);
		if (bundle != null)
			return bundle.getState() == Bundle.ACTIVE;
		return false;
	}

	/**
	 * This method will be called when a WorkbenchContext is instantiated on an EMFNature.
	 * <p>
	 * This not meant to be called by clients.
	 * </p>
	 * 
	 * @param aResourceSet
	 * 
	 * @see plugin.xml#ResourceHandlerExtension extension point.
	 * @since 1.0.0
	 */
	public void addExtendedResourceHandlers(ProjectResourceSet aResourceSet) {
		if (aResourceSet == null)
			return;
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint pct = registry.getExtensionPoint(getBundle().getSymbolicName(), "ResourceHandlerExtension"); //$NON-NLS-1$
		IExtension[] extension = pct.getExtensions();
		IExtension config;
		for (int l = 0; l < extension.length; ++l) {
			config = extension[l];
			IConfigurationElement[] cElems = config.getConfigurationElements();
			ResourceHandler handler = null;
			for (int i = 0; i < cElems.length; i++) {
				try {
					handler = (ResourceHandler) cElems[i].createExecutableExtension("run"); //$NON-NLS-1$

				} catch (Exception ex) {
					handler = null;
				}
				if (handler != null)
					aResourceSet.add(handler);
			}
		}
	}

	/**
	 * Get the project resource set for the plugin (there is one for the whole system).
	 * 
	 * @return system-wide resource set.
	 * @since 1.0.0
	 */
	public static ResourceSet getPluginResourceSet() {
		if (pluginResourceSet == null)
			pluginResourceSet = new ProjectResourceSetImpl(null);
		return pluginResourceSet;
	}

	/**
	 * Set the system-wide resource set.
	 * 
	 * @param set
	 * @since 1.0.0
	 */
	public static void setPluginResourceSet(ResourceSet set) {
		pluginResourceSet = set;
	}

	/**
	 * Get the global loading plugin names.
	 * <p>
	 * This is not meant to be called by clients.
	 * </p>
	 * 
	 * @return
	 * 
	 * @since 1.0.0
	 */
	public static String[] getGlobalLoadingPluginNames() {
		if (GLOBAL_LOADING_PLUGIN_NAMES == null)
			GLOBAL_LOADING_PLUGIN_NAMES = readGlobalLoadingPluginNames();
		return GLOBAL_LOADING_PLUGIN_NAMES;
	}

	/**
	 * Get the Logger for this plugin.
	 * 
	 * @return logger for this plugin.
	 * 
	 * @since 1.0.0
	 */
	public static Logger getLogger() {
		return Logger.getLogger(ID);
	}

	private static String[] readGlobalLoadingPluginNames() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint exPoint = reg.getExtensionPoint(ID, "globalPluginResourceLoad"); //$NON-NLS-1$
		IExtension[] extensions = exPoint.getExtensions();
		String[] names = new String[extensions.length];
		if (extensions.length > 0) {
			for (int i = 0; i < extensions.length; i++)
				names[i] = extensions[i].getNamespace();
		}
		return names;
	}

}