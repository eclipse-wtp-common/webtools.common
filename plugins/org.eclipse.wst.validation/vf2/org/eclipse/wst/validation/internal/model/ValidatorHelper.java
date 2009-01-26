/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.model;

import org.eclipse.wst.validation.internal.ValidatorMutable;

/**
 * Implement some common validator methods, that don't need to be part of the API.
 * 
 * @author karasiuk
 *
 */
public final class ValidatorHelper {
	
	/**
	 * Answer true if this validator already has an exclude filter.
	 */
	public static boolean hasExcludeGroup(ValidatorMutable v){
		for (FilterGroup group : v.getGroups())if (group.isExclude())return true;
		return false;		
	}
}
