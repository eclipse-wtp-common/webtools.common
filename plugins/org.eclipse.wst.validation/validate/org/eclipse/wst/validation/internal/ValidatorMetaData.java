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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.validation.core.IValidator;
import org.eclipse.wst.validation.internal.operations.IWorkbenchHelper;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

import com.ibm.wtp.common.logger.proxy.Logger;

/**
 * This class stores information, as specified by a validator's plugin.xml tags. There is one
 * ValidatorMetaData for each Validator. No Validator should attempt to access its
 * ValidatorMetaData; it is for use by the base framework only.
 */
public class ValidatorMetaData {
	private ValidatorFilter[] _filters = null;
	private ValidatorNameFilter[] _projectNatureFilters = null;
	private IValidator _validator = null;
	private IWorkbenchHelper _helper = null;
	private String _validatorDisplayName = null;
	private String _validatorUniqueName = null;
	private String[] _aggregatedValidators = null;
	private String[] _validatorNames = null;
	private String _pluginId = null;
	private boolean _supportsIncremental = RegistryConstants.ATT_INCREMENTAL_DEFAULT;
	private boolean _supportsFullBuild = RegistryConstants.ATT_FULLBUILD_DEFAULT;
	private Logger _logger = null;
	private boolean _isEnabledByDefault = RegistryConstants.ATT_ENABLED_DEFAULT;
	private MigrationMetaData _migrationMetaData = null;
	private int _ruleGroup = RegistryConstants.ATT_RULE_GROUP_DEFAULT;
	private boolean _async = RegistryConstants.ATT_ASYNC_DEFAULT;
	private boolean dependentValidator = RegistryConstants.DEP_VAL_VALUE_DEFAULT;
	private String markerId = null;
	private String _helperClassName = null;
	private IConfigurationElement _helperClassElement = null;
	private IConfigurationElement _validatorClassElement = null;
	private boolean _cannotLoad = false;

	/* package */ValidatorMetaData() {
		//default
	}

	/**
	 * Add to the list of class names of every validator which this validator aggregates. For
	 * example, if the EJB Validator instantiated another validator, and started its validate
	 * method, then that instantiated class' name should be in this list.
	 */
	/* package */void addAggregatedValidatorNames(String[] val) {
		_aggregatedValidators = val;
	}

	/**
	 * Add the name/type filter pair(s).
	 */
	/* package */void addFilters(ValidatorFilter[] filters) {
		_filters = filters;
	}

	/**
	 * Add the project nature filter(s).
	 */
	/* package */void addProjectNatureFilters(ValidatorNameFilter[] filters) {
		_projectNatureFilters = filters;
	}

	public List getNameFilters() {
		List nameFilters = new ArrayList();
		if (_filters != null && _filters.length > 0) {
			for (int i = 0; i < _filters.length; i++) {
				ValidatorFilter filter = _filters[i];
				ValidatorNameFilter nameFilter = filter.get_nameFilter();
				if (nameFilter != null) {
					nameFilters.add(nameFilter.getNameFilter());
				}

			}
		}
		return nameFilters;
	}

	/**
	 * Return the list of class names of the primary validator and its aggregates.
	 */
	public String[] getValidatorNames() {
		if (_validatorNames == null) {
			int aLength = (_aggregatedValidators == null) ? 0 : _aggregatedValidators.length;
			_validatorNames = new String[aLength + 1]; // add 1 for the primary validator name
			_validatorNames[0] = getValidatorUniqueName();
			if (_aggregatedValidators != null) {
				System.arraycopy(_aggregatedValidators, 0, _validatorNames, 1, aLength);
			}
		}
		return _validatorNames;
	}

	/**
	 * Return the list of class names of every validator which this validator aggregates. For
	 * example, if the EJB Validator instantiated another validator, and started its validate
	 * method, then that instantiated class' name should be in this list.
	 */
	public String[] getAggregatedValidatorNames() {
		return _aggregatedValidators;
	}

	/**
	 * Return the name/type filter pairs.
	 */
	public ValidatorFilter[] getFilters() {
		return _filters;
	}

	/**
	 * Return true if this vmd's helper and validator have been instantiated, and also if this
	 * validator's plugin is active.
	 */
	public boolean isActive() {
		if (_helperClassElement != null) {
			return false;
		}

		if (_validatorClassElement != null) {
			return false;
		}

		IPluginDescriptor desc = Platform.getPluginRegistry().getPluginDescriptor(_pluginId);
		if (desc != null)
			return desc.isPluginActivated();

		return false;
	}

