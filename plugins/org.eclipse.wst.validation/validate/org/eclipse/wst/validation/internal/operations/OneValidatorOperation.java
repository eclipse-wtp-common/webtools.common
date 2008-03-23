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
package org.eclipse.wst.validation.internal.operations;


import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.validation.internal.RegistryConstants;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.ValidatorMetaData;


/**
 * Implemented Validators must not be called directly by anyone other than instances of
 * ValidationOperation, because some initialization of the validator, and handling of error
 * conditions, is done in the operation. The initialization is separated because some of the
 * information needed to initialize the validator (i.e., the project) isn't known until runtime.
 * 
 * This operation runs a single validator on a project.
 * 
 * This operation is not intended to be subclassed outside of the validation framework.
 */
public class OneValidatorOperation extends ValidatorSubsetOperation {
	/**
	 * @deprecated Will be removed in Milestone 3. Use OneValidatorOperation(project, validatorId,
	 *             boolean, boolean)
	 */
	public OneValidatorOperation(IProject project, String validatorId) throws IllegalArgumentException {
		this(project, validatorId, DEFAULT_FORCE, DEFAULT_ASYNC);
	}

	/**
	 * @deprecated Will be removed in Milestone 3. Use OneValidatorOperation(project, validatorId,
	 *             boolean, boolean)
	 */
	public OneValidatorOperation(IProject project, String validatorId, boolean force) throws IllegalArgumentException {
		this(project, validatorId, force, DEFAULT_ASYNC);
	}

	/**
	 * @deprecated Will be removed in Milestone 3. Use OneValidatorOperation(IProject, String,
	 *             boolean, int, boolean)
	 */
	public OneValidatorOperation(IProject project, String validatorId, boolean force, int ruleGroup) throws IllegalArgumentException {
		this(project, validatorId, force, ruleGroup, DEFAULT_ASYNC);
	}

	/**
	 * @param validatorId
	 *            The plugin id of the validator which you wish to run.
	 * @param force
	 *            If the value is "true", the validator should be run regardless of what the
	 *            environment settings are; if the value is "false", this operation should be run
	 *            only if the validation builder will not run the validator.
	 * 
	 * IllegalArgumentException will be thrown if the named validator is not configured on the
	 * project.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all thread-safe validators in the background
	 * validation thread, and all other validators in the main thread. If async is false, all
	 * validators will run in in the main thread.
	 */
	public OneValidatorOperation(IProject project, String validatorId, boolean force, boolean async) throws IllegalArgumentException {
		this(project, validatorId, force, RegistryConstants.ATT_RULE_GROUP_DEFAULT, async);
	}

	/**
	 * OneValidatorOperation constructor comment.
	 * 
	 * @param project
	 *            org.eclipse.core.resources.IProject
	 * @param validatorId
	 *            The plugin id of the validator which you wish to run.
	 * @param force
	 *            If the value is "true", the validator should be run regardless of what the
	 *            environment settings are; if the value is "false", this operation should be run
	 *            only if the validation builder will not run the validator.
	 * @param ruleGroup
	 *            Whether a FULL or FAST pass should be invoked.
	 * 
	 * IllegalArgumentException will be thrown if the named validator is not configured on the
	 * project.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all thread-safe validators in the background
	 * validation thread, and all other validators in the main thread. If async is false, all
	 * validators will run in in the main thread.
	 */
	public OneValidatorOperation(IProject project, String validatorId, boolean force, int ruleGroup, boolean async) throws IllegalArgumentException {
		super(project, force, ruleGroup, async);

		ValidatorMetaData vmd = ValidationRegistryReader.getReader().getValidatorMetaData(validatorId);
		if (vmd == null) {
			// No validator, with that plugin id, can be run on that project.
			// Either the validator isn't installed, or the IProject passed in
			// doesn't have the necessary nature.
			throw new IllegalArgumentException(validatorId);
		}

		if (!vmd.isConfiguredOnProject(project)) {
			// No validator, with that plugin id, can be run on that project.
			// Either the validator isn't installed, or the IProject passed in
			// doesn't have the necessary nature.
			throw new IllegalArgumentException(validatorId);
		}

		Set<ValidatorMetaData> enabled = new HashSet<ValidatorMetaData>();
		enabled.add(vmd);
		setEnabledValidators(enabled);
	}
}
