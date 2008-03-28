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
package org.eclipse.wst.validation.internal.model;

/**
 * This class represents the global Preferences as set on the Validation Preferences page. It doesn't
 * hold any of the individual validator settings, just the global check boxes.
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
	
	/** 2 - The version of the framework meta data, if an explicit version isn't found. */
	public static final int DefaultFrameworkVersion = 2;

	private boolean _disableAllValidation = DefaultSuspend;
	private boolean _saveAutomatically = DefaultAutoSave;
	private boolean _confirmDialog = DefaultConfirm;
	private boolean _override = DefaultOverride;
	
	/** The plug-in state time stamp. */
	private long	_stateTimeStamp;
	
	/** The incoming version of the framework. This is used to determine if a migration is needed.*/
	private int		_version;
	
	/** Has a setting changed that could effect which validators get called? */
	private boolean	_configChange;
	
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
		if (_disableAllValidation != disableAllValidation){
			_configChange = true;
			_disableAllValidation = disableAllValidation;
		}
	}

	/**
	 * Reset all the global preferences to their default settings. This doesn't reset
	 * the individual validators.
	 */
	public void resetToDefault() {
		setDisableAllValidation(DefaultSuspend);
		_saveAutomatically = DefaultAutoSave;
		_confirmDialog = DefaultConfirm;
		setOverride(DefaultOverride);
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
		if (_override != override){
			_configChange = true;
			_override = override;
		}
	}

	public int getVersion() {
		return _version;
	}

	public void setVersion(int version) {
		_version = version;
	}

	public boolean isConfigChange() {
		return _configChange;
	}

	public void setConfigChange(boolean configChange) {
		_configChange = configChange;
	}

}
