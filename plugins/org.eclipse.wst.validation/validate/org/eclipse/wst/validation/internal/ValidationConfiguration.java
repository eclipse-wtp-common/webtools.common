/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.wst.validation.internal.delegates.ValidatorDelegateDescriptor;
import org.eclipse.wst.validation.internal.delegates.ValidatorDelegatesRegistry;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * This class represents the user's preference or project settings.
 * 
 * This class is populated from the multiple persistent properties, and is kept as a session
 * property while the resource is open.
 */
public abstract class ValidationConfiguration implements IPropertyChangeListener {
	private IResource 	_resource;
	private boolean 	disableAllValidation = getDisableAllValidation();
	private String 		_version;
	
	/**
	 * Map of all validators configured on the project or installed globally. The value of true
	 * means that the VMD is enabled, and a value of false means that the VMD is disabled.
	 */
	private Map<ValidatorMetaData, Boolean>		_validators = new HashMap<ValidatorMetaData, Boolean>();
	protected Map<ValidatorMetaData, Boolean> 	manualValidators;
	protected Map<ValidatorMetaData, Boolean> 	buildValidators;
	
	protected String USER_PREFERENCE 		= "USER_PREFERENCE"; //$NON-NLS-1$
	protected String USER_MANUAL_PREFERENCE = "USER_MANUAL_PREFERENCE"; //$NON-NLS-1$
	protected String USER_BUILD_PREFERENCE 	= "USER_BUILD_PREFERENCE"; //$NON-NLS-1$
	protected String DELEGATES_PREFERENCE 	= "DELEGATES_PREFERENCE"; //$NON-NLS-1$
	
	/**
	 * The key is the target id, that is the id of the place holder validator. The value is the id 
	 * of the real validator. 
	 */
	private Map<String, String> _delegatesByTarget = new HashMap<String, String>();
	
	private static final String DefaultValue = "default_value"; //$NON-NLS-1$

	public static String getEnabledElementsAsString(Set<ValidatorMetaData> elements) {
		if (elements == null)return null;

		StringBuffer buffer = new StringBuffer();
		for (ValidatorMetaData vmd : elements) {
			buffer.append(vmd.getValidatorUniqueName());
			buffer.append(ConfigurationConstants.ELEMENT_SEPARATOR);
		}
		return buffer.toString();
	}

	private boolean getDisableAllValidation() {
		return false;
	}

	public static String getEnabledElementsAsString(ValidatorMetaData[] elements) {
		StringBuffer buffer = new StringBuffer();
		for (ValidatorMetaData vmd : elements) {
			buffer.append(vmd.getValidatorUniqueName());
			buffer.append(ConfigurationConstants.ELEMENT_SEPARATOR);
		}
		return buffer.toString();
	}

	public static Set<ValidatorMetaData> getStringAsEnabledElements(String elements) {
		if (elements == null)return null;

		Set<ValidatorMetaData> result = new HashSet<ValidatorMetaData>();
		StringTokenizer tokenizer = new StringTokenizer(elements, ConfigurationConstants.ELEMENT_SEPARATOR);
		while (tokenizer.hasMoreTokens()) {
			String elem = tokenizer.nextToken();
			ValidatorMetaData vmd = ValidationRegistryReader.getReader().getValidatorMetaData(elem);
			if (vmd != null)result.add(vmd);
		}
		return result;
	}

	public static ValidatorMetaData[] getStringAsEnabledElementsArray(String elements) {
		if (elements == null)return null;

		StringTokenizer tokenizer = new StringTokenizer(elements, ConfigurationConstants.ELEMENT_SEPARATOR);
		ValidatorMetaData[] result = new ValidatorMetaData[tokenizer.countTokens()];
		int count = 0;
		while (tokenizer.hasMoreTokens()) {
			String elem = tokenizer.nextToken();
			ValidatorMetaData vmd = ValidationRegistryReader.getReader().getValidatorMetaData(elem);
			if (vmd != null)result[count++] = vmd;
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
	}

	protected ValidationConfiguration(IResource resource, ValidatorMetaData[] validators) throws InvocationTargetException {
		if (resource == null) {
			throw new InvocationTargetException(null, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_NULLCREATE));
		}

		setResource(resource);
		setValidators(validators);
	}

