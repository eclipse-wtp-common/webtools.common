/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;


/**
 * This class represents the global Preferences as set on the Validation Preferences page.
 */
public class GlobalConfiguration extends ValidationConfiguration {
	static final boolean PREF_PROJECTS_CAN_OVERRIDE_DEFAULT = true;
	static final boolean PREF_SAVE_AUTOMATICALLY_DEFAULT = false;

	private boolean _canProjectsOverride = getCanProjectsOverrideDefault();
	private boolean _saveAutomatically = getSaveAutomaticallyDefault();

	/**
	 * This constructor should be used in all cases except for the Preference page's values.
	 */
	public GlobalConfiguration(IWorkspaceRoot root) throws InvocationTargetException {
		super(root, convertToArray(ValidationRegistryReader.getReader().getAllValidators()));

		// Can't put the call to load() and passivate() in the ValidationConfiguration constructor
		// due
		// to the order of initialization.
		//    1. First the ValidationConfiguration constructor is called, and that loads the stored
		// values.
		//    2. Then this class's <init> method is called, and that initializes the "override" field
		// to the default,
		//       which may be different than the stored value.
	}

	/**
	 * This constructor is provided only for the Preference page, so that the page can store values
	 * without persisting them (i.e., if the user presses Cancel then nothing needs to be done.)
	 */
	public GlobalConfiguration(GlobalConfiguration original) throws InvocationTargetException {
		super();
		original.copyTo(this);
	}

	public boolean canProjectsOverride() {
		return _canProjectsOverride;
	}

	public void setCanProjectsOverride(boolean can) {
		_canProjectsOverride = can;
	}
	
	public boolean getSaveAutomatically() {
		return _saveAutomatically;
	}
	
	public void setSaveAutomatically(boolean save) {
		_saveAutomatically = save;
	}

	public void resetToDefault()  throws InvocationTargetException {
		setDisableAllValidation(getDisableValidationDefault());
		setEnabledValidators(getEnabledValidatorsDefault());
		setEnabledManualValidators(getManualEnabledValidators());
		setEnabledBuildValidators(getBuildEnabledValidators());
		setCanProjectsOverride(getCanProjectsOverrideDefault());
		setSaveAutomatically(getSaveAutomaticallyDefault());
    setDefaultDelegates(getValidators());
	}

	/**
	 * This method exists only for migration purposes. The root marker must be deleted after
	 * migration is complete.
	 */
	protected IMarker[] getMarker() {
		try {
			IWorkspaceRoot root = getRoot();
			IMarker[] markers = root.findMarkers(ConfigurationConstants.PREFERENCE_MARKER, false, IResource.DEPTH_ONE);

			if (markers.length == 1) {
				return markers;
			}
			// job is done. Nothing to migrate.
			return null;

		} catch (CoreException exc) {
			// Can't find the IMarker? Assume it's deleted.
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("GlobalConfiguration.getMarker()"); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
			return null;
		}
	}

	protected void load(IMarker[] marker) throws InvocationTargetException {
		// The 5.0 preferences were stored in an IMarker, and the current.preferences are stored in
		// PersistentProperties on the IResource.
		// A 5.0 root can have no marker values if the preference page, properties page, and
		// validation were never viewed or run.
		try {
			IWorkspaceRoot root = getRoot();
			if (marker == null) {
				// There were no global preferences in 4.03, so the migration is to create some.
				resetToDefault(); // assign the default values to the new Global Preference
				return;
			}

			IMarker rootMarker = marker[0]; // getMarker() has already checked that there's a marker
			// in the array
//			ValidatorMetaData[] enabledValidators = null;
//			String enabledValidatorsString = (String) getValue(rootMarker, ConfigurationConstants.ENABLED_VALIDATORS);
//			if (enabledValidatorsString == null) {
//				enabledValidators = ConfigurationConstants.DEFAULT_ENABLED_VALIDATORS;
//			} else {
//				enabledValidators = getStringAsEnabledElementsArray(enabledValidatorsString);
//			}
//
//			setEnabledValidators(enabledValidators);
			String enabledManualValidators = (String) getValue(rootMarker, ConfigurationConstants.ENABLED_MANUAL_VALIDATORS);
			setEnabledManualValidators(getStringAsEnabledElementsArray(enabledManualValidators));
			String enabledBuildValidators = (String) getValue(rootMarker, ConfigurationConstants.ENABLED_BUILD_VALIDATORS);
			setEnabledManualValidators(getStringAsEnabledElementsArray(enabledBuildValidators));
//			if (enabledManualValidators.equals(null) || enabledBuildValidators.equals(null)) 
//				enabledValidators = ConfigurationConstants.DEFAULT_ENABLED_VALIDATORS;
			setCanProjectsOverride(getValue(rootMarker, ConfigurationConstants.PREF_PROJECTS_CAN_OVERRIDE, PREF_PROJECTS_CAN_OVERRIDE_DEFAULT));
			root.getWorkspace().deleteMarkers(marker);
		} catch (CoreException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("GlobalConfiguration.loadV50"); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
		}
	}

