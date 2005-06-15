/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.componentcore.internal.resources;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.resources.ComponentHandle;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public class VirtualFolder extends VirtualContainer implements IVirtualFolder { 

	/**
	 * <p>
	 * Creates an unassigned mapping contained by the component identified by <aContainingProject,
	 * aComponentName> with a runtime path of aRuntimePath.
	 * </p>
	 * 
	 * @param aContainingProject
	 * @param aComponentName
	 * @param aRuntimePath
	 */
	public VirtualFolder(IProject aContainingProject, String aComponentName, IPath aRuntimePath) {
		super(aContainingProject, aComponentName, aRuntimePath);
	}

	/**
	 * p> Creates an unassigned mapping contained by the component identified by aComponentHandle
	 * with a runtime path of aRuntimePath.
	 * </p>
	 * 
	 * @param aComponentHandle
	 * @param aRuntimePath
	 */
	protected VirtualFolder(ComponentHandle aComponentHandle, IPath aRuntimePath) {
		super(aComponentHandle, aRuntimePath);
	}
 
	// TODO WTP:Implement this method
	public void create(int updateFlags, IProgressMonitor monitor) throws CoreException {

		IVirtualComponent container = ComponentCore.createComponent(getProject(), getComponentHandle().getName()); 
		
		if( !container.getProjectRelativePath().isRoot()){	
			IFolder realFolder = getProject().getFolder(container.getProjectRelativePath()); 
			IFolder newFolder = realFolder.getFolder(getRuntimePath()); 
			createResource(newFolder, updateFlags, monitor);			
		}	
	} 

	// TODO WTP:Implement this method
	public boolean exists(IPath path) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return false;
	}

 
	public int getType() {
		return IVirtualResource.FOLDER;
	} 
	
	public IResource getUnderlyingResource() {
		return getUnderlyingFolder();
	}
	
	public IResource[] getUnderlyingResources() {
		return getUnderlyingFolders();
	}

	public IFolder getUnderlyingFolder() { 
		return getProject().getFolder(getProjectRelativePath());
	}
	
	public IFolder[] getUnderlyingFolders() {
		return new IFolder[] {getUnderlyingFolder()};
	}

	protected void doDeleteMetaModel(int updateFlags, IProgressMonitor monitor) {

		// only handles explicit mappings
		StructureEdit moduleCore = null;
		try { 
			IPath runtimePath = getRuntimePath();
			moduleCore = StructureEdit.getStructureEditForWrite(getProject());
			WorkbenchComponent component = moduleCore.findComponentByName(getComponent().getName());
			ComponentResource[] resources = component.findResourcesByRuntimePath(runtimePath);
			for (int i = 0; i < resources.length; i++) {
				if(runtimePath.equals(resources[i].getRuntimePath())) 
					component.getResources().remove(resources[i]);								
			}
			
		} finally {
			if (moduleCore != null) {
				moduleCore.saveIfNecessary(null);
				moduleCore.dispose();
			}
		}
	}	
	
	protected void doDeleteRealResources(int updateFlags, IProgressMonitor monitor) throws CoreException {

		// only handles explicit mappings
		StructureEdit moduleCore = null;
		try {
			IPath runtimePath = getRuntimePath();
			moduleCore = StructureEdit.getStructureEditForWrite(getProject());
			WorkbenchComponent component = moduleCore.findComponentByName(getComponent().getName());
			ComponentResource[] resources = component.findResourcesByRuntimePath(runtimePath);
			IResource realResource;
			for (int i = 0; i < resources.length; i++) {
				if(runtimePath.equals(resources[i].getRuntimePath())) {
					realResource = StructureEdit.getEclipseResource(resources[i]);
					if(realResource != null && realResource.getType() == getType())
						realResource.delete(updateFlags, monitor);
				}
					
			}
			
		} finally {
			if (moduleCore != null) {
				moduleCore.saveIfNecessary(null);
				moduleCore.dispose();
			}
		}
	}

}
