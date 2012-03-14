/*******************************************************************************
 * Copyright (c) 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Validators may wish to join validator groups for performance or other
 * reasons. That way expensive operations like creating and releasing models can
 * be done in a common location, and be done only once per resource. Group listeners
 * are declared via the <code>org.eclipse.wst.validation.validatorGroup</code> extension point.
 * <p>
 * As validation proceeds, the validation framework, on a resource by resource
 * bases, determines if any validators that are members of a group are
 * interested in the resource. If they are, before the first validator in the
 * group is called, the <code>validationStarting</code> method is called. If this method was
 * called, then the <code>validationFinishing</code> method will be called once all the
 * validators have processed the resource.
 * </p>
 */
public interface IValidatorGroupListener {
	/**
	 * This is called before the first validator in the group that is interested
	 * in the resource is called. If no validators in the group are interested
	 * in the resource, then this method is not called.
	 * 
	 * @param resource
	 *            The resource that is being validated.
	 * @param monitor
	 *            A progress monitor that the method should use.
	 * @param state
	 *            The validation state for the current operation.
	 */
	void validationStarting(IResource resource, IProgressMonitor monitor, ValidationState state);
	
	/**
	 * If the validationStarting method was called on the resource, then this
	 * method will be called after the last validator has processed the
	 * resource.
	 * 
	 * @param resource
	 * 		The resource that is being validated.
	 * @param monitor
	 * 		A progress monitor that the method can use.
	 * @param state
	 * 		The validation state for the current operation.
	 */
	void validationFinishing(IResource resource, IProgressMonitor monitor, ValidationState state);
}
