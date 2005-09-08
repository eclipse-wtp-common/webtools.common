/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.componentcore.internal.resources;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.impl.ResourceTreeNode;
import org.eclipse.wst.common.componentcore.internal.impl.ResourceTreeRoot;
import org.eclipse.wst.common.componentcore.resources.ComponentHandle;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public class VirtualFile extends VirtualResource implements IVirtualFile {

	protected VirtualFile(ComponentHandle aComponentHandle, IPath aRuntimePath) {
		super(aComponentHandle, aRuntimePath); 
	}

 	public VirtualFile(IProject aProject, String aComponentName, IPath aRuntimePath) {
		super(ComponentHandle.create(aProject, aComponentName), aRuntimePath);  
	}

	/**
	 * @see IFolder#createLink(org.eclipse.core.runtime.IPath, int,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void createLink(IPath aProjectRelativeLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {

		StructureEdit moduleCore = null;
		try {
			IFile resource = getProject().getFile(aProjectRelativeLocation);

			moduleCore = StructureEdit.getStructureEditForWrite(getProject());
			WorkbenchComponent component = moduleCore.findComponentByName(getComponent().getName());
			
			ResourceTreeRoot root = ResourceTreeRoot.getDeployResourceTreeRoot(component);
			ComponentResource[] resources = root.findModuleResources(getRuntimePath(), ResourceTreeNode.CREATE_NONE);

			if(resources.length == 0) {
				ComponentResource componentResource = moduleCore.createWorkbenchModuleResource(resource);
				componentResource.setRuntimePath(getRuntimePath());
				component.getResources().add(componentResource);
			} else { 
				boolean foundMapping = false;
				for (int resourceIndx = 0; resourceIndx < resources.length && !foundMapping; resourceIndx++) {
					if(aProjectRelativeLocation.equals(resources[resourceIndx].getSourcePath()))
						foundMapping = true;
				}
				if(!foundMapping) {
					ComponentResource componentResource = moduleCore.createWorkbenchModuleResource(resource);
					componentResource.setRuntimePath(getRuntimePath());
					component.getResources().add(componentResource);					
				}
			} 

		} finally {
			if (moduleCore != null) {
				moduleCore.saveIfNecessary(monitor);
				moduleCore.dispose();
			}
		}
	} 
 
	public int getType() {
		return IVirtualResource.FILE;
	}
	
	public IResource getUnderlyingResource() {
		return getUnderlyingFile();
	}
	
	public IResource[] getUnderlyingResources() {
		return getUnderlyingFiles();
	}
	
	public IFile getUnderlyingFile() {
		return getProject().getFile(getProjectRelativePath());
	}
	
	public IFile[] getUnderlyingFiles() {
		IPath[] paths = getProjectRelativePaths();
		List result = new ArrayList();
		for (int i=0; i<paths.length; i++) {
			IFile file = getProject().getFile(paths[i]);
			if (file!=null && file.exists() && !result.contains(file))
				result.add(file);
		}
		return (IFile[]) result.toArray(new IFile[result.size()]);
	}

	protected void doDeleteMetaModel(int updateFlags,IProgressMonitor monitor) {
		//Default
	}	
	
	protected void doDeleteRealResources(int updateFlags, IProgressMonitor monitor) throws CoreException {
		//Default
	}

}
