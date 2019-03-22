/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.builder;

import org.eclipse.core.resources.IProject;

/**
 * @deprecated use {@link IDependencyGraph}
 */
public class DependencyGraphManager {

	private static DependencyGraphManager INSTANCE = null;

	private DependencyGraphManager() {
		super();
	}

	public synchronized static final DependencyGraphManager getInstance() {
		if (INSTANCE == null)
			INSTANCE = new DependencyGraphManager();
		return INSTANCE;
	}

	public void construct(IProject project) {
		// do nothing
	}

	/**
	 * Return the dependency graph which was initialized if need be in the
	 * singleton manager method.
	 */
	public DependencyGraph getDependencyGraph() {
		return DependencyGraph.getInstance();
	}

	public void forceRefresh() {
		// do nothing
	}

	public long getModStamp() {
		return IDependencyGraph.INSTANCE.getModStamp();
	}

	public boolean checkIfStillValid(long timeStamp) {
		return IDependencyGraph.INSTANCE.getModStamp() == timeStamp;
	}
}
