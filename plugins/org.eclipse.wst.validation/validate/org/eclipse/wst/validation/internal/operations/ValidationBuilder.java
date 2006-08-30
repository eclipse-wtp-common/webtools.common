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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.InternalValidatorManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ResourceConstants;
import org.eclipse.wst.validation.internal.ResourceHandler;
import org.eclipse.wst.validation.internal.TimeEntry;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * Validation Framework Builder.
 * 
 * This builder is configured on J2EE IProjects automatically, can be added to other types of
 * projects through the Properties page, and launches validation on the project if the project has
 * build validation enabled.
 */
public class ValidationBuilder extends IncrementalProjectBuilder {
	public static final int NO_DELTA_CHANGE = -1; // Since IResourceConstants
	protected List referencedProjects;
	protected IWorkbenchContext workbenchContext = null;

	// doesn't have a "no delta"
	// flag, let this constant be
	// the flag.
	public ValidationBuilder() {
		super();
	}

	private IProject[] getAllReferencedProjects(IProject project, Set visitedProjects) {
		if (visitedProjects == null)
			visitedProjects = new HashSet();
		else if (visitedProjects.contains(project))
			return getReferencedProjects();
		else
			visitedProjects.add(project);
		if (referencedProjects == null)
			referencedProjects = new ArrayList();
		try {
			if (project.isAccessible()) {
				IProject[] refProjArray = project.getReferencedProjects();
				collectReferecedProject(refProjArray);
				for (int i = 0; i < refProjArray.length; i++) {
					IProject refProject = refProjArray[i];
					getAllReferencedProjects(refProject, visitedProjects);
				}
			}
			return getReferencedProjects();
		} catch (CoreException core) {
			return null;
		}
	}
	
	public IWorkbenchContext getWorkbenchContext() {
		if(workbenchContext == null) {
			workbenchContext = new WorkbenchContext();
			workbenchContext.setProject(getProject());
		}
		return workbenchContext;
	}


