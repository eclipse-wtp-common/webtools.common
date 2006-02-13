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
 * [issue: LM - 2 issues.
 *  1. I don't think the name 'factory' provides a good description of 
 *     this interface. Unless I'm mistaken the ValidationFactory doesn't
 *     actually create validators but simply returns existing validators.
 *     Looking at the implementation this is currently the case. I suggest
 *     renaming this to ValidatorRegistry or something similar.
 *  2. I think the common way for creating a factory is to create a class
 *     with a static method such as getFactory. If this is to be a factory
 *     it should probably be structured as per the convention.]
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
