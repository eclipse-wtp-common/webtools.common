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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

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
	
	public IVirtualResource[] getResources(String aResourceType) {
		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForRead(getProject());
			WorkbenchComponent component = core.findComponentByName(getName());
			List currentResources = component.getResources();
			List foundResources = new ArrayList();
			
			for (Iterator iter = currentResources.iterator(); iter.hasNext();) {
				ComponentResource resource = (ComponentResource) iter.next();
				if(aResourceType == null || aResourceType.equals(resource.getResourceType())) {
					IVirtualResource vres = createVirtualResource(resource);
					if(vres != null)
						foundResources.add(vres);
				}
				
			}
			return (IVirtualResource[]) foundResources.toArray(new IVirtualResource[foundResources.size()]);
		} finally {
			if(core != null)
				core.dispose();
		}
	}

	private IVirtualResource createVirtualResource(ComponentResource aComponentResource) {
		IResource resource = StructureEdit.getEclipseResource(aComponentResource);
		switch(resource.getType()) {
			case IResource.FILE:
				return ComponentCore.createFile(getProject(), getName(), aComponentResource.getRuntimePath());
			case IResource.FOLDER:
				return ComponentCore.createFolder(getProject(), getName(), aComponentResource.getRuntimePath());
		}
		return null;
	}
	
	

}
