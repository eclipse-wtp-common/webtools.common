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
 * Created on Sep 24, 2003
 *
 */
package org.eclipse.wst.common.internal.emf.resource;

import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * An unsynchronized implementation of a Stack (LIFO) data structure. No casting is required when
 * using this data structure.
 * 
 * @author mdelder
 */
public class CacheEventStack extends ArrayList {

	/**
	 * 
	 * @return the top of the stack without removing it
	 */
	public CacheEventNode peek() {
		if (size() == 0)
			throw new EmptyStackException();

		return (CacheEventNode) get(size() - 1);
	}

	/**
	 * 
	 * @return the top of the stack and removing it
	 */
	public CacheEventNode pop() {
		if (size() == 0)
			throw new EmptyStackException();

		return (CacheEventNode) remove(size() - 1);
	}

	/**
	 * 
	 * @param adapter
	 *            A CENO to push onto the top of the stack
	 */
	public void push(CacheEventNode adapter) {
		add(adapter);
	}

}