	private void setResource(IResource resource) {
		_resource = resource;
	}
	
	public boolean isDisableAllValidation() throws InvocationTargetException {
		return disableAllValidation;
	}

	public void setDisableAllValidation(boolean allValidation) {
		disableAllValidation = allValidation;
	}

	public ValidatorMetaData[] getEnabledValidators() throws InvocationTargetException {
		return getValidators(true);
	}
	
	/**
	 * If "incremental" is true, return the enabled incremental validators. If "incremental" is
	 * false, return the enabled non-incremental validators.
	 */
	public ValidatorMetaData[] getEnabledIncrementalValidators(boolean incremental) throws InvocationTargetException {
		return getEnabledFullBuildValidators(!incremental);
	}

	/**
	 * If "fullBuild" is true, return the enabled validators that support full builds. If
	 * "fullBuild" is false, return the enabled validators that do not support full builds.
	 */
	public ValidatorMetaData[] getEnabledFullBuildValidators(boolean fullBuild) throws InvocationTargetException {
		return getEnabledFullBuildValidators(fullBuild, false);
	}

	public ValidatorMetaData[] getEnabledFullBuildValidators(boolean fullBuild, boolean onlyReferenced) throws InvocationTargetException {

		int count = 0;
		ValidatorMetaData[] result = null;
		 
		if( !isDisableAllValidation() ){
			Set<ValidatorMetaData> set = getBuildEnabledValidatorsMap().keySet();
			ValidatorMetaData[] temp = new ValidatorMetaData[set.size()];
			for (ValidatorMetaData vmd : set) {
				Boolean bvalue = getBuildEnabledValidatorsMap().get(vmd);
				if (bvalue) {
					// If the validator is enabled
					if (vmd == null)continue;
					if ( vmd.isBuildValidation() ) {
						if (!onlyReferenced || vmd.isDependentValidator())
							temp[count++] = vmd;
					}
				}
			}
			result = new ValidatorMetaData[count];
			System.arraycopy(temp, 0, result, 0, count);
		}
		else {
			 result = new ValidatorMetaData[0];
		}
		return result;
	}

	public void setEnabledValidators(ValidatorMetaData[] vmds) {
		// First, "disable" all validators
		Map<ValidatorMetaData, Boolean> all = getValidatorMetaData();
		for (ValidatorMetaData vmd : all.keySet()) {
			all.put(vmd, Boolean.FALSE);
		}

		// Then enable only the validators in the array
		if ((vmds == null) || (vmds.length == 0))return;
		
		for (ValidatorMetaData vmd : vmds) {
			all.put(vmd, Boolean.TRUE);
		}
	}
	
	public void setEnabledManualValidators(ValidatorMetaData[] vmds) throws InvocationTargetException {
		Set<ValidatorMetaData> manualEnabledVMDs = new HashSet<ValidatorMetaData>(vmds.length);
		for (ValidatorMetaData vmd : vmds)manualEnabledVMDs.add(vmd);
		
		for(ValidatorMetaData data : getValidators()) {
			if(manualEnabledVMDs.contains(data))
				getManualEnabledValidatorsMap().put(data, Boolean.TRUE);
			else
				getManualEnabledValidatorsMap().put(data, Boolean.FALSE);			
		}
	}
	
	/**
	 * Update the list of validators that have been enabled for manual validation.
	 * 
	 * @param vmds the validators that are enabled for manual validation.
	 * @throws InvocationTargetException
	 */
	public void setEnabledManualValidators(Set<ValidatorMetaData> vmds) throws InvocationTargetException {
		for(ValidatorMetaData data : getValidators()) {
			if(vmds.contains(data))getManualEnabledValidatorsMap().put(data, Boolean.TRUE);
			else getManualEnabledValidatorsMap().put(data, Boolean.FALSE);			
		}
	}
	
	public void setEnabledBuildValidators(ValidatorMetaData[] vmds) throws InvocationTargetException {
		Set<ValidatorMetaData> buildEnabledVMDs = new HashSet<ValidatorMetaData>(vmds.length);
		for (ValidatorMetaData vmd : vmds)buildEnabledVMDs.add(vmd);
		for(ValidatorMetaData data : getValidators()) {
			if(buildEnabledVMDs.contains(data))
				getBuildEnabledValidatorsMap().put(data, Boolean.TRUE);
			else
				getBuildEnabledValidatorsMap().put(data, Boolean.FALSE);			
		}
	}
	
