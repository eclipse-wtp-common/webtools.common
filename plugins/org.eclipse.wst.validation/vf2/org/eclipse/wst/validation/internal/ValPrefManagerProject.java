/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.util.HashMap;
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
import org.eclipse.wst.validation.Validator.V2;
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
	
	private List<Validator> _validators;
	
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
		for (IValChangedListener cl : _listeners)cl.validatorsForProjectChanged(project, true); 
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
	 * Answer the v2 validators that have been overridden by the global
	 * preferences.
	 * 
	 * @param baseValidators
	 *            V2 validators from the extension points, and customized by any
	 *            global preferences.
	 */
	public List<Validator> getValidators(Map<String, Validator> baseValidators) throws BackingStoreException {
		List<Validator> vals = _validators;
		if (vals == null){
			vals = loadValidators(baseValidators);
			_validators = vals;
		}
		return vals;
	}
	
	/**
	 * Load the validators from the preference store.
	 * 
	 * @param baseValidators
	 *            V2 validators from the extension points, and customized by any
	 *            global preferences.
	 * @return the validators that are in the project preference file, but have
	 *         only been configured to the global preference level. That is they have not had
	 *         any project level customizations applied yet.
	 */
	private List<Validator> loadValidators(Map<String, Validator> baseValidators) throws BackingStoreException {
		LinkedList<Validator> list = new LinkedList<Validator>();
		IEclipsePreferences pref = getPreferences();
		if (pref.nodeExists(PrefConstants.vals)){
			Preferences vals = pref.node(PrefConstants.vals);
			for (String id : vals.childrenNames()){
				Validator base = baseValidators.get(id);
				Validator v = ValPrefManagerGlobal.loadValidator(id, vals, base);
				if (v != null){
					V2 v2 = v.asV2Validator();
					if (v2 != null)v2.setLevel(Validator.Level.Project);					
					list.add(v);
				}
			}
		}
		return list;
	}
	
	/**
	 * Update the project preferences from the preference store.
	 * @return false if the project does not have any specific preferences.
	 */
	public boolean loadProjectPreferencesShallow(ProjectPreferences pp) {
		IEclipsePreferences pref = getPreferences();
		
		if (pref == null)return false;
		int version = pref.getInt(PrefConstants.frameworkVersion, 0);
		if (version == 0)return false;
		
		if (version != ValPrefManagerGlobal.frameworkVersion)ValPrefManagerGlobal.migrate(version, pref);

		pp.setOverride(pref.getBoolean(PrefConstants.override, ProjectPreferences.DefaultOverride));
		pp.setSuspend(pref.getBoolean(PrefConstants.suspend, ProjectPreferences.DefaultSuspend));
		return true;
	}
	
	
	//FIXME I suspect that this method should be removed	
	/**
	 * Update the project preferences from the preference store.
	 * @return false if the project does not have any specific preferences.
	 */
	public boolean loadProjectPreferences(ProjectPreferences pp, Map<String, Validator> baseValidators) 
		throws BackingStoreException {
		
		if (!loadProjectPreferencesShallow(pp))return false;
		
		IEclipsePreferences pref = getPreferences();
		if (!pref.nodeExists(PrefConstants.vals))return true;
		
		Preferences vp = pref.node(PrefConstants.vals);
		List<Validator> list = new LinkedList<Validator>();
		for (String id : vp.childrenNames()){
			Validator base = baseValidators.get(id);
			Validator v = ValPrefManagerGlobal.loadValidator(id, vp, base);
			if (v != null){
				V2 v2 = v.asV2Validator();
				if (v2 != null)v2.setLevel(Validator.Level.Project);
				list.add(v);
			}
		}
		Validator[] vals = new Validator[list.size()];
		list.toArray(vals);
		pp.setValidators(vals);
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
		Preferences vals = pref.node(PrefConstants.vals);
		try {
			Validator[] workspaceVals = ValManager.getDefault().getValidators();
			Map<String, Validator> base = new HashMap<String, Validator>(workspaceVals.length);
			for (Validator v : validators)ValPrefManagerGlobal.save(v, vals, base);
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
