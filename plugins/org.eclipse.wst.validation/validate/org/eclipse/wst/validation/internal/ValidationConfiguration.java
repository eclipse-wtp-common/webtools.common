/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

import com.ibm.wtp.common.logger.LogEntry;
import com.ibm.wtp.common.logger.proxy.Logger;


/**
 * This class represents the user's preference or project settings.
 * 
 * This class is populated from the multiple persistent properties, and is kept as a session
 * property while the resource is open.
 */
public abstract class ValidationConfiguration {
	private IResource _resource = null;
	private boolean _autoValidate = getAutoValidateDefault();
	private boolean _buildValidate = getBuildValidateDefault();
	private int _maxMessages = getMaximumNumberOfMessagesDefault();
	private String _version = null;
	private Map _validators = null; // Map of all validators (ValidatorMetaData) configured on the
	// project or installed globally. The value is a Boolean; TRUE
	// means that the VMD is enabled, FALSE means that the VMD is
	// disabled.
	private boolean _runAsync = getAsyncDefault();

	public static String getEnabledElementsAsString(Set elements) {
		if (elements == null) {
			return null;
		}

		StringBuffer buffer = new StringBuffer();
		Iterator iterator = elements.iterator();
		while (iterator.hasNext()) {
			buffer.append(((ValidatorMetaData) iterator.next()).getValidatorUniqueName());
			buffer.append(ConfigurationConstants.ELEMENT_SEPARATOR);
		}
		return buffer.toString();
	}

