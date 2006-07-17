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


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.internal.operations.IHeadlessRunnableWithProgress;
import org.eclipse.wst.validation.internal.FilterUtil;
import org.eclipse.wst.validation.internal.IProjectValidationHelper;
import org.eclipse.wst.validation.internal.InternalValidatorManager;
import org.eclipse.wst.validation.internal.ReferencialFileValidatorRegistryReader;
import org.eclipse.wst.validation.internal.RegistryConstants;
import org.eclipse.wst.validation.internal.ResourceConstants;
import org.eclipse.wst.validation.internal.ResourceHandler;
import org.eclipse.wst.validation.internal.TaskListUtility;
import org.eclipse.wst.validation.internal.TimeEntry;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.core.EmptySchedulingRule;
import org.eclipse.wst.validation.internal.core.IFileDelta;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.core.ValidatorLauncher;
import org.eclipse.wst.validation.internal.plugin.ValidationHelperRegistryReader;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.eclipse.wst.validation.internal.provisional.core.MessageLimitException;

/**
 * Implemented Validators methods must not be called directly by anyone other than this class, since
 * some initialization of the validator is done here (via the getProject() method). The
 * initialization is separated because the IProject isn't known until runtime.
 * 
 * This operation is not intended to be subclassed outside of the validation framework.
 */
public abstract class ValidationOperation implements IWorkspaceRunnable, IHeadlessRunnableWithProgress {
	public static final int NO_DELTA_CHANGE = -1; // Since IResourceConstants
	// doesn't have a "no delta"
	// flag, let this constant be
	// the flag.
	private static final String DELTA_AS_STRING = "IFileDelta[{0}] '{'{1}'}'"; //$NON-NLS-1$
	private static final String COMMA = ", "; //$NON-NLS-1$
	protected static final boolean DEFAULT_ASYNC = false; // For the deprecated
	// constructors, by
	// default the
	// operation will not
	// fork.
	protected static final boolean DEFAULT_FORCE = true; // By default, run the
	// operation whether
	// or not it needs to
	/**
	 * @deprecated Will be removed in Milestone 3. Use DEFAULT_ASYNC
	 */
	protected static final boolean DEFAULT_FORK = false; // @deprecated
	private IProject _project = null; // project to be validated
	private int _ruleGroup = RegistryConstants.ATT_RULE_GROUP_DEFAULT; // which
	// pass
	// should
	// the
	// validation
	// invoke
	private boolean _fork = DEFAULT_ASYNC; // do not fork the validation into a
	// different thread by default
	private Map _fileDeltas = null; // To reduce object creation,
	private IResourceDelta _delta = null;
	// the resource delta tree to be processed, or null if a full build/menu
	// option was triggered. This value is cached so that validation can be run
	// either from a builder, or from a menu item. (The Operation interface
	// doesn't allow any parameter on execute except the IProgressMonitor.)
	private Set _enabledValidators = null;
	private boolean _force = DEFAULT_FORCE; // force this operation to run even
	// if it doesn't need to?
	private boolean _isFullValidate = false; // Run a full validation or an
	// incremental? (true = full)
	private Boolean _isAutoBuild = null; // Is the global auto-build preference
	// enabled?
	private Set _launchedValidators = null; // A list of the validators that
	
	protected IWorkbenchContext context;

	// are enabled and were launched
	// (i.e., that have input to
	// validate). For internal
	// validation framework use only;
	// it's needed for the automatic
	// tests.
	/**
	 * This method is used for FINEST logging, to report exactly what deltas were about to be
	 * validated.
	 */
	private static final String getDeltaAsString(IFileDelta[] delta) {
		String args = ""; //$NON-NLS-1$
		int numArgs = 0;
		if (delta != null) {
			numArgs = delta.length;
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < delta.length; i++) {
				buffer.append(COMMA);
				buffer.append(delta[i].toString());
			}
			buffer.replace(0, 1, ""); //$NON-NLS-1$ // magic numbers 0 and 1 => Remove first COMMA from the list (hence index 0); length of COMMA is 2, hence index 0, 1.
			args = buffer.toString();
		}
		return MessageFormat.format(DELTA_AS_STRING, new String[]{String.valueOf(numArgs), args});
	}

	protected static void checkCanceled(WorkbenchReporter reporter) throws OperationCanceledException {
		if (reporter == null) {
			return;
		} else if (reporter.getProgressMonitor().isCanceled()) {
			throw new OperationCanceledException(""); //$NON-NLS-1$
		}
	}

	protected static boolean shouldForce(IResourceDelta delta) {
		return ((delta == null) ? DEFAULT_FORCE : false);
	}

	protected static boolean shouldForce(Object[] changedResources) {
		return (((changedResources == null) || (changedResources.length == 0)) ? DEFAULT_FORCE : false);
	}

	/**
	 * @deprecated. Will be removed in Milestone 3. Use ValidationOperation(project, boolean)
	 */
	public ValidationOperation(IProject project) {
		this(project, DEFAULT_ASYNC, DEFAULT_FORCE);
	}

	/**
	 * Internal.
	 */
	public ValidationOperation(IProject project, boolean force, boolean async) {
		this(project, null, null, RegistryConstants.ATT_RULE_GROUP_DEFAULT, force, async);
	}

	/**
	 * @deprecated. Will be removed in Milestone 3. Use ValidationOperation(project, int, boolean)
	 */
	public ValidationOperation(IProject project, int ruleGroup) {
		this(project, null, null, ruleGroup, DEFAULT_FORCE, DEFAULT_ASYNC);
	}

	/**
	 * @deprecated. Will be removed in Milestone 3. Use ValidationOperation(IProject,
	 *              IResourceDelta, Boolean, int, boolean, boolean)
	 */
	public ValidationOperation(IProject project, IResourceDelta delta, boolean isAutoBuild, int ruleGroup, boolean force, boolean fork) {
		this(project, delta, ((isAutoBuild) ? Boolean.TRUE : Boolean.FALSE), ruleGroup, fork, force);
	}

	/**
	 * Internal.
	 */
	protected ValidationOperation(IProject project, IResourceDelta delta, Boolean isAutoBuild, int ruleGroup, boolean force, boolean fork) {
		super();
		_project = project;
		_delta = delta;
		_isAutoBuild = isAutoBuild;
		_ruleGroup = ruleGroup;
		_fork = fork;
		_force = force;
		_enabledValidators = new HashSet();
	}
	
	/**
	 * Internal.
	 */
	protected ValidationOperation(IProject project, IWorkbenchContext aContext, IResourceDelta delta, Boolean isAutoBuild, int ruleGroup, boolean force, boolean fork) {
		super();
		_project = project;
		_delta = delta;
		_isAutoBuild = isAutoBuild;
		_ruleGroup = ruleGroup;
		_fork = fork;
		_force = force;
		_enabledValidators = new HashSet();
		context = aContext;
	}

	/**
	 * @deprecated Will be removed in Milestone 3.
	 */
	protected void terminateCleanup(WorkbenchReporter reporter) {
		Set enabledValidators = getEnabledValidators();
		Iterator iterator = enabledValidators.iterator();
		ValidatorMetaData vmd = null;

		while (iterator.hasNext()) {
			vmd = (ValidatorMetaData) iterator.next();
			reporter.displaySubtask(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_VALIDATOR_CLEANUP, new String[]{vmd.getValidatorDisplayName()}));
			try {
				reporter.removeAllMessages(vmd.getValidator());
			} catch (InstantiationException exc) {
				// Remove the vmd from the reader's list
				ValidationRegistryReader.getReader().disableValidator(vmd);
				// Log the reason for the disabled validator
				final Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidationOperation::terminateCleanup"); //$NON-NLS-1$
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
				continue;
			}
			addCancelTask(vmd);
			reporter.displaySubtask(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_VALIDATOR_TERMINATED, new String[]{getProject().getName(), vmd.getValidatorDisplayName()}));
		}
	} 
	

	/**
	 * @param vmd
	 * @param delta
	 * @param logger
	 * @param start
	 */
