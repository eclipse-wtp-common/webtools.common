package org.eclipse.wst.validation.internal;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.osgi.util.NLS;
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
	
	public ValPrefManagerGlobal(){}
			
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
			IEclipsePreferences pref = new InstanceScope().getNode(ValidationPlugin.PLUGIN_ID);
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
	
	public GlobalPreferences loadGlobalPreferences() {
		IEclipsePreferences pref = ValidationFramework.getDefault().getPreferenceStore();
		GlobalPreferences gp = new GlobalPreferences();
		gp.setSaveAutomatically(pref.getBoolean(PrefConstants.saveAuto, GlobalPreferences.DefaultAutoSave));
		gp.setDisableAllValidation(pref.getBoolean(PrefConstants.suspend, GlobalPreferences.DefaultSuspend));
		gp.setConfirmDialog(pref.getBoolean(PrefConstants.confirmDialog, GlobalPreferences.DefaultConfirm));
		gp.setOverride(pref.getBoolean(PrefConstants.override, GlobalPreferences.DefaultOverride));
		gp.setStateTimeStamp(pref.getLong(PrefConstants.stateTS, 0));
		return gp;
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
	public synchronized void save(Validator v){
		try {
			IEclipsePreferences prefs = ValidationFramework.getDefault().getPreferenceStore();
			Preferences filters = prefs.node(PrefConstants.filters);
			Preferences vp = filters.node(v.getId());
			vp.removeNode();
			save(v, filters);
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
	 * @param filters the filters node in the preference tree, i.e. 
	 * /instance/validator-framework-id/filters
	 */
	private void save(Validator validator, Preferences filters) {
		Preferences vp = filters.node(validator.getId());
		vp.putBoolean(PrefConstants.build, validator.isBuildValidation());
		vp.putBoolean(PrefConstants.manual, validator.isManualValidation());
		vp.putInt(PrefConstants.version, validator.getVersion());
		if (validator.getDelegatingId() != null)vp.put(PrefConstants.delegate, validator.getDelegatingId());
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
			for (Validator v : val)save(v, filters);
			pref.flush();
		}
		catch (BackingStoreException e){
			throw new RuntimeException(e);
		}
	}

	
	/**
	 * Save whether the validator are enabled or not.
	 */
	public synchronized void saveShallowPreferences(GlobalPreferences gp, Validator[] validators){
		try {
			IEclipsePreferences prefs = ValidationFramework.getDefault().getPreferenceStore();
			prefs.putBoolean(PrefConstants.saveAuto, gp.getSaveAutomatically());
			prefs.putBoolean(PrefConstants.suspend, gp.getDisableAllValidation());
			prefs.putLong(PrefConstants.stateTS, gp.getStateTimeStamp());
			prefs.putBoolean(PrefConstants.confirmDialog, gp.getConfirmDialog());
			prefs.putBoolean(PrefConstants.override, gp.getOverride());
			Preferences filters = prefs.node(PrefConstants.filters);
			for (Validator v : validators)saveShallowPreference(v, filters);
			prefs.flush();
		}
		catch (BackingStoreException e){
			ValidationPlugin.getPlugin().handleException(e);
		}
	}

	/**
	 * Save whether the validator is enabled or not. 
	 * @param validator
	 * @param prefs up to the filter part of the preference tree
	 */
	private void saveShallowPreference(Validator validator, Preferences prefs) {
		if (validator.asV2Validator() == null)return;
		Preferences val = prefs.node(validator.getId());
		val.putBoolean(PrefConstants.build, validator.isBuildValidation());
		val.putBoolean(PrefConstants.manual, validator.isManualValidation());
		val.putInt(PrefConstants.version, validator.getVersion());
	}

}
