/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
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

import org.eclipse.core.resources.IContainer;
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
import org.eclipse.wst.common.componentcore.resources.ITaggedVirtualResource;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public class VirtualFolder extends VirtualContainer implements IVirtualFolder, ITaggedVirtualResource { 

	/**
	 * p> Creates an unassigned mapping contained by the component identified by aComponentHandle
	 * with a runtime path of aRuntimePath.
	 * </p>
	 * 
	 * @param aComponentHandle
	 * @param aRuntimePath
	 */
	public VirtualFolder(IProject aComponentProject, IPath aRuntimePath) {
		super(aComponentProject, aRuntimePath);
	}
 
	// TODO WTP:Implement this method
	public void create(int updateFlags, IProgressMonitor monitor) throws CoreException {

		IVirtualComponent container = ComponentCore.createComponent(getProject()); 
		if( !container.getRootFolder().getProjectRelativePath().isRoot()){
			IFolder realFolder = getProject().getFolder(container.getRootFolder().getProjectRelativePath()); 
			IFolder newFolder = realFolder.getFolder(getRuntimePath()); 
			createResource(newFolder, updateFlags, monitor);  
		}
		else {
			IFolder newFolder = getProject().getFolder(getRuntimePath()); 
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

	public IContainer getUnderlyingFolder() { 
		IPath path = getProjectRelativePath();
		if( !path.isRoot() )
			return getProject().getFolder(getProjectRelativePath());
		return getProject();
	}
	
	public IContainer[] getUnderlyingFolders() {
		IPath[] paths = getProjectRelativePaths();
		List result = new ArrayList();
		for (int i=0; i<paths.length; i++) {
			if( !paths[i].isRoot() ){
				IContainer folder = getProject().getFolder(paths[i]);
				if (folder!=null && folder.exists() && !result.contains(folder))
					result.add(folder);
			} else 
				result.add(getProject());
		}
		return (IContainer[]) result.toArray(new IContainer[result.size()]);
	}

	protected void doDeleteMetaModel(int updateFlags, IProgressMonitor monitor) {

		// only handles explicit mappings
		StructureEdit moduleCore = null;
		try { 
			IPath runtimePath = getRuntimePath();
			moduleCore = StructureEdit.getStructureEditForWrite(getProject());
			WorkbenchComponent component = moduleCore.getComponent();
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
			WorkbenchComponent component = moduleCore.getComponent();
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


	public boolean tagResource(IPath aProjectRelativeLocation, String tag, IProgressMonitor monitor) {
		StructureEdit moduleCore = null;
		try {
			if (aProjectRelativeLocation != null){
				if (aProjectRelativeLocation.isUNC()){
					aProjectRelativeLocation = aProjectRelativeLocation.makeUNC(false);
				}
				IPath absolutePath = aProjectRelativeLocation.makeAbsolute();
				moduleCore = StructureEdit.getStructureEditForWrite(getProject());
				WorkbenchComponent aComponent = moduleCore.getComponent();
				ComponentResource[] resources = aComponent.findResourcesByRuntimePath(getRuntimePath());
				for (ComponentResource resource:resources){
					if (resource.getSourcePath().equals(absolutePath)){
						resource.setTag(tag);
						return true;
					}
				}
			}
			return false;
		}
		finally {
			if (moduleCore != null) {
				moduleCore.saveIfNecessary(monitor);
				moduleCore.dispose();
			}
		}
		
	}

	public IPath getFirstTaggedResource(String tag) {
		List<IPath> paths = internalGetTaggedResources(tag, true);
		return !paths.isEmpty() ? paths.get(0) : null;
	}

	public IPath[] getTaggedResources(String tag) {
		return internalGetTaggedResources(tag, false).toArray(new IPath[0]);
	}
	
	private List<IPath> internalGetTaggedResources (String tag, boolean firstOnly) {
		WorkbenchComponent aComponent = null;
		List<IPath> paths = new ArrayList<IPath>();
		
		aComponent = getReadOnlyComponent();
		if (aComponent != null) {
			ComponentResource[] resources = aComponent.findResourcesByRuntimePath(getRuntimePath());
			for (ComponentResource resource:resources){
				if (tag.equals(resource.getTag())){
					paths.add(resource.getSourcePath());
					if (firstOnly)
						break;
				}
			}
		}
		return paths;
	}

}
