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
package org.eclipse.wst.common.modulecore.util;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.wst.common.modulecore.WorkbenchModuleResource;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ResourceTreeNode {

	private Set moduleResources = new HashSet();
	private String pathSegment;
	private final Map children = new HashMap();
	private static final WorkbenchModuleResource[] NO_MODULE_RESOURCES = new WorkbenchModuleResource[]{};
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

	public ResourceTreeNode addChild(WorkbenchModuleResource aModuleResource) {
		IPath moduleResourcePath = new Path(getPathProvider().getPath(aModuleResource).toString());
		ResourceTreeNode newChild = findChild(moduleResourcePath, true);
		newChild.addModuleResource(aModuleResource);
		return newChild;
	}

	public ResourceTreeNode removeChild(ResourceTreeNode aChild) {
		return (ResourceTreeNode) children.remove(aChild.getPathSegment());
	}

	public ResourceTreeNode removeChild(WorkbenchModuleResource aModuleResource) {
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

	public WorkbenchModuleResource[] findModuleResources(IPath aPath, boolean toCreateChildIfNecessary) {

		if (aPath.segmentCount() == 0) {
			List resources = aggregateResources(new ArrayList());
			return (WorkbenchModuleResource[])resources.toArray(new WorkbenchModuleResource[resources.size()]);
		}		
		ResourceTreeNode child = findChild(aPath.segment(0), toCreateChildIfNecessary);
		if (child == null) 
			return findMatchingVirtualPaths(aPath);
		WorkbenchModuleResource[] resourcesFromChildren = child.findModuleResources(aPath.removeFirstSegments(1), toCreateChildIfNecessary);
		WorkbenchModuleResource[] resourcesFromCurrent = findMatchingVirtualPaths(aPath);
		WorkbenchModuleResource[] collectedResources = new WorkbenchModuleResource[resourcesFromChildren.length+resourcesFromCurrent.length];
		System.arraycopy(resourcesFromChildren, 0, collectedResources, 0, resourcesFromChildren.length);
		System.arraycopy(resourcesFromCurrent, 0, collectedResources, resourcesFromChildren.length, resourcesFromCurrent.length);
		return collectedResources;
	}

	public boolean hasModuleResources() {
		return moduleResources.size() > 0;
	}
	
	public WorkbenchModuleResource[] getModuleResources() {
		return (WorkbenchModuleResource[])moduleResources.toArray(new WorkbenchModuleResource[moduleResources.size()]);
	}
	
	private WorkbenchModuleResource[] findMatchingVirtualPaths(IPath aPath) {
		if(hasModuleResources()) {
			WorkbenchModuleResource moduleResource = null;
			IResource eclipseResource = null;
			IContainer eclipseContainer = null;
			for(Iterator resourceIter = moduleResources.iterator(); resourceIter.hasNext(); ) {
				moduleResource = (WorkbenchModuleResource) resourceIter.next();
				eclipseResource = ModuleCore.getResource(moduleResource);
				if(eclipseResource.getType() == IResource.FOLDER) {
					eclipseContainer = (IContainer)eclipseResource;
					if(eclipseContainer.getFile(aPath).exists() || eclipseContainer.getFolder(aPath).exists())
						return new WorkbenchModuleResource[] {moduleResource};
				}
					
			}
		}  
		return NO_MODULE_RESOURCES;
	}

	private List aggregateResources(List anAggregationList) {
		if (hasModuleResources())
			anAggregationList.addAll(moduleResources);
		ResourceTreeNode childNode = null;
		for (Iterator childrenIterator = children.values().iterator(); childrenIterator.hasNext();) {
			childNode = (ResourceTreeNode) childrenIterator.next();
			childNode.aggregateResources(anAggregationList);
		}
		return anAggregationList;
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

	void addModuleResource(WorkbenchModuleResource aModuleResource) {
		moduleResources.add(aModuleResource);
	}
	
	IPathProvider getPathProvider() { 
		return pathProvider;
	}
}
