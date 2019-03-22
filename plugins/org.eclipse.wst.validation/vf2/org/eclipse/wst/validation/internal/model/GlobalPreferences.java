/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.model;

/**
 * This class represents the global Preferences as set on the Validation Preferences page. It doesn't
 * hold any of the individual validator settings, just the global check boxes. It is an immutable object.
 */
public final class GlobalPreferences {
	
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
	
	/** Bit masks for what has changed. */
	public final static int ConfirmDialogMask = 1;
	public final static int DisableAllValidationMask = 2;
	public final static int OverrideMask = 4;
	public final static int SaveAutomaticallyMask = 8;
	public final static int StateTimeStampMask = 16;
	public final static int VersionMask = 32;
	
	/**
	 * The changes that could affect what gets validated.
	 */
	public final static int BuildChangeMask = DisableAllValidationMask | OverrideMask;
	

	private final boolean _confirmDialog;
	private final boolean _disableAllValidation;
	private final boolean _override;
	private final boolean _saveAutomatically;
	
	/** The plug-in state time stamp. */
	private final long	_stateTimeStamp;
	
	/** The incoming version of the framework. This is used to determine if a migration is needed.*/
	private final int		_version;
			
	/**
	 * The only valid way to get the global preferences is through the ValManager.
	 * 
	 * @see org.eclipse.wst.validation.internal.ValManager#getGlobalPreferences()
	 */
	public GlobalPreferences(GlobalPreferencesValues gp) {
		_confirmDialog = gp.confirmDialog;
		_disableAllValidation = gp.disableAllValidation;
		_override = gp.override;
		_saveAutomatically = gp.saveAutomatically;
		_stateTimeStamp = gp.stateTimeStamp;
		_version = gp.version;
	}
	
	/**
	 * Answer a copy of the values.
	 * @return
	 */
	public GlobalPreferencesValues asValues(){
		GlobalPreferencesValues gp = new GlobalPreferencesValues();
		gp.confirmDialog = _confirmDialog;
		gp.disableAllValidation = _disableAllValidation;
		gp.override = _override;
		gp.saveAutomatically = _saveAutomatically;
		gp.stateTimeStamp = _stateTimeStamp;
		gp.version = _version;
		return gp;
	}

	public boolean getSaveAutomatically() {
		return _saveAutomatically;
	}


	/**
	 * Answer if all validation has been disabled.
	 */
	public boolean getDisableAllValidation() {
		return _disableAllValidation;
	}

	public boolean getConfirmDialog() {
		return _confirmDialog;
	}

	public long getStateTimeStamp() {
		return _stateTimeStamp;
	}

	/** Answer whether or not projects are allowed to override the global preferences. */
	public boolean getOverride() {
		return _override;
	}

	public int getVersion() {
		return _version;
	}

	/**
	 * Compare yourself to the other global preferences and answer a bitmask with the differences.
	 * @param gp
	 * @return bit mask of the changes. See the xxxMask constants for the values of the bits. A zero means that they are the same.
	 */
	public int compare(GlobalPreferences gp) {
		int changes = 0;
		if (_confirmDialog != gp.getConfirmDialog())changes |= ConfirmDialogMask;
		if (_disableAllValidation != gp.getDisableAllValidation())changes |= DisableAllValidationMask;
		if (_override != gp.getOverride())changes |= OverrideMask;
		if (_saveAutomatically != gp.getSaveAutomatically())changes |= SaveAutomaticallyMask;
		if (_stateTimeStamp != gp.getStateTimeStamp())changes |= StateTimeStampMask;
		if (_version != gp.getVersion())changes |= VersionMask;
		return changes;
	}

}
