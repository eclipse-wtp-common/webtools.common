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
	
	private ComponentHandle componentHandle;
	private IPath runtimePath;
	private int hashCode;
	private String toString;
	
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

	public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
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
		doDeleteMetaModel(updateFlags, monitor);
		
		
		if( (updateFlags & ModuleCore.DELETE_METAMODEL_ONLY) == 0) {
			doDeleteRealResources(updateFlags, monitor);
		} 
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
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return false;
	}

	public String getFileExtension() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return null;
	}
	
	// TODO WTP:Implement this method 
	public IPath getWorkspaceRelativePath() {
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
							return finalPath;
						} 
						// make workspace relative
						return new Path(IPath.SEPARATOR+getProject().getName()).append(finalPath);
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

	private boolean isPotentalMatch(IPath aRuntimePath) {
		return aRuntimePath.isPrefixOf(getRuntimePath());
	}

	// TODO WTP:Implement this method 
	public IPath getLocation() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return null;
	} 
	
	// TODO WTP:Implement this method 
	public String getName() {
		return getRuntimePath().lastSegment();
	}
	
	public String getComponentName() {
		return getComponentHandle().getName();
	}

	// TODO WTP:Implement this method 
	public IVirtualContainer getParent() {
		return new VirtualFolder(getComponentHandle(), getRuntimePath().removeLastSegments(1));
	} 

	public IProject getProject() {
		return getComponentHandle().getProject();
	}

	// TODO WTP:Implement this method 
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

	public int getType() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return 0;
	}
 

	public boolean isAccessible() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return false;
	} 
	
	public boolean isReadOnly() {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return false;
	} 

	public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	} 

	public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	} 

	public void setReadOnly(boolean readOnly) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

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

	public IPath getRuntimePath() {
		return runtimePath;
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

	public ComponentHandle getComponentHandle() {
		return componentHandle;
	} 
}