	/**
	 * @param referencedProjects2
	 * @param refProjArray
	 */
	private void collectReferecedProject(IProject[] refProjArray) {
		for (int i = 0; i < refProjArray.length; i++) {
			IProject project = refProjArray[i];
			if (!referencedProjects.contains(project))
				referencedProjects.add(project);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#clean(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void clean(IProgressMonitor monitor) throws CoreException {
		IProject currentProject = getProject();
		if (currentProject == null || !currentProject.isAccessible())
			return;
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(currentProject);
			ValidatorMetaData[] vmds = prjp.getValidators();
			for (int i = 0; i < vmds.length; i++) {
				ValidatorMetaData vmd = vmds[i];
				// For validators who aren't going to run, clear their messages from the task list.
				// Don't need to check for duplicate entries because each Validator must be unique.
				// The uniqueness of each Validator is checked by the plugin registry.
				WorkbenchReporter.removeAllMessages(currentProject, vmd.getValidatorNames(), null);
			}
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidatorManager.updateTaskList(" + currentProject.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
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
	 * @param referencedProjects
	 * @return
	 */
	private IProject[] getReferencedProjects() {
		IProject[] refProjArray = new IProject[referencedProjects.size()];
		for (int i = 0; i < referencedProjects.size(); i++) {
			refProjArray[i] = (IProject) referencedProjects.get(i);
		}
		return refProjArray;
	}

	public IProject[] build(int kind, Map parameters, IProgressMonitor monitor) {
		long start = System.currentTimeMillis();
		int executionMap = 0x0;
		Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
		IResourceDelta delta = null;
		IProject project = getProject();
		IProject[] referenced = getAllReferencedProjects(project, null);
		try {
			if (ValidatorManager.getManager().isSuspended(project)) {
				// Do not perform validation on this project
				executionMap |= 0x1;
				return referenced;
			}
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			delta = getDelta(project);
			boolean doFullBuild = (kind == FULL_BUILD);
			boolean doAutoBuild = ((delta != null) && (kind == AUTO_BUILD));
			boolean doIncrementalBuild = ((delta != null) && (kind == INCREMENTAL_BUILD));
//			if ((doFullBuild || doIncrementalBuild) && !prjp.isBuildValidate()) {
//				// Is a build validation about to be invoked? If so, does the
//				// user want build validation to run?
//				executionMap |= 0x2;
//				return referenced;
//			}
			// It is possible for kind to == AUTO_BUILD while delta is null
			// (saw this
			// when creating a project by copying another project.)
			// However, a "Rebuild Project" will invoke this builder with
			// kind==FULL_BUILD
			// and a null delta, and validation should run in that case.
			if (!doFullBuild && delta == null) {
				if (isReferencedProjectInDelta(referenced)) {
					performFullBuildForReferencedProjectChanged(monitor, prjp);
				} else {
					String[] msgParms = new String[]{project.getName()};
					monitor.subTask(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_NULL_DELTA, msgParms));
					// A null delta means that a full build must be performed,
					// but this builder was invoked with an incremental or
					// automatic
					// build kind. Return without doing anything so that the
					// user
					// doesn't have to wait forever.
					executionMap |= 0x4;
				}
				return referenced;
			}
			if (doFullBuild) {
				performFullBuild(monitor, prjp);
			} else {
//				if (doAutoBuild && !prjp.isAutoValidate()) {
//					executionMap |= 0x8;
//					return referenced;
//				}
				if (delta.getAffectedChildren().length == 0) {
					if (isReferencedProjectInDelta(referenced))
						performFullBuildForReferencedProjectChanged(monitor, prjp);
					else
						executionMap |= 0x10;
					return referenced;
				}
				EnabledIncrementalValidatorsOperation operation = new EnabledIncrementalValidatorsOperation(project, delta, true);
				operation.run(monitor);
			}
			return referenced;
		} catch (InvocationTargetException exc) {
			logInvocationTargetException(logger, exc);
			executionMap |= 0x20;
			return referenced;
		} catch (Throwable exc) {
			logBuildError(logger, exc);
			executionMap |= 0x40;
			return referenced;
		} finally {
			referencedProjects = null;
			// The builder's time needs to be FINE because the builder is
			// called often.
			if (logger.isLoggingLevel(Level.FINE)) {
				logBuilderTimeEntry(start, executionMap, logger, delta);
			}
		}
	}

	/**
	 * @param referenced
	 * @return
	 */
	private boolean isReferencedProjectInDelta(IProject[] referenced) {
		IProject p = null;
		for (int i = 0; i < referenced.length; i++) {
			p = referenced[i];
			IResourceDelta delta = getDelta(p);
			if (delta != null && delta.getAffectedChildren().length > 0)
				return true;
		}
		return false;
	}

	/**
	 * @param monitor
	 * @param prjp
	 */
	private void performFullBuildForReferencedProjectChanged(IProgressMonitor monitor, ProjectConfiguration prjp) throws InvocationTargetException {
		performFullBuild(monitor, prjp, true);
	}

	private void performFullBuild(IProgressMonitor monitor, ProjectConfiguration prjp) throws InvocationTargetException {
		performFullBuild(monitor, prjp, false);
	}

	private void performFullBuild(IProgressMonitor monitor, ProjectConfiguration prjp, boolean onlyDependentValidators) throws InvocationTargetException {
		ValidatorMetaData[] enabledValidators = prjp.getEnabledFullBuildValidators(true, onlyDependentValidators);
		if ((enabledValidators != null) && (enabledValidators.length > 0)) {
			Set enabledValidatorsSet = InternalValidatorManager.wrapInSet(enabledValidators);
			EnabledValidatorsOperation op = new EnabledValidatorsOperation(getProject(), enabledValidatorsSet, true);
			op.run(monitor);
		}
	}

	private void logInvocationTargetException(Logger logger, InvocationTargetException exc) {
		if (logger.isLoggingLevel(Level.SEVERE)) {
			LogEntry entry = ValidationPlugin.getLogEntry();
			entry.setSourceID("ValidationBuilder::build"); //$NON-NLS-1$
			entry.setTargetException(exc);
			logger.write(Level.SEVERE, entry);
			if (exc.getTargetException() != null) {
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
		}
	}

	private void logBuildError(Logger logger, Throwable exc) {
		if (logger.isLoggingLevel(Level.SEVERE)) {
			if( ! (exc instanceof OperationCanceledException) ){
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationBuilder.build(int, Map, IProgressMonitor)"); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
		}
	}

	private void logBuilderTimeEntry(long start, int executionMap, Logger logger, IResourceDelta delta) {
		TimeEntry entry = ValidationPlugin.getTimeEntry();
		entry.setSourceID("ValidationBuilder.build(int, Map, IProgressMonitor)"); //$NON-NLS-1$
		entry.setProjectName(getProject().getName()); //$NON-NLS-1$  //$NON-NLS-2$
		entry.setExecutionMap(executionMap);
		entry.setElapsedTime(System.currentTimeMillis() - start);
		if (delta == null) {
			entry.setDetails("delta == null"); //$NON-NLS-1$
		}
		entry.setToolName("ValidationBuilder"); //$NON-NLS-1$
		logger.write(Level.FINE, entry);
	}
}