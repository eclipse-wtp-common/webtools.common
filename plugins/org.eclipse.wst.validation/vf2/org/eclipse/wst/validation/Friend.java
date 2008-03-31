/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation;

import java.util.Map;

/**
 * This class is only to be called by the validation framework and it's test cases.
 * This class is NOT part of the API.
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

}
