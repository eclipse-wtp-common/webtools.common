/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
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
