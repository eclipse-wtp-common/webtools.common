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
package org.eclipse.wst.validation.internal.operations;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.InternalValidatorManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.TaskListUtility;
import org.eclipse.wst.validation.internal.ValidationConfiguration;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * A centralised class for accessing validation metadata.
 * 
 * This class is not intended to be subclassed outside of the validation framework.
 */
public final class ValidatorManager {
	public static final String VALIDATOR_JOB_FAMILY = "validators";	 //$NON-NLS-1$	
	private static ValidatorManager inst = null;
	private static IResourceUtil _resourceUtil = null; // a common utility, different whether or not
	// WSAD is running in headless or UI mode,
	// which can retrieve the line number of some
	// MOF objects.
	private static final Class RESOURCEUTIL_DEFAULTCLASS = org.eclipse.wst.validation.internal.operations.DefaultResourceUtil.class;
	private static Class _resourceUtilClass = RESOURCEUTIL_DEFAULTCLASS;
	private static final Set EMPTY_SET = Collections.EMPTY_SET; // an empty set, provided for
	// convenience, so that we only
	// construct one empty set once.
	private Set _suspendedProjects = null;
	private boolean _suspendAllValidation = false;
	private static Class _messageLimitOwner = null;
	private String[] _internalOwners = null;
	
	/**
	 * ValidatorManager constructor comment.
	 */
	private ValidatorManager() {
		super();
		_suspendedProjects = new HashSet();
		_internalOwners = new String[0];

		addInternalOwner(getMessageLimitOwner());
	}

	/**
	 * Disable all validators for the current project, and does not touch the auto-validate setting.
	 */
	public void disableAllValidators(IProject currentProject, IProgressMonitor monitor) {
		// Disable the individual validators
		setEnabledValidators(currentProject, EMPTY_SET, monitor);
	}

