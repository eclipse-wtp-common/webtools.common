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
package org.eclipse.wst.validation.internal;

import org.eclipse.wst.validation.internal.provisional.ValidationFactory;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

public class ValidationFactoryImpl implements ValidationFactory {
	static ValidationFactory inst = null;
	
	public ValidationFactoryImpl() {
		super();
	}

	public static ValidationFactory getInstance() {
		if(inst == null)
			inst = new ValidationFactoryImpl();
		return inst;
	}

	public IValidator getValidator(String validatorUniqueId) throws InstantiationException {
		ValidationRegistryReader reader = ValidationRegistryReader.getReader();
		return reader.getValidator(validatorUniqueId);
	}

}