	/**
	 * This method will throw an InstantiationException if the helper cannot be instantiated, e.g.,
	 * if the helper's plugin is disabled for some reason. Before the InstantiationException is
	 * thrown, this validator will be disabled.
	 * 
	 * The IWorkbenchHelper must ALWAYS have its project set before it is used; but it can't be
	 * created with the IProject. So, before using the single instance, always initialize that
	 * instance with the IProject.
	 * 
	 * If this validator supports asynchronous validation, then instead of maintaining a single the
	 * helper instance, create a new IWorkbenchHelper instance every time that the helper is needed.
	 * This feature is provided because several validation Runnables may be queued to run, and if
	 * those Runnables's project is different from the current validation's project, then the
	 * current validation will suddenly start validating another project.
	 */
	//TODO just want to remember to figure out the many-temporary-objects problem if this method
	// continues to new an IHelper every time - Ruth
	public IWorkbenchHelper getHelper(IProject project) throws InstantiationException {
		if (isAsync()) {
			IWorkbenchHelper helper = ValidationRegistryReader.createHelper(_helperClassElement, _helperClassName);
			if (helper == null) {
				setCannotLoad();
				throw new InstantiationException(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_DISABLEH, new String[]{_helperClassName, getValidatorUniqueName()}));
			}
			helper.setProject(project);
			return helper;
		}
		if (_helper == null) {
			_helper = ValidationRegistryReader.createHelper(_helperClassElement, _helperClassName);
			if (_helper == null) {
				setCannotLoad();
				throw new InstantiationException(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_DISABLEH, new String[]{_helperClassName, getValidatorUniqueName()}));
			}
			// Won't be using the element & name again, so clear them.
			_helperClassElement = null;
			_helperClassName = null;
		}
		if ((_helper.getProject() == null) || !(_helper.getProject().equals(project))) {
			// Initialize helper with the new project
			_helper.setProject(project);
		}
		return _helper;
	}

	/**
	 * cannotLoad is false if both the IValidator and IWorkbenchHelper instance can be instantiated.
	 * This method should be called only by the validation framework, and only if an
	 * InstantiationException was thrown.
	 * 
	 * @param can
	 */
	private void setCannotLoad() {
		_cannotLoad = true;
	}

	/**
	 * Return false if both the IValidator and IWorkbenchHelper instance can be instantiated.
	 * 
	 * @return boolean
	 */
	public boolean cannotLoad() {
		return _cannotLoad;
	}

	/**
	 * This method must not be called until the unique id of the validator has been initialized.
	 */
	public Logger getMsgLogger() {
		if (_logger == null) {
			_logger = ValidationPlugin.getPlugin().getMsgLogger();
			/*
			 * // Decided against having a logger for each validator because each validator // would
			 * need to contribute an extension in their plugins for it to be recognized // by the
			 * logging preference page. For now, just use the validation framework's logger. _logger =
			 * (MsgLogger)MsgLogger.getFactory().getLogger(getValidatorUniqueName());
			 * _logger.write(Level.CONFIG, getValidatorDisplayName());
			 */
		}
		return _logger;
	}

	public MigrationMetaData getMigrationMetaData() {
		return _migrationMetaData;
	}

	/**
	 * Return the IRuleGroup integer indicating which groups of rules this validator recognizes.
	 */
	public int getRuleGroup() {
		return _ruleGroup;
	}

	/**
	 * Return the filters which identify which project(s) this validator may run on.
	 */
	/* package */ValidatorNameFilter[] getProjectNatureFilters() {
		return _projectNatureFilters;
	}

	/**
	 * This method returns the validator if it can be loaded; if the validator cannot be loaded,
	 * e.g., if its plugin is disabled for some reason, then this method throws an
	 * InstantiationException. Before the CoreException is thrown, this validator is disabled.
	 * 
	 * @return IValidator
	 * @throws InstantiationException
	 */
	public IValidator getValidator() throws InstantiationException {
		if (_validator == null) {
			_validator = ValidationRegistryReader.createValidator(_validatorClassElement, getValidatorUniqueName());

			// Since the element won't be used any more, clear it.
			_validatorClassElement = null;

			if (_validator == null) {
				setCannotLoad();
				throw new InstantiationException(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_DISABLEV, new String[]{getValidatorUniqueName()}));
			}
		}
		return _validator;
	}

	public String getValidatorDisplayName() {
		return _validatorDisplayName;
	}

	public String getValidatorUniqueName() {
		return _validatorUniqueName;
	}

	/**
	 * If the resource is applicable to the Validator which this ValidatorMetaData is associated
	 * with, return true; else return false.
	 * 
	 * A resource is applicable if it passes the name/type filters. This method is called if there
	 * is no resource delta (i.e., a full validation).
	 */
	public boolean isApplicableTo(IResource resource) {
		return isApplicableTo(resource, ValidatorActionFilter.ALL_ACTIONS);
	}

	/**
	 * If the resource is applicable to the Validator which this ValidatorMetaData is associated
	 * with, return true; else return false.
	 * 
	 * A resource is applicable if it passes the name/type filters.
	 */
	public boolean isApplicableTo(IResource resource, int resourceDelta) {
		// If no filters are specified, then every type of resource should be validated/trigger a
		// rebuild of the model cache
		if (_filters == null)
			return true;

		return isApplicableTo(resource, resourceDelta, _filters);
	}

	/**
	 * Return true if the resource passes the name/type filters for this validator.
	 */
	/* package */boolean isApplicableTo(IResource resource, int resourceDelta, ValidatorFilter[] filters) {
		// Are any of the filters satisfied? (i.e., OR them, not AND them.)
		for (int i = 0; i < filters.length; i++) {
			ValidatorFilter filter = filters[i];
			if (filter.isApplicableType(resource) && filter.isApplicableName(resource) && filter.isApplicableAction(resourceDelta)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If this validator recognizes the project nature, whether included or excluded, return the
	 * name filter which describes the nature. Otherwise return null.
	 */
	ValidatorNameFilter findProjectNature(String projId) {
		if (projId == null) {
			return null;
		}

		if (_projectNatureFilters == null) {
			// If no tag is specified, this validator is configured on all IProjects
			return null;
		}

		for (int i = 0; i < _projectNatureFilters.length; i++) {
			ValidatorNameFilter filter = _projectNatureFilters[i];
			// In this case, we're not checking if the project is an instance of the filter class,
			// but if it has the Nature specified in the filter class.
			String projectNatureID = filter.getNameFilter();
			if (projId.equals(projectNatureID)) {
				return filter;
			}
		}
		return null;
	}

	/**
	 * Convenience method. Rather than store the is-this-vmd-configured-on-this-IProject algorithm
	 * in two places, refer back to the reader's cache.
	 */
	public boolean isConfiguredOnProject(IProject project) {
		return ValidationRegistryReader.getReader().isConfiguredOnProject(this, project);
	}

	public boolean isEnabledByDefault() {
		return _isEnabledByDefault;
	}

	public boolean isIncremental() {
		return _supportsIncremental;
	}

	public boolean isFullBuild() {
		return _supportsFullBuild;
	}

	/**
	 * Return true if the validator is thread-safe and can be run asynchronously.
	 */
	public boolean isAsync() {
		return _async;
	}

	/* package */void setHelperClass(IConfigurationElement element, String helperClassName) {
		_helperClassElement = element;
		_helperClassName = helperClassName;
	}

	/* package */void setEnabledByDefault(boolean enabledByDefault) {
		_isEnabledByDefault = enabledByDefault;
	}

	/* package */void setIncremental(boolean isIncremental) {
		_supportsIncremental = isIncremental;
	}

	/* package */void setFullBuild(boolean fullBuild) {
		_supportsFullBuild = fullBuild;
	}

	/* package */void setAsync(boolean isAsync) {
		_async = isAsync;
	}

	/* package */void setMigrationMetaData(MigrationMetaData mmd) {
		_migrationMetaData = mmd;
	}

	/* package */void setRuleGroup(int ruleGroup) {
		_ruleGroup = ruleGroup;
	}

	/* package */void setValidatorClass(IConfigurationElement element) {
		_validatorClassElement = element;
		// validator class name == validatorUniqueName
	}

	/* package */void setValidatorDisplayName(String validatorName) {
		_validatorDisplayName = validatorName;
	}

	/* package */void setValidatorUniqueName(String validatorUniqueName) {
		_validatorUniqueName = validatorUniqueName;
	}

	/* package */void setPluginId(String validatorPluginId) {
		_pluginId = validatorPluginId;
	}

	public String toString() {
		return getValidatorUniqueName();
	}

	public class MigrationMetaData {
		private Set _ids = null;

		public MigrationMetaData() {
			//default
		}

		public void addId(String oldId, String newId) {
			if (oldId == null) {
				// log
				return;
			}

			if (newId == null) {
				// log
				return;
			}

			String[] ids = new String[]{oldId, newId};
			getIds().add(ids);
		}

		public Set getIds() {
			if (_ids == null) {
				_ids = new HashSet();
			}
			return _ids;
		}
	}

	/**
	 * @param b
	 */
	public void addDependentValidator(boolean b) {
		dependentValidator = b;
	}

	/**
	 * @param b
	 */
	public boolean isDependentValidator() {
		return dependentValidator;
	}

	/**
	 * @return Returns the markerId.
	 */
	public String getMarkerId() {
		return markerId;
	}

	/**
	 * @param markerId
	 *            The markerId to set.
	 */
	public void setMarkerId(String markerId) {
		this.markerId = markerId;
	}
}