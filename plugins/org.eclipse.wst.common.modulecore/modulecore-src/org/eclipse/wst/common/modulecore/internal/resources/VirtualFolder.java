/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.modulecore.internal.resources;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.modulecore.ComponentResource;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.WorkbenchComponent;
import org.eclipse.wst.common.modulecore.internal.impl.ResourceTreeRoot;
import org.eclipse.wst.common.modulecore.resources.IVirtualContainer;
import org.eclipse.wst.common.modulecore.resources.IVirtualFolder;
import org.eclipse.wst.common.modulecore.resources.IVirtualResource;

public class VirtualFolder extends VirtualContainer implements IVirtualFolder {

	private final Set realFolders = new HashSet();

	public VirtualFolder(IFolder aRealFolder, String aComponentName, IPath aRuntimePath) {

		super(aRealFolder.getProject(), aComponentName, aRuntimePath);
		realFolders.add(aRealFolder);
	}

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

		IVirtualContainer container = ModuleCore.create(getProject(), getComponentHandle().getName());
		IVirtualFolder root = container.getFolder(new Path("/"));  //$NON-NLS-1$		
		IFolder realFolder = getProject().getFolder(root.getProjectRelativePath()); 
		IFolder newFolder = realFolder.getFolder(getRuntimePath()); 
		createResource(newFolder, updateFlags, monitor); 

	}

	/**
	 * @see IFolder#createLink(org.eclipse.core.runtime.IPath, int,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void createLink(IPath aProjectRelativeLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {

		ModuleCore moduleCore = null;
		try {
			IFolder resource = getProject().getFolder(aProjectRelativeLocation);

			moduleCore = ModuleCore.getModuleCoreForWrite(getProject());
			WorkbenchComponent component = moduleCore.findWorkbenchModuleByDeployName(getComponentHandle().getName());
			
			ResourceTreeRoot root = ResourceTreeRoot.getDeployResourceTreeRoot(component);
			ComponentResource[] resources = root.findModuleResources(getRuntimePath(), false);

			if(resources.length == 0) {
				ComponentResource componentResource = moduleCore.createWorkbenchModuleResource(resource);
				componentResource.setRuntimePath(URI.createURI(getRuntimePath().toString()));
				component.getResources().add(componentResource);
			} else {
				URI projectRelativeURI = URI.createURI(aProjectRelativeLocation.toString());
				boolean foundMapping = false;
				for (int resourceIndx = 0; resourceIndx < resources.length && !foundMapping; resourceIndx++) {
					if(projectRelativeURI.equals(resources[resourceIndx].getSourcePath()))
						foundMapping = true;
				}
				if(!foundMapping) {
					ComponentResource componentResource = moduleCore.createWorkbenchModuleResource(resource);
					componentResource.setRuntimePath(URI.createURI(getRuntimePath().toString()));
					component.getResources().add(componentResource);					
				}
			}

			createResource(resource, updateFlags, monitor);

		} finally {
			if (moduleCore != null) {
				moduleCore.saveIfNecessary(monitor);
				moduleCore.dispose();
			}
		}
	}

	private void createResource(IContainer resource, int updateFlags, IProgressMonitor monitor) throws CoreException {

		if (!resource.getParent().exists())
			createResource(resource.getParent(), updateFlags, monitor);
		if (!resource.exists())
			((IFolder) resource).create(updateFlags, true, monitor);

	}

	// TODO WTP:Implement this method
	public boolean exists(IPath path) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return false;
	}

	// TODO WTP:Implement this method
	public IVirtualResource findMember(String name) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}

	// TODO WTP:Implement this method
	public IVirtualResource findMember(String name, boolean includePhantoms) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}

	// TODO WTP:Implement this method
	public IVirtualResource findMember(IPath path) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}

	// TODO WTP:Implement this method
	public IVirtualResource findMember(IPath path, boolean includePhantoms) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}
 
	public int getType() {
		return IResource.FOLDER;
	}

	public void commit() throws CoreException {
 
	}

	public IFolder getRealFolder() { 
		return getProject().getFolder(getProjectRelativePath());
	}
	
	public IFolder[] getRealFolders() {
		return new IFolder[] {getRealFolder()};
	}

	protected void doDeleteMetaModel(int updateFlags, IProgressMonitor monitor) {

		// only handles explicit mappings
		ModuleCore moduleCore = null;
		try {
			URI runtimeURI = URI.createURI(getRuntimePath().toString());
			moduleCore = ModuleCore.getModuleCoreForWrite(getProject());
			WorkbenchComponent component = moduleCore.findWorkbenchModuleByDeployName(getComponentName());
			ComponentResource[] resources = component.findWorkbenchModuleResourceByDeployPath(runtimeURI);
			for (int i = 0; i < resources.length; i++) {
				if(runtimeURI.equals(resources[i].getRuntimePath())) 
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
		ModuleCore moduleCore = null;
		try {
			URI runtimeURI = URI.createURI(getRuntimePath().toString());
			moduleCore = ModuleCore.getModuleCoreForWrite(getProject());
			WorkbenchComponent component = moduleCore.findWorkbenchModuleByDeployName(getComponentName());
			ComponentResource[] resources = component.findWorkbenchModuleResourceByDeployPath(runtimeURI);
			IResource realResource;
			for (int i = 0; i < resources.length; i++) {
				if(runtimeURI.equals(resources[i].getRuntimePath())) {
					realResource = ModuleCore.getEclipseResource(resources[i]);
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
