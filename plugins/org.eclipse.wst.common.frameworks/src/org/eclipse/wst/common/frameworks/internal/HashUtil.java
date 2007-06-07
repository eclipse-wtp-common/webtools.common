/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal;

public class HashUtil {

	public static int SEED = 11;
	private static int MULTI = 31;
	
	public static int hash(int seed, int i){
		return seed * MULTI + i;
	}
	
	public static int hash(int seed, Object obj){
		return hash(seed, null != obj ? obj.hashCode() : SEED);
	}
	
	public static int hash(int seed, boolean b){
		return hash(seed, b ? 1 : SEED);
	}
}
