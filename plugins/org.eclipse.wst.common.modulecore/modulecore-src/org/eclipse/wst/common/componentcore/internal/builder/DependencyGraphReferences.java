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

class DependencyGraphReferences implements IDependencyGraphReferences {

	IProject targetProject = null;

	Set<IProject> referencingProjects = null;

	boolean stale;

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.componentcore.internal.builder.IDependencyGraphReferences#getTargetProject()
	 */
	public IProject getTargetProject() {
		return targetProject;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.componentcore.internal.builder.IDependencyGraphReferences#getReferencingComponents()
	 */
	public Set<IProject> getReferencingComponents() {
		return referencingProjects;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.componentcore.internal.builder.IDependencyGraphReferences#isStale()
	 */
	public boolean isStale() {
		return stale;
	}
}
