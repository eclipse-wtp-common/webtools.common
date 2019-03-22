/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-2.0/
 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.eclipse.wst.common.componentcore.internal.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public class ResourceListVirtualFolder extends VirtualFolder {

	public interface ResourceFilter {
		public boolean accepts(IResource resource);
	}
	
	private ArrayList<IResource> children;
	private ArrayList<IContainer> underlying;
	private ResourceFilter filter;
	public ResourceListVirtualFolder(
			IProject aComponentProject,
			IPath aRuntimePath) {
		super(aComponentProject, aRuntimePath);
		this.children = new ArrayList<IResource>();
		this.underlying = new ArrayList<IContainer>();
	}

	public ResourceListVirtualFolder(
			IProject aComponentProject,
			IPath aRuntimePath, IContainer[] underlyingContainers) {
		this(aComponentProject, aRuntimePath);
		addUnderlyingResource(underlyingContainers);
	}

	public ResourceListVirtualFolder(
			IProject aComponentProject,
			IPath aRuntimePath, IContainer[] underlyingContainers, 
			IResource[] looseResources) {
		this(aComponentProject, aRuntimePath, underlyingContainers);
		addChildren(looseResources);
	}

	public void setFilter(ResourceFilter filter) {
		this.filter = filter;
	}
	
	protected void addUnderlyingResource(IResource resource) {
		if( resource instanceof IContainer ) { 
			underlying.add((IContainer)resource);
			try {
				IResource[] newChildren = ((IContainer)resource).members();
				for( int i = 0; i < newChildren.length; i++ ) {
					children.add(newChildren[i]);
				}
			} catch( CoreException ce) {
				// TODO log
			}
		}
	}

	protected void addUnderlyingResource(IResource[] resources) {
		for( int i = 0; i < resources.length; i++ ) {
			addUnderlyingResource(resources[i]);
		}
	}
	
	protected void addChild(IResource resource) {
		this.children.add(resource);
	}

	protected void addChildren(IResource[] resources) {
		this.children.addAll(Arrays.asList(resources));
	}
	
	@Override
	public IResource getUnderlyingResource() {
		return getUnderlyingFolder();
	}
	
	@Override
	public IResource[] getUnderlyingResources() {
		return getUnderlyingFolders();
	}

	@Override
	public IContainer getUnderlyingFolder() { 
		return underlying.size() > 0 ? underlying.get(0) : null;
	}
	
	@Override
	public IContainer[] getUnderlyingFolders() {
		return underlying.toArray(new IContainer[underlying.size()]);
	}

	@Override
	public IVirtualResource[] members(int memberFlags) throws CoreException {
		HashMap<String, IVirtualResource> virtualResources = new HashMap<String, IVirtualResource>(); // result
		IResource[] resources = this.children.toArray(new IResource[this.children.size()]);
		for( int i = 0; i < resources.length; i++ ) {
			handleResource(resources[i], virtualResources, memberFlags);
		}
		Collection c = virtualResources.values();
		return (IVirtualResource[]) c.toArray(new IVirtualResource[c.size()]);
	}

	protected void handleResource(IResource resource, HashMap<String, IVirtualResource> map, int memberFlags) throws CoreException {
		if( filter != null && !filter.accepts(resource))
			return;
		
		if( resource instanceof IFile ) {
			if( !map.containsKey(resource.getName()) ) {
				IVirtualFile virtFile = new VirtualFile(getProject(), 
						getRuntimePath().append(((IFile)resource).getName()), (IFile)resource);
				map.put(resource.getName(), virtFile);
				return;
			} 
		}// end file
		else if( resource instanceof IContainer ) {
			IContainer realContainer = (IContainer) resource;
			//IResource[] realChildResources = realContainer.members(memberFlags);
			IVirtualResource previousValue = map.get(resource.getName());
			if( previousValue != null && previousValue instanceof ResourceListVirtualFolder ) {
				((ResourceListVirtualFolder)previousValue).addUnderlyingResource(realContainer);
			} else if( previousValue == null ) {
				ResourceListVirtualFolder childFolder = 
					new ResourceListVirtualFolder(getProject(), getRuntimePath().append(resource.getName()));
				childFolder.addUnderlyingResource(realContainer);
				if( filter != null )
					childFolder.setFilter(filter);
				map.put(resource.getName(), childFolder);
			}
		} // end container
	}
}
