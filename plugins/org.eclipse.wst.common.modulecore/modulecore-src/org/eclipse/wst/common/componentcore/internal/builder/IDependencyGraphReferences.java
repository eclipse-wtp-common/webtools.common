/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.Set;

import org.eclipse.core.resources.IProject;

public interface IDependencyGraphReferences {

	IProject getTargetProject();

	/**
	 * Returns the set of referencing projects; see
	 * {@link IDependencyGraph#getReferencingComponents(IProject)}
	 */
	Set<IProject> getReferencingComponents();

	/**
	 * If this returns <code>true</code>, then it is possible that this data
	 * are stale. It is also possible this data are accurate.
	 */
	boolean isStale();

}