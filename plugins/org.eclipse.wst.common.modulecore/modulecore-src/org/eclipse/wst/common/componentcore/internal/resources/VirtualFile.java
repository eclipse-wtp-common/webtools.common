/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.resources;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public class VirtualFile extends VirtualResource implements IVirtualFile {

	private IFile underlyingFile;

	public VirtualFile(IProject aComponentProject, IPath aRuntimePath) {
		super(aComponentProject, aRuntimePath); 
	}

	public VirtualFile(IProject aComponentProject, IPath aRuntimePath, IFile underlyingFile) {
		super(aComponentProject, aRuntimePath);
		this.underlyingFile = underlyingFile; 
	}

	/**
	 * @see org.eclipse.core.resources.IFolder#createLink(org.eclipse.core.runtime.IPath, int,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void createLink(IPath aProjectRelativeLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {

		StructureEdit moduleCore = null;
		try {
			IFile resource = getProject().getFile(aProjectRelativeLocation);

			moduleCore = StructureEdit.getStructureEditForWrite(getProject());
			WorkbenchComponent component = moduleCore.getComponent();
			
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
		if (underlyingFile == null) {
			underlyingFile = getProject().getFile(getProjectRelativePath());
		}
		return underlyingFile;
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

	@Override
	public Object getAdapter(Class adapter) { 
		if( java.io.File.class.equals(adapter)) {
			IFile file = getUnderlyingFile();
			return file.getLocation().toFile();
		}
		if( IFile.class.equals(adapter)) {
			return getUnderlyingFile();
		}
		if( IVirtualFile.class.equals(adapter))
			return this;
		if( IVirtualResource.class.equals(adapter))
			return this;
		return null;
	}
}
