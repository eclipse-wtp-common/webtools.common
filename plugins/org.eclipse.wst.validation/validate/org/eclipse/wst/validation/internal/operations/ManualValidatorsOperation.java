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


import org.eclipse.core.resources.IProject;
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
public class ManualValidatorsOperation extends ValidatorSubsetOperation {

	public ManualValidatorsOperation(IProject project) {
		super(project, DEFAULT_FORCE, RegistryConstants.ATT_RULE_GROUP_DEFAULT, false);
		setEnabledValidators(ValidatorManager.getManager().getManualEnabledValidators(project));
	}	
	public ManualValidatorsOperation(IProject project, Object[] changedResources) {
		super(project, shouldForce(changedResources), RegistryConstants.ATT_RULE_GROUP_DEFAULT, false);
		setEnabledValidators(ValidatorManager.getManager().getManualEnabledValidators(project));
	}	
}