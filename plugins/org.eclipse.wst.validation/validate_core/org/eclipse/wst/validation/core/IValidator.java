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
package org.eclipse.wst.validation.core;



/**
 * This is the base interface for all Validators. A Validator is a class which verifies that objects
 * follow some rules. For example, in a MOF model which represents an EJB jar, the EJB specification
 * determines the rules. <br>
 * <br>
 * A validator can perform full validation or incremental validation. All validators must implement
 * full validation but incremental validation is optional. <br>
 * <br>
 * Each validator must not be tied to any particular workbench implementation. <br>
 * <br>
 * A validator's verification starts when the ValidatorLauncher singleton calls
 * <code>validate</code>.
 */
public interface IValidator {
	/**
	 * Perform any resource cleanup once validation is complete. If cleanup will take some time, the
	 * IValidator should report subtask information to the user through the IReporter parameter. The
	 * IReporter parameter will not be null.
	 */
	public void cleanup(IReporter reporter);

	/**
	 * This is the method which performs the validation on the objects. <br>
	 * <br>
	 * <code>helper</code> and <code>reporter</code> may not be null. <code>changedFiles</code>
	 * may be null, if a full build is desired. <br>
	 * <br>
	 * <code>helper</code> loads an object. <br>
	 * <br>
	 * <code>reporter</code> is an instance of an IReporter interface, which is used for
	 * interaction with the user. <br>
	 * <br>
	 * <code>changedFiles</code> is an array of files which have been added, changed, or deleted
	 * since the last validation. If <code>changedFiles</code> is null, or if it is an empty
	 * array, then a full validation should be performed. Otherwise, validation on just the files
	 * listed in the array should performed if the validator supports incremental validation.
	 */
	public void validate(IHelper helper, IReporter reporter, IFileDelta[] changedFiles) throws ValidationException;
}