	public static String getEnabledElementsAsString(Object[] elements) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < elements.length; i++) {
			buffer.append(((ValidatorMetaData) elements[i]).getValidatorUniqueName());
			buffer.append(ConfigurationConstants.ELEMENT_SEPARATOR);
		}
		return buffer.toString();
	}

	public static Set getStringAsEnabledElements(String elements) {
		if (elements == null) {
			return null;
		}

		HashSet result = new HashSet();
		StringTokenizer tokenizer = new StringTokenizer(elements, ConfigurationConstants.ELEMENT_SEPARATOR);
		while (tokenizer.hasMoreTokens()) {
			String elem = tokenizer.nextToken();
			ValidatorMetaData vmd = ValidationRegistryReader.getReader().getValidatorMetaData(elem);
			if (vmd != null) {
				result.add(vmd);
			}
		}
		return result;
	}

	public static ValidatorMetaData[] getStringAsEnabledElementsArray(String elements) {
		if (elements == null) {
			return null;
		}

		StringTokenizer tokenizer = new StringTokenizer(elements, ConfigurationConstants.ELEMENT_SEPARATOR);
		ValidatorMetaData[] result = new ValidatorMetaData[tokenizer.countTokens()];
		int count = 0;
		while (tokenizer.hasMoreTokens()) {
			String elem = tokenizer.nextToken();
			ValidatorMetaData vmd = ValidationRegistryReader.getReader().getValidatorMetaData(elem);
			if (vmd != null) {
				result[count++] = vmd;
			}
		}

		if (count != result.length) {
			ValidatorMetaData[] trimResult = new ValidatorMetaData[count];
			System.arraycopy(result, 0, trimResult, 0, count);
			return trimResult;
		}

		return result;
	}

	public static IWorkspaceRoot getRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	protected ValidationConfiguration() throws InvocationTargetException {
		_validators = new HashMap();
	}

	protected ValidationConfiguration(IResource resource, ValidatorMetaData[] validators) throws InvocationTargetException {
		this();

		if (resource == null) {
			throw new InvocationTargetException(null, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_NULLCREATE));
		}

		setResource(resource);
		setValidators(validators);
	}

	private void setResource(IResource resource) {
		_resource = resource;
	}

	public boolean isAutoValidate() throws InvocationTargetException {
		return _autoValidate;
	}

	public void setAutoValidate(boolean auto) {
		_autoValidate = auto;
	}

	public boolean isBuildValidate() throws InvocationTargetException {
		return _buildValidate;
	}

	public void setBuildValidate(boolean build) {
		_buildValidate = build;
	}

	public boolean runAsync() throws InvocationTargetException {
		return _runAsync;
	}

	public void setAsync(boolean doRunInBackgroundThread) throws InvocationTargetException {
		_runAsync = doRunInBackgroundThread;
	}

	public ValidatorMetaData[] getEnabledValidators() throws InvocationTargetException {
		return getValidators(true);
	}

	/**
	 * If "incremental" is true, return the enabled incremental validators. If "incremental" is
	 * false, return the enabled non-incremental validators.
	 */
	public ValidatorMetaData[] getEnabledIncrementalValidators(boolean incremental) throws InvocationTargetException {
		ValidatorMetaData[] temp = new ValidatorMetaData[numberOfValidators()];
		Iterator iterator = getValidatorMetaData().keySet().iterator();
		int count = 0;
		while (iterator.hasNext()) {
			ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();
			Boolean bvalue = (Boolean) getValidatorMetaData().get(vmd);
			if (bvalue.booleanValue() == true) {
				// If the validator is enabled
				if ((vmd.isIncremental() && incremental) || (!vmd.isIncremental() && !incremental)) {
					temp[count++] = vmd;
				}
			}
		}

		ValidatorMetaData[] result = new ValidatorMetaData[count];
		System.arraycopy(temp, 0, result, 0, count);
		return result;
	}

	/**
	 * If "fullBuild" is true, return the enabled validators that support full builds. If
	 * "fullBuild" is false, return the enabled validators that do not support full builds.
	 */
	public ValidatorMetaData[] getEnabledFullBuildValidators(boolean fullBuild) throws InvocationTargetException {
		return getEnabledFullBuildValidators(fullBuild, false);
	}

	public ValidatorMetaData[] getEnabledFullBuildValidators(boolean fullBuild, boolean onlyReferenced) throws InvocationTargetException {
		ValidatorMetaData[] temp = new ValidatorMetaData[numberOfValidators()];
		Iterator iterator = getValidatorMetaData().keySet().iterator();
		int count = 0;
		while (iterator.hasNext()) {
			ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();
			Boolean bvalue = (Boolean) getValidatorMetaData().get(vmd);
			if (bvalue.booleanValue() == true) {
				// If the validator is enabled
				if ((vmd.isFullBuild() && fullBuild) || (!vmd.isFullBuild() && !fullBuild)) {
					if (!onlyReferenced || vmd.isDependentValidator())
						temp[count++] = vmd;
				}
			}
		}

		ValidatorMetaData[] result = new ValidatorMetaData[count];
		System.arraycopy(temp, 0, result, 0, count);
		return result;
	}

	public void setEnabledValidators(ValidatorMetaData[] vmds) {
		// First, "disable" all validators
		Map all = getValidatorMetaData();
		Iterator iterator = all.keySet().iterator();
		while (iterator.hasNext()) {
			ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();
			all.put(vmd, Boolean.FALSE);
		}

		// Then enable only the validators in the array
		if ((vmds == null) || (vmds.length == 0)) {
			return;
		}
		for (int i = 0; i < vmds.length; i++) {
			all.put(vmds[i], Boolean.TRUE);
		}
	}

	private Map getValidatorMetaData() {
		return _validators;
	}

	public ValidatorMetaData[] getDisabledValidators() throws InvocationTargetException {
		return getValidators(false);
	}

	/**
	 * Return an array of ValidatorMetaData - if value is false, return the disabled validators; if
	 * value is true, return the enabled validators.
	 */
	private ValidatorMetaData[] getValidators(boolean value) throws InvocationTargetException {
		ValidatorMetaData[] temp = new ValidatorMetaData[numberOfValidators()];
		Iterator iterator = getValidatorMetaData().keySet().iterator();
		int count = 0;
		while (iterator.hasNext()) {
			ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();
			Boolean bvalue = (Boolean) getValidatorMetaData().get(vmd);
			if (bvalue.booleanValue() == value) {
				temp[count++] = vmd;
			}
		}

		ValidatorMetaData[] result = new ValidatorMetaData[count];
		System.arraycopy(temp, 0, result, 0, count);
		return result;
	}

	/**
	 * Return all incremental validators for this preference; either every installed validator
	 * (global) or every validator configured on the project (project).
	 */
	public ValidatorMetaData[] getIncrementalValidators() throws InvocationTargetException {
		ValidatorMetaData[] temp = new ValidatorMetaData[numberOfValidators()];
		Iterator iterator = getValidatorMetaData().keySet().iterator();
		int count = 0;
		while (iterator.hasNext()) {
			ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();
			Boolean bvalue = (Boolean) getValidatorMetaData().get(vmd);
			if (bvalue.booleanValue() == true) {
				// If the validator is enabled
				if (vmd.isIncremental()) {
					temp[count++] = vmd;
				}
			}
		}

		ValidatorMetaData[] result = new ValidatorMetaData[count];
		System.arraycopy(temp, 0, result, 0, count);
		return result;
	}

	/**
	 * Return all validators for this preference; either every installed validator (global) or every
	 * validator configured on the project (project).
	 */
	public ValidatorMetaData[] getValidators() throws InvocationTargetException {
		return convertToArray(_validators.keySet());
	}

	public void setValidators(ValidatorMetaData[] vmds) {
		_validators.clear();
		for (int i = 0; i < vmds.length; i++) {
			_validators.put(vmds[i], (vmds[i].isEnabledByDefault() ? Boolean.TRUE : Boolean.FALSE));
		}
	}

	/**
	 * Returns the number of configured validators on the given project or installed validators in
	 * the workspace.
	 */
	public int numberOfValidators() throws InvocationTargetException {
		return _validators.size();
	}

	public int numberOfEnabledIncrementalValidators() throws InvocationTargetException {
		return numberOfIncrementalValidators(getEnabledValidators());
	}

	public int numberOfIncrementalValidators() throws InvocationTargetException {
		return numberOfIncrementalValidators(getValidators());
	}

	private static int numberOfIncrementalValidators(ValidatorMetaData[] vmds) {
		int count = 0;
		for (int i = 0; i < vmds.length; i++) {
			ValidatorMetaData vmd = vmds[i];
			if (vmd.isIncremental()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns the number of enabled validators on the project or workspace.
	 */
	public int numberOfEnabledValidators() throws InvocationTargetException {
		return getEnabledValidators().length;
	}

	/**
	 * Returns the number of disabled validators on the project or workspace.
	 */
	public int numberOfDisabledValidators() throws InvocationTargetException {
		return getDisabledValidators().length;
	}

	public int getMaximumNumberOfMessages() throws InvocationTargetException {
		return _maxMessages;
	}

	public void setMaximumNumberOfMessages(int max) {
		_maxMessages = max;
	}

	/**
	 * The value returned from this method is guaranteed to be non-null.
	 */
	public final String getVersion() throws InvocationTargetException {
		if (_version == null) {
			loadVersion();
		}
		return _version;
	}

	private void setVersion(String version) {
		_version = version;
	}

	/**
	 * This preference has been migrated; change the version to the current version.
	 */
	public void markVersionCurrent() {
		// The version should not be marked current until the migration is complete
		// (i.e., ValidationMigrator has been invoked.) Migrating the user's configuration
		// is only the first step of the migration.
		setVersion(ConfigurationConstants.CURRENT_VERSION);
	}

	public boolean isVersionCurrent() throws InvocationTargetException {
		return getVersion().equals(ConfigurationConstants.CURRENT_VERSION);
	}

	// IResource could be an IProject or an IWorkspaceRoot
	public IResource getResource() {
		return _resource;
	}

	/**
	 * Return true if the validator is enabled on this preference.
	 */
	public boolean isEnabled(String validatorClassName) throws InvocationTargetException {
		if (validatorClassName == null) {
			return false;
		}

		ValidatorMetaData vmd = ValidationRegistryReader.getReader().getValidatorMetaData(validatorClassName);
		return isEnabled(vmd);
	}

	/**
	 * Return true if the validator is enabled on this preference.
	 */
	public boolean isEnabled(ValidatorMetaData vmd) throws InvocationTargetException {
		if (vmd == null) {
			return false;
		}

		Boolean value = (Boolean) getValidatorMetaData().get(vmd);
		if (value == null) {
			return false;
		}

		return value.booleanValue();
	}

	/**
	 * Once all of the fields have been updated on this ValidationConfiguration instance, this
	 * preference should be stored back on the IResource for later use. This method must be called
	 * manually by the validation framework once the fields of this type have been updated.
	 */
	public final void passivate() throws InvocationTargetException {
		try {
			if (getResource() == null) {
				throw new InvocationTargetException(null, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_NULLSAVE));
			}

			getResource().setSessionProperty(ConfigurationConstants.USER_PREFERENCE, this);
		} catch (CoreException exc) {
			throw new InvocationTargetException(exc, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_SAVE, new String[]{getResource().getName()}));
		}
	}

	/**
	 * Reset the values to the default values.
	 */
	public abstract void resetToDefault() throws InvocationTargetException;

	protected Boolean convertToBoolean(boolean value) {
		return (value == true ? Boolean.TRUE : Boolean.FALSE);
	}

	protected Integer convertToInteger(int value) {
		return new Integer(value);
	}

	protected boolean convertToBoolean(String value) {
		Boolean b = Boolean.valueOf(value);
		return b.booleanValue();
	}

	protected int convertToInt(String value) {
		Integer t = Integer.valueOf(value);
		return t.intValue();
	}

	public static ValidatorMetaData[] convertToArray(Collection c) {
		int length = (c == null) ? 0 : c.size();
		ValidatorMetaData[] result = new ValidatorMetaData[length];
		if (length == 0) {
			return result;
		}

		Iterator iterator = c.iterator();
		int count = 0;
		while (iterator.hasNext()) {
			ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();
			result[count++] = vmd;
		}

		return result;
	}

	public static ValidatorMetaData[] convertToArray(Object[] c) {
		int length = (c == null) ? 0 : c.length;
		ValidatorMetaData[] result = new ValidatorMetaData[length];
		if (length == 0) {
			return result;
		}

		System.arraycopy(c, 0, result, 0, length);
		return result;
	}

	/**
	 * Save the values of these fields before the project or workspace is closed.
	 */
	public void store() throws InvocationTargetException {
		if (getResource() == null) {
			throw new InvocationTargetException(null, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_NULLSAVE));
		}

		try {
			getResource().setPersistentProperty(ConfigurationConstants.USER_PREFERENCE, serialize());
		} catch (CoreException exc) {
			throw new InvocationTargetException(exc, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_SAVE, new String[]{getResource().getName()}));
		}
	}

	/**
	 * Load and store the version number, but nothing else. The version isn't a preference, but it's
	 * stored and loaded as if it is. Because the load mechanism is different between an IProject
	 * and the IWorkspaceRoot, keep the load mechanism inside the ValidationConfiguration
	 * implementation, but initialize only the minimum at first. After the project has been
	 * migrated, load the other fields (loading the other fields before the migration may overwrite
	 * the values of those fields).
	 */
	protected final void loadVersion() throws InvocationTargetException {
		if (getResource() == null) {
			throw new InvocationTargetException(null, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_NULLRETRIEVE));
		}

		IMarker[] marker = getMarker();
		String version = null;
		if (marker == null) {
			// either a new workspace or already migrated; proceed as normal
			version = loadVersion(getResource());
		} else {
			// migrate
			version = loadVersion(marker);
		}
		setVersion(version);
	}

	protected final String loadVersion(IMarker[] marker) throws InvocationTargetException {
		String version = (String) getValue(marker[0], ConfigurationConstants.VERSION);
		if (version == null) {
			return ConfigurationConstants.VERSION4_03;
		}

		return version;
	}

	protected final String loadVersion(IResource resource) throws InvocationTargetException {
		try {
			// This method will be called in one of two situations:
			//    1. This is a new workspace and no preferences exist.
			//    2. This is a migrated workspace and the old preferences have already been created as
			// persistent properties.
			String storedConfiguration = resource.getPersistentProperty(ConfigurationConstants.USER_PREFERENCE);
			String version = null;
			if (storedConfiguration == null) {
				version = getVersionDefault();
			} else {
				int versionIndex = storedConfiguration.indexOf(ConfigurationConstants.VERSION);
				if (versionIndex != -1)
					version = storedConfiguration.substring(versionIndex + ConfigurationConstants.VERSION.length());
			}

			if (version == null) {
				return getVersionDefault();
			}

			return version;
		} catch (CoreException exc) {
			throw new InvocationTargetException(exc, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_RETRIEVE, new String[]{getResource().getName()}));
		}
	}

	/**
	 * Load the values of these fields when the project or workspace is opened.
	 */
	protected final void load() throws InvocationTargetException {
		try {
			if (getResource() == null) {
				throw new InvocationTargetException(null, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_NULLRETRIEVE));
			}

			IMarker[] marker = getMarker();
			if (marker == null) {
				// either a new workspace or already migrated; proceed as normal
				load(getResource());
			} else {
				// migrate
				load(marker);
			}
		} catch (InvocationTargetException exc) {
			throw exc;
		} catch (Throwable exc) {
			String resourceName = (getResource() == null) ? "null" : getResource().getName(); //$NON-NLS-1$
			throw new InvocationTargetException(exc, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_RETRIEVE, new String[]{resourceName}));
		}
	}

	/**
	 * Return the IMarker that used to store the preference information, if the marker exists. (If
	 * it doesn't exist, then it's already been migrated.)
	 */
	protected abstract IMarker[] getMarker();

	protected abstract void load(IMarker[] marker) throws InvocationTargetException;

	protected Object getValue(IMarker marker, String attribName) {
		if (marker == null) {
			return null;
		}

		try {
			return marker.getAttribute(attribName);
		} catch (CoreException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidationConfiguration::getValue(" + attribName + ")"); //$NON-NLS-1$ //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
			return null;
		}
	}

	protected boolean getValue(IMarker marker, String attribName, boolean defaultValue) {
		Boolean bool = (Boolean) getValue(marker, attribName);
		if (bool == null) {
			return defaultValue;
		}

		return bool.booleanValue();
	}

	protected int getValue(IMarker marker, String attribName, int defaultValue) {
		Integer integer = (Integer) getValue(marker, attribName);
		if (integer == null) {
			return defaultValue;
		}

		return integer.intValue();
	}

	protected String getValue(IMarker marker, String attribName, String defaultValue) {
		String string = (String) getValue(marker, attribName);
		if (string == null) {
			return defaultValue;
		}

		return string;
	}

	protected final void load(IResource resource) throws InvocationTargetException {
		// This method will be called in one of two situations:
		//    1. This is a new workspace and no preferences exist.
		//    2. This is a migrated workspace and the old preferences have already been created as
		// persistent properties.
		try {
			String storedConfiguration = resource.getPersistentProperty(ConfigurationConstants.USER_PREFERENCE);
			deserialize(storedConfiguration);
		} catch (CoreException exc) {
			throw new InvocationTargetException(exc, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_RETRIEVE, new String[]{getResource().getName()}));
		}
	}

	protected void copyTo(ValidationConfiguration up) throws InvocationTargetException {
		up.setVersion(getVersion());
		up.setResource(getResource());
		up.setValidators(getValidators());
		up.setAutoValidate(isAutoValidate());
		up.setBuildValidate(isBuildValidate());
		up.setEnabledValidators(getEnabledValidators());
		up.setMaximumNumberOfMessages(getMaximumNumberOfMessages());
	}

	/**
	 * Return true if the enabled validators have not changed since this ValidationConfiguration was
	 * constructed, false otherwise. (This method is needed for the Properties and Preference pages;
	 * if the list of validators hasn't changed, then there is no need to update the task list;
	 * updating the task list is a costly operation.)
	 */
	protected boolean hasEnabledValidatorsChanged(ValidatorMetaData[] oldEnabledVmd) throws InvocationTargetException {
		// First check the obvious: is every enabled validator still enabled, and is
		// the number of enabled validators the same as it was before? If not, return true.
		if (oldEnabledVmd == null) {
			// Something's gone wrong...
			return true;
		}

		for (int i = 0; i < oldEnabledVmd.length; i++) {
			ValidatorMetaData vmd = oldEnabledVmd[i];
			if (!isEnabled(vmd)) {
				return true;
			}
		}

		// Everything that was enabled is still enabled; have any additional
		// validators been enabled?
		if (numberOfEnabledValidators() != oldEnabledVmd.length) {
			return true;
		}

		return false;
	}

	protected String serialize() throws InvocationTargetException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(ConfigurationConstants.AUTO_SETTING);
		buffer.append(String.valueOf(isAutoValidate()));
		buffer.append(ConfigurationConstants.BUILD_SETTING);
		buffer.append(String.valueOf(isBuildValidate()));
		buffer.append(ConfigurationConstants.MAXNUMMESSAGES);
		buffer.append(String.valueOf(getMaximumNumberOfMessages()));
		buffer.append(ConfigurationConstants.ASYNC_SETTING);
		buffer.append(String.valueOf(runAsync()));
		buffer.append(ConfigurationConstants.ENABLED_VALIDATORS);
		buffer.append(getEnabledElementsAsString(getEnabledValidators()));
		buffer.append(ConfigurationConstants.VERSION);
		buffer.append(getVersion());
		return buffer.toString();
	}

	/**
	 * Deserialize everything except the version number; the version is deserialized first, in the
	 * loadVersion() method.
	 */
	protected void deserialize(String storedConfiguration) throws InvocationTargetException {
		if (storedConfiguration == null || storedConfiguration.length() == 0) {
			// Assume that the configuration has never been set (new workspace).
			resetToDefault();
			return;
		}

		int autoIndex = storedConfiguration.indexOf(ConfigurationConstants.AUTO_SETTING);
		int buildIndex = storedConfiguration.indexOf(ConfigurationConstants.BUILD_SETTING);
		int maxIndex = storedConfiguration.indexOf(ConfigurationConstants.MAXNUMMESSAGES);
		int asyncIndex = storedConfiguration.indexOf(ConfigurationConstants.ASYNC_SETTING);
		int enabledIndex = storedConfiguration.indexOf(ConfigurationConstants.ENABLED_VALIDATORS);
		int versionIndex = storedConfiguration.indexOf(ConfigurationConstants.VERSION);

		String auto = storedConfiguration.substring(autoIndex + ConfigurationConstants.AUTO_SETTING.length(), buildIndex);
		String build = storedConfiguration.substring(buildIndex + ConfigurationConstants.BUILD_SETTING.length(), maxIndex);
		String max = storedConfiguration.substring(maxIndex + ConfigurationConstants.MAXNUMMESSAGES.length(), asyncIndex);
		String async = storedConfiguration.substring(asyncIndex + ConfigurationConstants.ASYNC_SETTING.length(), enabledIndex);
		String enabled = storedConfiguration.substring(enabledIndex + ConfigurationConstants.ENABLED_VALIDATORS.length(), versionIndex);

		setAutoValidate(Boolean.valueOf(auto).booleanValue());
		setBuildValidate(Boolean.valueOf(build).booleanValue());
		setMaximumNumberOfMessages(Integer.valueOf(max).intValue());
		setAsync(Boolean.valueOf(async).booleanValue());
		setEnabledValidators(getStringAsEnabledElementsArray(enabled));
	}

	public static boolean getAutoValidateDefault() {
		return ConfigurationConstants.DEFAULT_AUTO_SETTING;
	}

	public static boolean getBuildValidateDefault() {
		return ConfigurationConstants.DEFAULT_BUILD_SETTING;
	}

	public static ValidatorMetaData[] getEnabledValidatorsDefault() {
		return ConfigurationConstants.DEFAULT_ENABLED_VALIDATORS;
	}

	public static int getMaximumNumberOfMessagesDefault() {
		return ConfigurationConstants.DEFAULT_MAXNUMMESSAGES;
	}

	public static String getVersionDefault() {
		// If the version can't be retrieved, assume that it's a new workspace. (A null version
		// could also mean a 4.03 workspace, but that's taken care of in the load(IMarker[])
		// method.)
		return ConfigurationConstants.CURRENT_VERSION;
	}

	public static boolean getAsyncDefault() {
		return ConfigurationConstants.DEFAULT_ASYNC;
	}
}