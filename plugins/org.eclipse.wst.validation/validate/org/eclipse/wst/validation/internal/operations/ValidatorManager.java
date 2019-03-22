/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.operations;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.GlobalConfiguration;
import org.eclipse.wst.validation.internal.InternalValidatorManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.TaskListUtility;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;

/**
 * A centralized class for accessing validation metadata.
 * 
 * This class is not intended to be subclassed outside of the validation framework.
 */
public final class ValidatorManager {
	public static final String VALIDATOR_JOB_FAMILY = "validators";	 //$NON-NLS-1$	
	private static ValidatorManager inst;
	private static IResourceUtil _resourceUtil; // a common utility, different whether or not
	// WSAD is running in headless or UI mode, which can retrieve the line number of some MOF objects.
	private static final Class RESOURCEUTIL_DEFAULTCLASS = org.eclipse.wst.validation.internal.operations.DefaultResourceUtil.class;
	private static Class _resourceUtilClass = RESOURCEUTIL_DEFAULTCLASS;		
	private static Class 	_messageLimitOwner;
	
	private static final Set<ValidatorMetaData> EmptySet = Collections.emptySet();
		
	private String[] 		_internalOwners;
	private Map<IValidatorJob, List<MessageInfo>> _validatorMsgs = 
		Collections.synchronizedMap( new HashMap<IValidatorJob, List<MessageInfo>>() );	
	private Set<ValidatorMetaData> _problemValidators = new HashSet<ValidatorMetaData>();	
	
	private ValidatorManager() {
		super();
		_internalOwners = new String[0];

		addInternalOwner(getMessageLimitOwner());
	}

	/**
	 * Disable all validators for the current project, and does not touch the auto-validate setting.
	 */
	public void disableAllValidators(IProject currentProject, IProgressMonitor monitor) {
		// Disable the individual validators
		setEnabledValidators(currentProject, EmptySet, monitor);
	}

