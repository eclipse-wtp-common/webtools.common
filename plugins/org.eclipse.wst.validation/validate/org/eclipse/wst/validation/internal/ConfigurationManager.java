/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;


/**
 * Only the validation framework can use this class.
 */
public final class ConfigurationManager implements ConfigurationConstants {
	private static ConfigurationManager _instance = null;

	private ConfigurationManager() {}

	public static ConfigurationManager getManager() {
		if (_instance == null) {
			_instance = new ConfigurationManager();
		}
		return _instance;
	}

	/**
	 * Given a validation marker, return the fully-qualified class name of the validator who owns
	 * the message. If the validator cannot be found or if the marker is not a validation marker,
	 * return null.
	 */
	public String getValidator(IMarker marker) {
		if (marker == null)return null;

		try {
			if (!marker.getType().equals(VALIDATION_MARKER))return null;

			Object attrib = marker.getAttribute(VALIDATION_MARKER_OWNER);
			if (attrib == null)return null;
			return attrib.toString();
		} catch (CoreException e) {
			ValidationPlugin.getPlugin().handleException(e);
			return null;
		}
	}

	/**
	 * Return true if the given marker is a validation marker. Otherwise return false.
	 */
	public boolean isValidationMarker(IMarker marker) {
		if (marker == null)return false;

		try {
			return marker.getType().equals(VALIDATION_MARKER);
		} catch (CoreException e) {
			ValidationPlugin.getPlugin().handleException(e);
			return false;
		}
	}

	/**
	 * This method is for use by the TVT Validation plug-in ONLY!!! No code should access the
	 * validation markers in the list directly except for the validation framework and the TVT
	 * Validation plug-in.
	 */
	public void removeAllValidationMarkers(IProject project) {
		if ((project == null) || (!project.isOpen()))return;

		try {
			project.deleteMarkers(VALIDATION_MARKER, false, DEPTH_INFINITE); // false means only
			// consider VALIDATION_MARKER, not variants of VALIDATION_MARKER. 
			//Since addTask only adds VALIDATION_MARKER, we don't need to consider its subtypes.
		} catch (CoreException e) {
			ValidationPlugin.getPlugin().handleException(e);
		}
	}

