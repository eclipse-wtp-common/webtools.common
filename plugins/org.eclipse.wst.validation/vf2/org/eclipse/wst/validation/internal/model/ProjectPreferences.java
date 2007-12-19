package org.eclipse.wst.validation.internal.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValPrefManagerProject;

/**
 * Validation preferences for a particular project.
 * @author karasiuk
 *
 */
public class ProjectPreferences {
	/** false - Default setting for the should all the validation be suspended setting. */ 
	public static final boolean DefaultSuspend = false;
	
	/** false - Default setting for letting projects override the global settings. */
	public static final boolean DefaultOverride = false;
	
	private IProject	_project;

	private boolean 	_override = DefaultOverride;
	private boolean		_suspend = DefaultSuspend;
	
	private Validator[]	_validators;
	
	public ProjectPreferences(IProject project){
		_project = project;
	}
	
	public boolean getOverride() {
		return _override;
	}
	public void setOverride(boolean override) {
		_override = override;
	}
	public boolean getSuspend() {
		return _suspend;
	}
	public void setSuspend(boolean suspend) {
		_suspend = suspend;
	}
	
	public Validator[] getValidators() {
		Validator[] vals = _validators;
		if (vals == null){
			vals = init();
			_validators = vals;
		}
		return vals;
	}
	
	public void setValidators(Validator[] validators){
		_validators = validators;
	}
	
	private Validator[] init(){
		Validator[] vals = null;
		ValPrefManagerProject vpm = new ValPrefManagerProject(_project);
		if (vpm.hasProjectSpecificSettings()){
			vpm.loadProjectPreferences();			
		}
		else {
			vals = ValManager.getDefault().getValidators();
		}
		return vals;
	}

	public IProject getProject() {
		return _project;
	}

}
