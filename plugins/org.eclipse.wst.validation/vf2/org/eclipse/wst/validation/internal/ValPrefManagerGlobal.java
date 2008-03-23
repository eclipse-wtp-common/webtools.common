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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.MessageSeveritySetting;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.model.FilterGroup;
import org.eclipse.wst.validation.internal.model.FilterRule;
import org.eclipse.wst.validation.internal.model.GlobalPreferences;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * A class that knows how to manage the global persisted validation settings.
 * @author karasiuk
 */
public class ValPrefManagerGlobal {
	
	/** Version of the framework properties. */
	public final static int frameworkVersion = 2;
	
	private List<IValChangedListener> _listeners = new LinkedList<IValChangedListener>();
	private static ValPrefManagerGlobal _me;
	
	private ValPrefManagerGlobal(){}
	
	public static ValPrefManagerGlobal getDefault(){
		if (_me == null)_me = new ValPrefManagerGlobal();
		return _me;
	}
	
	public void addListener(IValChangedListener listener){
		if (_listeners.contains(listener))return;
		_listeners.add(listener);
	}
	
	public void removeListener(IValChangedListener listener){
		_listeners.remove(listener);
	}
	
	private void updateListeners(){
		for (IValChangedListener cl : _listeners)cl.validatorsForProjectChanged(null); 
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
	public boolean loadPreferences(Validator[] val) {
	
		try {
			IEclipsePreferences pref = ValidationFramework.getDefault().getPreferenceStore();
			if (!pref.nodeExists(PrefConstants.filters))return false;
		
			Preferences filters = pref.node(PrefConstants.filters);
			for (Validator v : val){
				String id = v.getId();
				if (filters.nodeExists(id)){
					Preferences vp = filters.node(id);
					loadPreferences(v, vp);
				}
			}			
		}
		catch (Exception e){
			ValidationPlugin.getPlugin().handleException(e);
			return false;
		}
		
		return true;
	}
	
	/**
	 * The only valid way to get the global preferences is through the ValManager.
	 * 
	 * @see ValManager#getGlobalPreferences()
	 */
	public void loadGlobalPreferences(GlobalPreferences gp) {
		IEclipsePreferences pref = ValidationFramework.getDefault().getPreferenceStore();
		gp.setSaveAutomatically(pref.getBoolean(PrefConstants.saveAuto, GlobalPreferences.DefaultAutoSave));
		gp.setDisableAllValidation(pref.getBoolean(PrefConstants.suspend, GlobalPreferences.DefaultSuspend));
		gp.setConfirmDialog(pref.getBoolean(PrefConstants.confirmDialog, GlobalPreferences.DefaultConfirm));
		gp.setOverride(pref.getBoolean(PrefConstants.override, GlobalPreferences.DefaultOverride));
		gp.setStateTimeStamp(pref.getLong(PrefConstants.stateTS, 0));
	}
	
	/**
	 * Load the preferences for a validator.
	 * 
	 * @param v the validator that is being built up
	 * @param p the node in the preference tree for the validator, 
	 * 	e.g. /instance/validator-framework-id/filters/validator-id
	 * 
	 * @throws BackingStoreException
	 */
	static void loadPreferences(Validator v, Preferences p) throws BackingStoreException {
		v.setBuildValidation(p.getBoolean(PrefConstants.build, true));
		v.setManualValidation(p.getBoolean(PrefConstants.manual, true));
		v.setVersion(p.getInt(PrefConstants.version, 1));
		v.setDelegatingId(p.get(PrefConstants.delegate, null));
		
		Validator.V2 v2 = v.asV2Validator();
		if (v2 == null)return;
		if (!p.nodeExists(PrefConstants.groups))return;
		
		Preferences groupNode = p.node(PrefConstants.groups);
		for (String groupName : groupNode.childrenNames()){
			Preferences group = groupNode.node(groupName);
			String type = group.get(PrefConstants.type, null);
			if (type == null)throw new IllegalStateException(ValMessages.ErrGroupNoType);
			FilterGroup fg = FilterGroup.create(type);
			if (fg == null)throw new IllegalStateException(NLS.bind(ValMessages.ErrGroupInvalidType, type));
			v2.add(fg);
			
			if (group.nodeExists(PrefConstants.rules)){
				Preferences ruleNode = group.node(PrefConstants.rules);
				for (String ruleName : ruleNode.childrenNames()){
					Preferences rule = ruleNode.node(ruleName);
					FilterRule fr = FilterRule.create(rule.get(PrefConstants.ruleType, null));
					if (fr != null){
						fr.load(rule);
						fg.add(fr);
					}
				}
			}
		}		
	}
	
	/** 
	 * Save the validator into the preference store, including it's filter settings.
	 * @param v
	 */
	private synchronized void save(Validator v){
		try {
			IEclipsePreferences prefs = ValidationFramework.getDefault().getPreferenceStore();
			Preferences filters = prefs.node(PrefConstants.filters);
			Preferences vp = filters.node(v.getId());
			vp.removeNode();
			save(v, prefs);
			prefs.flush();

		}
		catch (BackingStoreException e){
			ValidationPlugin.getPlugin().handleException(e);
		}
		
	}
	
	/**
	 * Save the validator into the preference store. 
	 * 
	 * @param validator the validator being saved.
	 * 
	 * @param root the top of the preference tree for validators, i.e. 
	 * /instance/validator-framework-id/ for workspace validators and / for project validators.
	 */
	static void save(Validator validator, Preferences root) {
		Preferences filters = root.node(PrefConstants.filters);
		Preferences vp = filters.node(validator.getId());
		vp.putBoolean(PrefConstants.build, validator.isBuildValidation());
		vp.putBoolean(PrefConstants.manual, validator.isManualValidation());
		vp.putInt(PrefConstants.version, validator.getVersion());
		if (validator.getDelegatingId() != null)vp.put(PrefConstants.delegate, validator.getDelegatingId());
		
		Collection<MessageSeveritySetting> msgs = validator.getMessageSettings().values();
		if (msgs.size() > 0){
			Preferences msgsNode = filters.parent().node(PrefConstants.msgs);
			Preferences valNode = msgsNode.node(validator.getId());
			for (MessageSeveritySetting ms : msgs){
				valNode.putInt(ms.getId(), ms.getCurrent().ordinal());
			}			
		}

		Validator.V2 v2 = validator.asV2Validator();
		if (v2 == null)return;
		
		FilterGroup[] groups = v2.getGroups();
		Preferences group = vp.node(PrefConstants.groups);
		for (int i=0; i<groups.length; i++){
			Preferences gid= group.node(String.valueOf(i));
			gid.put(PrefConstants.type, groups[i].getType());
			FilterRule[] rules = groups[i].getRules();
			Preferences r = gid.node(PrefConstants.rules);
			for (int j=0; j<rules.length; j++){
				Preferences rid= r.node(String.valueOf(j));
				rules[j].save(rid);
			}
		}
	}
	
	public void saveAsPrefs(Validator[] val) {
		try {
			IEclipsePreferences pref = ValidationFramework.getDefault().getPreferenceStore();
			Preferences filters = pref.node(PrefConstants.filters);
			filters.removeNode();
			
			filters = pref.node(PrefConstants.filters);
			for (Validator v : val)save(v, pref);
			pref.flush();
			updateListeners();
		}
		catch (BackingStoreException e){
			throw new RuntimeException(e);
		}
	}

	
	/**
	 * Save the global preferences and the validators.
	 */
	public synchronized void savePreferences(GlobalPreferences gp, Validator[] validators){
		try {
			IEclipsePreferences prefs = ValidationFramework.getDefault().getPreferenceStore();
			prefs.putBoolean(PrefConstants.saveAuto, gp.getSaveAutomatically());
			prefs.putBoolean(PrefConstants.suspend, gp.getDisableAllValidation());
			prefs.putLong(PrefConstants.stateTS, gp.getStateTimeStamp());
			prefs.putBoolean(PrefConstants.confirmDialog, gp.getConfirmDialog());
			prefs.putBoolean(PrefConstants.override, gp.getOverride());
			for (Validator v : validators)save(v);
			prefs.flush();
			updateListeners();
		}
		catch (BackingStoreException e){
			ValidationPlugin.getPlugin().handleException(e);
		}
	}

	/**
	 * Update any message preferences in the map.
	 * @param validator
	 * @param settings
	 */
	public void loadMessages(Validator validator, Map<String, MessageSeveritySetting> settings) {
		IEclipsePreferences pref = ValidationFramework.getDefault().getPreferenceStore();
		try {
			loadMessageSettings(validator, settings, pref);
		}
		catch (BackingStoreException e){
			ValidationPlugin.getPlugin().handleException(e);
		}
	}
		
	static void loadMessageSettings(Validator val, Map<String, MessageSeveritySetting> settings, Preferences prefs) 
		throws BackingStoreException {
		if (!prefs.nodeExists(PrefConstants.msgs))return;
		
		Preferences msgs = prefs.node(PrefConstants.msgs); 
		if (!msgs.nodeExists(val.getId()))return;
		
		Preferences valPrefs = msgs.node(val.getId());
		for (String key : valPrefs.keys()){
			int sev = valPrefs.getInt(key, -1);
			if (sev != -1){
				MessageSeveritySetting ms = settings.get(key);
				if (ms != null)ms.setCurrent(MessageSeveritySetting.Severity.values()[sev]);
			}
		}		
	}

	/**
	 * Save whether the validator is enabled or not. 
	 * @param validator
	 * @param prefs up to the filter part of the preference tree
	 */
//	private void saveShallowPreference(Validator validator, Preferences prefs) {
//		if (validator.asV2Validator() == null)return;
//		Preferences val = prefs.node(validator.getId());
//		val.putBoolean(PrefConstants.build, validator.isBuildValidation());
//		val.putBoolean(PrefConstants.manual, validator.isManualValidation());
//		val.putInt(PrefConstants.version, validator.getVersion());
//	}
	
//	/**
//	 * Load the customized message settings from the preference store.
//	 * @param messageSettings
//	 */
//	public void loadMessageSettings(Validator val, MessageCategory[] messageSettings) {
//		try {
//			loadMessageSettings(val, messageSettings, ValidationFramework.getDefault().getPreferenceStore());
//		}
//		catch (Exception e){
//			ValidationPlugin.getPlugin().handleException(e);
//		}
//	}

}
