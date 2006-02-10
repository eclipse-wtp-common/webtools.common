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
package org.eclipse.wst.validation.internal.provisional.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.validation.internal.core.ValidationException;

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
 * 
 * [issue: LM - This interface will be implemented by clients. This should be considered a candidate
 * for an abstract class. ]
 */
public interface IValidatorJob extends IValidator{
	public static IStatus OK_STATUS = new Status(IStatus.OK, "org.eclipse.wst.validation", 0, "OK", null); //$NON-NLS-1$ //$NON-NLS-2$
	


	/**
	 * This is the method which performs the validation on the objects. <br>
	 * <br>
	 * <code>helper</code> and <code>reporter</code> may not be null. <code>changedFiles</code>
	 * may be null, if a full build is desired. <br>
	 * <br>
	 * 
	 * @param helper
	 *            loads an object.
	 * @param reporter
	 *            Is an instance of an IReporter interface, which is used for interaction with the
	 *            user.

	 */
	public IStatus validate(IValidationContext helper, IReporter reporter, IProgressMonitor monitor) throws ValidationException;

	public ISchedulingRule getSchedulingRule();
}