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
package org.eclipse.wst.validation.internal.provisional;

import org.eclipse.wst.validation.internal.ValidationFactoryImpl;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * This interface provides a way to access an instance of the ValidationFactoryImpl
 * instance, an internal implementation that provides access to some of the internal state
 * of validators.
 * 
 * @plannedfor 1.0
 * 
 */
public interface ValidationFactory {
	
	ValidationFactory instance = ValidationFactoryImpl.getInstance();
	/**
	 * This api returns the IValidator given the validatorUniqueName id. The unique
	 * id name is the class name that is defined in the class name element in the 
	 * plugin extension of the validator.
	 * @param validatorUniqueId
	 * @return IValidator
	 * @plannedfor WTP 1.0
	 */
	public IValidator getValidator(String validatorUniqueId) throws InstantiationException;

}