//	private void logValidationInfo(ValidatorMetaData vmd, IFileDelta[] delta, Logger logger, long start) {
//		long finish = System.currentTimeMillis();
//		TimeEntry entry = ValidationPlugin.getTimeEntry();
//		entry.setSourceID("ValidationOperation.launchValidator"); //$NON-NLS-1$
//		entry.setProjectName(getProject().getName());
//		entry.setToolName(vmd.getValidatorUniqueName());
//		entry.setElapsedTime(finish - start);
//		if (logger.isLoggingLevel(Level.FINE)) {
//			StringBuffer buffer = new StringBuffer();
//			if (isFullValidate()) {
//				buffer.append("EVERYTHING"); //$NON-NLS-1$
//			} else {
//				if (delta.length == 0) {
//					buffer.append("NOTHING"); //$NON-NLS-1$
//				} else {
//					buffer.append(getDeltaAsString(delta));
//				}
//			}
//			entry.setDetails(buffer.toString());
//		}
//		logger.write(Level.INFO, entry);
//	}

	/**
	 * @param reporter
	 * @param vmd
	 * @param logger
	 * @param exc
	 */
//	private void handleThrowables(WorkbenchReporter reporter, ValidatorMetaData vmd, Logger logger, Throwable exc) {
//		// If a runtime exception has occured, e.g. NullPointer or ClassCast,
//		// display it with the "A runtime exception has occurred " messsage.
//		// This will provide more information to the user when he/she calls IBM
//		// Service.
//		if (logger.isLoggingLevel(Level.SEVERE)) {
//			LogEntry entry = ValidationPlugin.getLogEntry();
//			entry.setSourceID("ValidationOperation::launchValidator"); //$NON-NLS-1$
//			entry.setTargetException(exc);
//			logger.write(Level.SEVERE, entry);
//		}
//		String[] msgParm = {exc.getClass().getName(), vmd.getValidatorDisplayName(), (exc.getMessage() == null ? "" : exc.getMessage())}; //$NON-NLS-1$
//		Message message = ValidationPlugin.getMessage();
//		message.setSeverity(IMessage.NORMAL_SEVERITY);
//		message.setId(ResourceConstants.VBF_EXC_RUNTIME);
//		message.setParams(msgParm);
//		try {
//			reporter.addMessage(vmd.getValidator(), message);
//		} catch (InstantiationException exc2) {
//			handleInstantiationException(vmd, logger, exc2);
//		} catch (MessageLimitException e) {
//			throw e;
//		}
//		return;
//	}

	/**
	 * @param vmd
	 * @param logger
	 * @param exc2
	 */
//	private void handleInstantiationException(ValidatorMetaData vmd, Logger logger, InstantiationException exc2) {
//		// Remove the vmd from the reader's list
//		ValidationRegistryReader.getReader().disableValidator(vmd);
//		// Log the reason for the disabled validator
//		if (logger.isLoggingLevel(Level.SEVERE)) {
//			LogEntry entry = ValidationPlugin.getLogEntry();
//			entry.setSourceID("ValidationOperation::launchValidator (deprecated)"); //$NON-NLS-1$
//			entry.setTargetException(exc2);
//			logger.write(Level.SEVERE, entry);
//		}
//	}

	/**
	 * @param reporter
	 * @param vmd
	 * @param logger
	 * @param exc
	 */
//	private void handleValidationExceptions(WorkbenchReporter reporter, ValidatorMetaData vmd, Logger logger, ValidationException exc) {
//		// First, see if a validator just caught all Throwables and
//		// accidentally wrapped a MessageLimitException instead of propagating
//		// it.
//		if (exc.getAssociatedException() != null) {
//			if (exc.getAssociatedException() instanceof MessageLimitException) {
//				MessageLimitException mssgExc = (MessageLimitException) exc.getAssociatedException();
//				throw mssgExc;
//			} else if (exc.getAssociatedException() instanceof ValidationException) {
//				try {
//					ValidationException vexc = (ValidationException) exc.getAssociatedException();
//					vexc.setClassLoader(vmd.getValidator().getClass().getClassLoader()); // first,
//					// set the class loader,so that the exception's getMessage() method can retrieve
//					// the resource bundle
//				} catch (InstantiationException exc2) {
//					handleInstantiationException(vmd, logger, exc2);
//				}
//			}
//		}
//		// If there is a problem with this particular validator, log the error
//		// and continue
//		// with the next validator.
//		try {
//			exc.setClassLoader(vmd.getValidator().getClass().getClassLoader()); // first,
//			// set the class loader,so that the exception's getMessage() method can retrieve the
//			// resource bundle
//		} catch (InstantiationException exc2) {
//			handleInstantiationException(vmd, logger, exc2);
//		}
//		if (logger.isLoggingLevel(Level.SEVERE)) {
//			LogEntry entry = ValidationPlugin.getLogEntry();
//			entry.setSourceID("ValidationOperation.validate(WorkbenchMonitor)"); //$NON-NLS-1$
//			entry.setTargetException(exc);
//			logger.write(Level.SEVERE, entry);
//			if (exc.getAssociatedException() != null) {
//				entry.setTargetException(exc.getAssociatedException());
//				logger.write(Level.SEVERE, entry);
//			}
//		}
//		String message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_ENDING_VALIDATION_ABNORMALLY, new String[]{getProject().getName(), vmd.getValidatorDisplayName()});
//		reporter.displaySubtask(message);
//		if (exc.getAssociatedMessage() != null) {
//			try {
//				reporter.addMessage(vmd.getValidator(), exc.getAssociatedMessage());
//			} catch (InstantiationException exc2) {
//				handleInstantiationException(vmd, logger, exc2);
//			}
//		}
//	}

	/**
	 * @param reporter
	 * @param vmd
	 * @param logger
	 * @param exc
	 */
