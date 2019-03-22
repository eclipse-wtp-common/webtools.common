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

package org.eclipse.wst.common.componentcore.internal.resources;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

/**
 * This class is a stub for virtual components whose underlying project is not accessible (e.g the project 
 * is closed, or was deleted). This class can be returned by virtual component methods to get references 
 * if the underlying project of the referenced component is not accessible. 
 */

public class NonResolvableVirtualComponent extends AbstractResourceListVirtualComponent {

	public NonResolvableVirtualComponent(IProject p, IVirtualComponent referencingComponent) {
		super(p, referencingComponent);
	}
	
	@Override
	protected IContainer[] getUnderlyingContainers() {
		return new IContainer[]{};
	}

	@Override
	protected IResource[] getLooseResources() {
		return new IResource[]{};
	}
	
	@Override
	public boolean exists() { 
		IProject project = getProject();
		return (project.isAccessible() && ModuleCoreNature.isFlexibleProject(project));
	}

	@Override
	protected String getFirstIdSegment() {
		return null;
	}
	
	@Override
	public String getName(){
		return getProject().getName();
	}
}
