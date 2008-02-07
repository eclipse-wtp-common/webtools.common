package org.eclipse.wst.validation.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.model.FilterGroup;
import org.eclipse.wst.validation.internal.model.FilterRule;
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
	
	public ValPrefManagerProject(IProject project){
		assert project != null;
		_project = project;
	}
	
	/**
	 * Answer whether or not this project has overridden the validation settings.  
	 * @return
	 */
	public boolean hasProjectSpecificSettings(){
		IEclipsePreferences pref = getPreferences();
		
		if (pref == null)return false;
		int version = pref.getInt(PrefConstants.frameworkVersion, 0);
		if (version == 0)return false;

		return true;
	}
		
	/**
	 * If the project has preferences answer them.
	 * @return null if the project does not have any specific preferences.
	 */
	public ProjectPreferences loadProjectPreferences() {
		IEclipsePreferences pref = getPreferences();
		
		if (pref == null)return null;
		int version = pref.getInt(PrefConstants.frameworkVersion, 0);
		if (version == 0)return null;

		ProjectPreferences pp = new ProjectPreferences(_project);
		pp.setOverride(pref.getBoolean(PrefConstants.override, ProjectPreferences.DefaultOverride));
		pp.setSuspend(pref.getBoolean(PrefConstants.suspend, ProjectPreferences.DefaultSuspend));
		
		Validator[] vals = ValManager.getDefault().getValidators(false, _project);
		loadPreferences(vals, pref);
		pp.setValidators(vals);
		return pp;
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
		for (Validator v : validators)savePreference(v, filters);
		try {
			pref.flush();
			ProjectConfiguration pc = ConfigurationManager.getManager()
				.getProjectConfiguration(projectPreferences.getProject());
			pc.setEnabledBuildValidators(getEnabledBuildValidators(validators));
			pc.setEnabledManualValidators(getEnabledManualValidators(validators));
			pc.passivate();
			pc.store();
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
	
	/**
	 * Save the validator preferences and filters. 
	 * @param validator
	 * @param prefs up to the filter part of the preference tree
	 */
	private void savePreference(Validator validator, Preferences prefs) {
		if (validator.asV2Validator() == null)return;
		Preferences val = prefs.node(validator.getId());
		val.putBoolean(PrefConstants.build, validator.isBuildValidation());
		val.putBoolean(PrefConstants.manual, validator.isManualValidation());
		val.putInt(PrefConstants.version, validator.getVersion());
		if (validator.getDelegatingId() != null)val.put(PrefConstants.delegate, validator.getDelegatingId());
		Validator.V2 v2 = validator.asV2Validator();
		if (v2 == null)return;
		
		FilterGroup[] groups = v2.getGroups();
		Preferences group = val.node(PrefConstants.groups);
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

}