	public void enableValidator(String validatorId) {

		try {
			ValidatorMetaData vmd = ValidationRegistryReader.getReader().getValidatorMetaData(validatorId);
			GlobalConfiguration gf = ConfigurationManager.getManager().getGlobalConfiguration();
			gf.enableSingleValidator(vmd);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	
	public void disableValidator(String validatorId){

		try {
			ValidatorMetaData vmd = ValidationRegistryReader.getReader().getValidatorMetaData(validatorId);
			GlobalConfiguration gf = ConfigurationManager.getManager().getGlobalConfiguration();
			gf.disableSingleValidator(vmd);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	
	public void enableValidator(String validatorId, IProject project, boolean manualValidation, boolean buildValidation) {		
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			prjp.setDoesProjectOverride(true);
			ValidatorMetaData vmd = ValidationRegistryReader.getReader().getValidatorMetaData(validatorId);
			
			if(manualValidation)prjp.enableSingleManualValidator(vmd);
			if (buildValidation)prjp.enableSingleBuildValidator(vmd);
			prjp.store();		
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void disableValidator(String validatorId, IProject project, boolean manualValidation, boolean buildValidation){

		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			prjp.setDoesProjectOverride(true);
			ValidatorMetaData vmd = ValidationRegistryReader.getReader().getValidatorMetaData(validatorId);
			
			if(manualValidation)prjp.disableSingleManualValidator(vmd);
			if (buildValidation)prjp.disableSingleBuildValidator(vmd);
			prjp.store();
			
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}
	
	
	
	/**
	 * Given an IProject, if the IProject does not support build validation, add the validation
	 * builder to the project description so that the project can support build validation.
	 */
	public static void addProjectBuildValidationSupport(IProject project) {
		/*
		 * I'm working my way through the code to see where this gets called. One place was in ModuleCoreNature
		 * which had hard coded the builder id.
		 */
		if (project == null)return;

		boolean isBuilderAdded = false;
		try {
			IProjectDescription description = project.getDescription();
			ICommand[] commands = description.getBuildSpec();
			if (commands != null) {
				for (ICommand command : commands) {
					String builderName = command.getBuilderName();
					if (builderName == null) {
						// builder name will be null if it has not been set
						continue;
					}

					if (builderName.equals(ValidationPlugin.VALIDATION_BUILDER_ID)) {
						isBuilderAdded = true;
						break;
					}
				}
			}

			if (!isBuilderAdded) {
				ICommand newCommand = description.newCommand();
				newCommand.setBuilderName(ValidationPlugin.VALIDATION_BUILDER_ID);

				ICommand[] newCommands = null;
				if (commands != null) {
					newCommands = new ICommand[commands.length + 1];
					System.arraycopy(commands, 0, newCommands, 0, commands.length);
					newCommands[commands.length] = newCommand;
				} else {
					newCommands = new ICommand[1];
					newCommands[0] = newCommand;
				}
				description.setBuildSpec(newCommands);

				project.setDescription(description, null);
			}
		} catch (CoreException exc) {
			// if we can't read the information, the project isn't open, so it can't run
			// auto-validate
			return;
		}
	}


	/**
	 * Given an IProject, this method returns true if the project can run build validation (i.e.,
	 * incremental validation), and false otherwise. The test, to find out if the project supports
	 * build validation or not, is to see if the ValidationBuilder is configured on that type of
	 * project.
	 * <p>
	 * This is a long-running process - is there any way that I can shorten the amount of time this
	 * takes?
	 */
	public static boolean doesProjectSupportBuildValidation(IProject project) {
		boolean canRunAV = false;
		if (project == null) {
			return canRunAV;
		}

		try {
			IProjectDescription description = project.getDescription();
			ICommand[] commands = description.getBuildSpec(); // don't need to check if description
			// is null, because it's never null
			if (commands == null) {
				return canRunAV;
			}

			for (int i = 0; i < commands.length; i++) {
				String builderName = commands[i].getBuilderName();
				if (builderName == null) {
					// builder name will be null if it has not been set
					continue;
				}

				if (builderName.equals(ValidationPlugin.VALIDATION_BUILDER_ID)) {
					canRunAV = true;
					break;
				}
			}
		} catch (CoreException exc) {
			// if we can't read the information, the project isn't open, so it can't run
			// auto-validate
			return false;
		}

		return canRunAV;
	}

	/**
	 * Enable all validators for the current project, and does not touch the auto-validate setting.
	 */
	public void enableAllValidators(IProject project, IProgressMonitor monitor) {
		// Turn auto-validate off
		//	setAutoValidate(currentProject, false);

		// Enable the individual validators
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			prjp.setEnabledValidators(prjp.getValidators());
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
		}
	}

	/**
	 * @deprecated For use by the validation framework only.
	 * 
	 * Return a collection of incremental ValidatorMetaData configured on a certain type of IProject
	 * (e.g. EJB Project vs. Web Project).
	 */
	public Set<ValidatorMetaData> getProjectConfiguredIncrementalValidators(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			return InternalValidatorManager.wrapInSet(prjp.getIncrementalValidators());
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
			return EmptySet;
		}
	}

	/**
	 * @deprecated For use by the validation framework only.
	 * 
	 * Return a collection of ValidatorMetaData configured on a certain type of IProject (e.g. EJB
	 * Project vs. Web Project).
	 * 
	 * If the collection has not been calculated before, calculate it now, and cache the result.
	 */
	public Set<ValidatorMetaData> getProjectConfiguredValidatorMetaData(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			return InternalValidatorManager.wrapInSet(prjp.getValidators());
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
			return EmptySet;
		}
	}

	/**
	 * @deprecated For use by the validation framework only.
	 * 
	 * Return a collection of ValidatorMetaData enabled on a certain type of IProject (e.g. EJB
	 * Project vs. Web Project). The second parameter, isIncremental, identifies whether it's the
	 * incremental, or non-incremental, validators which should be returned. If the parameter is
	 * true, return incremental validators. If the parameter is false, return nonincremental
	 * validators.
	 */
	public Set<ValidatorMetaData> getProjectEnabledIncrementalValidators(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			return InternalValidatorManager.wrapInSet(prjp.getEnabledIncrementalValidators(true));
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
			return EmptySet;
		}
	}

	/**
	 * @deprecated For use by the validation framework only.
	 * 
	 * Return a collection of ValidatorMetaData enabled on a certain type of IProject (e.g. EJB
	 * Project vs. Web Project). The second parameter, isIncremental, identifies whether it's the
	 * incremental, or non-incremental, validators which should be returned. If the parameter is
	 * true, return incremental validators. If the parameter is false, return nonincremental
	 * validators.
	 */
	public Collection<ValidatorMetaData> getProjectEnabledNonIncrementalValidators(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			return InternalValidatorManager.wrapInSet(prjp.getEnabledIncrementalValidators(false));
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
			return EmptySet;
		}
	}

