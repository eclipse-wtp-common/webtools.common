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
package org.eclipse.wst.common.componentcore.internal.resources;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.resources.IFlexibleProject;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

public class FlexibleProject implements IFlexibleProject {
	
	private final IProject project;
	private static final IVirtualComponent[] NO_COMPONENTS = new IVirtualComponent[0];
	
	public FlexibleProject(IProject aProject) {
		project = aProject;
	}

	public IVirtualComponent[] getComponents() { 
		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForRead(getProject());
			if(core == null)
				return NO_COMPONENTS;
			WorkbenchComponent[] components = core.getWorkbenchModules();
			if(components.length == 0)
				return NO_COMPONENTS;
			IVirtualComponent[] virtualComponents = new IVirtualComponent[components.length];
			for (int i = 0; i < components.length; i++) {
				virtualComponents[i] = ComponentCore.createComponent(getProject(), components[i].getName());
			}
			return virtualComponents;
		} finally {
			if(core != null)
				core.dispose();
		}
	}

	public IVirtualComponent getComponent(String aComponentName) { 
		return ComponentCore.createComponent(getProject(), aComponentName);
	}

	public IProject getProject() {
		return project;
	}

}
