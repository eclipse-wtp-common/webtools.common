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
package org.eclipse.wst.common.modulecore.internal.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.ComponentResource;
import org.eclipse.wst.common.modulecore.internal.util.IPathProvider;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ResourceTreeNode {

	private Set moduleResources = new HashSet();
	private String pathSegment;
	private final Map children = new HashMap();
	private static final ComponentResource[] NO_MODULE_RESOURCES = new ComponentResource[]{};
	private IPathProvider pathProvider;
	private ResourceTreeNode parent;

	public ResourceTreeNode(String aPathSegment, ResourceTreeNode parent, IPathProvider aPathProvider) {
		pathSegment = aPathSegment;
		pathProvider = aPathProvider;
	}

	public ResourceTreeNode addChild(ResourceTreeNode aChild) {
		children.put(aChild.getPathSegment(), aChild);
		return aChild;
	}

	public ResourceTreeNode addChild(ComponentResource aModuleResource) {
		IPath moduleResourcePath = new Path(getPathProvider().getPath(aModuleResource).toString());
		ResourceTreeNode newChild = findChild(moduleResourcePath, true);
		newChild.addModuleResource(aModuleResource);
		return newChild;
	}

	public ResourceTreeNode removeChild(ResourceTreeNode aChild) {
		return (ResourceTreeNode) children.remove(aChild.getPathSegment());
	}

	public ResourceTreeNode removeChild(ComponentResource aModuleResource) {
		IPath moduleResourcePath = new Path(getPathProvider().getPath(aModuleResource).toString());
		ResourceTreeNode removedChild = findChild(moduleResourcePath, false);
		return removeChild(removedChild);
	}

	public ResourceTreeNode findChild(IPath aPath) {
		return findChild(aPath, true);
	}

	public ResourceTreeNode findChild(IPath aPath, boolean toCreateChildIfNecessary) {
		ResourceTreeNode child = this;
		if (aPath.segmentCount() > 0) {
			child = findChild(aPath.segment(0), toCreateChildIfNecessary);
			if (child == null)
				return null; 
			if(aPath.segmentCount() == 1) 
				return child; 
			child = child.findChild(aPath.removeFirstSegments(1), toCreateChildIfNecessary); 
			
		}
		return child;
	}

	public ResourceTreeNode findChild(String aPathSegment) {
		if(aPathSegment == null || aPathSegment.length() == 0)
			return this;
		return findChild(aPathSegment, false);
	}

	public ResourceTreeNode findChild(String aPathSegment, boolean toCreateChildIfNecessary) {
		ResourceTreeNode childNode = (ResourceTreeNode) children.get(aPathSegment);
		if (childNode == null && toCreateChildIfNecessary)
				childNode = addChild(aPathSegment);
		return childNode;
	}

	public ComponentResource[] findModuleResources(IPath aPath, boolean toCreateChildIfNecessary) {

		Set foundModuleResources = findModuleResourcesSet(aPath, toCreateChildIfNecessary);
		if(foundModuleResources.size() == 0)
			return NO_MODULE_RESOURCES;
		return (ComponentResource[])foundModuleResources.toArray(new ComponentResource[foundModuleResources.size()]);
	}

	public boolean hasModuleResources() {
		return moduleResources.size() > 0;
	}
	
	public ComponentResource[] getModuleResources() {
		return (ComponentResource[])moduleResources.toArray(new ComponentResource[moduleResources.size()]);
	}
	
	private Set findModuleResourcesSet(IPath aPath, boolean toCreateChildIfNecessary) {

		if (aPath.segmentCount() == 0) {
			Set resources = aggregateResources(new HashSet());
			return resources;
		}		
		ResourceTreeNode child = findChild(aPath.segment(0), toCreateChildIfNecessary);
		if (child == null) 
			return findMatchingVirtualPathsSet(aPath);
		Set foundResources = new HashSet();
		foundResources.addAll(child.findModuleResourcesSet(aPath.removeFirstSegments(1), toCreateChildIfNecessary));
		foundResources.addAll(findMatchingVirtualPathsSet(aPath));
		return foundResources;
	} 
	
	private Set findMatchingVirtualPathsSet(IPath aPath) {
		if(hasModuleResources()) {
			ComponentResource moduleResource = null;
			IResource eclipseResource = null;
			IContainer eclipseContainer = null;
			for(Iterator resourceIter = moduleResources.iterator(); resourceIter.hasNext(); ) {
				moduleResource = (ComponentResource) resourceIter.next();
				eclipseResource = ModuleCore.getEclipseResource(moduleResource);
				if(eclipseResource.getType() == IResource.FOLDER) {
					eclipseContainer = (IContainer)eclipseResource;
					if(eclipseContainer.getFile(aPath).exists() || eclipseContainer.getFolder(aPath).exists())
						return Collections.singleton(moduleResource);
				}
					
			}
		}  
		return Collections.EMPTY_SET;
	}

	private Set aggregateResources(Set anAggregationSet) {
		if (hasModuleResources())
			anAggregationSet.addAll(moduleResources);
		ResourceTreeNode childNode = null;
		for (Iterator childrenIterator = children.values().iterator(); childrenIterator.hasNext();) {
			childNode = (ResourceTreeNode) childrenIterator.next();
			childNode.aggregateResources(anAggregationSet);
		}
		return anAggregationSet;
	}

	public int childrenCount() {
		return children.size();
	}

	public String getPathSegment() {
		return pathSegment;
	}

	protected ResourceTreeNode addChild(String aPathSegment) {
		ResourceTreeNode newChild = null;
		if ( (newChild = (ResourceTreeNode) children.get(aPathSegment)) == null) {
			newChild = new ResourceTreeNode(aPathSegment, this, pathProvider);
			children.put(newChild.getPathSegment(), newChild);
		}  
		return newChild;
	}

	protected ResourceTreeNode removeChild(String aPathSegment) {
		return (ResourceTreeNode) children.remove(aPathSegment);
	}

	/* package */ void addModuleResource(ComponentResource aModuleResource) {
		moduleResources.add(aModuleResource);
	}
	
	/* package */ IPathProvider getPathProvider() { 
		return pathProvider;
	}
}
