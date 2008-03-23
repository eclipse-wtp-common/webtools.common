/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.wst.validation.MessageSeveritySetting;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.model.ProjectPreferences;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * A class that knows how to manage the project level persisted validation settings.
 * @author karasiuk
 *
 */
public class ValPrefManagerProject {
	
	private IProject	_project;
	private static List<IValChangedListener> _listeners = new LinkedList<IValChangedListener>();

	
	public ValPrefManagerProject(IProject project){
		assert project != null;
		_project = project;
	}
	
	public static void addListener(IValChangedListener listener){
		if (_listeners.contains(listener))return;
		_listeners.add(listener);
	}
	
	public static void removeListener(IValChangedListener listener){
		_listeners.remove(listener);
	}
	
	private static void updateListeners(IProject project){
		for (IValChangedListener cl : _listeners)cl.validatorsForProjectChanged(project); 
	}

	
	/**
	 * Answer whether or not this project has validation settings.
	 *   
	 * @return true if it has settings. This does not mean that the settings are enabled, only that it
	 * has settings.
	 * 
	 * @see ValManager#hasEnabledProjectPreferences(IProject)
	 */
	public boolean hasProjectSpecificSettings(){
		IEclipsePreferences pref = getPreferences();
		
		if (pref == null)return false;
		int version = pref.getInt(PrefConstants.frameworkVersion, 0);
		if (version == 0)return false;

		return true;
	}
		
	/**
	 * Update the project preferences from the preference store.
	 * @return false if the project does not have any specific preferences.
	 */
	public boolean loadProjectPreferences(ProjectPreferences pp) {
		IEclipsePreferences pref = getPreferences();
		
		if (pref == null)return false;
		int version = pref.getInt(PrefConstants.frameworkVersion, 0);
		if (version == 0)return false;

		pp.setOverride(pref.getBoolean(PrefConstants.override, ProjectPreferences.DefaultOverride));
		pp.setSuspend(pref.getBoolean(PrefConstants.suspend, ProjectPreferences.DefaultSuspend));
		
		Validator[] vals = ValManager.getDefault().getValidators2(_project);
		loadPreferences(vals, pref);
		pp.setValidators(vals);
		return true;
	}
	
	/**
	 * Update the validator filters from the preference store.
	 *  
	 * @param val
	 * 
	 * @return false if there are no preferences, that means that the user has never changed any
	 * of the default settings. Also answer false if there was some sort of error, which essentially
	 * means that the preferences aren't valid for whatever reason.   
	 */
	private boolean loadPreferences(Validator[] val, IEclipsePreferences pref) {
	
		try {
			if (!pref.nodeExists(PrefConstants.filters))return false;
		
			Preferences filters = pref.node(PrefConstants.filters);
			for (Validator v : val){
				String id = v.getId();
				if (filters.nodeExists(id)){
					Preferences vp = filters.node(id);
					ValPrefManagerGlobal.loadPreferences(v, vp);
				}
			}			
		}
		catch (Exception e){
			ValidationPlugin.getPlugin().handleException(e);
			return false;
		}
		
		return true;
	}


	private IEclipsePreferences getPreferences() {
		IScopeContext projectContext = new ProjectScope(_project);
		IEclipsePreferences pref = projectContext.getNode(ValidationPlugin.PLUGIN_ID);
		return pref;
	}

	public void savePreferences(ProjectPreferences projectPreferences, Validator[] validators) {
		IEclipsePreferences pref = getPreferences();
		pref.putBoolean(PrefConstants.suspend, projectPreferences.getSuspend());
		pref.putBoolean(PrefConstants.override, projectPreferences.getOverride());
		pref.putInt(PrefConstants.frameworkVersion, ValPrefManagerGlobal.frameworkVersion);
		Preferences filters = pref.node(PrefConstants.filters);
		try {
			filters.removeNode();
			filters = pref.node(PrefConstants.filters);
		}
		catch (BackingStoreException e){
			ValidationPlugin.getPlugin().handleException(e);
		}
		for (Validator v : validators)ValPrefManagerGlobal.save(v, pref);
		try {
			pref.flush();
			ProjectConfiguration pc = ConfigurationManager.getManager()
				.getProjectConfiguration(projectPreferences.getProject());
			pc.setEnabledBuildValidators(getEnabledBuildValidators(validators));
			pc.setEnabledManualValidators(getEnabledManualValidators(validators));
			pc.passivate();
			pc.store();
			updateListeners(_project);
		}
		catch (Exception e){
			ValidationPlugin.getPlugin().handleException(e);
		}		
	}
	
	/**
	 * Answer all the V1 validators that are enabled for build.
	 * @return
	 */
	private Set<ValidatorMetaData> getEnabledBuildValidators(Validator[] validators) {
		Set<ValidatorMetaData> set = new HashSet<ValidatorMetaData>(50);
		for (Validator v : validators){
			if (v.isBuildValidation()){
				Validator.V1 v1 = v.asV1Validator();
				if (v1 != null)set.add(v1.getVmd());
			}
		}
		return set;
	}
	
	/**
	 * Answer all the V1 validators that are enabled for manual validation.
	 * @return
	 */
	private Set<ValidatorMetaData> getEnabledManualValidators(Validator[] validators) {
		Set<ValidatorMetaData> set = new HashSet<ValidatorMetaData>(50);
		for (Validator v : validators){
			if (v.isManualValidation()){
				Validator.V1 v1 = v.asV1Validator();
				if (v1 != null)set.add(v1.getVmd());
			}
		}
		return set;
	}
	
	public void loadMessages(Validator validator, Map<String, MessageSeveritySetting> settings) {
		try {
			ValPrefManagerGlobal.loadMessageSettings(validator, settings, getPreferences());
		}
		catch (BackingStoreException e){
			ValidationPlugin.getPlugin().handleException(e);
		}		
	}

}