	/**
	 * Update the list of validators that have been enabled for build validation.
	 * 
	 * @param vmds the validators that are enabled for build validation.
	 * @throws InvocationTargetException
	 */
	public void setEnabledBuildValidators(Set<ValidatorMetaData> vmds) throws InvocationTargetException {
		for(ValidatorMetaData data : getValidators()) {
			if(vmds.contains(data))getBuildEnabledValidatorsMap().put(data, Boolean.TRUE);
			else getBuildEnabledValidatorsMap().put(data, Boolean.FALSE);			
		}
	}

	private Map<ValidatorMetaData, Boolean> getValidatorMetaData() {
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
		int count = 0;
		for (ValidatorMetaData vmd : getValidatorMetaData().keySet()) {
			Boolean bvalue = getValidatorMetaData().get(vmd);
			if (bvalue.booleanValue() == value) {
				temp[count++] = vmd;
			}
		}

		ValidatorMetaData[] result = new ValidatorMetaData[count];
		System.arraycopy(temp, 0, result, 0, count);
		return result;
	}
	
	public ValidatorMetaData[] getManualEnabledValidators() throws InvocationTargetException {
		Map<ValidatorMetaData, Boolean> map = getManualEnabledValidatorsMap();
		if (!map.isEmpty()) {
			List<ValidatorMetaData> list = new LinkedList<ValidatorMetaData>();
			for (Map.Entry<ValidatorMetaData, Boolean> me : map.entrySet()) {
				Boolean obj = me.getValue();
				if (obj != null && obj)list.add(me.getKey());
			}
			return (ValidatorMetaData[]) list.toArray(new ValidatorMetaData[list.size()]);
		}
		return getEnabledValidators();
	}	
	
	public  ValidatorMetaData[] getBuildEnabledValidators() throws InvocationTargetException {
		Map<ValidatorMetaData, Boolean> map = getBuildEnabledValidatorsMap();
		List<ValidatorMetaData> list = new LinkedList<ValidatorMetaData>();
		if (!map.isEmpty()) {
			for (Map.Entry<ValidatorMetaData, Boolean> me : map.entrySet()) {
				Boolean obj = me.getValue();
				if (obj != null && obj)list.add(me.getKey());
			}
			return (ValidatorMetaData[]) list.toArray(new ValidatorMetaData[list.size()]);
		}
		return getEnabledValidators();
	}
	
	/**
	 * Answer the map of the validators that have been enabled for build validation, 
	 * creating it if necessary.
	 */
	protected  Map<ValidatorMetaData, Boolean> getBuildEnabledValidatorsMap() {
		if(buildValidators == null)buildValidators = new HashMap<ValidatorMetaData, Boolean>();
		return buildValidators;
	}	
	
	/**
	 * Answer the map of the validators that have been enabled for manual validation, 
	 * creating it if necessary.
	 */
	protected  Map<ValidatorMetaData, Boolean> getManualEnabledValidatorsMap() {
		if(manualValidators == null)manualValidators = new HashMap<ValidatorMetaData, Boolean>();
		return manualValidators;
	}	

