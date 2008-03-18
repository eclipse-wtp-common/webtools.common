package org.eclipse.wst.validation.internal.model;

/**
 * This class represents the global Preferences as set on the Validation Preferences page.
 */
public class GlobalPreferences {
	
	/** false - Default setting for the should all the validation be suspended setting. */ 
	public static final boolean DefaultSuspend = false;
	
	/** false - Default setting for the auto save setting. */ 
	public static final boolean DefaultAutoSave = false;
	
	/** false - Default setting for the confirm dialog setting. */ 
	public static final boolean DefaultConfirm = true;
	
	/** false - Default setting for letting projects override the global settings. */
	public static final boolean DefaultOverride = true;

	private boolean _disableAllValidation = DefaultSuspend;
	private boolean _saveAutomatically = DefaultAutoSave;
	private boolean _confirmDialog = DefaultConfirm;
	private boolean _override = DefaultOverride;
	
	/** The plug-in state time stamp. */
	private long	_stateTimeStamp;
	
	/**
	 * The only valid way to get the global preferences is through the ValManager.
	 * 
	 * @see org.eclipse.wst.validation.internal.ValManager#getGlobalPreferences()
	 */
	public GlobalPreferences(){	}
	
	public boolean getSaveAutomatically() {
		return _saveAutomatically;
	}

	public void setSaveAutomatically(boolean saveAutomatically) {
		_saveAutomatically = saveAutomatically;
	}

	/**
	 * Answer if all validation has been disabled.
	 */
	public boolean getDisableAllValidation() {
		return _disableAllValidation;
	}

	public void setDisableAllValidation(boolean disableAllValidation) {
		_disableAllValidation = disableAllValidation;
	}

	/**
	 * Reset all the global preferences to their default settings. This doesn't reset
	 * the individual validators.
	 */
	public void resetToDefault() {
		_disableAllValidation = DefaultSuspend;
		_saveAutomatically = DefaultAutoSave;
		_confirmDialog = DefaultConfirm;
		_override = DefaultOverride;
	}

	public boolean getConfirmDialog() {
		return _confirmDialog;
	}

	public void setConfirmDialog(boolean confirmDialog) {
		_confirmDialog = confirmDialog;
	}

	public long getStateTimeStamp() {
		return _stateTimeStamp;
	}

	public void setStateTimeStamp(long stateTimeStamp) {
		_stateTimeStamp = stateTimeStamp;
	}

	/** Answer whether or not projects are allowed to override the global preferences. */
	public boolean getOverride() {
		return _override;
	}

	public void setOverride(boolean override) {
		_override = override;
	}

}
