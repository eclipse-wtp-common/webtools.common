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
 * <p>
 * This singleton launches the validation on a single validator. Clients should call this class's
 * <code>start</code> method to begin the validation.
 * </p>
 */
public class ValidatorLauncher {
	private static ValidatorLauncher _launcher = null;

	
	/**
	 * Default constructor 
	 */
	private ValidatorLauncher() {
		//Default constructor
	}

	/**
	 * @return the singleton launcher.
	 */
	public static ValidatorLauncher getLauncher() {
		if (_launcher == null) {
			_launcher = new ValidatorLauncher();
		}
		return _launcher;
	}

	/**
	 * <p>
	 * This method is the launch point of the validation. It runs validation on the validator
	 * accessed by the IHelper. When the validation is complete, each validator may perform resource
	 * cleanup, if necessary.
	 * 
	 * <br>
	 * <br>
	 * If <code>helper</code>,<code>validator</code>, or <code>reporter</code> are null,
	 * validation is not performed. <code>changedFiles</code> may be null, or empty, if a full
	 * build should be done.
	 * </p>
	 * 
	 * @param helper 
	 * 			loads an object. 
	 * @param validator
	 * 			validator object to launch validation.
	 * @param reporter
	 * 			Is an instance of an IReporter interface, which is used for
	 * interaction with the user.
	 * @param changedFiles
	 * 			Is an array of files which have been added, changed, or deleted
	 * since the last validation. If <code>changedFiles</code> is null, or if it is an empty
	 * array, then a full validation should be performed. Otherwise, validation on just the files
	 * listed in the array should performed if the validator supports incremental validation.
	 */
	public void start(IHelper helper, IValidator validator, IReporter reporter, IFileDelta[] changedFiles) throws ValidationException {
		if ((helper == null) || (validator == null) || (reporter == null)) {
			return;
		}

		// Can't force each validator to check if it's cancelled or not,
		// so check for cancellation here. Hopefully the user won't wait
		// too long.
		if (reporter.isCancelled()) {
			return;
		}

		// If the validator is about to perform a full build, remove all of its previous validation
		// messages.
		if ((changedFiles == null) || (changedFiles.length == 0)) {
			reporter.removeAllMessages(validator);
		}

		validator.validate(helper, reporter, changedFiles);
		validator.cleanup(reporter);
	}
}