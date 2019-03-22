/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation;

import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.wst.validation.internal.ContentTypeWrapper;
import org.eclipse.wst.validation.internal.ValType;

/**
 * This class is only to be called by the validation framework and it's test cases.
 * This class is NOT part of the API.
 * <p>
 * This class provides access to some internal methods.
 * </p>
 * @author karasiuk
 *
 */
public class Friend {
	
	public static void setMessages(Validator validator, Map<String, MessageSeveritySetting> map) {
		validator.setMessages(map);
	}
	
	/**
	 * Has the validator's implementation been loaded yet? This is used by some test cases to ensure that 
	 * plug-ins are not loaded too early.
	 */
	public static boolean isLoaded(Validator validator){
		return validator.isLoaded();
	}
	
	public static boolean shouldValidate(Validator validator, IResource resource, boolean isManual, boolean isBuild, 
			ContentTypeWrapper contentTypeWrapper){
		return validator.shouldValidate(resource, isManual, isBuild, contentTypeWrapper);
	}
	
	public static boolean shouldValidate(Validator validator, IResource resource, ValType valType, 
		ContentTypeWrapper contentTypeWrapper){
		
		return validator.shouldValidate(resource, valType, contentTypeWrapper);		
	}
	
	public static void setMigrated(Validator validator, boolean migrated){
		validator.setMigrated(migrated);
	}

}