	protected void copyTo(GlobalConfiguration gp) throws InvocationTargetException {
		super.copyTo(gp);

		// Need to have a distinct method for this child class (i.e., the parameter
		// is not a ValidationConfiguration) because if this initialization is
		// called as part of ValidationConfiguration's constructor, then the value of
		// this field is overwritten. Fields of this class are initialized to the
		// default after the ValidationConfiguration parent is created.
		gp.setCanProjectsOverride(canProjectsOverride());
		gp.setSaveAutomatically(getSaveAutomatically());
	}

	public static boolean getCanProjectsOverrideDefault() {
		return PREF_PROJECTS_CAN_OVERRIDE_DEFAULT;
	}
	
	public static boolean getSaveAutomaticallyDefault() {
		return PREF_SAVE_AUTOMATICALLY_DEFAULT;
	}

	/**
	 * @see org.eclipse.wst.validation.internal.operations.internal.attribute.ValidationConfiguration#deserialize(String)
	 */
	public void deserialize(String storedConfiguration) throws InvocationTargetException {
		super.deserialize(storedConfiguration);

		if (storedConfiguration != null && storedConfiguration.length() > 0) {
			// If it's null, then super.deserialize has already called resetToDefault to initialize
			// this instance.
			int canOverrideIndex = storedConfiguration.indexOf(ConfigurationConstants.PREF_PROJECTS_CAN_OVERRIDE);
			int disableAllValidationIndex = storedConfiguration.indexOf(ConfigurationConstants.DISABLE_ALL_VALIDATION_SETTING);
			int saveAutomaticallyIndex = storedConfiguration.indexOf(ConfigurationConstants.SAVE_AUTOMATICALLY_SETTING);
			if (disableAllValidationIndex != -1) {
				String canOverride = storedConfiguration.substring(canOverrideIndex + ConfigurationConstants.PREF_PROJECTS_CAN_OVERRIDE.length(), disableAllValidationIndex);
				setCanProjectsOverride(Boolean.valueOf(canOverride).booleanValue());
			}
			if(saveAutomaticallyIndex != -1)
			{
				String saveAutomatically = storedConfiguration.substring(saveAutomaticallyIndex + ConfigurationConstants.SAVE_AUTOMATICALLY_SETTING.length(), canOverrideIndex);
				setSaveAutomatically(Boolean.valueOf(saveAutomatically).booleanValue());
			}
		}
	}

	/**
	 * @see org.eclipse.wst.validation.internal.operations.internal.attribute.ValidationConfiguration#serialize()
	 */
	public String serialize() throws InvocationTargetException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(ConfigurationConstants.SAVE_AUTOMATICALLY_SETTING);
		buffer.append(String.valueOf(getSaveAutomatically()));
		buffer.append(ConfigurationConstants.PREF_PROJECTS_CAN_OVERRIDE);
		buffer.append(String.valueOf(canProjectsOverride()));
		buffer.append(super.serialize());
		return buffer.toString();
	}

	
}
