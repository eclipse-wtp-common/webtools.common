package org.eclipse.wst.validation.internal.model;

/**
 * A mutable object that is used to initialize a GlobalPreference.
 * @author karasiuk
 *
 */
public class GlobalPreferencesValues {
	public boolean disableAllValidation = GlobalPreferences.DefaultSuspend;
	public boolean saveAutomatically = GlobalPreferences.DefaultAutoSave;
	public boolean confirmDialog = GlobalPreferences.DefaultConfirm;
	public boolean override = GlobalPreferences.DefaultOverride;
	
	/** The plug-in state time stamp. */
	public long	stateTimeStamp;
	
	/** The incoming version of the framework. This is used to determine if a migration is needed.*/
	public int		version;
	
	public GlobalPreferencesValues(){
		
	}

}
