/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.impl;

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
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.util.IPathProvider;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ResourceTreeNode {
	
	public static final int CREATE_NONE = 0x0;
	/** 
	 * Type constant (bit mask value 1) which identifies creating child nodes.
	 *
	 */
	public static final int CREATE_TREENODE_IFNEC = 0x1;

	/**
	 * Type constant (bit mask value 2) which identifies always creating a virtual resource.
	 *
	 */
	public static final int CREATE_RESOURCE_ALWAYS = 0x2;

	private final Set moduleResources = Collections.synchronizedSet(new HashSet());	
	private final Map children = Collections.synchronizedMap(new HashMap());
	private static final ComponentResource[] NO_MODULE_RESOURCES = new ComponentResource[]{};
	private IPathProvider pathProvider;
//	private ResourceTreeNode parent;
	private String pathSegment;

	public ResourceTreeNode(String aPathSegment, ResourceTreeNode parent, IPathProvider aPathProvider) {
		pathSegment = aPathSegment;
		pathProvider = aPathProvider;
	}

	public ResourceTreeNode addChild(ResourceTreeNode aChild) {
		children.put(aChild.getPathSegment(), aChild);
		return aChild;
	}

	public ResourceTreeNode addChild(ComponentResource aModuleResource) {
		ResourceTreeNode newChild = findChild(getPathProvider().getPath(aModuleResource), CREATE_TREENODE_IFNEC);
		if(newChild != null) {
			newChild.addModuleResource(aModuleResource);
			return newChild;
		}
		return null;
	}

	public ResourceTreeNode removeChild(ResourceTreeNode aChild) {
		return (ResourceTreeNode) children.remove(aChild.getPathSegment());
	}

	public ResourceTreeNode removeChild(ComponentResource aModuleResource) { 
		ResourceTreeNode containingChild = findChild(getPathProvider().getPath(aModuleResource), CREATE_NONE);
		if(containingChild != null) {
			containingChild.removeResource(aModuleResource);
			if(containingChild.hasModuleResources())
				return containingChild;
			return removeChild(containingChild);
		}
		return null;
	}

	public ResourceTreeNode removeChild(IPath targetPath, ComponentResource aModuleResource) { 
		ResourceTreeNode containingChild = findChild(targetPath, CREATE_NONE);
		if(containingChild != null) {
			containingChild.removeResource(aModuleResource);
			if(containingChild.hasModuleResources())
				return containingChild;
			return removeChild(containingChild);
		}
		return null;			
	}
	
	public void removeResource(ComponentResource aResource) {
		moduleResources.remove(aResource);
	}

	public ResourceTreeNode findChild(IPath aPath) {
		return findChild(aPath, CREATE_TREENODE_IFNEC);
	}

	public ResourceTreeNode findChild(IPath aPath, int creationFlags) {
		if(aPath == null)
			return null;
		ResourceTreeNode child = this;
		if (aPath.segmentCount() > 0) {
			child = findChild(aPath.segment(0), creationFlags);
			if (child == null)
				return null;
			if (aPath.segmentCount() == 1)
				return child;
			child = child.findChild(aPath.removeFirstSegments(1), creationFlags);

		}
		return child;
	}

	public ResourceTreeNode findChild(String aPathSegment) {
		if (aPathSegment == null || aPathSegment.length() == 0)
			return this;
		return findChild(aPathSegment, CREATE_NONE);
	}

	public ResourceTreeNode findChild(String aPathSegment, int creationFlags) {
		boolean toCreateChildIfNecessary = (creationFlags & CREATE_TREENODE_IFNEC) == CREATE_TREENODE_IFNEC;
		ResourceTreeNode childNode = (ResourceTreeNode) children.get(aPathSegment);
		if (childNode == null && toCreateChildIfNecessary)
			childNode = addChild(aPathSegment);
		return childNode;
	}

	public ComponentResource[] findModuleResources(IPath aPath, int creationFlags) {

		Set foundModuleResources = findModuleResourcesSet(aPath, aPath, creationFlags);
		if (foundModuleResources.size() == 0)
			return NO_MODULE_RESOURCES;
		return (ComponentResource[]) foundModuleResources.toArray(new ComponentResource[foundModuleResources.size()]);
	}
	public boolean exists(IPath aPath, int creationFlags) {

		Set foundModuleResources = findModuleResourcesSet(aPath, aPath, creationFlags);
		if (foundModuleResources.size() == 0) {
			if (true) {
				ResourceTreeNode child = findChild(aPath.segment(0), creationFlags);
				if (child != null)
					return true;
			}
			return false;
		}
		return true;
	}

	public boolean hasModuleResources() {
		return moduleResources.size() > 0;
	}

	public ComponentResource[] getModuleResources() {
		return (ComponentResource[]) moduleResources.toArray(new ComponentResource[moduleResources.size()]);
	}

	private Set findModuleResourcesSet(IPath aFullPath, IPath aPath, int creationFlags) {

		if (aPath.segmentCount() == 0) {
			Set resources = aggregateResources(new HashSet());
			return resources;
		}
		ResourceTreeNode child = findChild(aPath.segment(0), creationFlags);
		if (child == null)
			return findMatchingVirtualPathsSet(aFullPath, aPath, creationFlags);
		Set foundResources = new HashSet();
		foundResources.addAll(child.findModuleResourcesSet(aFullPath, aPath.removeFirstSegments(1), creationFlags));
		foundResources.addAll(findMatchingVirtualPathsSet(aFullPath, aPath, creationFlags));
		return foundResources;
	}

	private Set findMatchingVirtualPathsSet(IPath aFullPath, IPath aPath, int creationFlags) {
		boolean toCreateResourceAlways = (creationFlags & CREATE_RESOURCE_ALWAYS) == CREATE_RESOURCE_ALWAYS;
		if (hasModuleResources()) {
			ComponentResource moduleResource = null;
			IResource eclipseResource = null;
			IContainer eclipseContainer = null;
			Set resultSet = new HashSet();
			for (Iterator resourceIter = moduleResources.iterator(); resourceIter.hasNext();) {
				moduleResource = (ComponentResource) resourceIter.next();
				if(moduleResource.getRuntimePath() != null) {
					eclipseResource = StructureEdit.getEclipseResource(moduleResource);
					
					if (eclipseResource != null && (eclipseResource.getType() == IResource.FOLDER || eclipseResource.getType() == IResource.PROJECT)) {
						eclipseContainer = (IContainer) eclipseResource;
				 
						IPath runtimeURI = moduleResource.getRuntimePath().append(aPath);
						
						// check for existing subpath in tree
						ComponentResource newResource = findExistingComponentResource(moduleResource.getComponent(), runtimeURI);
						
						// add new resource if null or found resource does not have the same source path
						IPath srcPath = eclipseContainer.getProjectRelativePath().append(aPath);
						if(newResource == null || !newResource.getSourcePath().equals(srcPath)) {
							// flesh out the tree
							if ((toCreateResourceAlways) || (eclipseContainer.findMember(aPath)) != null) {
								newResource = ComponentcorePackage.eINSTANCE.getComponentcoreFactory().createComponentResource();
								newResource.setComponent(moduleResource.getComponent());		
								newResource.setRuntimePath(runtimeURI);
								newResource.setSourcePath(srcPath);
								resultSet.add(newResource);
							}
						}
					}
		
				}
			}
			return resultSet.size() > 0 ? resultSet : Collections.EMPTY_SET;
		}
		return Collections.EMPTY_SET;
	}

	private ComponentResource findExistingComponentResource(WorkbenchComponent component, IPath runtimeURI) { 
		List resources = component.getResources();
		for (Iterator iter = resources.iterator(); iter.hasNext();) {
			ComponentResource element = (ComponentResource) iter.next();
			if(runtimeURI.equals(element.getRuntimePath()))
				return element;
			
		}
		return null;
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
		if ((newChild = (ResourceTreeNode) children.get(aPathSegment)) == null) {
			newChild = new ResourceTreeNode(aPathSegment, this, pathProvider);
			children.put(newChild.getPathSegment(), newChild);
		}
		return newChild;
	}

	protected ResourceTreeNode removeChild(String aPathSegment) {
		return (ResourceTreeNode) children.remove(aPathSegment);
	}

	/* package */void addModuleResource(ComponentResource aModuleResource) {
		moduleResources.add(aModuleResource);
	}

	/* package */IPathProvider getPathProvider() {
		return pathProvider;
	}
}
