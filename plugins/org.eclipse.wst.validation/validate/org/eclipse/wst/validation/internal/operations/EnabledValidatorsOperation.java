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


import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.wst.validation.internal.RegistryConstants;


/**
 * Implemented Validators must not be called directly by anyone other than instances of
 * ValidationOperation, because some initialization of the validator, and handling of error
 * conditions, is done in the operation. The initialization is separated because some of the
 * information needed to initialize the validator (i.e., the project) isn't known until runtime.
 * 
 * Instances of this operation run every enabled validator (both full and incremental) on the
 * project.
 * 
 * This operation is not intended to be subclassed outside of the validation framework.
 */
public class EnabledValidatorsOperation extends ValidatorSubsetOperation {
	/**
	 * @deprecated Will be removed in Milestone 3. For delta validation, use
	 *             EnabledIncrementalValidatorsOperation instead.
	 */
	public EnabledValidatorsOperation(IProject project, IResourceDelta delta) {
		this(project, delta, RegistryConstants.ATT_RULE_GROUP_DEFAULT);
	}

	/**
	 * @deprecated Will be removed in Milestone 3. For delta validation, use
	 *             EnabledIncrementalValidatorsOperation instead.
	 */
	public EnabledValidatorsOperation(IProject project, IResourceDelta delta, int ruleGroup) {
		super(project, delta, ruleGroup);
		setEnabledValidators(ValidatorManager.getManager().getEnabledValidators(project));
	}

	/**
	 * @deprecated Will be removed in Milestone 3. Use EnabledValidatorsOperation(IProject, boolean)
	 */
	public EnabledValidatorsOperation(IProject project) {
		this(project, DEFAULT_ASYNC);
	}

	/**
	 * @deprecated Will be removed in Milestone 3. Use EnabledValidatorsOperation(IProject, int,
	 *             boolean, boolean)
	 */
	public EnabledValidatorsOperation(IProject project, int ruleGroup) {
		this(project, ruleGroup, DEFAULT_FORCE, DEFAULT_ASYNC);
	}

	/**
	 * @deprecated Will be removed in Milestone 3. Use EnabledValidatorsOperation(IProject, int,
	 *             boolean, boolean)
	 */
	public EnabledValidatorsOperation(IProject project, int ruleGroup, boolean force) {
		this(project, ruleGroup, force, DEFAULT_ASYNC);
	}

	/**
	 * Run all enabled validators on the project.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all thread-safe validators in the background
	 * validation thread, and all other validators in the main thread. If async is false, all
	 * validators will run in in the main thread.
	 */
	public EnabledValidatorsOperation(IProject project, boolean async) {
		this(project, RegistryConstants.ATT_RULE_GROUP_DEFAULT, DEFAULT_FORCE, async);
	}

	/**
	 * Run all enabled validators on the project with the identified ruleGroup.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all thread-safe validators in the background
	 * validation thread, and all other validators in the main thread. If async is false, all
	 * validators will run in in the main thread.
	 */
	public EnabledValidatorsOperation(IProject project, int ruleGroup, boolean force, boolean async) {
		this(project, ValidatorManager.getManager().getEnabledValidators(project), ruleGroup, force, async);
	}

	/**
	 * Run the identified validators on the project.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all thread-safe validators in the background
	 * validation thread, and all other validators in the main thread. If async is false, all
	 * validators will run in in the main thread.
	 */
	protected EnabledValidatorsOperation(IProject project, Set enabledValidators, boolean async) {
		this(project, enabledValidators, RegistryConstants.ATT_RULE_GROUP_DEFAULT, DEFAULT_FORCE, async); // true
		// =
		// force
		// validation
		// to
		// run
		// whether
		// or
		// not
		// auto-validate
		// is
		// on
	}

	/**
	 * Run the identified validators on the project with the ruleGroup.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all thread-safe validators in the background
	 * validation thread, and all other validators in the main thread. If async is false, all
	 * validators will run in in the main thread.
	 */
	protected EnabledValidatorsOperation(IProject project, Set enabledValidators, int ruleGroup, boolean force, boolean async) {
		super(project, force, ruleGroup, async);
		setEnabledValidators(enabledValidators);
	}
}