//	private void handleHelperCleanupExceptions(WorkbenchReporter reporter, ValidatorMetaData vmd, Logger logger, Throwable exc) {
//		// If a runtime exception has occured, e.g. NullPointer or ClassCast,
//		// display it with the "A runtime exception has occurred " messsage.
//		// This will provide more information to the user when he/she calls IBM
//		// Service.
//		if (logger.isLoggingLevel(Level.SEVERE)) {
//			LogEntry entry = ValidationPlugin.getLogEntry();
//			entry.setSourceID("ValidationOperation::launchValidator"); //$NON-NLS-1$
//			entry.setTargetException(exc);
//			logger.write(Level.SEVERE, entry);
//		}
//		String[] msgParm = {exc.getClass().getName(), vmd.getValidatorDisplayName(), (exc.getMessage() == null ? "" : exc.getMessage())}; //$NON-NLS-1$
//		Message message = ValidationPlugin.getMessage();
//		message.setSeverity(IMessage.NORMAL_SEVERITY);
//		message.setId(ResourceConstants.VBF_EXC_RUNTIME);
//		message.setParams(msgParm);
//		try {
//			reporter.addMessage(vmd.getValidator(), message);
//		} catch (InstantiationException exc2) {
//			handleInstantiationException(vmd, logger, exc2);
//		} catch (MessageLimitException e) {
//			throw e;
//		}
//		return;
//	}

	public boolean isFork() {
		return _fork;
	}

	public boolean isForce() {
		return _force;
	}

	public void setForce(boolean force) {
		_force = force;
	}

	/**
	 * If the code that invoked this operation suspended auto-build before invoking this operation,
	 * the user's auto-build setting is stored in the following methods.
	 */
	public boolean isAutoBuild() {
		if (_isAutoBuild == null) {
			return ValidatorManager.getManager().isGlobalAutoBuildEnabled();
		}
		return _isAutoBuild.booleanValue();
	}

	protected void setAutoBuild(boolean autoOn) {
		_isAutoBuild = ((autoOn) ? Boolean.TRUE : Boolean.FALSE);
	}

	protected boolean isFullValidate() {
		return _isFullValidate;
	}

	private void setFullValidate(boolean b) {
		_isFullValidate = b;
	}

	protected int getRuleGroup() {
		return _ruleGroup;
	}

	/**
	 * Return true if the given validator must run (i.e., it has changes to validate, and it was not
	 * run automatically.)
	 */
	private boolean isValidationNecessary(ValidatorMetaData vmd, IFileDelta[] delta) {
		// Validation is not necessary if:
		//    1. auto-validation has run and the validator is incremental,
		//    2. There are no files for the validator to validate.
		// There are files to validate if this is a full validation or if the
		// validator filtered in some of the deltas.
		boolean autoValidateRan = false;
		if (_isAutoBuild != null) {
			// User set the autoBuild default, so check if validation is
			// necessary or not.
			autoValidateRan = ValidatorManager.getManager().isAutoValidate(getProject(), _isAutoBuild.booleanValue()) && vmd.isIncremental();
		}
		boolean hasFiles = (isFullValidate() || (delta.length > 0));
		return (!autoValidateRan && hasFiles);
	}

	/**
	 * Return true if, given the enabled validators and file deltas, there is work for this
	 * operation to do.
	 */
	public boolean isNecessary(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		Set enabledValidators = getEnabledValidators();
		if ((enabledValidators == null) || (enabledValidators.size() == 0)) {
			return false;
		}
		if (isFullValidate()) {
			return true;
		}
		Iterator iterator = enabledValidators.iterator();
		while (iterator.hasNext()) {
			ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();
			if (isValidationNecessary(vmd, getFileDeltas(monitor, vmd))) {
				return true;
			}
		}
		return false;
	}

	private Map getFileDeltas(IProgressMonitor monitor) throws CoreException {
		if (_fileDeltas == null) {
			loadFileDeltas(monitor);
		}
		return _fileDeltas;
	}

	private IFileDelta[] getFileDeltas(IProgressMonitor monitor, ValidatorMetaData vmd) throws CoreException {
		Set result = (Set) getFileDeltas(monitor).get(vmd);
		if (result == null) {
			return new IFileDelta[0];
		}
		IFileDelta[] temp = new IFileDelta[result.size()];
		result.toArray(temp);
		return temp;
	}

	/**
	 * Store the file deltas (VMD <=>Set[IFileDelta]) if the file deltas haven't been loaded. If the
	 * deltas have already been loaded, return without doing anything.
	 */
	private void loadFileDeltas(IProgressMonitor monitor) throws CoreException {
		// Although, for a full build, we don't build up a list of changed
		// files, we do need to notify each IWorkbenchContext that an
		// IResource has been filtered in.
		// It's a full validation if the IResourceDelta is null and the
		// Object[] (or IResource[]) is also null.
		// i.e., it's a full validation if no incremental input has been set.
		setFullValidate((getDelta() == null) && (_fileDeltas == null));
		if (isFullValidate()) {
			_fileDeltas = FilterUtil.loadDeltas(monitor, getEnabledValidators(), getProject());
		} else {
			_fileDeltas = FilterUtil.loadDeltas(monitor, getEnabledValidators(), getDelta()); // traverse,
			// and process, each resource in the delta tree
		}
	}

	protected void setFileDeltas(Map deltas) {
		_fileDeltas = deltas;
	}

	protected IResourceDelta getDelta() {
		return _delta;
	}

	protected void setDelta(IResourceDelta delta) {
		_delta = delta;
	}

	protected boolean areValidatorsEnabled() {
		return (getEnabledValidators().size() != 0);
	}

	/**
	 * Return the validators which are both configured on this type of project, (as stored in
	 * getProject()), and enabled by the user on this project.
	 */
	public Set getEnabledValidators() {
		return _enabledValidators;
	}

	/**
	 * This is an internal method, subject to change without notice. It is provided only for the
	 * automated validation framework tests.
	 */
	public Set getLaunchedValidators() {
		if (_launchedValidators == null) {
			_launchedValidators = new HashSet();
		}
		return _launchedValidators;
	}

	protected void setEnabledValidators(Set evmds) {
		// Check that every VMD in the set is configured on this project.
		// Necessary because the user can manually choose which validators
		// to launch, and the validator may not be installed.
		_enabledValidators.clear();
		Iterator iterator = evmds.iterator();
		while (iterator.hasNext()) {
			ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();
			if (ValidationRegistryReader.getReader().isConfiguredOnProject(vmd, getProject())) {
				_enabledValidators.add(vmd);
			}
		}
	}

	/**
	 * This method returns the IProject that this ValidationOperation was created with.
	 */
	public IProject getProject() {
		return _project;
	}

	protected int getUnitsOfWork() {
		/*
		 * // Let one unit of work equal one resource. number of enabled validators // i.e., each
		 * enabled validator must process (at most) each resource in the project; count each process
		 * as one unit of work // Note that this is a ceiling number, because if we're doing an
		 * incremental validation, not all resources will // be validated.
		 * setNumResources(countResources(getProject())); getEnabledValidators().size();
		 */
		// Until the validators can report units-of-work complete,
		// initialize the monitor with an unknown amount of work.
		// (So the user will see movement in the progress bar, even
		// if the movement doesn't indicate the amount of work done.)
		return IProgressMonitor.UNKNOWN;
	}

	/**
	 * If the user is cancelling validation on the current project/resource, Add an information task
	 * to the task list informing the user that validation has not been run on the current project.
	 */
	protected void addCancelTask(ValidatorMetaData vmd) {
		InternalValidatorManager.getManager().addOperationTask(getProject(), vmd, ResourceConstants.VBF_STATUS_VALIDATOR_TERMINATED, new String[]{getProject().getName(), vmd.getValidatorDisplayName()});
	}

	/**
	 * This method starts the validation of each configured enabled validator on the current
	 * project.
	 * 
	 * The IProgressMonitor passed in must not be null.
	 */
	public void run(IProgressMonitor progressMonitor) throws OperationCanceledException {
		long start = System.currentTimeMillis();
		final Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
		try {
			// In order to check whether or not the monitor has been cancelled,
			// the monitor must not be null.
			if (progressMonitor == null) {
				return;
			}
			if (ValidatorManager.getManager().isSuspended(getProject())) {
				return;
			}
			if (!areValidatorsEnabled()) {
				// save some processing time...
				return;
			}
			
			final WorkbenchReporter reporter = new WorkbenchReporter(getProject(), progressMonitor);

			try {
				// Periodically check if the user has cancelled the operation
				checkCanceled(reporter);
				preValidate(reporter);
				validate(reporter);
				validateReferencialFiles(reporter);
			} catch (CoreException exc) {
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidationOperation.run"); //$NON-NLS-1$
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
		} finally {
			if (logger.isLoggingLevel(Level.FINE)) {
				long finish = System.currentTimeMillis();
				TimeEntry entry = ValidationPlugin.getTimeEntry();
				entry.setSourceID("ValidationOperation.run(WorkbenchMonitor)"); //$NON-NLS-1$
				entry.setProjectName(getProject().getName());
				entry.setToolName("ValidationOperation"); //$NON-NLS-1$
				entry.setElapsedTime(finish - start);
				logger.write(Level.FINE, entry);
			}
		}
	}

	/**
	 * @param reporter
	 */
	private void validateReferencialFiles(WorkbenchReporter reporter) {
		ReferencialFileValidatorRegistryReader reader = ReferencialFileValidatorRegistryReader.getInstance();
		if (reader != null) {
			reader.readRegistry();
			ReferencialFileValidator refFileValidator = reader.getReferencialFileValidator();
			if (refFileValidator != null) {
				if (_delta != null) {
					refFileValidateFileDelta(reporter, refFileValidator);
				} else if (_project != null) {
					postValidateProject(reporter, refFileValidator);
				}
			}
		}
	}

	/**
	 * @param reporter
	 * @param referencialFileValidator
	 */
	private void refFileValidateFileDelta(WorkbenchReporter reporter, ReferencialFileValidator refFileValidator) {
		IResourceDelta[] resourceDelta = _delta.getAffectedChildren(IResourceDelta.ADDED | IResourceDelta.CHANGED | IResourceDelta.REMOVED);
		List inputFiles = new ArrayList();
		List referencingFiles = new ArrayList();
		if (resourceDelta != null && resourceDelta.length > 0) {
			for (int i = 0; i < resourceDelta.length; i++) {
				IResource resource = resourceDelta[i].getResource();
				if (resource instanceof IFolder) {
					getFileResourceDeltaInFolder(resourceDelta[i], inputFiles);
				} else if (resource instanceof IFile)
					inputFiles.add(resource);
			}
			List rFilesToValidate = refFileValidator.getReferencedFile(inputFiles);
			if (rFilesToValidate != null && !rFilesToValidate.isEmpty())
				referencingFiles.addAll(rFilesToValidate);
			try {
				if (!referencingFiles.isEmpty())
					validateReferencingFiles(reporter, referencingFiles);
			} catch (Exception e) {
				Logger.getLogger().log(e);
			}
		}
	}

	/**
	 * @param delta
	 * @return
	 */
	private void getFileResourceDeltaInFolder(IResourceDelta delta, List inputFiles) {
		IResourceDelta[] resourceDelta = delta.getAffectedChildren();
		for (int i = 0; i < resourceDelta.length; i++) {
			IResource resource = resourceDelta[i].getResource();
			if (resource instanceof IFile) {
				inputFiles.add(resource);
			} else if (resource instanceof IFolder)
				getFileResourceDeltaInFolder(resourceDelta[i], inputFiles);
		}
	}

	/**
	 * @param reporter
	 * @param referencialFileValidator
	 */
	private void postValidateProject(WorkbenchReporter reporter, ReferencialFileValidator refFileValidator) {
		Set set = ValidationRegistryReader.getReader().getValidatorMetaData(_project);
		Iterator it = set.iterator();
		while (it.hasNext()) {
			ValidatorMetaData data = (ValidatorMetaData) it.next();
			List filters = data.getNameFilters();
			List files = getAllFilesForFilter(filters);
			if (!files.isEmpty()) {
				List fileForValidation = refFileValidator.getReferencedFile(files);
				try {
					validateReferencingFiles(reporter, fileForValidation);
				} catch (Exception e) {
					Logger.getLogger().log(e);
				}
			}
		}
	}

	/**
	 * @param filters
	 * @return
	 */
	private List getAllFilesForFilter(List filters) {
		if (!filters.isEmpty()) {
			List allProjectFiles = ReferencialFileValidatorHelper.getAllProjectFiles(_project);
			List filterFiles = new ArrayList();
			for (int i = 0; i < filters.size(); i++) {
				String fileName = (String) filters.get(i);
				if (fileName == null)
					continue;
				for (int j = 0; j < allProjectFiles.size(); j++) {
					IFile projectFile = (IFile) allProjectFiles.get(j);
					if (fileName.charAt(0) == '*') {
						String extName = fileName.substring(2, fileName.length());
						String ext = projectFile.getFileExtension();
						if (ext != null && ext.equals(extName))
							filterFiles.add(projectFile);
					} else if (fileName.equals(projectFile.getName()))
						filterFiles.add(projectFile);
				}

			}
			return filterFiles;
		}
		return Collections.EMPTY_LIST;
	}

	private void validateReferencingFiles(IReporter reporter, List referencingFiles) throws Exception {
		HashSet validatedFiles = new HashSet();
		for (int i = 0; i < referencingFiles.size(); i++) {
			IFile refFile = (IFile) referencingFiles.get(i);
			if (!validatedFiles.contains(refFile)) {
				IResource resource = refFile.getParent();
				IProject project = null;
				if (resource != null && !(resource instanceof IProject))
					project = getProjectContainer(resource);
				else
					project = (IProject) resource;
				if (project != null) {
					Set set = ValidationRegistryReader.getReader().getValidatorMetaData(project);
//					IFileDelta[] changedfiles = new FileDelta[]{new WorkbenchFileDelta(refFile.getProjectRelativePath().toString(), IFileDelta.CHANGED, refFile)};
					Iterator it = set.iterator();
					while (it.hasNext()) {
						ValidatorMetaData data = (ValidatorMetaData) it.next();
						if (data.isApplicableTo(refFile)) {
							IValidator validator = (IValidator)data.getValidator();
							validator.validate(data.getHelper(project),reporter);
							validatedFiles.add(refFile);
						}
					}
				}
			}
		}
	}

	private IProject getProjectContainer(IResource resource) {
		IResource pResource = resource.getParent();
		if (!(pResource instanceof IProject))
			return getProjectContainer(pResource);
		return (IProject) pResource;
	}

	protected void preValidate(WorkbenchReporter reporter) throws CoreException, OperationCanceledException {
		// Load the input.
		getFileDeltas(reporter.getProgressMonitor());
	}

	/**
	 * Iterate over all of the enabled validators and run the thread-safe validators in a background
	 * thread, and the not-thread-safe validators in this thread.
	 */
	protected void validate(WorkbenchReporter reporter) throws OperationCanceledException {
		if (reporter == null) {
			return;
		}
		checkCanceled(reporter);
		reporter.getProgressMonitor().beginTask(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_PROGRESSMONITOR_TITLE), getUnitsOfWork());
		IValidator validator = null;
		ValidatorMetaData vmd = null;
		Iterator iterator = null;
		WorkbenchReporter nullReporter = new WorkbenchReporter(getProject(), new NullProgressMonitor());
		final Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
		IFileDelta[] delta = null;
		
		HashSet jobValidators = new HashSet();
		HashSet validators = new HashSet();
		
		iterator = getEnabledValidators().iterator();
		while( iterator.hasNext() ){
			vmd = (ValidatorMetaData) iterator.next();
			try {
				if( vmd.getValidator() instanceof IValidatorJob ){
					try {
						delta = getFileDeltas(reporter.getProgressMonitor(), vmd);
					} catch (CoreException e) {
						e.printStackTrace();
					}
					boolean willRun = (isForce() || isValidationNecessary(vmd, delta));
					if( willRun ){
						jobValidators.add( vmd );
					}
				}else{
					validators.add( vmd );
				}
			} catch (InstantiationException e) {
				Logger.getLogger().logError(e);
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidationOperation.validate(WorkbenchReporter)"); //$NON-NLS-1$
					entry.setTargetException(e);
					logger.write(Level.SEVERE, entry);
				}				
			}
		}
		launchJobs( jobValidators, reporter );
		
		try {
			//iterator = getEnabledValidators().iterator();
			iterator = validators.iterator();
			
			// In order to allow validators to run, must first check if there's
			// space for new markers.
			// But we don't want the old markers to prevent validation from
			// running again, so delete all
			// of the old markers first, and then run validation.
			while (iterator.hasNext()) {
				vmd = (ValidatorMetaData) iterator.next();
				// Is validation about to be run on this validator?
				// Validation will run either if this operation forces
				// regardless
				// of need, or if the validator was not run automatically.
				// If validation is not about to be run, then don't activate
				// the plugin
				try {
					delta = getFileDeltas(reporter.getProgressMonitor(), vmd);
					boolean willRun = (isForce() || isValidationNecessary(vmd, delta));
					if (logger.isLoggingLevel(Level.FINEST)) {
						TimeEntry entry = ValidationPlugin.getTimeEntry();
						entry.setSourceID("ValidationOperation.validate(WorkbenchReporter)"); //$NON-NLS-1$
						entry.setProjectName(getProject().getName());
						entry.setToolName(vmd.getValidatorUniqueName());
						entry.setElapsedTime(0);
						StringBuffer buffer = new StringBuffer();
						buffer.append("will run? "); //$NON-NLS-1$
						buffer.append(willRun);
						buffer.append("  "); //$NON-NLS-1$
						buffer.append("is force? "); //$NON-NLS-1$
						buffer.append(isForce());
						buffer.append("  "); //$NON-NLS-1$
						buffer.append("isAutoBuild? "); //$NON-NLS-1$
						buffer.append(_isAutoBuild);
						buffer.append("  "); //$NON-NLS-1$
						buffer.append("isAutoValidate? "); //$NON-NLS-1$
						boolean autoBuild = (_isAutoBuild == null) ? ValidatorManager.getManager().isGlobalAutoBuildEnabled() : _isAutoBuild.booleanValue();
						buffer.append(ValidatorManager.getManager().isAutoValidate(getProject(), autoBuild));
						buffer.append("  "); //$NON-NLS-1$
						buffer.append("isIncremental? "); //$NON-NLS-1$
						buffer.append(vmd.isIncremental());
						buffer.append("  "); //$NON-NLS-1$
						if (isFullValidate()) {
							buffer.append("EVERYTHING"); //$NON-NLS-1$
						} else {
							if (delta.length == 0) {
								buffer.append("NOTHING"); //$NON-NLS-1$
							} else {
								buffer.append(getDeltaAsString(delta));
							}
						}
						entry.setDetails(buffer.toString());
						logger.write(Level.FINEST, entry);
					}
					if (!willRun) {
						continue;
					}
				} catch (CoreException exc) {
					if (logger.isLoggingLevel(Level.SEVERE)) {
						LogEntry entry = ValidationPlugin.getLogEntry();
						entry.setSourceID("ValidationOperation.validate(WorkbenchReporter)"); //$NON-NLS-1$
						entry.setTargetException(exc);
						logger.write(Level.SEVERE, entry);
					}
					String mssg = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_ENDING_VALIDATION_ABNORMALLY, new String[]{getProject().getName(), vmd.getValidatorDisplayName()});
					reporter.displaySubtask(mssg);
					/*
					 The code  below  causes bundle not found exception since, the  bundle here is
					 validate_base and we  are  trying to load that bundle from the classloader of 
					 the Validator. 
			  
					String[] msgParm = {exc.getClass().getName(), vmd.getValidatorDisplayName(), (exc.getMessage() == null ? "" : exc.getMessage())}; //$NON-NLS-1$
					Message message = ValidationPlugin.getMessage();
					message.setSeverity(IMessage.NORMAL_SEVERITY);
					message.setId(ResourceConstants.VBF_EXC_RUNTIME);
					message.setParams(msgParm);
					reporter.addMessage(validator, message);
					*/
					continue;
				}
				try {
					context = vmd.getHelper(getProject());
					initValidateContext(delta);
					validator = vmd.getValidator();
					
					checkCanceled(reporter);
					
				} catch (InstantiationException exc) {
					// Remove the vmd from the reader's list
					ValidationRegistryReader.getReader().disableValidator(vmd);
					// Log the reason for the disabled validator
					if (logger.isLoggingLevel(Level.SEVERE)) {
						LogEntry entry = ValidationPlugin.getLogEntry();
						entry.setSourceID("ValidationOperation::validate(WorkbenchReporter)"); //$NON-NLS-1$
						entry.setTargetException(exc);
						logger.write(Level.SEVERE, entry);
					}
					continue;
				}
				
//				if (isFork() && vmd.isAsync()) {
//					// Don't appear to run in the foreground by sending
//					// progress to the IProgressMonitor in the
//					// WorkbenchMonitor. Suppress the status messages by
//					// changing the IProgressMonitor to a
//					// NullProgressMonitor.
//					VThreadManager.getManager().queue(wrapInRunnable(nullReporter, validator, vmd,(WorkbenchContext)getContext(),delta, iterator));
//				} else {
//					internalValidate(reporter, validator, vmd, context, delta);
//				}
				internalValidate(reporter, (IValidator)validator, vmd, context, delta);
				}
		} catch (OperationCanceledException exc) {
			handleOperationCancelledValidateException(reporter, validator, vmd, iterator, logger, exc);
		} finally {
			releaseCachedMaps();
		}
	}

	/**
	 * 
	 */
	private void releaseCachedMaps() {
		if (ValidationRegistryReader.getReader().projectValidationMetaData != null) {
			ValidationRegistryReader.getReader().projectValidationMetaData.clear();
			ValidationRegistryReader.getReader().projectValidationMetaData = null;
		}
		IProjectValidationHelper helper = ValidationHelperRegistryReader.getInstance().getValidationHelper();
		if (helper != null)
			helper.disposeInstance();
	}

	private void initValidateContext(IFileDelta[] delta) {
		 if (context instanceof WorkbenchContext) {
			 ((WorkbenchContext)context).setValidationFileURIs(new ArrayList());
			 for(int i = 0; i < delta.length; i++) {
				 IFileDelta file = delta[i];
				 if(file.getDeltaType() != IFileDelta.DELETED ) {
					 ((WorkbenchContext)context).getValidationFileURIs().add(file.getFileName());
				 }
			 } 
		}
	}

	/**
	 * @param reporter
	 * @param validator
	 * @param vmd
	 * @param iterator
	 * @param logger
	 * @param exc
	 */
	private void handleOperationCancelledValidateException(WorkbenchReporter reporter, IValidator validator, ValidatorMetaData vmd, Iterator iterator, final Logger logger, OperationCanceledException exc) {
		/*
		 * If the user terminates validation (i.e., presses "cancel" on the progress monitor) before
		 * the validation completes, perform clean up on each configured enabled validator.
		 * 
		 * To clean up, several steps are performed: 1. call <code></code> on each configured
		 * enabled validator, so that each validator can perform cleanup that it knows is necessary.
		 * 2. remove all tasks that this validator has added to the task list 3. add another task to
		 * the task list to say that validation, using this validator on this project, was
		 * terminated.
		 * 
		 * Steps 2 and 3 are done so that it's clear what has, and has not, been validated. If these
		 * steps weren't performed, validation could be done on some items in the project, but not
		 * others; and the user could mistakenly believe that those are the only problems with the
		 * project. Unless the user knows that a full verification needs to be done, he/she could
		 * continue to rely on automatic verification, and never know that there are problems with a
		 * resource which hasn't been validated.
		 */
		reporter.displaySubtask(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_VALIDATOR_CLEANUP, new String[]{vmd.getValidatorDisplayName()}));
		reporter.removeAllMessages(validator);
		addCancelTask(vmd);
		reporter.displaySubtask(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_VALIDATOR_TERMINATED, new String[]{getProject().getName(), vmd.getValidatorDisplayName()}));
		while (iterator.hasNext()) {
			vmd = (ValidatorMetaData) iterator.next();
			try {
				validator = vmd.getValidator();
			} catch (InstantiationException exc2) {
				// Remove the vmd from the reader's list
				ValidationRegistryReader.getReader().disableValidator(vmd);
				// Log the reason for the disabled validator
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidationOperation::validate(WorkbenchReporter)"); //$NON-NLS-1$
					entry.setTargetException(exc2);
					logger.write(Level.SEVERE, entry);
				}
				continue;
			}
			reporter.displaySubtask(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_VALIDATOR_CLEANUP, new String[]{vmd.getValidatorDisplayName()}));
			reporter.removeAllMessages(validator);
			addCancelTask(vmd);
			reporter.displaySubtask(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_VALIDATOR_TERMINATED, new String[]{getProject().getName(), vmd.getValidatorDisplayName()}));
		}
		throw exc; // propagate the exception up to the framework so that
		// the framework can display the correct "cancelled"
		// message in the dialog

	}

	/* package */
	void internalValidate(final WorkbenchReporter reporter, final IValidator validator, final ValidatorMetaData vmd,final IWorkbenchContext aContext, final IFileDelta[] delta) throws OperationCanceledException {
		final Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
		try {
			checkCanceled(reporter);
			removeOldMessages(reporter, validator, vmd, delta);
			// Do NOT check if the message limit is exceeded before launching
			// the validator.
			// Even if the limit is exceeded when the messages are removed from
			// the delta
			// files, it could be that the validator itself will remove
			// messages before
			// proceeding. Let the validator run so that it can remove messages
			// if it
			// needs to, and if it tries to add a message when the limit is
			// exceeded, let
			// the WorkbenchReporter take care of it.
			launchValidator(reporter, validator, vmd, aContext, delta);
		} catch (OperationCanceledException exc) {
			// This is handled in the validate(WorkbenchReporter) method.
			throw exc;
		}catch (Throwable exc) {
			// If there is a problem with this particular validator, log the
			// error and continue
			// with the next validator.
			// If a runtime exception has occured, e.g. NullPointer or
			// ClassCast, display it with the "A runtime exception has occurred
			// " messsage.
			// This will provide more information to the user when he/she calls
			// IBM Service.
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationOperation.internalValidate"); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
			String mssg = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_ENDING_VALIDATION_ABNORMALLY, new String[]{getProject().getName(), vmd.getValidatorDisplayName()});
			reporter.displaySubtask(mssg);
			
			/*
			 The code  below  causes bundle not found exception since, the  bundle here is
			 validate_base and we  are  trying to load that bundle from the classloader of 
			 the Validator. 
			  
			String[] msgParm = {exc.getClass().getName(), vmd.getValidatorDisplayName(), (exc.getMessage() == null ? "" : exc.getMessage())}; //$NON-NLS-1$
			Message message = ValidationPlugin.getMessage();
			message.setSeverity(IMessage.NORMAL_SEVERITY);
			message.setId(ResourceConstants.VBF_EXC_RUNTIME);
			message.setParams(msgParm);
			reporter.addMessage(validator, message);
			*/
		} finally {
			// If user fixes problem, and limit exceeded, add "exceeded"
			// message, or
			// if limit not exceeded any more, remove "exceeded" message.
			//Message Limit is removed from the framework
			//ValidatorManager.getManager().checkMessageLimit(getProject(), true);
			reporter.getProgressMonitor().done();
		}
	}

	/**
	 * In order to allow validators to run, must first check if there's space for new markers. But
	 * the old markers must not prevent validation from running again (limit exceeded), so delete
	 * all of the old markers first, and then run validation.
	 */
	private final void removeOldMessages(WorkbenchReporter reporter, IValidator validator, ValidatorMetaData vmd, IFileDelta[] delta) {
		if (reporter == null) {
			return;
		}
		// If the validator has been enabled, remove the "cancel" task.
		// If the validator, on the last run, threw a Throwable, remove the
		// "internal error" task. (For the same reasons we remove the "cancel"
		// task.
		InternalValidatorManager.getManager().removeOperationTasks(getProject(), vmd);
		checkCanceled(reporter);
		Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
		try {
			// Check to see if a full build must be performed, or if a delta
			// build is to be performed, if there are files to verify for that
			// validator. (If it's delta, but there are no files, calling
			// validate on that validator starts a full build, instead of just
			// returning.)
			if (isFullValidate()) {
				String message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_START_REMOVING_OLD_MESSAGES, new String[]{vmd.getValidatorDisplayName(), getProject().getName()});
				reporter.displaySubtask(message);
				reporter.removeAllMessages(validator);
				message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_FINISH_REMOVING_OLD_MESSAGES, new String[]{getProject().getName(), vmd.getValidatorDisplayName()});
				reporter.displaySubtask(message);
			} else {
				// Don't need to check that there are deltas to validate
				// because that's already checked in isValidationNecessary
				String message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_START_REMOVING_OLD_MESSAGES, new String[]{vmd.getValidatorDisplayName(), getProject().getName()});
				reporter.displaySubtask(message);
				for (int i = 0; i < delta.length; i++) {
					WorkbenchFileDelta fd = (WorkbenchFileDelta) delta[i];
					if (fd.getDeltaType() != IFileDelta.DELETED) {
						// If the file has been deleted, eclipse erases all
						// markers on the file.
						// Also, when a resource doesn't exist,
						// WorkbenchReporter's getMessageResource()
						// returns the IProject, which means that removing the
						// messages from this
						// file removes all of this validator's messages on
						// this IProject (aix defect 206157)
						IResource resource = reporter.getMessageResource(validator, fd);
						if (fd.getObject().equals(fd.getResource())) {
							WorkbenchReporter.removeAllMessages(resource, validator); // remove
							// all
							// messages
							// from
							// this
							// resource
						} else {
							reporter.removeAllMessages(validator, fd.getObject());
						}
					}
				}
				message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_FINISH_REMOVING_OLD_MESSAGES, new String[]{getProject().getName(), vmd.getValidatorDisplayName()});
				reporter.displaySubtask(message);
			}
		} catch (OperationCanceledException exc) {
			throw exc;
		} catch (Throwable exc) {
			// If there is a problem with this particular validator, log the
			// error and continue
			// with the next validator.
			// If a runtime exception has occured, e.g. NullPointer or
			// ClassCast, display it with the "A runtime exception has occurred
			// " messsage.
			// This will provide more information to the user when he/she calls
			// IBM Service.
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationOperation.validate(WorkbenchMonitor)"); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
			String mssg = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_ENDING_VALIDATION_ABNORMALLY, new String[]{getProject().getName(), vmd.getValidatorDisplayName()});
			reporter.displaySubtask(mssg);
			
			/*
			 The code  below  causes bundle not found exception since, the  bundle here is
			 validate_base and we  are  trying to load that bundle from the classloader of 
			 the Validator. 			 * 
			String[] msgParm = {exc.getClass().getName(), vmd.getValidatorDisplayName(), (exc.getMessage() == null ? "" : exc.getMessage())}; //$NON-NLS-1$
			Message message = ValidationPlugin.getMessage();
			message.setSeverity(IMessage.NORMAL_SEVERITY);
			message.setId(ResourceConstants.VBF_EXC_RUNTIME);
			message.setParams(msgParm);
			reporter.addMessage(validator, message);
			*/
			return;
		}
	}

	/**
	 * Whether a full verification or a delta verification is in progress, both will call this
	 * method to process the resource. This method calls the current Validator to filter the
	 * resource (i.e., this method returns if the resource fails the filter test).
	 * <code>process</code> also sends output to the <code>IProgressMonitor</code>, and calls
	 * the current Validator to validate the resource.
	 * 
	 * To process a resource, there are several steps: 1. check if the resource is registered for
	 * this validator (i.e., the validator has either specified it in a filter, or has not filtered
	 * it out explicitly) 2. call <code>isValidationSource</code> on the current validator with
	 * the current resource. This method performs further filtering by the Validator itself, in
	 * addition to the static filtering done by the framework, based on the information in
	 * plugin.xml. 3. If the resource passes both filters, call <code>validate</code> on the
	 * validator, with the resource. 4. When complete (either by failing to pass a filter, or by the
	 * completion of the <code>validate</code>), increment the IProgressMonitor's status by one
	 * (i.e., one resource has been processed.)
	 */
	private final void launchValidator(WorkbenchReporter reporter, IValidator validator, ValidatorMetaData vmd, IWorkbenchContext helper, IFileDelta[] delta) {
		if (reporter == null) {
			return;
		}
		checkCanceled(reporter);
		Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
		// Check to see if a full build must be performed, or if a delta
		// build is to be performed, if there are files to verify for that
		// validator. (If it's delta, but there are no files, calling
		// validate on that validator starts a full build, instead of just
		// returning.)
		try {
			// Validate the resource; this step will add errors/warnings to the
			// task list, and remove errors/warnings from the task list.
			if (helper instanceof WorkbenchContext) {
				// Initialize the "loadRuleGroup" method with the group of
				// rules
				// which the validator should validate.
				((WorkbenchContext) helper).setRuleGroup(getRuleGroup());
			}
			long start = System.currentTimeMillis();
			String message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_STARTING_VALIDATION, new String[]{getProject().getName(), vmd.getValidatorDisplayName()});
			reporter.displaySubtask(message);
			if (logger.isLoggingLevel(Level.FINEST)) {
				// This internal "launched validators" value is used only in
				// tests.
				getLaunchedValidators().add(vmd);
			}
			//initValidateContext(delta);
			ValidatorLauncher.getLauncher().start(helper, validator, reporter);
			long finish = System.currentTimeMillis();
			if (logger.isLoggingLevel(Level.INFO)) {
				TimeEntry entry = ValidationPlugin.getTimeEntry();
				entry.setSourceID("ValidationOperation.launchValidator"); //$NON-NLS-1$
				entry.setProjectName(getProject().getName());
				entry.setToolName(vmd.getValidatorUniqueName());
				entry.setElapsedTime(finish - start);
				if (logger.isLoggingLevel(Level.FINE)) {
					StringBuffer buffer = new StringBuffer();
					if (isFullValidate()) {
						buffer.append("EVERYTHING"); //$NON-NLS-1$
					} else {
						if (delta.length == 0) {
							buffer.append("NOTHING"); //$NON-NLS-1$
						} else {
							buffer.append(getDeltaAsString(delta));
						}
					}
					entry.setDetails(buffer.toString());
				}
				logger.write(Level.INFO, entry);
			}
			message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_ENDING_VALIDATION, new String[]{getProject().getName(), vmd.getValidatorDisplayName()});
			reporter.displaySubtask(message);
		} catch (MessageLimitException exc) {
			throw exc;
		} catch (OperationCanceledException exc) {
			throw exc;
		} catch (ValidationException exc) {
			// First, see if a validator just caught all Throwables and
			// accidentally wrapped a MessageLimitException instead of
			// propagating it.
			if (exc.getAssociatedException() != null) {
				if (exc.getAssociatedException() instanceof MessageLimitException) {
					MessageLimitException mssgExc = (MessageLimitException) exc.getAssociatedException();
					throw mssgExc;
				} else if (exc.getAssociatedException() instanceof ValidationException) {
					ValidationException vexc = (ValidationException) exc.getAssociatedException();
					vexc.setClassLoader(validator.getClass().getClassLoader()); // first,
					// set
					// the
					// class
					// loader,
					// so
					// that
					// the
					// exception's
					// getMessage()
					// method
					// can
					// retrieve
					// the
					// resource
					// bundle
				}
			}
			// If there is a problem with this particular validator, log the
			// error and continue
			// with the next validator.
			exc.setClassLoader(validator.getClass().getClassLoader()); // first,
			// set
			// the
			// class
			// loader,
			// so
			// that
			// the
			// exception's
			// getMessage()
			// method
			// can
			// retrieve
			// the
			// resource
			// bundle
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationOperation.validate(WorkbenchMonitor)"); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
				if (exc.getAssociatedException() != null) {
					entry.setTargetException(exc.getAssociatedException());
					logger.write(Level.SEVERE, entry);
				}
			}
			String message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_ENDING_VALIDATION_ABNORMALLY, new String[]{getProject().getName(), vmd.getValidatorDisplayName()});
			reporter.displaySubtask(message);
			if (exc.getAssociatedMessage() != null) {
				reporter.addMessage(validator, exc.getAssociatedMessage());
			}
		} catch (Throwable exc) {
			// If there is a problem with this particular validator, log the
			// error and continue
			// with the next validator.
			// If a runtime exception has occured, e.g. NullPointer or
			// ClassCast, display it with the "A runtime exception has occurred
			// " messsage.
			// This will provide more information to the user when he/she calls
			// IBM Service.
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationOperation.validate(WorkbenchMonitor)"); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
			String mssg = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_ENDING_VALIDATION_ABNORMALLY, new String[]{getProject().getName(), vmd.getValidatorDisplayName()});
			reporter.displaySubtask(mssg);
			
			/*
			 The code  below  causes bundle not found exception since, the  bundle here is
			 validate_base and we  are  trying to load that bundle from the classloader of 
			 the Validator. 
			 
			String[] msgParm = {exc.getClass().getName(), vmd.getValidatorDisplayName(), (exc.getMessage() == null ? "" : exc.getMessage())}; //$NON-NLS-1$
			Message message = ValidationPlugin.getMessage();
			message.setSeverity(IMessage.NORMAL_SEVERITY);
			message.setId(ResourceConstants.VBF_EXC_RUNTIME);
			message.setParams(msgParm);
			reporter.addMessage(validator, message);
			*/
			
		} finally {
			try {
				validator.cleanup(reporter);
			} catch (MessageLimitException e) {
				throw e;
			} catch (OperationCanceledException e) {
				throw e;
			} catch (Throwable exc) {
				// If a runtime exception has occured, e.g. NullPointer or
				// ClassCast, display it with the "A runtime exception has
				// occurred " messsage.
				// This will provide more information to the user when he/she
				// calls IBM Service.
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidationOperation::launchValidator"); //$NON-NLS-1$
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
				
				/*
			 The code  below  causes bundle not found exception since, the  bundle here is
			 validate_base and we  are  trying to load that bundle from the classloader of 
			 the Validator.  
				String[] msgParm = {exc.getClass().getName(), vmd.getValidatorDisplayName(), (exc.getMessage() == null ? "" : exc.getMessage())}; //$NON-NLS-1$
				Message message = ValidationPlugin.getMessage();
				message.setSeverity(IMessage.NORMAL_SEVERITY);
				message.setId(ResourceConstants.VBF_EXC_RUNTIME);
				message.setParams(msgParm);
				try {
					reporter.addMessage(validator, message);
				} catch (MessageLimitException e) {
					throw e;
				}
				
				*/
				return;
			}
			try {
				helper.cleanup(reporter);
			} catch (MessageLimitException e) {
				throw e;
			} catch (OperationCanceledException e) {
				throw e;
			} catch (Throwable exc) {
				// If a runtime exception has occured, e.g. NullPointer or
				// ClassCast, display it with the "A runtime exception has
				// occurred " messsage.
				// This will provide more information to the user when he/she
				// calls IBM Service.
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidationOperation::launchValidator"); //$NON-NLS-1$
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
				
				/*
			 The code  below bundle not found exception since, the  bundle here is
			 validate_base and we  are  trying to load that bundle from the classloader of 
			 the Validator. 
			 				 
				String[] msgParm = {exc.getClass().getName(), vmd.getValidatorDisplayName(), (exc.getMessage() == null ? "" : exc.getMessage())}; //$NON-NLS-1$
				Message message = ValidationPlugin.getMessage();
				message.setSeverity(IMessage.NORMAL_SEVERITY);
				message.setId(ResourceConstants.VBF_EXC_RUNTIME);
				message.setParams(msgParm);
				try {
					reporter.addMessage(validator, message);
				} catch (MessageLimitException e) {
					throw e;
				}
				*/
				return;
			} finally {
				// Now that cleanup has been called, set the project to null.
				// This project's
				// resources have been freed so this project should also be
				// cleared on the helper.
				// If it isn't set to null, then the next time that the helper
				// is retrieved from
				// the ValidatorMetaData, the resources allocated for this
				// project, in the
				// helper's initialize method, will not be reallocated if the
				// project is the same.
				helper.setProject(null);
			}
			// Tell the progress monitor that we've completed n units of work
			// (i.e., n resources validated by one validator).
			reporter.getProgressMonitor().worked(((delta == null) ? 1 : delta.length)); // One
			// unit
			// of
			// work
			// = 1
			// (i.e.,
			// 1
			// resource)
		}
	}

	private Runnable wrapInRunnable(final WorkbenchReporter reporter, final IValidator validator, final ValidatorMetaData vmd, final IWorkbenchContext helper, final IFileDelta[] delta, final Iterator iterator) {
		// Need to create a new Runnable each time because several Runnable
		// instances may exist at the same time.
		Runnable runnable = new ProjectRunnable(reporter, validator, vmd, helper, delta, iterator);
		return runnable;
	}

	/*
	 * // For convenience, keep this method in the class but commented out. // When async needs to
	 * be tested, this method may be needed again. private static void debug(String prefix,
	 * IWorkbenchContext helper) { IProject hProject = helper.getProject(); System.err.println(prefix +
	 * "Start ValidationOperation "+Thread.currentThread().getName() + "::" + hProject.getName());
	 * if( Thread.currentThread().getName().equals("ValidationThread") &&
	 * (hProject.getName().indexOf("noFork") > -1)) { Thread.dumpStack(); } else
	 * if((!Thread.currentThread().getName().equals("ValidationThread")) &&
	 * (hProject.getName().indexOf("fork") > -1)) { Thread.dumpStack(); } System.err.println(prefix +
	 * "End ValidationOperation"); }
	 */
	public class ProjectRunnable implements Runnable {
		private WorkbenchReporter _reporter = null;
		private IValidator _validator = null;
		private ValidatorMetaData _vmd = null;
//		private IValidationContext _helper = null;
		private IFileDelta[] __delta = null;

		public ProjectRunnable(WorkbenchReporter reporter, IValidator validator, ValidatorMetaData vmd, IWorkbenchContext helper, IFileDelta[] delta, Iterator iterator) {
			_reporter = reporter;
			_validator = validator;
			_vmd = vmd;
//			_helper = helper;
			__delta = delta;
		}

		public void run() {
			try {
				internalValidate(_reporter, _validator, _vmd, context,__delta);
			} catch (OperationCanceledException exc) {
				// User can't cancel a job in a background thread, so ignore
				// this exception.
			}
		}

		public IProject getProject() {
			return _reporter.getProject();
		}
	}

	/**
	 * @return Returns the context.
	 */
	public IValidationContext getContext() {
		return context;
	}

	/**
	 * @param context The context to set.
	 */
	public void setContext(IWorkbenchContext context) {
		this.context = context;
	}
	
	void launchJobs(HashSet validators, final WorkbenchReporter reporter) throws OperationCanceledException{
		
		final Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
		Iterator iterator = validators.iterator();
		ValidatorMetaData vmd = null;
		IValidator validator = null;
		IFileDelta[] delta = null;
		IWorkbenchContext workbenchcontext = null;
		
		while (iterator.hasNext()) {
			checkCanceled(reporter);
			
			vmd = (ValidatorMetaData) iterator.next();

			try {
				delta = getFileDeltas(reporter.getProgressMonitor(), vmd);
				boolean willRun = (isForce() || isValidationNecessary(vmd, delta));
				if (!willRun) {
					continue;
				}
			} catch (CoreException exc) {
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidationOperation.launchJobs()"); //$NON-NLS-1$
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
				String mssg = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_ENDING_VALIDATION_ABNORMALLY, new String[]{getProject().getName(), vmd.getValidatorDisplayName()});
				reporter.displaySubtask(mssg);
				
				/*
			 The code  below  causes bundle not found exception since, the  bundle here is
			 validate_base and we  are  trying to load that bundle from the classloader of 
			 the Validator. 
			  
				String[] msgParm = {exc.getClass().getName(), vmd.getValidatorDisplayName(), (exc.getMessage() == null ? "" : exc.getMessage())}; //$NON-NLS-1$
				Message message = ValidationPlugin.getMessage();
				message.setSeverity(IMessage.NORMAL_SEVERITY);
				message.setId(ResourceConstants.VBF_EXC_RUNTIME);
				message.setParams(msgParm);
				reporter.addMessage(validator, message);
				*/
				
				continue;
			}
			
			try {
				validator = vmd.createValidator();
				workbenchcontext = vmd.createHelper( getProject() );
				initValidateContext( delta, workbenchcontext );
				vmd.addHelper((IValidatorJob)validator, workbenchcontext);				
				checkCanceled(reporter);
				
			} catch (InstantiationException exc) {
				// Remove the vmd from the reader's list
				ValidationRegistryReader.getReader().disableValidator(vmd);
				// Log the reason for the disabled validator
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidationOperation.launchJobs()"); //$NON-NLS-1$
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
				continue;
			}
			
			try {
				checkCanceled(reporter);
				removeOldMessages(reporter, validator, vmd, delta);
				
				if( validator instanceof IValidatorJob ){
					launchValidatorJob( reporter, (IValidatorJob)validator, vmd, workbenchcontext, delta);
				}

				
			} catch (OperationCanceledException exc) {
				throw exc;

			} catch (Throwable exc) {
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidationOperation.launchJobs()"); //$NON-NLS-1$
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
				String mssg = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_ENDING_VALIDATION_ABNORMALLY, new String[]{getProject().getName(), vmd.getValidatorDisplayName()});
				reporter.displaySubtask(mssg);

				/*
				 The code  below  causes bundle not found exception since, the  bundle here is
				 validate_base and we  are  trying to load that bundle from the classloader of 
				 the Validator. 
				 */
				 
//				String[] msgParm = {exc.getClass().getName(), vmd.getValidatorDisplayName(), (exc.getMessage() == null ? "" : exc.getMessage())}; //$NON-NLS-1$
//				Message message = ValidationPlugin.getMessage();
//				message.setSeverity(IMessage.NORMAL_SEVERITY);
//				message.setId(ResourceConstants.VBF_EXC_RUNTIME);
//				message.setParams(msgParm);
//				reporter.addMessage(validator, message);
			} finally {
				// If user fixes problem, and limit exceeded, add "exceeded"
				// message, or
				// if limit not exceeded any more, remove "exceeded" message.
				reporter.getProgressMonitor().done();
			}
		}
				
				
				
		

	}
	
	private void initValidateContext(IFileDelta[] delta, IWorkbenchContext context ) {
		 if (context instanceof WorkbenchContext) {
			 ((WorkbenchContext)context).setValidationFileURIs(new ArrayList());
			 for(int i = 0; i < delta.length; i++) {
				 IFileDelta file = delta[i];
				 if(file.getDeltaType() != IFileDelta.DELETED ) {
					 ((WorkbenchContext)context).getValidationFileURIs().add(file.getFileName());
				 }
			 } 
		}
	}
			
			
	private final void launchValidatorJob(WorkbenchReporter reporter,
				   IValidatorJob validator, ValidatorMetaData vmd,
				   IWorkbenchContext helper, IFileDelta[] delta) {
		
		if (reporter == null) {
			return;
		}
		checkCanceled(reporter);
		Logger logger = ValidationPlugin.getPlugin().getMsgLogger();

		if (helper instanceof WorkbenchContext) {
			((WorkbenchContext) helper).setRuleGroup(getRuleGroup());
		}
		if (logger.isLoggingLevel(Level.FINEST)) {
			// This internal "launched validators" value is used only in
			// tests.
			getLaunchedValidators().add(vmd);
		}
		
		ValidatorJob validatorjob = new ValidatorJob( validator, vmd.getValidatorDisplayName(), vmd.getValidatorUniqueName(),
					helper.getProject(), helper );


		ISchedulingRule schedulingRule = validator.getSchedulingRule(helper);
		if( schedulingRule == null ){
			schedulingRule = new EmptySchedulingRule();
		}
		validatorjob.setRule( schedulingRule );		
		QualifiedName validatorKey = new QualifiedName(null, "Validator"); //$NON-NLS-1$
		validatorjob.setProperty( validatorKey, validator );
		validatorjob.addJobChangeListener(
					new JobChangeAdapter(){
						
						public void done(IJobChangeEvent event){
							Job job = event.getJob();
							QualifiedName validatorKey = new QualifiedName(null, "Validator"); //$NON-NLS-1$
							IValidatorJob validator = (IValidatorJob)job.getProperty( validatorKey );
							ValidatorManager mgr = ValidatorManager.getManager();
							final ArrayList list = mgr.getMessages(validator);							
							
							IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
							    public void run(IProgressMonitor monitor) throws CoreException {

							    	Iterator it = list.iterator();
									while( it.hasNext() ){
										MessageInfo info = (MessageInfo)it.next();
										try {
										
										TaskListUtility.addTask( info.getMessageOwnerId(), info.getResource(),
												info.getLocation(), info.getMsg().getId(), info.getText(),
												info.getMsg().getSeverity(),
												info.getMarkerId(),
												info.getTargetObjectName(),
												info.getMsg().getGroupName(),
												info.getMsg().getOffset(),
												info.getMsg().getLength());
										
										
										} catch (CoreException exc) {
											Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
											if (logger.isLoggingLevel(Level.SEVERE)) {
												LogEntry entry = ValidationPlugin.getLogEntry();
												entry.setTargetException(exc);
												logger.write(Level.SEVERE, entry);
											}
										}										
									}
							    }
							};
							try {
								ResourcesPlugin.getWorkspace().run(runnable, null, IWorkspace.AVOID_UPDATE, null);
							} catch (CoreException e) {
							   e.printStackTrace();
							}
							mgr.clearMessages( validator );
							validator = null;
						}
					}
		);
		validatorjob.schedule();		
		
	}
	
	
}