	/**
	 * Given an IProject, if the IProject does not support build validation, add the validation
	 * builder to the project description so that the project can support bulid validation.
	 */
	public static void addProjectBuildValidationSupport(IProject project) {
		if (project == null) {
			return;
		}

		boolean isBuilderAdded = false;
		try {
			IProjectDescription description = project.getDescription();
			ICommand[] commands = description.getBuildSpec(); // don't need to check if description
			// is null, because it's never null
			if (commands != null) {
				for (int i = 0; i < commands.length; i++) {
					String builderName = commands[i].getBuilderName();
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
	 * 
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
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidatorManager::enableAllValidators(" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
		}
	}

	/**
	 * @deprecated For use by the validation framework only.
	 * 
	 * Return a collection of incremental ValidatorMetaData configured on a certain type of IProject
	 * (e.g. EJB Project vs. Web Project).
	 */
	public Set getProjectConfiguredIncrementalValidators(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			return InternalValidatorManager.wrapInSet(prjp.getIncrementalValidators());
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidatorManager::getProjectConfiguredIncrementalValidators(" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
			return Collections.EMPTY_SET;
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
	public Set getProjectConfiguredValidatorMetaData(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			return InternalValidatorManager.wrapInSet(prjp.getValidators());
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidatorManager::getProjectConfiguredValidatorMetaData(" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
			return Collections.EMPTY_SET;
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
	public Set getProjectEnabledIncrementalValidators(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			return InternalValidatorManager.wrapInSet(prjp.getEnabledIncrementalValidators(true));
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidatorManager::getProjectEnabledIncrementalValidators" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
			return Collections.EMPTY_SET;
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
	public Collection getProjectEnabledNonIncrementalValidators(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			return InternalValidatorManager.wrapInSet(prjp.getEnabledIncrementalValidators(false));
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidatorManager::getProjectEnabledNonIncrementalValidators" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
			return Collections.EMPTY_SET;
		}
	}

	/**
	 * @deprecated For use by the validation framework only.
	 * 
	 * Returns a Vector of all ValidatorMetaData who are both configured on this type of project,
	 * and who are also enabled by this project. If the list of enabled validators hasn't been
	 * loaded into the cache, load it now. Otherwise, just return it.
	 */
	public Set getProjectEnabledValidators(IProject project) {
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

	public int getMaximumMessagesAllowed(IProject project) {
		try {
			return ConfigurationManager.getManager().getProjectConfiguration(project).getMaximumNumberOfMessages();
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidatorManager.getMaximumMessagesAllowed(" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}

			return ValidationConfiguration.getMaximumNumberOfMessagesDefault();
		}
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
	public Set getIncrementalValidators(Collection vmds) {
		if (vmds == null) {
			return Collections.EMPTY_SET;
		}

		Set result = new HashSet();
		Iterator iterator = vmds.iterator();
		while (iterator.hasNext()) {
			ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();
			if (vmd.isIncremental()) {
				result.add(vmd);
			}
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
	public Set getEnabledIncrementalValidators(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			ValidatorMetaData[] vmds = prjp.getEnabledIncrementalValidators(true);
			return InternalValidatorManager.wrapInSet(vmds);
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidatorManager::getEnabledIncrementalValidators" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
			return Collections.EMPTY_SET;
		}
	}

	/**
	 * @deprecated For use by the validation framework only.
	 * 
	 * Given a checked list of enabled validators, return a set of the ones which are configured on
	 * the project and, if getIncremental is true, which also run incrementally.
	 */
	public Set getProjectConfiguredValidators(IProject project, Object[] enabledVal, boolean getIncremental) {
		if ((project == null) || (enabledVal == null) || (enabledVal.length == 0)) {
			return Collections.EMPTY_SET;
		}

		Set val = new HashSet();
		for (int i = 0; i < enabledVal.length; i++) {
			ValidatorMetaData vmd = (ValidatorMetaData) enabledVal[i];
			if (!vmd.isConfiguredOnProject(project)) {
				continue;
			}

			if (!getIncremental || vmd.isIncremental()) {
				val.add(vmd);
			}
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
	protected Set getEnabledValidators(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			ValidatorMetaData[] vmds = prjp.getEnabledValidators();
			return InternalValidatorManager.wrapInSet(vmds);
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidatorManager::getEnabledValidators" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
			return Collections.EMPTY_SET;
		}
	}

	protected Set getManualEnabledValidators(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			ValidatorMetaData[] vmds = prjp.getManualEnabledValidators();
			return InternalValidatorManager.wrapInSet(vmds);
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidatorManager::getEnabledValidators" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
			return Collections.EMPTY_SET;
		}
	}	
	
	protected Set getBuildEnabledValidators(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			ValidatorMetaData[] vmds = prjp.getBuildEnabledValidators();
			return InternalValidatorManager.wrapInSet(vmds);
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidatorManager::getEnabledValidators" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
			return Collections.EMPTY_SET;
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
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidatorManager.updateTaskList(" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
		}
	}


	public boolean canAutoValidateButtonBeEnabled(IProject project, boolean isGlobalAutoBuildOn) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			boolean incrementalValEnabled = (prjp.numberOfEnabledIncrementalValidators() > 0);
			return canAutoValidateButtonBeEnabled(project, isGlobalAutoBuildOn, incrementalValEnabled);
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidatorManager::canAutoValidateButtonBeEnabled" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
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
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidatorManager.isEnabled(" + project.getName() + ", " + validatorName + ")"); //$NON-NLS-1$  //$NON-NLS-2$ //$NON-NLS-3$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
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
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidatorManager::isEnabled" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
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
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidatorManager::numberProjectEnabledValidators" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
			return 0;
		}
	}


	/**
	 * Suspends, or undoes the suspension of, validation on the current project. If "suspend" is
	 * true then validation is suspended and if it's "false" then validation will run on the
	 * project. The value of this variable is not persisted.
	 * 
	 * Be VERY CAREFUL when you use this method! Turn validation back on in a finally block because
	 * if the code which suspended validation crashes, the user has no way to unsuspend validation.
	 * The user will have to shut down & restart WSAD to get validation to work again.
	 * 
	 * If an operation is used to make changes that should not be validated, then use the technique
	 * documented in the "Preventing Validation" topic of the "Validation Guide" (in the
	 * org.eclipse.wst.validation.internal.provisional.core.core.prop plugin). If you don't, validation may not be suspended.
	 */
	public void suspendValidation(IProject project, boolean suspend) {
		if (project == null) {
			return;
		}

		if (!project.exists()) {
			return;
		}

		// Ignore whether or not the project is closed. If it's closed then it will not be built
		// and the "Run Validation" option will not be available.
		if (suspend) {
			_suspendedProjects.add(project);
		} else {
			_suspendedProjects.remove(project);
		}
	}

	/**
	 * Suspends, or undoes the suspension of, validation on all projects in the workbench. If
	 * "suspend" is true then validation is suspended and if it's "false" then validation will run.
	 * The value of this variable is not persisted.
	 * 
	 * Be VERY CAREFUL when you use this method! Turn validation back on in a finally block because
	 * if the code which suspended validation crashes, the user has no way to unsuspend validation.
	 * The user will have to shut down & restart WSAD to get validation to work again.
	 * 
	 * If an operation is used to make changes that should not be validated, then use the technique
	 * documented in the "Preventing Validation" topic of the "Validation Guide" (in the
	 * org.eclipse.wst.validation.internal.provisional.core.core.prop plugin). If you don't, validation may not be suspended.
	 */
	public void suspendAllValidation(boolean suspend) {
		_suspendAllValidation = suspend;
	}

	/**
	 * Return true if "suspend all" is enabled, false otherwise.
	 */
	public boolean isSuspended() {
		return _suspendAllValidation;
	}

	/**
	 * Returns true if validation will not run on the project because it's been suspended. This
	 * method checks only the suspension status; if validation cannot run for some other reason (for
	 * example, there are no enabled validators), yet the IProject is not suspended, this method
	 * will return true even though validation will not run.
	 */
	public boolean isSuspended(IProject project) {
		if (project == null) {
			return false;
		}

		if (_suspendAllValidation) {
			return true;
		}

		return _suspendedProjects.contains(project);
	}

	/**
	 * This method should be called by any code that is preparing to suspend validation on a
	 * project. Rather than calling isSuspsend(IProject), which will return true if all validation
	 * has been suspended, this method returns the state of the project itself. See the
	 * ValidationMigrator::migrateProject for an example.
	 * 
	 * @param project
	 * @return boolean
	 */
	public boolean isProjectSuspended(IProject project) {
		if (project == null) {
			return false;
		}

		return _suspendedProjects.contains(project);
	}

	/**
	 * @deprecated For use by the validation framework only.
	 * 
	 * Given a list of validators' plugin ids, make those validators enabled for this project. All
	 * others, disable for this project.
	 */
	public void setEnabledValidators(IProject project, Set vmdsSet, IProgressMonitor monitor) {
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
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidatorManager.setEnabledValidators(" + project.getName() + ", Set, IProgressMonitor)"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
		}
	}

	public static void setResourceUtilClass(Class clazz) {
		_resourceUtilClass = clazz;
	}

	/**
	 * This method is for use by batch EJB deploy only. Only in batch mode is an infinitie number of
	 * messages allowed.
	 * 
	 * Enable a project to have an infinite number of messages.
	 * @deprecated
	 */
	public void setNoMessageLimit(IProject project) {/*
		setMessageLimit(project, WorkbenchReporter.NO_MESSAGE_LIMIT);
	*/}

	/**
	 * This method is for use by batch EJB deploy only. Only in batch mode is an infinitie number of
	 * messages allowed.
	 * 
	 * Return true if the given project is allowed an infinite number of validation messages.
	 */
	public boolean isNoMessageLimit(IProject project) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			int max = prjp.getMaximumNumberOfMessages();
			if (max == WorkbenchReporter.NO_MESSAGE_LIMIT) {
				return true;
			}
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidatorManager.setEnabledValidators(" + project.getName() + ", Set, IProgressMonitor)"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
		}
		return false;
	}

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

	/**
	 * Return true if the given IMarker is a "limit was exceeded" message, false otherwise.
	 */
	private boolean isLimitMessage(IMarker marker) {
		if (marker == null) {
			return false;
		}

		return TaskListUtility.isOwner(marker, WorkbenchReporter.getUniqueId(getMessageLimitOwner()));
	}

	/**
	 * Return all of the IMarkers on the IProject excluding the "limit was exceeded" message. If
	 * there are no markers, return null.
	 */
	private IMarker[] getValidationTasksWithoutLimitMessage(IProject project) {
		IMarker[] allTasks = TaskListUtility.getValidationTasks(project, IMessage.ALL_MESSAGES);
		if ((allTasks == null) || (allTasks.length == 0)) {
			return null;
		}

		// Don't check if the limit message exists because
		// any interaction with markers is costly. Since the
		// interaction has to be done at least once, make that
		// single occasion in the for loop below.
		IMarker[] validatorMessages = new IMarker[allTasks.length];
		int count = 0; // how many markers have been added to the result array?
		for (int i = 0; i < allTasks.length; i++) {
			IMarker marker = allTasks[i];
			if (isLimitMessage(marker)) {
				continue;
			}
			validatorMessages[count++] = marker;
		}

		IMarker[] result = new IMarker[count];
		System.arraycopy(validatorMessages, 0, result, 0, count);
		return result;
	}

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
			} catch (CoreException exc) {
				Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidatorManager.removeMessageLimitExceeded(" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
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

}