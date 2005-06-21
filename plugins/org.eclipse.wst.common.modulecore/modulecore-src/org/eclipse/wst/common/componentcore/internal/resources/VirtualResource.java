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

import java.util.Arrays;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.impl.ResourceTreeNode;
import org.eclipse.wst.common.componentcore.internal.impl.ResourceTreeRoot;
import org.eclipse.wst.common.componentcore.resources.ComponentHandle;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualContainer;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public abstract class VirtualResource implements IVirtualResource {
	
	protected static final IResource[] NO_RESOURCES = null;
	private ComponentHandle componentHandle;
	private IPath runtimePath;
	private int hashCode;
	private String toString;
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	private IVirtualComponent component;
	private String resourceType;
	
	
	protected VirtualResource(ComponentHandle aComponentHandle, IPath aRuntimePath) {
		componentHandle = aComponentHandle;		
		runtimePath = aRuntimePath;
	}
	
	
	protected VirtualResource(IProject aProject, String aComponentName, IPath aRuntimePath) {
		this(ComponentHandle.create(aProject, aComponentName), aRuntimePath);		
	}	 

	public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {
		
		if( (updateFlags & IVirtualResource.IGNORE_UNDERLYING_RESOURCE) == 0) {
			doDeleteRealResources(updateFlags, monitor);
		} 

		doDeleteMetaModel(updateFlags, monitor);		
	}

	protected void doDeleteMetaModel(int updateFlags,IProgressMonitor monitor) {
		StructureEdit moduleCore = null; 
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(getComponentHandle().getProject());
			WorkbenchComponent component = moduleCore.findComponentByName(getComponentHandle().getName());
			ComponentResource[] resources = component.findResourcesByRuntimePath(getRuntimePath());
			component.getResources().removeAll(Arrays.asList(resources));
		} finally {
			if(moduleCore != null) {
				moduleCore.saveIfNecessary(monitor);
				moduleCore.dispose();
			}
		}
	}


	protected abstract void doDeleteRealResources(int updateFlags, IProgressMonitor monitor) throws CoreException;

	public boolean exists() {
		IResource resource = getUnderlyingResource();
		return resource != null && resource.exists();
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

		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(getProject());
			WorkbenchComponent component = moduleCore.findComponentByName(getComponentHandle().getName());
			if(component != null) {
				ResourceTreeRoot root = ResourceTreeRoot.getDeployResourceTreeRoot(component);
				// still need some sort of loop here to search subpieces of the runtime path.
				ComponentResource[] componentResources = null;
		
				IPath estimatedPath = null;
				IPath searchPath = null;
				do{ 
					searchPath = (searchPath == null) ? getRuntimePath(): searchPath.removeLastSegments(1);
					componentResources = root.findModuleResources(searchPath, ResourceTreeNode.CREATE_NONE);
					estimatedPath = findBestMatch(componentResources);					
				} while(estimatedPath == null && canSearchContinue(componentResources, searchPath));
				return estimatedPath;
			}
		} finally {
			if(moduleCore != null) {
				moduleCore.dispose();
			}
		}
		return getRuntimePath();
	}
 
	private boolean canSearchContinue(ComponentResource[] componentResources, IPath searchPath) {
		return (searchPath.segmentCount() > 0);
	}  
	
	private IPath findBestMatch(ComponentResource[] theComponentResources) {

		int currentMatchLength = 0;
		int bestMatchLength = -1;
		IPath estimatedPath = null;
		IPath currentPath = null;
		final IPath runtimePath = getRuntimePath();
		for (int i = 0; i < theComponentResources.length; i++) { 
			currentPath = theComponentResources[i].getRuntimePath();
			
			if(currentPath.isPrefixOf(runtimePath)) {			
				if(currentPath.segmentCount() == runtimePath.segmentCount())
					return theComponentResources[i].getSourcePath();
				
				currentMatchLength = currentPath.matchingFirstSegments(runtimePath);				
				if(currentMatchLength == currentPath.segmentCount() && currentMatchLength > bestMatchLength) {
					bestMatchLength = currentMatchLength;
					IPath sourcePath = theComponentResources[i].getSourcePath();
					IPath subpath = runtimePath.removeFirstSegments(currentMatchLength);
					estimatedPath = sourcePath.append(subpath);  
				} 
			}
		}    
		return estimatedPath;
	}
	 
	public String getName() {
		return getRuntimePath().lastSegment();
	}
	
	public IVirtualComponent getComponent() {
		if(component == null)
			component = ComponentCore.createComponent(getProject(), getComponentHandle().getName());
		return component;
	}
 
	public IVirtualContainer getParent() {
		if(getRuntimePath().segmentCount() > 1)
			return new VirtualFolder(getComponentHandle(), getRuntimePath().removeLastSegments(1));
		return getComponent().getRootFolder();
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
	 
	
	//TODO Persist the resource type to the model
	public void setResourceType(String aResourceType) {
		resourceType = aResourceType;		
	}
	
	//TODO Fetch the resource type from the model.
	public String getResourceType() {
		return resourceType;
	}

	public ComponentHandle getComponentHandle() {
		return componentHandle;
	} 
	
	protected void createResource(IContainer resource, int updateFlags, IProgressMonitor monitor) throws CoreException {

		if( resource.exists() )
			return;
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
