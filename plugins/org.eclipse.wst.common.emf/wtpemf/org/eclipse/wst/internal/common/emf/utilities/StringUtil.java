/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Mar 18, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.internal.common.emf.utilities;

/**
 * @author schacher
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class StringUtil {
	/**
	 * Enhanced equality check for two string parameters, that takes into consideration null values.
	 * If both values are null, this will return true.
	 * 
	 * @param s1
	 * @param s2
	 * @return boolean
	 */
	public static boolean stringsEqual(String s1, String s2) {
		if (s1 == null)
			return s2 == null;
		return s1.equals(s2);
	}

}