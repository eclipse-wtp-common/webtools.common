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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.validation.internal.core.ValidationException;

/**
 * This is the Job interface for the validator. Validators implementing this interface are executed as
 * background jobs. 
 * Following is a  sequence of  call  <br/>
 * validator.getSchedulingRule(helper) <br/>
 * validator.validate(reporter)
 */

public interface IValidatorJob extends IValidator{

	public static IStatus OK_STATUS = new Status(IStatus.OK, "org.eclipse.wst.validation", 0, "OK", null); //$NON-NLS-1$ //$NON-NLS-2$
	


	/**
	 * This is the method which performs the validation on the objects. <br>
	 * <br>
	 * <code>reporter</code> may not be null. <code>changedFiles</code>
	 * may be null, if a full build is desired. <br>
	 * <br>
     *
	 * @param reporter
	 *            Is an instance of an IReporter interface, which is used for interaction with the
	 *            user.
	 * @param helper will not be null,
	 *		loads an object.
	 */
	public IStatus validateInJob(IValidationContext helper, IReporter reporter) throws ValidationException;

	/**
	 * Get the scheduling rule, which the framework applies to the Validator job, 
	 * @param helper will not be null,
	 *		loads an object.
	 * @return
	 */
	public ISchedulingRule getSchedulingRule(IValidationContext helper);
}