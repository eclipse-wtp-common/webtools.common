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
package org.eclipse.wst.common.modulecore.internal.resources;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.modulecore.ComponentResource;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.WorkbenchComponent;
import org.eclipse.wst.common.modulecore.internal.impl.ResourceTreeRoot;

public class VirtualContainer extends VirtualResource implements IContainer {

	public VirtualContainer(IProject aProject, String aName, IPath aRuntimePath) {
		super(aProject, aName, aRuntimePath);
	}	

	public VirtualContainer(ComponentHandle aComponentHandle, IPath aRuntimePath) {
		super(aComponentHandle, aRuntimePath);
	}

	// TODO WTP:Implement this method 
	public boolean exists(IPath path) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return false;
	}

	/**
	 * @see IContainer#findMember(java.lang.String)
	 */
	public IResource findMember(String aChildName) { 
		return findMember(aChildName, false);
	}

	/**
	 * @see IContainer#findMember(java.lang.String, boolean)
	 */
	public IResource findMember(String aChildName, boolean includePhantoms) {
		return findMember(getRuntimePath().append(aChildName), includePhantoms);
	}

	/**
	 * @see IContainer#findMember(org.eclipse.core.runtime.IPath)
	 */
	public IResource findMember(IPath aChildPath) { 
		return findMember(aChildPath, false);
	}

	public IResource findMember(IPath path, boolean includePhantoms) {

//		ModuleCore moduleCore = null;
//		Set virtualResources = null;
//		try { 
//			
//			moduleCore = ModuleCore.getModuleCoreForRead(getComponentHandle().getProject());
//			WorkbenchComponent component = moduleCore.findWorkbenchModuleByDeployName(getComponentHandle().getName());
//			ResourceTreeRoot root = ResourceTreeRoot.getDeployResourceTreeRoot(component);
//			ComponentResource[] componentResources = root.findModuleResources(getRuntimePath(), false);
//			 
//			
//		} finally {
//			if(moduleCore != null)
//				moduleCore.dispose();
//		}
		return new VirtualFolder(getComponentHandle(), getRuntimePath().append(path));
	}

	public String getDefaultCharset() throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return null;
	}

	public String getDefaultCharset(boolean checkImplicit) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return null;
	}

	/**
	 * @see IContainer#getFile(org.eclipse.core.runtime.IPath)
	 */
	public IFile getFile(IPath aPath) {
		return new VirtualFile(getComponentHandle(), getRuntimePath().append(aPath));
	}

	/**
	 * @see IContainer#getFolder(org.eclipse.core.runtime.IPath)
	 */
	public IFolder getFolder(IPath aPath) {
		return new VirtualFolder(getComponentHandle(), getRuntimePath().append(aPath));
	}
	

	/**
	 * @see IFolder#getFile(java.lang.String) 
	 */
	public IFile getFile(String name) {
		return getFile(new Path(name));
	}

	/**
	 * @see IFolder#getFolder(java.lang.String)
	 */
	public IFolder getFolder(String name) {
		return getFolder(new Path(name));
	}

	/**
	 * @see IContainer#members()
	 */
	public IResource[] members() throws CoreException {
		return members(IResource.NONE);
	}

	/**
	 * @see IContainer#members(boolean)
	 */
	public IResource[] members(boolean includePhantoms) throws CoreException {
		return members(includePhantoms ? INCLUDE_PHANTOMS : IResource.NONE);
	}
 
	/**
	 * @see IContainer#members(int)
	 */
	public IResource[] members(int memberFlags) throws CoreException {
		
		ModuleCore moduleCore = null;
		Set virtualResources = null;
		try { 
			
			moduleCore = ModuleCore.getModuleCoreForRead(getComponentHandle().getProject());
			WorkbenchComponent component = moduleCore.findWorkbenchModuleByDeployName(getComponentHandle().getName());
			ResourceTreeRoot root = ResourceTreeRoot.getDeployResourceTreeRoot(component);
			ComponentResource[] componentResources = root.findModuleResources(getRuntimePath(), false);
			
			virtualResources = new HashSet();
			IResource realResource = null;
			IPath fullRuntimePath = null;
			IPath newRuntimePath = null;
			
			for (int componentResourceIndex = 0; componentResourceIndex < componentResources.length; componentResourceIndex++) {
				fullRuntimePath = new Path(componentResources[componentResourceIndex].getRuntimePath().path());
				
				// exact match 
				if(fullRuntimePath.equals(getRuntimePath())) {

					realResource = ModuleCore.getEclipseResource(componentResources[componentResourceIndex]); 
					if(realResource.getType() == IResource.FOLDER) {
						IFolder realFolder = (IFolder) realResource;
						IResource[] realChildResources = realFolder.members(memberFlags);
						for (int realResourceIndex = 0; realResourceIndex < realChildResources.length; realResourceIndex++) {
							newRuntimePath = getRuntimePath().append(realChildResources[realResourceIndex].getName());
							addVirtualResource(virtualResources, realChildResources[realResourceIndex], newRuntimePath); 	
						}						
					} // An IResource.FILE would be an error condition (as this is a container)
				
				} else { // fuzzy match
					newRuntimePath = getRuntimePath().append(fullRuntimePath.segment(getRuntimePath().segmentCount()));
					
					if (fullRuntimePath.segmentCount() == 1) {			
						realResource = ModuleCore.getEclipseResource(componentResources[componentResourceIndex]);
						addVirtualResource(virtualResources, realResource, newRuntimePath);
					} else if(fullRuntimePath.segmentCount() > 1) { 
						virtualResources.add(new VirtualFolder(getComponentHandle(), newRuntimePath));
					}
				}
					
			}
			
		} finally {
			if(moduleCore != null)
				moduleCore.dispose();
		}
		return (IResource[]) virtualResources.toArray(new IResource[virtualResources.size()]);
	}

	/**
	 * @param virtualResources
	 * @param realResource
	 * @param newRuntimePath
	 */
	private void addVirtualResource(Set virtualResources, IResource realResource, IPath newRuntimePath) {
		if(realResource.getType() == IResource.FOLDER)
			virtualResources.add(new VirtualFolder(getComponentHandle(), newRuntimePath));
		else
			virtualResources.add(new VirtualFile(getComponentHandle(), newRuntimePath));
	}
	 
	public IFile[] findDeletedMembersWithHistory(int depth, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return null;
	}

	public void setDefaultCharset(String charset) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void setDefaultCharset(String charset, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}
	
	protected void doDeleteMetaModel(int updateFlags,IProgressMonitor monitor) {
		ModuleCore moduleCore = null; 
		try {
			moduleCore = ModuleCore.getModuleCoreForRead(getComponentHandle().getProject());
			WorkbenchComponent component = moduleCore.findWorkbenchModuleByDeployName(getComponentHandle().getName());
			moduleCore.getModuleModelRoot().getComponents().remove(component);
		} finally {
			if(moduleCore != null) {
				moduleCore.saveIfNecessary(monitor);
				moduleCore.dispose();
			}
		}
	}
	
	protected void doDeleteRealResources(int updateFlags,IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

}