	/**
	 * @deprecated For use by the validation framework only.
	 * 
	 * Returns a Vector of all ValidatorMetaData who are both configured on this type of project,
	 * and who are also enabled by this project. If the list of enabled validators hasn't been
	 * loaded into the cache, load it now. Otherwise, just return it.
	 */
	public Set<ValidatorMetaData> getProjectEnabledValidators(IProject project) {
		return getEnabledValidators(project);
	}

	public static ValidatorManager getManager() {
		if (inst == null) {
			inst = new ValidatorManager();
		}
		return inst;
	}

	public static IResourceUtil getResourceUtil() {
		if (_resourceUtil == null) {
			if (_resourceUtilClass == null) {
				// Since the value returned from this method must never be null,
				// default the resource util to the headless resource util.
				_resourceUtilClass = RESOURCEUTIL_DEFAULTCLASS;
			}

			try {
				_resourceUtil = (IResourceUtil) _resourceUtilClass.newInstance();
			} catch (ClassCastException e) {
				_resourceUtil = null;
			} catch (InstantiationException e) {
				_resourceUtil = null;
			} catch (IllegalAccessException e) {
				_resourceUtil = null;
			}
		}
		return _resourceUtil;
	}

	/**
	 * @deprecated This method is intended for use only by the validation framework. It will be
	 *             moved to a class in the "internal" package at some time.
	 */
	public IMarker[] getValidationTasks(int severity, IProject project) {
		return getValidationTasks(project, severity);
	}

	/**
	 * @deprecated This method is intended for use only by the validation framework. It will be
	 *             moved to a class in the "internal" package at some time.
	 */
	public IMarker[] getValidationTasks(IResource resource, int severity) {
		return TaskListUtility.getValidationTasks(resource, severity);
	}

	/**
	 * @deprecated This method is intended for use only by the validation framework. It will be
	 *             moved to a class in the "internal" package at some time.
	 */
	public IMarker[] getValidationTasks(IResource resource, String[] validatorNames) {
		return TaskListUtility.getValidationTasks(resource, validatorNames);
	}

	/**
	 * @deprecated For use by the validation framework only.
	 */
	public ValidatorMetaData getValidatorMetaData(IValidator validator) {
		return ValidationRegistryReader.getReader().getValidatorMetaData(validator);
	}

	/**
	 * @deprecated For use by the validation framework only.
	 */
	public Set<ValidatorMetaData> getIncrementalValidators(Collection<ValidatorMetaData> vmds) {
		if (vmds == null)return new HashSet<ValidatorMetaData>();

		Set<ValidatorMetaData> result = new HashSet<ValidatorMetaData>();
		for (ValidatorMetaData vmd : vmds) {
			if (vmd.isIncremental())result.add(vmd);
		}
		return result;
	}


	/**
	 * Return true if the validator identified by validatorId will validate the given resource. If
	 * either parameter is null, false is returned. If the validator is not registered with the
	 * validation framework (i.e., either no validator in the activated plugins is identified by
	 * that plugin id, or the given IResource's project does not run that particular validator),
	 * then this method will return false.
	 */
	public boolean isApplicableTo(String validatorId, IResource res) {
		if ((validatorId == null) || (res == null)) {
			return false;
		}

		ValidatorMetaData vmd = ValidationRegistryReader.getReader().getValidatorMetaData(validatorId);
		if (vmd == null) {
			// validator not registered with the framework
			return false;
		}

		if (!ValidationRegistryReader.getReader().isConfiguredOnProject(vmd, res.getProject())) {
			return false;
		}

		return vmd.isApplicableTo(res);
	}


	public boolean isAutoValidate(IProject project) {
		return isAutoValidate(project, isGlobalAutoBuildEnabled());
	}

	/**
	 * Keeping this API around so as not to break existing code.
	 * 
	 * @deprecated use isAutoValidate
	 */
	public boolean isAutoValidateChecked(IProject project) {
		return isAutoValidate(project);
	}