	/**
	 * This method returns the global preferences for the workspace.
	 */
	public GlobalConfiguration getGlobalConfiguration() throws InvocationTargetException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		GlobalConfiguration gp = null;
		try {
			gp = (GlobalConfiguration) root.getSessionProperty(USER_PREFERENCE);
			if (gp == null)gp = getGlobalConfiguration(root);
		} catch (CoreException exc) {
			throw new InvocationTargetException(exc, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_RETRIEVE, new String[]{root.getName()}));
		}
		return gp;
	}
	
	private synchronized GlobalConfiguration getGlobalConfiguration(IWorkspaceRoot root) throws InvocationTargetException {
		GlobalConfiguration gp = null;
		try {
			gp = (GlobalConfiguration) root.getSessionProperty(USER_PREFERENCE);
			if (gp == null) {
				gp = new GlobalConfiguration(root);
				
				Preferences prefs = ValidationPlugin.getPlugin().getPluginPreferences();
				if(prefs != null)
					prefs.addPropertyChangeListener(gp);
				
				gp.getVersion(); // initialize the configuration's version attribute
				gp.load(); // initialize this instance from the stored values
				gp.passivate(); // store this instance as a property on the IResource
			}
			return gp;
		} catch (CoreException exc) {
			throw new InvocationTargetException(exc, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_RETRIEVE, new String[]{root.getName()}));
		}
	}

	public ProjectConfiguration getProjectConfiguration(IProject project) throws InvocationTargetException {
		ProjectConfiguration prjp = null;
		try {
			prjp = (ProjectConfiguration) project.getSessionProperty(USER_PREFERENCE);
			if (prjp == null || !prjp.getResource().exists()) {
				prjp = new ProjectConfiguration(project);
				prjp.getVersion(); // initialize the configuration's version attribute
				prjp.load(); // initialize this instance from the stored values
				prjp.passivate(); // store this instance as a property on the IResource
			}
			return prjp;
		} catch (CoreException exc) {
			throw new InvocationTargetException(exc, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_RETRIEVE, new String[]{project.getName()}));
		}
	}

	public ProjectConfiguration getProjectConfigurationWithoutMigrate(IProject project) throws InvocationTargetException {
		ProjectConfiguration prjp = null;
		try {
			prjp = (ProjectConfiguration) project.getSessionProperty(USER_PREFERENCE);
			if (prjp == null || !prjp.getResource().exists()) {
				prjp = new ProjectConfiguration(project);
				prjp.getVersion(); // initialize the configuration's version attribute
				prjp.load(); // initialize this instance from the stored values
				prjp.passivate(); // store this instance as a property on the IResource
			}
			return prjp;
		} catch (CoreException exc) {
			throw new InvocationTargetException(exc, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_RETRIEVE, new String[]{project.getName()}));
		}
	}


	/**
	 * The nature of the project has changed; update the enabled validators on the project.
	 * 
	 * @deprecated this method doesn't do anything.
	 */
	public void resetProjectNature(IProject project) throws InvocationTargetException {
	}

	public void closing(IProject project) {
		try {
			if (isMigrated(project)) {
				ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
				if(!prjp.useGlobalPreference())prjp.store();
			}
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			if (e.getTargetException() != null)
				ValidationPlugin.getPlugin().handleException(e.getTargetException());
		}
	}

	/**
	 * @deprecated this method does not do anything.
	 * @param project
	 */
	public void deleting(IProject project) {
	}

	/**
	 * @deprecated this method does not do anything.
	 * @param project
	 */
	public void opening(IProject project) {
		// Do not load or migrate the project in this method; let the getConfiguration(IProject)
		// method do that. Do not load the project before it's necessary.
	}

	/**
	 * Return true if the global preferences are at the current level of metadata, false otherwise.
	 */
	public boolean isGlobalMigrated() throws InvocationTargetException {
		IWorkspaceRoot root = ValidationConfiguration.getRoot();
		if (root == null)return false;

		try {
			GlobalConfiguration gp = (GlobalConfiguration) root.getSessionProperty(USER_PREFERENCE);
			if (gp != null) {
				return gp.isVersionCurrent();
			}

			String serializedPrjp = root.getPersistentProperty(USER_PREFERENCE);
			if (serializedPrjp != null) {
				gp = new GlobalConfiguration(root);
				gp.getVersion(); // initialize the configuration's version attribute
				return gp.isVersionCurrent();
			}
		} catch (CoreException exc) {
			throw new InvocationTargetException(exc, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_RETRIEVE, new String[]{root.getName()}));
		}

		return false;
	}

	/**
	 * Return true if the given project has the current level of metadata, false otherwise.
	 */
	public boolean isMigrated(IProject project) throws InvocationTargetException {
		if (project == null)return false;
		
		try {
			if (project.isAccessible()) {
				ProjectConfiguration prjp = (ProjectConfiguration) project.getSessionProperty(USER_PREFERENCE);
				if (prjp != null)return prjp.isVersionCurrent();
				
				String serializedPrjp = project.getPersistentProperty(USER_PREFERENCE);
				if (serializedPrjp != null) {
					prjp = new ProjectConfiguration(project);
					prjp.getVersion(); 
					return prjp.isVersionCurrent();
				}
			}
		} catch (CoreException exc) {
			throw new InvocationTargetException(exc, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_RETRIEVE, new String[]{project.getName()}));
		}
		return false;
	}

	/**
	 * Answer the appropriate configuration based on whether the project has overridden the configuration
	 * or not. If the project exists and is allowed to override the global configuration answer the
	 * project configuration, otherwise answer the global configuration.
	 * @param project it can be null
	 */
	public ValidationConfiguration getConfiguration(IProject project) throws InvocationTargetException {
		if (project == null)return getGlobalConfiguration();
		ProjectConfiguration pc = getProjectConfiguration(project);
		if (pc != null && !pc.useGlobalPreference())return pc;
		return getGlobalConfiguration();
	}

}
