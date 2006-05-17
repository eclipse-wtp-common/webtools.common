/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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
 *  $$Revision: 1.5 $$  $$Date: 2006/05/17 20:13:45 $$ 
 */
package org.eclipse.jem.util.plugin;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.Bundle;

import org.eclipse.jem.internal.util.emf.workbench.ProjectResourceSetImpl;
import org.eclipse.jem.internal.util.emf.workbench.WorkspaceResourceNotifier;
import org.eclipse.jem.internal.util.emf.workbench.nls.EMFWorkbenchResourceHandler;
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
	 * Delete the contents of the directory (and the directory if deleteRoot is true).
	 * @param root
	 * @param deleteRoot <code>true</code> to delete the root directory too.
	 * @param monitor 
	 * @return <code>true</code> if there was an error deleting anything.
	 * 
	 * @since 1.1.0
	 */
	public static boolean deleteDirectoryContent(File root, boolean deleteRoot, IProgressMonitor monitor) {
		boolean error = false;
		if (root.canRead()) {
			if (root.isDirectory()) {
				File[] files = root.listFiles();
				monitor.beginTask(MessageFormat.format(EMFWorkbenchResourceHandler.getString("ProjectUtil_Delete_1"), new Object[] {root.getName()}), files.length+(deleteRoot ? 1 : 0)); //$NON-NLS-1$
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory())
						error |= deleteDirectoryContent(files[i], true, new SubProgressMonitor(monitor, 1));
					else {
						error |= !files[i].delete();
					}
					monitor.worked(1);
				}
			} else {
				monitor.beginTask(MessageFormat.format(EMFWorkbenchResourceHandler.getString("ProjectUtil_Delete_1"), new Object[] {root.getName()}), 1);				 //$NON-NLS-1$
			}
			if (deleteRoot) {
				error |= !root.delete();
				monitor.worked(1);
			}
			monitor.done();
		} else {
			error = true;
		}
		return error;
	}
	
	/**
	 * Add a clean resource changelistener.
	 * @param listener
	 * @param eventMask mask of event types to listen for in addition to ones that are necessary for clean. Use 0 if no additional ones.
	 * 
	 * @since 1.1.0
	 */
	public static void addCleanResourceChangeListener(CleanResourceChangeListener listener, int eventMask) {
		// PRE_BUILD: Handle Clean.
		// TODO Until https://bugs.eclipse.org/bugs/show_bug.cgi?id=101942 is fixed, we must do POST_BUILD, that will probably be sent because a clean will cause a build to occur which should cause a delta.
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, eventMask | IResourceChangeEvent.POST_BUILD);		
	}
	
	/**
	 * A resource listener that can be used in addition to listen for Clean requests and process them.
	 * <p>
	 * Use <code>{@link IResourceChangeEvent#PRE_BUILD}</code> when adding as listener to get the
	 * clean events.
	 * <p>
	 * <b>Note</b> : TODO Until https://bugs.eclipse.org/bugs/show_bug.cgi?id=101942 is fixed, you must do POST_BUILD, that will probably be sent because a clean will cause a build to occur which should cause a delta.
	 * @since 1.1.0
	 */
	public abstract static class CleanResourceChangeListener implements IResourceChangeListener {
		
		public void resourceChanged(IResourceChangeEvent event) {
			// Subclasses can override this to handle more events than just clean.
			if (event.getBuildKind() == IncrementalProjectBuilder.CLEAN_BUILD) {
				if (event.getSource() instanceof IProject)
					cleanProject((IProject) event.getSource());
				else if (event.getSource() instanceof IWorkspace)
					cleanAll();				
			}
		}

		/**
		 * Clear out the project.
		 * @param project
		 * 
		 * @since 1.1.0
		 */
		protected abstract void cleanProject(IProject project);

		/**
		 * Clean all.
		 * <p>
		 * By default this will simply call a clean project on each open project. Subclasses should override and either
		 * add more function to clear out non-project data and then call super. Or if they can handle all of the projects
		 * in a faster way, then can completely handle this.
		 * 
		 * @since 1.1.0
		 */
		protected void cleanAll() {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (int i = 0; i < projects.length; i++) {
				IProject project = projects[i];
				if (project.isOpen()) {
					cleanProject(project);
				}
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
				names[i] = extensions[i].getContributor().getName();
		}
		return names;
	}

}
