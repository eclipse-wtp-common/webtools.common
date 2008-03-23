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


import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.InternalValidatorManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * Implemented Validators must not be called directly by anyone other than instances of
 * ValidationOperation, because some initialization of the validator, and handling of error
 * conditions, is done in the operation. The initialization is separated because some of the
 * information needed to initialize the validator (i.e., the project) isn't known until runtime.
 * 
 * This operation runs all validators: enabled validators, disabled validators, incremental
 * validators, full validators.
 * 
 * This operation is not intended to be subclassed outside of the validation framework.
 */
public class AllValidatorsOperation extends ValidatorSubsetOperation {
	/**
	 * @deprecated Will be removed in Milestone 3. Use AllValidatorsOperation(IProject, boolean)
	 */
	public AllValidatorsOperation(IProject project) {
		this(project, DEFAULT_ASYNC);
	}

	/**
	 * Run all configured validators on the project, regardless of whether the validator is enabled
	 * or disabled by the user.
	 * 
	 * If async is true, the validation will run all thread-safe validators in a background thread.
	 * If async is false, no validators will run in a background thread.
	 */
	public AllValidatorsOperation(IProject project, boolean async) {
		super(project, true, async);// always force validation to run
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			setEnabledValidators(InternalValidatorManager.wrapInSet(prjp.getValidators()));
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			if (e.getTargetException() != null) {
				ValidationPlugin.getPlugin().handleException(e.getTargetException());
			}
		}
	}

}