	/**
	 * Keeping this API around so as not to break existing code.
	 * 
	 * @deprecated use isAutoValidate
	 */
	public boolean isAutoValidateChecked(IProject project, boolean isGlobalAutoBuildOn) {
		return isAutoValidate(project, isGlobalAutoBuildOn);
	}

	/**
	 * This method returns true if validate will run when there is a resource change. The following
	 * attributes are checked: 1. does this project have auto-validate on or off? 2. if this project
	 * does not have an auto-validate value set, is the global auto-validate preference on? 3. if 1
	 * or 2 is true, does the project support auto-validate? 4. if 1/2, & 3, is auto-build on? 5. if
	 * 1/2, 3, and 4, is there at least one incremental validator enabled on the project?
	 * 
	 * @deprecated auto validate is not used any more
	 */
	public boolean isAutoValidate(IProject project, boolean isGlobalAutoBuildOn) {
		/*try {
			// 1. does the project have auto-validate on or off?
			boolean isAutoValidate = ConfigurationManager.getManager().getProjectConfiguration(project).isAutoValidate();
			if (!isAutoValidate) {
				return false;
			}

			// 3. does the project support auto-validate?
			// 4. is auto-build on?
			// 5. is there at least one incremental validator enabled on the project?
			
			 * Auto-validation, on the properties page, can be enabled under these conditions: 1.
			 * the project supports auto-validation, AND 2. fhe platform's global "automatically
			 * build" is selected, AND 3. at least one of the project's validators supports
			 * incremental validation. Without #1, the ValidationBuilder is never called because
			 * it's not configured on the project. Without #2, the ValidationBuilder will not be
			 * called because auto-building is turned off. Without #3, the ValidationBuilder will be
			 * called, but there's no point because no validators can run.
			 
			return canAutoValidateButtonBeEnabled(project, isGlobalAutoBuildOn);
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidatorManager.canAutoValidateButtonBeEnabled(" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}

			// If the user's setting can't be retrieved, return the default
			return ValidationConfiguration.getAutoValidateDefault();
		}*/
		return false;
	}

	public boolean canAutoValidateButtonBeEnabled(IProject project) {
		return canAutoValidateButtonBeEnabled(project, isGlobalAutoBuildEnabled());
	}

