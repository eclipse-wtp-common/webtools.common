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
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.FilterUtil;
import org.eclipse.wst.validation.internal.InternalValidatorManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.RegistryConstants;
import org.eclipse.wst.validation.plugin.ValidationPlugin;

import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;

/**
 * Implemented Validators must not be called directly by anyone other than instances of
 * ValidationOperation, because some initialization of the validator, and handling of error
 * conditions, is done in the operation. The initialization is separated because some of the
 * information needed to initialize the validator (i.e., the project) isn't known until runtime.
 * 
 * Instances of this operation load the project's configured validators, and run the validators if
 * they are both enabled and incremental.
 * 
 * This operation is not intended to be subclassed outside of the validation framework.
 */
public class EnabledIncrementalValidatorsOperation extends EnabledValidatorsOperation {
	/**
	 * @deprecated Will be removed in Milestone 3. Use
	 *             EnabledIncrementalValidatorsOperation(IProject, IResourceDelta, boolean)
	 */
	public EnabledIncrementalValidatorsOperation(IProject project, IResourceDelta delta) {
		this(project, delta, DEFAULT_ASYNC);
	}

	/**
	 * @deprecated Will be removed in Milestone 3. Use
	 *             EnabledIncrementalValidatorsOperation(IProject, IResourceDelta, int, boolean)
	 */
	public EnabledIncrementalValidatorsOperation(IProject project, IResourceDelta delta, int ruleGroup) {
		this(project, delta, ruleGroup, DEFAULT_ASYNC);
	}

	/**
	 * @deprecated Will be removed in Milestone 3. Use
	 *             EnabledIncrementalValidatorsOperation(IProject, IResourceDelta, int, boolean)
	 *             instead.
	 */
	public EnabledIncrementalValidatorsOperation(IProject project, Set validators, IResourceDelta delta, int ruleGroup) {
		this(project, delta, ruleGroup, DEFAULT_ASYNC);
		setEnabledValidators(validators);
	}

	/**
	 * IProject must exist and be open.
	 * 
	 * If delta is null, a full validation of the project using only the incremental validators is
	 * performed. If delta is not null, all enabled incremental validators that validate resources
	 * in the delta will validate those resources.
	 * 
	 * If async is true, all thread-safe validators will run in the background validation thread,
	 * and all other validators will run in the main thread. If async is false, all validators will
	 * run in the main thread.
	 */
	public EnabledIncrementalValidatorsOperation(IProject project, IResourceDelta delta, boolean async) {
		this(project, delta, RegistryConstants.ATT_RULE_GROUP_DEFAULT, async);
	}

	/**
	 * IProject must exist and be open.
	 * 
	 * If delta is null, a full validation of the project using only the incremental validators is
	 * performed. If delta is not null, all enabled incremental validators that validate resources
	 * in the delta will validate those resources.
	 * 
	 * If async is true, all thread-safe validators will run in the background validation thread,
	 * and all other validators will run in the main thread. If async is false, all validators will
	 * run in the main thread.
	 */
	public EnabledIncrementalValidatorsOperation(IProject project, IResourceDelta delta, int ruleGroup, boolean async) {
		super(project, ruleGroup, shouldForce(delta), async);
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			setEnabledValidators(InternalValidatorManager.wrapInSet(prjp.getEnabledIncrementalValidators(true)));
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("EnabledIncrementalVAlidatorsOperation(IProject<" + project.getName() + ">, IResourceDelta, int, boolean)"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, exc);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, exc);
				}
			}
		}
		setDelta(delta);
	}

	/**
	 * IProject must exist, be open, and contain all of the resources in changedResources. If some
	 * of the resources in changedResources belong to different projects, the result is undefined.
	 * 
	 * If changedResources is null, a full validation of the project using only the incremental
	 * validators is performed. If changedResources is not null, all enabled incremental validators
	 * that validate resources in the changedResources array will validate those resources.
	 * 
	 * If async is true, all thread-safe validators will run in the background validation thread,
	 * and all other validators will run in the main thread. If async is false, all validators will
	 * run in the main thread.
	 */
	public EnabledIncrementalValidatorsOperation(IResource[] changedResources, IProject project, boolean async) {
		super(project, RegistryConstants.ATT_RULE_GROUP_DEFAULT, shouldForce(changedResources), async);
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			setEnabledValidators(InternalValidatorManager.wrapInSet(prjp.getEnabledIncrementalValidators(true)));
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("EnabledIncrementalValidatorsOperation(IResource[], IProject<" + project.getName() + ">, boolean)"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, exc);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
		}
		setFileDeltas(FilterUtil.getFileDeltas(getEnabledValidators(), changedResources, false)); // construct
		// an
		// array
		// of
		// IFileDelta[]
		// to
		// wrap
		// the
		// Object[];
		// one
		// IFileDelta
		// for
		// each
		// Object
		// in
		// the
		// array
	}

}