	/**
	 * Return all incremental validators for this preference; either every installed validator
	 * (global) or every validator configured on the project (project).
	 */
	public ValidatorMetaData[] getIncrementalValidators() throws InvocationTargetException {
		ValidatorMetaData[] temp = new ValidatorMetaData[numberOfValidators()];
		int count = 0;
		for (ValidatorMetaData vmd : getValidatorMetaData().keySet()) {
			Boolean bvalue = getValidatorMetaData().get(vmd);
			if (bvalue && vmd.isIncremental())temp[count++] = vmd;
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
		for (ValidatorMetaData vmd : vmds) {
			if (vmd == null)continue;
			_validators.put(vmd, (vmd.isEnabledByDefault() ? Boolean.TRUE : Boolean.FALSE));
		}
	}
	
  public void setDefaultDelegates(ValidatorMetaData[] vmds) throws InvocationTargetException {
    _delegatesByTarget.clear();
    for (ValidatorMetaData vmd : vmds) {
      if (vmd == null)continue;
      
      String targetID = vmd.getValidatorUniqueName();
      String delegateID = ValidatorDelegatesRegistry.getInstance().getDefaultDelegate(targetID);
      if (delegateID == null)continue;
      _delegatesByTarget.put(targetID, ValidatorDelegatesRegistry.getInstance().getDefaultDelegate(targetID));
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
		for (ValidatorMetaData vmd : vmds) {
			if (vmd.isIncremental())count++;
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

	/**
	 * The value returned from this method is guaranteed to be non-null.
	 */
	public final String getVersion() throws InvocationTargetException {
		if (_version == null)loadVersion();
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
		if (vmd == null)return false;

		Boolean value = getValidatorMetaData().get(vmd);
		if (value == null)return false;

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

	public static ValidatorMetaData[] convertToArray(Collection<ValidatorMetaData> c) {
		int length = (c == null) ? 0 : c.size();
		ValidatorMetaData[] result = new ValidatorMetaData[length];
		if (length == 0)return result;

		int count = 0;
		for (ValidatorMetaData vmd : c)result[count++] = vmd;

		return result;
	}

	public static ValidatorMetaData[] convertToArray(Object[] c) {
		int length = (c == null) ? 0 : c.length;
		ValidatorMetaData[] result = new ValidatorMetaData[length];
		if (length == 0)return result;

		System.arraycopy(c, 0, result, 0, length);
		return result;
	}

	/**
	 * Save the values of these fields before the project or workspace is closed.
	 */
	public void store() throws InvocationTargetException {
		Preferences pref = ValidationPlugin.getPlugin().getPluginPreferences();
		if (pref != null) {
			try {
				OutputStream os = new ByteArrayOutputStream();
				pref.setValue(USER_PREFERENCE, serialize());
				pref.store(os, USER_PREFERENCE);
				pref.setValue(USER_MANUAL_PREFERENCE, serializeManualSetting());
				pref.store(os, USER_MANUAL_PREFERENCE);
				pref.setValue(USER_BUILD_PREFERENCE, serializeBuildSetting());
				pref.store(os, USER_BUILD_PREFERENCE);
				pref.setValue(DELEGATES_PREFERENCE, serializeDelegatesSetting());
				pref.store(os, DELEGATES_PREFERENCE);
			} catch (IOException e) {
				ValidationPlugin.getPlugin().handleException(e);
			}

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
		if (version == null)	return ConfigurationConstants.VERSION4_03;

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
				loadPreference();
			} else {
				// migrate
				load(marker);
			}
		} catch (InvocationTargetException exc) {
			throw exc;
		} catch (Exception exc) {
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
		if (marker == null)return null;

		try {
			return marker.getAttribute(attribName);
		} catch (CoreException e) {
			ValidationPlugin.getPlugin().handleException(e);
			return null;
		}
	}

	protected boolean getValue(IMarker marker, String attribName, boolean defaultValue) {
		Boolean bool = (Boolean) getValue(marker, attribName);
		if (bool == null)return defaultValue;

		return bool.booleanValue();
	}

	protected int getValue(IMarker marker, String attribName, int defaultValue) {
		Integer integer = (Integer) getValue(marker, attribName);
		if (integer == null)return defaultValue;

		return integer.intValue();
	}

	protected String getValue(IMarker marker, String attribName, String defaultValue) {
		String string = (String) getValue(marker, attribName);
		if (string == null)return defaultValue;

		return string;
	}

	protected void loadPreference() throws InvocationTargetException {
		// This method will be called in one of two situations:
		// 1. This is a new workspace and no preferences exist.
		// 2. This is a migrated workspace and the old preferences have already been created as
		// persistent properties.
		Preferences prefs = ValidationPlugin.getPlugin().getPluginPreferences();
		if (prefs != null) {
			deserializeAllPrefs(prefs);
		}
	}

	/**
	 * @param prefs
	 * @throws InvocationTargetException
	 */
	private void deserializeAllPrefs(Preferences prefs) throws InvocationTargetException {
		String storedConfig = prefs.getString(USER_PREFERENCE);
		deserialize(storedConfig);
		String storedManualConfig = prefs.getString(USER_MANUAL_PREFERENCE);
		deserializeManual(storedManualConfig);
		String storedBuildConfig = prefs.getString(USER_BUILD_PREFERENCE);
		deserializeBuild(storedBuildConfig);
		String storedDelegatesConfiguration = prefs.getString(DELEGATES_PREFERENCE);
		deserializeDelegates(storedDelegatesConfiguration);
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		Preferences prefs = (Preferences) event.getSource();
		if (prefs != null && !event.getOldValue().equals(event.getNewValue())) {
			try {
				deserializeAllPrefs(event);
				passivate();
			 } catch (InvocationTargetException e) {
				 ValidationPlugin.getPlugin().handleException(e);
			}
		}
	}

	private void deserializeAllPrefs(PropertyChangeEvent event) throws InvocationTargetException {
		String storedConfig = (String)event.getNewValue();
		if( event.getProperty().equals(USER_PREFERENCE) ){
			deserialize(storedConfig);
		}else if(event.getProperty().equals(USER_MANUAL_PREFERENCE)){
			deserializeManual(storedConfig);
		}else if(event.getProperty().equals(USER_BUILD_PREFERENCE)){
			deserializeBuild(storedConfig);
		}else if(event.getProperty().equals(DELEGATES_PREFERENCE)){
			deserializeDelegates(storedConfig);
		}
	}
	
	
	protected void deserializeBuild(String storedConfiguration) throws InvocationTargetException {
		if (storedConfiguration == null || storedConfiguration.length() == 0 || storedConfiguration.equals(DefaultValue)) {
			// Assume that the configuration has never been set (new workspace).
			resetToDefault();
			return;
		}
		int buildValidationIndex = storedConfiguration.indexOf(ConfigurationConstants.ENABLED_BUILD_VALIDATORS);

		String manualValidation = storedConfiguration.substring(buildValidationIndex + ConfigurationConstants.ENABLED_BUILD_VALIDATORS.length(),storedConfiguration.length());
		setEnabledBuildValidators(getStringAsEnabledElementsArray(manualValidation));
	}

	protected void deserializeManual(String storedConfiguration) throws InvocationTargetException {
		if (storedConfiguration == null || storedConfiguration.length() == 0 || storedConfiguration.equals(DefaultValue)) {
			// Assume that the configuration has never been set (new workspace).
			resetToDefault();
			return;
		}
		int manualValidationIndex = storedConfiguration.indexOf(ConfigurationConstants.ENABLED_MANUAL_VALIDATORS);

		String manualValidation = storedConfiguration.substring(manualValidationIndex + ConfigurationConstants.ENABLED_MANUAL_VALIDATORS.length(),storedConfiguration.length());
		setEnabledManualValidators(getStringAsEnabledElementsArray(manualValidation));
	}

	protected void deserializeDelegates(String storedConfiguration) throws InvocationTargetException {

    if (storedConfiguration == null || storedConfiguration.length() == 0 || storedConfiguration.equals(DefaultValue)) {
	    // Assume that the configuration has never been set (new workspace).
	    resetToDefault();
	    return;
	  }

	  int delegatesIndex = storedConfiguration.indexOf(ConfigurationConstants.DELEGATE_VALIDATORS);

	  String delegates = storedConfiguration.substring(delegatesIndex + ConfigurationConstants.DELEGATE_VALIDATORS.length(),storedConfiguration.length());

	  if (delegates == null) {
	    return;
	  }

	  StringTokenizer tokenizer = new StringTokenizer(delegates, ConfigurationConstants.ELEMENT_SEPARATOR);
	  while (tokenizer.hasMoreTokens()) {
	    String delegateConfiguration = tokenizer.nextToken();
	    int separatorIndex = delegateConfiguration.indexOf(ConfigurationConstants.DELEGATES_SEPARATOR);
	    String targetID = delegateConfiguration.substring(0, separatorIndex);
	    String delegateID = delegateConfiguration.substring(separatorIndex + 1);
      _delegatesByTarget.put(targetID, delegateID);
	  }
	}

	protected void copyTo(ValidationConfiguration up) throws InvocationTargetException {
		up.setVersion(getVersion());
		up.setResource(getResource());
		up.setValidators(getValidators());
		up.setDisableAllValidation(isDisableAllValidation());
		up.setEnabledValidators(getEnabledValidators());
		up.setEnabledManualValidators(getManualEnabledValidators());
		up.setEnabledBuildValidators(getBuildEnabledValidators());
    up.setDelegatingValidators(getDelegatingValidators());
	}

	/**
	 * The key is the id of the place holder validator, and the value is the id of validator
	 * that will perform the validation. 
	 */
	public Map<String, String> getDelegatingValidators() throws InvocationTargetException {
		return _delegatesByTarget;
	}

	public void setDelegatingValidators(Map<String, String> source)
	{
		// It is safe to copy this map as it contains only immutable strings.
		_delegatesByTarget.putAll(source);
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
  
  /**
   * Determines if there has been a change in the list of delegate validators.
   * @param oldDelegates a Map with the old delegates ID by target ID.
   * @return true if there has been a change, false otherwise.
   * @throws InvocationTargetException
   */
  protected boolean haveDelegatesChanged(Map<String, String> oldDelegates) throws InvocationTargetException {
    
    if (oldDelegates == null)return true;
    
    for (String targetID : oldDelegates.keySet()) {
      String oldDelegateID = oldDelegates.get(targetID);
      String newDelegateID = _delegatesByTarget.get(targetID);
      
      if (oldDelegateID == null || newDelegateID == null)return true;
        
      if (!newDelegateID.equals(oldDelegateID))return true;
    }
    
    if (oldDelegates.size() != _delegatesByTarget.size())return true;
    
    return false;
  }

	protected String serialize() throws InvocationTargetException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(ConfigurationConstants.DISABLE_ALL_VALIDATION_SETTING);
		buffer.append(String.valueOf(isDisableAllValidation()));
		buffer.append(ConfigurationConstants.VERSION);
		buffer.append(getVersion());
		return buffer.toString();
	}
	
	protected String serializeManualSetting() throws InvocationTargetException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(ConfigurationConstants.ENABLED_MANUAL_VALIDATORS);
		buffer.append(getEnabledElementsAsString(getManualEnabledValidators()));
		return buffer.toString();
	}
	
	protected String serializeBuildSetting() throws InvocationTargetException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(ConfigurationConstants.ENABLED_BUILD_VALIDATORS);
		buffer.append(getEnabledElementsAsString(getBuildEnabledValidators()));
		return buffer.toString();
	}

  protected String serializeDelegatesSetting() throws InvocationTargetException {
    StringBuffer buffer = new StringBuffer();
    buffer.append(ConfigurationConstants.DELEGATE_VALIDATORS);
    buffer.append(getDelegatesAsString(getValidatorMetaData()));
    return buffer.toString();
  }

  /**
   * Provides a String which contains pairs of targetID=delegateID separated by a semicolon.
   * @param validatorMetaData a Map with the currently configured validators.
   * @return a String.
   */
  private String getDelegatesAsString(Map<ValidatorMetaData, Boolean> validatorMetaData) {
    
    StringBuffer buffer = new StringBuffer();
    for (ValidatorMetaData vmd : validatorMetaData.keySet()) {    
      String targetID = vmd.getValidatorUniqueName();
      String delegateID = getDelegateUniqueName(vmd);
      
      if (delegateID == null)continue;

      // Write out pairs targetID=delegateID

      buffer.append(targetID);
      buffer.append(ConfigurationConstants.DELEGATES_SEPARATOR);
      buffer.append(delegateID);
      buffer.append(ConfigurationConstants.ELEMENT_SEPARATOR);
    }
    
    return buffer.toString();
  }
  
  /**
   * Provides the delegate's ID of the validator delegate configured in this configuration for 
   * a given delegating validator. That is answer the id of validator that is going to do the real work.
   * 
   * @param vmd the delegating validator's meta data. Must not be null.
   * @return a String with the id of the validator delegate, null if there isn't one.
   */
  public String getDelegateUniqueName(ValidatorMetaData vmd) {
    String targetID = vmd.getValidatorUniqueName();    
    return _delegatesByTarget.get(targetID);
  }
  
  /**
   * Provides the delegate's ID of the validator delegate configured in this configuration for 
   * a given delegating validator. That is answer the id of validator that is going to do the real work.
   * 
   * @param targetId the id of the target (or place holder) validator. Must not be null.
   * @return a String with the id of the validator delegate, null if there isn't one.
   */
  public String getDelegateForTarget(String targetId){
	  return _delegatesByTarget.get(targetId);
  }

  /**
   * Sets the delegate's ID of the validator delegate to be used in this configuration for the
   * given delegating validator.
   * 
   * @param vmd the delegating validator's metadata. Must not be null.
   * @param delegateID a String with the unique name (ID) of the validator delegate. Must not be null.
   */
  public void setDelegateUniqueName(ValidatorMetaData vmd, String delegateID) {
    String targetID = vmd.getValidatorUniqueName();    
    _delegatesByTarget.put(targetID, delegateID);
  }

  /**
	 * Deserialize everything except the version number; the version is deserialized first, in the
	 * loadVersion() method.
	 */
	protected void deserialize(String storedConfiguration) throws InvocationTargetException {
		if (storedConfiguration == null || storedConfiguration.length() == 0 || storedConfiguration.equals(DefaultValue)) {
			// Assume that the configuration has never been set (new workspace).
			resetToDefault();
			return;
		}
		int disableAllValidationIndex = storedConfiguration.indexOf(ConfigurationConstants.DISABLE_ALL_VALIDATION_SETTING);
		int versionIndex = storedConfiguration.indexOf(ConfigurationConstants.VERSION);
		if(disableAllValidationIndex != -1) {
				String disableAllValidation = storedConfiguration.substring(disableAllValidationIndex + ConfigurationConstants.DISABLE_ALL_VALIDATION_SETTING.length(),versionIndex);
				setDisableAllValidation(Boolean.valueOf(disableAllValidation).booleanValue());
		} else {
				setDisableAllValidation(false);
		}
		
	}

	public static boolean getDisableValidationDefault() {
		return ConfigurationConstants.DEFAULT_DISABLE_VALIDATION_SETTING;
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
	
	public boolean isManualEnabled(ValidatorMetaData vmd) {
		if (vmd == null)return false;
		Boolean value = getManualEnabledValidatorsMap().get(vmd);
		if (value == null)return false;
		return value;
	}

	public boolean isBuildEnabled(ValidatorMetaData vmd) {
		if (vmd == null)return false;
		Boolean value = getBuildEnabledValidatorsMap().get(vmd);
		if (value == null)return false;
		return value;
	}
	
	public int numberOfManualEnabledValidators() throws InvocationTargetException {
		return getManualEnabledValidators().length;
	}
	
	public void enableSingleValidator(ValidatorMetaData  vmd){
		getValidatorMetaData().put(vmd, Boolean.TRUE);
	}
	
	
	public void disableSingleValidator(ValidatorMetaData  vmd){
		getValidatorMetaData().put(vmd, Boolean.FALSE);
	}
	
	public void enableSingleManualValidator(ValidatorMetaData  vmd){
		getManualEnabledValidatorsMap().put(vmd, Boolean.TRUE);
	}
	
	
	public void disableSingleManualValidator(ValidatorMetaData  vmd){
		getManualEnabledValidatorsMap().put(vmd, Boolean.FALSE);
	}

	
	public void enableSingleBuildValidator(ValidatorMetaData  vmd){
		getBuildEnabledValidatorsMap().put(vmd, Boolean.TRUE);
	}
	
	
	public void disableSingleBuildValidator(ValidatorMetaData  vmd){
		getBuildEnabledValidatorsMap().put(vmd, Boolean.FALSE);
	}
	
  /**
   * Provides the delegate validator descriptor of the validator delegate configured 
   * for the given delegating validator in the context of this configuration. 
   * @param vmd the delegating validator's meta data. Must not be null.
   * @return a ValidatorDelegateDescriptor for the given delegating validator.
   */
  public ValidatorDelegateDescriptor getDelegateDescriptor(ValidatorMetaData vmd)  throws InvocationTargetException {
    String targetID = vmd.getValidatorUniqueName();
    String delegateID = getDelegateUniqueName(vmd);
  
    ValidatorDelegateDescriptor descriptor = ValidatorDelegatesRegistry.getInstance().getDescriptor(targetID, delegateID); 
    return descriptor;    
  }
}