	/**
	 * @deprecated For use by the validation framework only.
	 */
	public Set<ValidatorMetaData> getEnabledIncrementalValidators(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			ValidatorMetaData[] vmds = prjp.getEnabledIncrementalValidators(true);
			return InternalValidatorManager.wrapInSet(vmds);
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
			return EmptySet;
		}
	}

	/**
	 * @deprecated For use by the validation framework only.
	 * 
	 * Given a checked list of enabled validators, return a set of the ones which are configured on
	 * the project and, if getIncremental is true, which also run incrementally.
	 */
	public Set<ValidatorMetaData> getProjectConfiguredValidators(IProject project, Object[] enabledVal, 
			boolean getIncremental) {
		if ((project == null) || (enabledVal == null) || (enabledVal.length == 0)) {
			return new HashSet<ValidatorMetaData>();
		}

		Set<ValidatorMetaData> val = new HashSet<ValidatorMetaData>();
		for (int i = 0; i < enabledVal.length; i++) {
			ValidatorMetaData vmd = (ValidatorMetaData) enabledVal[i];
			if (!vmd.isConfiguredOnProject(project))continue;

			if (!getIncremental || vmd.isIncremental())val.add(vmd);
		}
		return val;
	}

	public boolean containsIncrementalValidators(Object[] enabledVal) {
		if ((enabledVal == null) || (enabledVal.length == 0)) {
			return false;
		}

		for (int i = 0; i < enabledVal.length; i++) {
			ValidatorMetaData vmd = (ValidatorMetaData) enabledVal[i];

			if (vmd.isIncremental()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * For use by the validation framework only.
	 */
	protected Set<ValidatorMetaData> getEnabledValidators(IProject project) {
		try {
			ValidatorMetaData[] vmds = null;
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			if(!prjp.useGlobalPreference()) 
			   vmds = prjp.getEnabledValidators();
			else
			   vmds = getStateOfProjectLevelValidatorsFromGlobal(prjp);
			return InternalValidatorManager.wrapInSet(vmds);
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
			return EmptySet;
		}
	}

	private ValidatorMetaData[] getStateOfProjectLevelValidatorsFromGlobal(ProjectConfiguration prjp) throws InvocationTargetException {
		List<ValidatorMetaData> enabledGlobalValidatorsForProject = new ArrayList<ValidatorMetaData>();
		GlobalConfiguration gf = ConfigurationManager.getManager().getGlobalConfiguration();
		List<String> allProjectValidator = getAllValidatorUniqueNames(prjp.getValidators());
		for(ValidatorMetaData vmd : gf.getBuildEnabledValidators()) {
			if(allProjectValidator.contains(vmd.getValidatorUniqueName())) {
				enabledGlobalValidatorsForProject.add(vmd);
			}
	   }
		return (ValidatorMetaData[]) enabledGlobalValidatorsForProject.toArray(new ValidatorMetaData[enabledGlobalValidatorsForProject.size()]);
	}
	
	private List<String> getAllValidatorUniqueNames(ValidatorMetaData[] metaData) {
		List<String> names = new ArrayList<String>();
		for(ValidatorMetaData vmd : metaData) {
			names.add(vmd.getValidatorUniqueName());
		}
		return names;
	}
	
	

	protected Set<ValidatorMetaData> getManualEnabledValidators(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			ValidatorMetaData[] vmds = prjp.getManualEnabledValidators();
			return InternalValidatorManager.wrapInSet(vmds);
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
			return EmptySet;
		}
	}	
	
	protected Set<ValidatorMetaData> getBuildEnabledValidators(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			ValidatorMetaData[] vmds = prjp.getBuildEnabledValidators();
			return InternalValidatorManager.wrapInSet(vmds);
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
			return EmptySet;
		}
	}	
	
	/**
	 * This method is for use only by the validation framework. Update the task list based on which
	 * validators are enabled or disabled. This method should be called only by the validation
	 * framework UI classes. Remove the messages belonging to disabled validators.
	 */
	public void updateTaskList(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			ValidatorMetaData[] vmds = prjp.getDisabledValidators();
			for (int i = 0; i < vmds.length; i++) {
				ValidatorMetaData vmd = vmds[i];
				// For validators who aren't going to run, clear their messages from the task list.
				// Don't need to check for duplicate entries because each Validator must be unique.
				// The uniqueness of each Validator is checked by the plugin registry.
				WorkbenchReporter.removeAllMessages(project, vmd.getValidatorNames(), null);
			}
			 //Message Limit is removed from the framework - WTP1.5M5
			/*if (prjp.numberOfEnabledValidators() > 0) {
				ValidatorManager.getManager().checkMessageLimit(project, false); // Do not remove
				// the exceeded
				// message; only
				// ValidationOperation
				// should do that
				// because it's
				// about to run
				// validation. If
				// the limit is
				// increased,
				// messages may
				// still be
				// missing, so
				// don't remove the
				// "messages may be
				// missing"
				// message.
			} else {
				// Can't run validation, so remove the "exceeded" message
				ValidatorManager.getManager().removeMessageLimitExceeded(project);
			}*/
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
		}
	}


	public boolean canAutoValidateButtonBeEnabled(IProject project, boolean isGlobalAutoBuildOn) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			boolean incrementalValEnabled = (prjp.numberOfEnabledIncrementalValidators() > 0);
			return canAutoValidateButtonBeEnabled(project, isGlobalAutoBuildOn, incrementalValEnabled);
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
			return false;
		}
	}

	public boolean canAutoValidateButtonBeEnabled(IProject project, boolean isGlobalAutoBuildOn, boolean incrementalValEnabled) {
		boolean doesProjectSupportAutoValidate = doesProjectSupportBuildValidation(project);
		return (doesProjectSupportAutoValidate && isGlobalAutoBuildOn && incrementalValEnabled);
	}

	/**
	 * Return true if the validator identified by the String is configured on the IProject and
	 * enabled on the IProject. Otherwise return false;
	 */
	public boolean isEnabled(IProject project, String validatorName) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			return prjp.isEnabled(validatorName);
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
			return false;
		}
	}

	/**
	 * @deprecated For use by the validation framework only. Return true if the ValidatorMetaData is
	 *             enabled for the given project
	 */
	public boolean isEnabled(IProject project, ValidatorMetaData vmd) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			return prjp.isEnabled(vmd);
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
			return false;
		}
	}

	/**
	 * @deprecated For use by the validation framework only.
	 */
	public boolean isConfigured(IProject project, ValidatorMetaData vmd) {
		return ValidationRegistryReader.getReader().isConfiguredOnProject(vmd, project);
	}

	/**
	 * This method returns true if the global auto-build setting is turned on.
	 */
	public boolean isGlobalAutoBuildEnabled() {
		return ResourcesPlugin.getWorkspace().isAutoBuilding();
	}

	/**
	 * Returns the number of enabled validators on the given project.
	 */
	public int numberProjectEnabledValidators(IProject project) {
		if (project == null) {
			return 0;
		}

		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			return prjp.numberOfEnabledValidators();
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
			return 0;
		}
	}


	/**
	 * @deprecated Use ValidationFramework.suspendValidation(project, suspend) directly.
	 * @see ValidationFramework#suspendValidation(IProject, boolean)
	 */
	public void suspendValidation(IProject project, boolean suspend) {
		ValidationFramework.getDefault().suspendValidation(project, suspend);
	}

	/**
	 * @deprecated Use ValidationFramework.getDefault().suspendAllValidation(suspend) directly.
	 * @see ValidationFramework#suspendAllValidation(boolean)
	 */
	public void suspendAllValidation(boolean suspend) {
		ValidationFramework.getDefault().suspendAllValidation(suspend);
	}

	/**
	 * @deprecated Use ValidationFramework.getDefault().isSuspended() directly.
	 * @see ValidationFramework#isSuspended()
	 */
	public boolean isSuspended() {
		return ValidationFramework.getDefault().isSuspended();
	}

	/**
	 * @deprecated Use ValidationFramework.getDefault().isSuspended(project) directly.
	 * @see ValidationFramework#isSuspended(IProject)
	 */
	public boolean isSuspended(IProject project) {
		return ValidationFramework.getDefault().isSuspended(project);
	}

	/**
	 * @deprecated Use ValidationFramework.getDefault().isProjectSuspended(project) directly.
	 * @see ValidationFramework#isProjectSuspended(IProject)
	 */
	public boolean isProjectSuspended(IProject project) {
		return ValidationFramework.getDefault().isProjectSuspended(project);
	}

	/**
	 * @deprecated For use by the validation framework only.
	 * 
	 * Given a list of validators' plugin ids, make those validators enabled for this project. All
	 * others, disable for this project.
	 */
	public void setEnabledValidators(IProject project, Set<ValidatorMetaData> vmdsSet, IProgressMonitor monitor) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			ValidatorMetaData[] vmds = null;
			if (vmdsSet == null) {
				vmds = new ValidatorMetaData[0];
			} else {
				vmds = new ValidatorMetaData[vmdsSet.size()];
				vmdsSet.toArray(vmds);
			}

			prjp.setEnabledValidators(vmds);

			updateTaskList(project);
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
		}
	}

	public static void setResourceUtilClass(Class clazz) {
		_resourceUtilClass = clazz;
	}

	/**
	 * This method is for use by batch EJB deploy only. Only in batch mode is an infinite number of
	 * messages allowed.
	 * 
	 * Enable a project to have an infinite number of messages.
	 * @deprecated
	 */
	public void setNoMessageLimit(IProject project) {/*
		setMessageLimit(project, WorkbenchReporter.NO_MESSAGE_LIMIT);
	*/}

	/**
	 * This message is for use only by the validation framework. If the "max messages were reported"
	 * IMarker exists, return it. Otherwise return null.
	 */
	private IMarker[] getLimitMessage(IProject project) {
		IMarker[] exceededMessage = TaskListUtility.getValidationTasks(project, WorkbenchReporter.getUniqueId(getMessageLimitOwner()));
		if ((exceededMessage != null) && (exceededMessage.length != 0)) {
			return exceededMessage;
		}

		return null;
	}

