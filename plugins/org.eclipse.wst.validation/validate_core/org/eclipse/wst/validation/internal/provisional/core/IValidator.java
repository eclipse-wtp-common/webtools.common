/*******************************************************************************
 * Copyright (c) 2001, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.provisional.core;

import org.eclipse.wst.validation.internal.core.ValidationException;

/**
 * This is the base interface for all Validators. A Validator is a class which verifies that objects
 * follow some rules. For example, in a MOF model which represents an EJB jar, the EJB specification
 * determines the rules. 
 * <p>
 * A validator can perform full validation or incremental validation. All validators must implement
 * full validation but incremental validation is optional.
 * </p>
 * <p>
 * Each validator must not be tied to any particular workbench implementation.
 * </p>
 * <p>
 * A validator's verification starts when the ValidatorLauncher singleton calls
 * <code>validate</code>.
 * </p>
 */
public interface IValidator{
	
	/*
	 * [issue : CS - Perhaps the IValidator should be required to provide a 'name' that can be used describe 
	 * the running validation 'Job'.  Then the framework could automatically say something like 'XYZ validator : cleanup'.
	 * Relying on the IValidator to provide subtask information seems error prone.]
	 * [issue: LM - Is the cleanup method necessary? Can the framework put a requirement that client validators 'clean up'
	 *  before returning from the validate method? ] 
	 */
	
	/**
	 * Perform any resource cleanup once validation is complete. If cleanup will take some time, the
	 * IValidator should report subtask information to the user through the IReporter parameter. The
	 * IReporter parameter will not be null.
	 * 
	 * @param reporter Used for the interaction with the user.
	 */
	void cleanup(IReporter reporter);

	/*
	 * [issue : CS - I'm curious to understand why the validator is not invoked directly on a file.  It seems it should be the
	 * domain of another API to manage manage incremental file changes and triggering validations accordingly. 
	 * Do we have a current use case in WTP where the validator does anything more validate a file from the changedFiles list?]
	 */
	/**
	 * This is the method which performs the validation on the objects.
	 * <p>
	 * <code>helper</code> and <code>reporter</code> may not be null. <code>changedFiles</code>
	 * may be null, if a full build is desired. <br>
	 * </p>
	 * @param helper 
	 * 			Loads an object. 
	 * @param reporter
	 * 			Is an instance of an IReporter interface, which is used for interaction with the user.
	 * 
	 */
	void validate(IValidationContext helper, IReporter reporter) throws ValidationException;
}
