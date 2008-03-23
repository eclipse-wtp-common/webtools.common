/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.model;

import org.eclipse.wst.validation.Validator;

/**
 * Implement some common validator methods, that don't need to be part of the API.
 * 
 * @author karasiuk
 *
 */
public class ValidatorHelper {
	
	/**
	 * Answer true if this validator already has an exclude filter.
	 * 
	 * @param v
	 * @return
	 */
	public static boolean hasExcludeGroup(Validator.V2 v){
		FilterGroup[] groups = v.getGroups();
		for (int i=0; i<groups.length; i++){
			if (groups[i].isExclude())return true;
		}
		return false;
		
	}
}