//	/**
//	 * Return true if the given IMarker is a "limit was exceeded" message, false otherwise.
//	 */
//	private boolean isLimitMessage(IMarker marker) {
//		if (marker == null) {
//			return false;
//		}
//
//		return TaskListUtility.isOwner(marker, WorkbenchReporter.getUniqueId(getMessageLimitOwner()));
//	}

//	/**
//	 * Return all of the IMarkers on the IProject excluding the "limit was exceeded" message. If
//	 * there are no markers, return null.
//	 */
//	private IMarker[] getValidationTasksWithoutLimitMessage(IProject project) {
//		IMarker[] allTasks = TaskListUtility.getValidationTasks(project, IMessage.ALL_MESSAGES);
//		if ((allTasks == null) || (allTasks.length == 0)) {
//			return null;
//		}
//
//		// Don't check if the limit message exists because
//		// any interaction with markers is costly. Since the
//		// interaction has to be done at least once, make that
//		// single occasion in the for loop below.
//		IMarker[] validatorMessages = new IMarker[allTasks.length];
//		int count = 0; // how many markers have been added to the result array?
//		for (int i = 0; i < allTasks.length; i++) {
//			IMarker marker = allTasks[i];
//			if (isLimitMessage(marker)) {
//				continue;
//			}
//			validatorMessages[count++] = marker;
//		}
//
//		IMarker[] result = new IMarker[count];
//		System.arraycopy(validatorMessages, 0, result, 0, count);
//		return result;
//	}

	/**
	 * This method should be called only by the validation framework. Return true if the message was
	 * removed, false if the message didn't exist.
	 * @deprecated This method should be not be used anymore as Message Limit is removed from
	 * the framework - WTP1.5M5
	 */
	public boolean removeMessageLimitExceeded(IProject project) {
		IMarker[] exceededMessage = getLimitMessage(project);
		if (exceededMessage != null) {
			try {
				ResourcesPlugin.getWorkspace().deleteMarkers(exceededMessage);
				return true;
			} catch (CoreException e) {
				ValidationPlugin.getPlugin().handleException(e);
			}
		}
		return false;
	}

	/**
	 * Return true if the last validation operation terminated due to the maximum number of messages
	 * having already been reported.
	 */
	public boolean wasValidationTerminated(IProject project) {
		IMarker[] exceededMessage = getLimitMessage(project);
		return (exceededMessage != null); // Validation was terminated if the message exists.
	}

	private Class getMessageLimitOwner() {
		if (_messageLimitOwner == null) {
			_messageLimitOwner = getClass();
		}
		return _messageLimitOwner;
	}

	/**
	 * Return true if owner is the name of the class which owns validation framework status
	 * messages.
	 * 
	 * ONLY the validation framework should use this method.
	 */
	public boolean isInternalOwner(String owner) {
		if (owner == null) {
			return false;
		}

		for (int i = 0; i < _internalOwners.length; i++) {
			String internalOwner = _internalOwners[i];
			if (owner.equals(internalOwner)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Add a class to the list of owners of validation messages. ONLY by the validation framework
	 * should use this method (it is used for messages owned by framework classes or classes used by
	 * the TVT plugin.)
	 */
	public void addInternalOwner(Class clazz) {
		if (clazz == null) {
			return;
		}

		String[] tempInternalOwners = new String[_internalOwners.length + 1];
		if (_internalOwners.length > 0) {
			System.arraycopy(_internalOwners, 0, tempInternalOwners, 0, _internalOwners.length);
		}
		tempInternalOwners[_internalOwners.length] = WorkbenchReporter.getUniqueId(clazz);

		_internalOwners = tempInternalOwners;
	}
	
	public void cacheMessage(IValidatorJob validator, MessageInfo info){
		List<MessageInfo> list = _validatorMsgs.get(validator);
		if( list == null ){
			list = new ArrayList<MessageInfo>();
			_validatorMsgs.put(validator, list);
		}
		list.add(info);
	}
	
	public List<MessageInfo> getMessages(IValidatorJob validator){
		List<MessageInfo> list = _validatorMsgs.get(validator);
		if( list == null )list = new ArrayList<MessageInfo>();		
		return list;
	}
	
	public void clearMessages(IValidatorJob validator){
		List<MessageInfo> list = _validatorMsgs.get(validator);
		if( list != null ){
			list.clear();
		}
		_validatorMsgs.remove( validator );
	}

	
	public Set<ValidatorMetaData> getProblemValidators() {
		return _problemValidators;
	}
	
}
