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

import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

public class VirtualComponent extends VirtualContainer implements IVirtualComponent {

	public VirtualComponent(IProject aProject, String aName, IPath aRuntimePath) {
		super(aProject, aName, aRuntimePath);
	}

	public VirtualComponent(ComponentHandle aComponentHandle, IPath aRuntimePath) {
		super(aComponentHandle, aRuntimePath);
	}
	
	public IVirtualComponent getComponent() {
		return this;
	}
	
	public String getName() {
		return getComponentHandle().getName();
	}
	
	public String getComponentTypeId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setComponentTypeId(String aComponentTypeId) {
		// TODO Auto-generated method stub

	}

	public Properties getMetaProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPath[] getMetaResources() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setMetaResources(IPath[] theMetaResourcePaths) {
		// TODO Auto-generated method stub

	}

}
