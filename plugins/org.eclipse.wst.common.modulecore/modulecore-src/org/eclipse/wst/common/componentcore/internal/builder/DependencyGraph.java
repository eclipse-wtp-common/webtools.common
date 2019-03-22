/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.IProject;

/**
 * @deprecated use {@link IDependencyGraph}
 */
public class DependencyGraph {

	private static final DependencyGraph INSTANCE = new DependencyGraph();

	public static DependencyGraph getInstance() {
		return INSTANCE;
	}

	public IProject[] getReferencingComponents(IProject target) {
		Set<IProject> referencingComponents = IDependencyGraph.INSTANCE.getReferencingComponents(target);
		return referencingComponents.toArray(new IProject[referencingComponents.size()]);
	}

	public void addReference(IProject target, IProject referencingComponent) {
		// do nothing
	}

	public void removeReference(IProject target, IProject referencingComponent) {
		// do nothing
	}

	protected Set internalGetReferencingComponents(IProject target) {
		return Collections.EMPTY_SET;
	}

	public void clear() {
	}
}
