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

import java.util.Arrays;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.modulecore.ComponentResource;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.WorkbenchComponent;
import org.eclipse.wst.common.modulecore.internal.impl.ResourceTreeRoot;
import org.eclipse.wst.common.modulecore.resources.IVirtualContainer;
import org.eclipse.wst.common.modulecore.resources.IVirtualResource;

public abstract class VirtualResource implements IVirtualResource {
	
	protected static final IResource[] NO_RESOURCES = null;
	private ComponentHandle componentHandle;
	private IPath runtimePath;
	private int hashCode;
	private String toString;
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	
	
	protected VirtualResource(ComponentHandle aComponentHandle, IPath aRuntimePath) {
		componentHandle = aComponentHandle;		
		runtimePath = aRuntimePath;
	}
	
	
	protected VirtualResource(IProject aProject, String aComponentName, IPath aRuntimePath) {
		this(ComponentHandle.create(aProject, aComponentName), aRuntimePath);		
	}	

	public void accept(IResourceProxyVisitor visitor, int memberFlags) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void accept(IResourceVisitor visitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void accept(IResourceVisitor visitor, int depth, int memberFlags) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}   
	
	public void delete(boolean force, IProgressMonitor monitor) throws CoreException {
		delete(force ? IResource.FORCE : IResource.NONE, monitor);
	}

	public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		int updateFlags = force ? IResource.FORCE : IResource.NONE;
		updateFlags |= keepHistory ? IResource.KEEP_HISTORY : IResource.NONE;
		delete(updateFlags, monitor);
	}


	public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {
		
		if( (updateFlags & IVirtualResource.IGNORE_UNDERLYING_RESOURCE) == 0) {
			doDeleteRealResources(updateFlags, monitor);
		} 

		doDeleteMetaModel(updateFlags, monitor);		
	}

	protected void doDeleteMetaModel(int updateFlags,IProgressMonitor monitor) {
		ModuleCore moduleCore = null; 
		try {
			moduleCore = ModuleCore.getModuleCoreForWrite(getComponentHandle().getProject());
			WorkbenchComponent component = moduleCore.findWorkbenchModuleByDeployName(getComponentHandle().getName());
			ComponentResource[] resources = component.findWorkbenchModuleResourceByDeployPath(URI.createURI(getRuntimePath().toOSString()));
			component.getResources().removeAll(Arrays.asList(resources));
		} finally {
			if(moduleCore != null) {
				moduleCore.saveIfNecessary(monitor);
				moduleCore.dispose();
			}
		}
	}


	protected abstract void doDeleteRealResources(int updateFlags, IProgressMonitor monitor) throws CoreException;

	// TODO WTP:Implement this method 
	public boolean exists() {
		return true;
	}

	public String getFileExtension() {
		String name = getName();
		int dot = name.lastIndexOf('.');
		if (dot == -1)
			return null;
		if(dot == name.length()-1)
			return EMPTY_STRING;		
		return name.substring(dot+1);
	}
	 
	public IPath getWorkspaceRelativePath() {
		return getProject().getFullPath().append(getProjectRelativePath());
	}
	
	public IPath getRuntimePath() {
		return runtimePath;
	}  
	 
	public IPath getProjectRelativePath() {

		ModuleCore moduleCore = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForRead(getProject());
			WorkbenchComponent component = moduleCore.findWorkbenchModuleByDeployName(getComponentHandle().getName());
			ResourceTreeRoot root = ResourceTreeRoot.getDeployResourceTreeRoot(component);
			
			ComponentResource[] componentResources = new ComponentResource[0];
			IPath currentPath = null;
			IPath potentialMatchRuntimePath = null; 
			
			do { 
				currentPath = (currentPath == null) ? getRuntimePath() : currentPath.removeLastSegments(1);
				componentResources = root.findModuleResources(currentPath, false);
				for (int i = 0; i < componentResources.length; i++) {
					potentialMatchRuntimePath = new Path(componentResources[i].getRuntimePath().path());					
					if(isPotentalMatch(potentialMatchRuntimePath)) {
						IPath sourcePath = new Path(componentResources[i].getSourcePath().path());
						IPath subpath = getRuntimePath().removeFirstSegments(potentialMatchRuntimePath.segmentCount());
						IPath finalPath = sourcePath.append(subpath);
						// already workspace relative
						if(finalPath.segment(0).equals(getComponentHandle().getProject().getName())) {
							return finalPath.removeFirstSegments(1);
						} 
						// make workspace relative
						return finalPath;
					}
				}   
			} while(currentPath.segmentCount() > 0 && componentResources.length == 0);
		} finally {
			if(moduleCore != null) {
				moduleCore.dispose();
			}
		}
		return getRuntimePath();
	}  
	 
	public String getName() {
		return getRuntimePath().lastSegment();
	}
	
	public String getComponentName() {
		return getComponentHandle().getName();
	}
 
	public IVirtualContainer getParent() {
		if(getRuntimePath().segmentCount() > 0)
			return new VirtualFolder(getComponentHandle(), getRuntimePath().removeLastSegments(1));
		return ModuleCore.createContainer(getProject(), getComponentName());
	} 

	public IProject getProject() {
		return getComponentHandle().getProject();
	}

	public boolean isAccessible() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return false;
	} 
	 
	public Object getAdapter(Class adapter) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return null;
	}

	public boolean contains(ISchedulingRule rule) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return false;
	}

	public boolean isConflicting(ISchedulingRule rule) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return false;
	}
  
	public String toString() {
		if(toString == null)
			toString = "["+getComponentHandle()+":"+getRuntimePath()+"]";  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return toString;
	}
	
	public int hashCode() {
		if(hashCode == 0) 
			hashCode = toString().hashCode();
		return hashCode;
	}
	
	public boolean equals(Object anOther) {
		return hashCode() == ((anOther != null && anOther instanceof VirtualResource) ? anOther.hashCode() : 0 );
	} 
	
	public IResource getUnderlyingResource() {
		return null;
	}

	protected ComponentHandle getComponentHandle() {
		return componentHandle;
	} 
	
	protected void createResource(IContainer resource, int updateFlags, IProgressMonitor monitor) throws CoreException {

		if (!resource.getParent().exists())
			createResource(resource.getParent(), updateFlags, monitor);
		if (!resource.exists() && resource.getType() == IResource.FOLDER) { 
			((IFolder) resource).create(updateFlags, true, monitor);
		} 
	}

	protected boolean isPotentalMatch(IPath aRuntimePath) {
		return aRuntimePath.isPrefixOf(getRuntimePath());
	}
 
}
