/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.Set;

import org.eclipse.core.resources.IProject;

public interface IDependencyGraphReferences {

	public abstract IProject getTargetProject();

	/**
	 * Returns the set of referencing projects; see
	 * {@link IDependencyGraph#getReferencingComponents(IProject)}
	 * 
	 * @return
	 */
	public abstract Set<IProject> getReferencingComponents();

	/**
	 * If this is value is <code>true</code>, then it is possible that this data
	 * is stale. It is also possible this data is accurate.
	 * 
	 * @return
	 */
	public abstract boolean isStale();

}