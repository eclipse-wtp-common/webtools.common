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
package org.eclipse.wst.common.componentcore.internal.resources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.impl.ResourceTreeNode;
import org.eclipse.wst.common.componentcore.internal.impl.ResourceTreeRoot;
import org.eclipse.wst.common.componentcore.resources.IVirtualContainer;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public abstract class VirtualContainer extends VirtualResource implements IVirtualContainer {


	public VirtualContainer(IProject aComponentProject, IPath aRuntimePath) {
		super(aComponentProject, aRuntimePath);
	}

	// TODO WTP:Implement this method
	public boolean exists(IPath path) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	/**
	 * @see IContainer#findMember(java.lang.String)
	 */
	public IVirtualResource findMember(String aChildName) {
		return findMember(new Path(aChildName), 0);
	}

	/**
	 * @see IContainer#findMember(java.lang.String, boolean)
	 */
	public IVirtualResource findMember(String aChildName, int searchFlags) {
		return findMember(new Path(aChildName), searchFlags);
	}

	/**
	 * @see IContainer#findMember(org.eclipse.core.runtime.IPath)
	 */
	public IVirtualResource findMember(IPath aChildPath) {
		return findMember(aChildPath, 0);
	}

	public IVirtualResource findMember(IPath aPath, int theSearchFlags) {

		StructureEdit structureEdit = null;
		try {

			structureEdit = StructureEdit.getStructureEditForRead(getProject());
			WorkbenchComponent component = structureEdit.getComponent();
			ResourceTreeRoot root = ResourceTreeRoot.getDeployResourceTreeRoot(component);
			ComponentResource[] resources = root.findModuleResources(getRuntimePath().append(aPath), ResourceTreeNode.CREATE_NONE);

			for (int i=0; i<resources.length; i++) {
				// return the resources corresponding to the root, not any of the children if its a folder
				if (resources[i].getRuntimePath().equals(getRuntimePath().append(aPath))) {
					IResource platformResource = getProject().findMember(resources[i].getSourcePath()); 
					if(platformResource == null)
						platformResource = ResourcesPlugin.getWorkspace().getRoot().findMember(resources[i].getSourcePath()); 
					if (platformResource != null) {
						switch (platformResource.getType()) {
						case IResource.FOLDER:
						case IResource.PROJECT:
							return new VirtualFolder(getProject(), getRuntimePath().append(aPath));
						case IResource.FILE:
							return new VirtualFile(getProject(), getRuntimePath().append(aPath));
						}
					}
				}
			}
		} finally {
			if (structureEdit != null)
				structureEdit.dispose();
		}
		return null;
	}

	/**
	 * @see IContainer#getFile(org.eclipse.core.runtime.IPath)
	 */
	public IVirtualFile getFile(IPath aPath) {
		return new VirtualFile(getProject(), getRuntimePath().append(aPath));
	}

	/**
	 * @see IContainer#getFolder(org.eclipse.core.runtime.IPath)
	 */
	public IVirtualFolder getFolder(IPath aPath) {
		return new VirtualFolder(getProject(), getRuntimePath().append(aPath));
	}

	/**
	 * @see IFolder#getFile(java.lang.String)
	 */
	public IVirtualFile getFile(String name) {
		return new VirtualFile(getProject(), getRuntimePath().append(name));
	}

	/**
	 * @see IFolder#getFolder(java.lang.String)
	 */
	public IVirtualFolder getFolder(String name) {
		return new VirtualFolder(getProject(), getRuntimePath().append(name));
	}

	/**
	 * @see IContainer#members()
	 */
	public IVirtualResource[] members() throws CoreException {
		return members(IResource.NONE);
	}

	/**
	 * @see IContainer#members(boolean)
	 */
	public IVirtualResource[] members(boolean includePhantoms) throws CoreException {
		return members(includePhantoms ? IGNORE_EXCLUSIONS : IResource.NONE);
	}

	/**
	 * @see IContainer#members(int)
	 */
	public IVirtualResource[] members(int memberFlags) throws CoreException {
		List virtualResources = new ArrayList(); // result
		Set allNames = new HashSet();
		// Ignore all meta data paths in the virtual container resource set
		IPath[] metaPaths = getComponent().getMetaResources();
		for (int i=0; i<metaPaths.length; i++) {
			String localName = getLocalName(metaPaths[i]);
			if (localName != null)
				allNames.add(localName);
		}
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(getProject());
			WorkbenchComponent wbComponent = moduleCore.getComponent();
			if (wbComponent != null) {
				ResourceTreeRoot root = ResourceTreeRoot.getDeployResourceTreeRoot(wbComponent);
				ComponentResource[] componentResources = root.findModuleResources(getRuntimePath(), ResourceTreeNode.CREATE_NONE);
				//componentResources = orderResourcesByFolder(componentResources);

				for (int componentResourceIndex = 0; componentResourceIndex < componentResources.length; componentResourceIndex++) {
					IPath fullRuntimePath = componentResources[componentResourceIndex].getRuntimePath();
					// exact match
					if (fullRuntimePath.equals(getRuntimePath())) {
						
						IResource realResource = getProject().findMember(componentResources[componentResourceIndex].getSourcePath());
						if ((realResource != null) && (realResource.getType() == IResource.FOLDER || realResource.getType() == IResource.PROJECT)) {
							IContainer realContainer = (IContainer) realResource;
							IResource[] realChildResources = realContainer.members(memberFlags);
							for (int realResourceIndex = 0; realResourceIndex < realChildResources.length; realResourceIndex++) {
								IResource child = realChildResources[realResourceIndex];
								String localName = child.getName();
								if (allNames.add(localName)) {
									IPath newRuntimePath = getRuntimePath().append(localName);
									if (child instanceof IFile) {
										virtualResources.add(new VirtualFile(getProject(), newRuntimePath, (IFile) child));
									} else {
										virtualResources.add(new VirtualFolder(getProject(), newRuntimePath));
									}
								}
							}
						}
						// An IResource.FILE would be an error condition (as this is a container)

					} else { // fuzzy match
						String localName = getLocalName(fullRuntimePath);
						if (localName != null && allNames.add(localName)) {
							IResource realResource = StructureEdit.getEclipseResource(componentResources[componentResourceIndex]);
							if (realResource != null) {
								IPath newRuntimePath = getRuntimePath().append(localName);
								if (fullRuntimePath.segmentCount() > getRuntimePath().segmentCount() + 1) {	// not a direct child
									virtualResources.add(new VirtualFolder(getProject(), newRuntimePath));
								} else {
									if (realResource instanceof IFile) {
										virtualResources.add(new VirtualFile(getProject(), newRuntimePath, (IFile) realResource));
									} else {
										virtualResources.add(new VirtualFolder(getProject(), newRuntimePath));
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			if (virtualResources == null)
				return new IVirtualResource[0];
		} finally {
			if (moduleCore != null)
				moduleCore.dispose();
		}
		return (IVirtualResource[]) virtualResources.toArray(new IVirtualResource[virtualResources.size()]);
	}

	/**
	 * Local name within context of this virtual container. 
	 */
	private String getLocalName(IPath path) {
		if (!getRuntimePath().isPrefixOf(path)) return null;
		return path.segment(getRuntimePath().segmentCount());
	}

	public IVirtualFile[] findDeletedMembersWithHistory(int depth, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}

	/**
	 * @see IFolder#createLink(org.eclipse.core.runtime.IPath, int,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void createLink(IPath aProjectRelativeLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {

		StructureEdit moduleCore = null;
		try {
			IContainer resource = null;
			if (aProjectRelativeLocation.isRoot()) {
				resource = getProject();
			} else {
				resource = getProject().getFolder(aProjectRelativeLocation);
			}

			moduleCore = StructureEdit.getStructureEditForWrite(getProject());
			WorkbenchComponent component = moduleCore.getComponent();

			ResourceTreeRoot root = ResourceTreeRoot.getDeployResourceTreeRoot(component);
			ComponentResource[] resources = root.findModuleResources(getRuntimePath(), ResourceTreeNode.CREATE_NONE);

			if (resources.length == 0) {
				ComponentResource componentResource = moduleCore.createWorkbenchModuleResource(resource);
				componentResource.setRuntimePath(getRuntimePath());
				component.getResources().add(componentResource);
			} else {
				boolean foundMapping = false;
				for (int resourceIndx = 0; resourceIndx < resources.length && !foundMapping; resourceIndx++) {
					if (aProjectRelativeLocation.makeAbsolute().equals(resources[resourceIndx].getSourcePath()))
						foundMapping = true;
				}
				if (!foundMapping) {
					ComponentResource componentResource = moduleCore.createWorkbenchModuleResource(resource);
					componentResource.setRuntimePath(getRuntimePath());
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

	public int getType() {
		return IVirtualResource.COMPONENT;
	}

	public IResource getUnderlyingResource() {
		return null;
	}

	public IResource[] getUnderlyingResources() {
		return NO_RESOURCES;
	}

	protected void doDeleteMetaModel(int updateFlags, IProgressMonitor monitor) {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(getProject());
			WorkbenchComponent component = moduleCore.getComponent();
			moduleCore.getComponentModelRoot().getComponents().remove(component);
		} finally {
			if (moduleCore != null) {
				moduleCore.saveIfNecessary(monitor);
				moduleCore.dispose();
			}
		}
	}

	protected void doDeleteRealResources(int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	public IVirtualResource[] getResources(String aResourceType) {
		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForRead(getProject());
			WorkbenchComponent component = core.getComponent();
			List currentResources = component.getResources();
			List foundResources = new ArrayList();

			if (aResourceType != null) {
				for (Iterator iter = currentResources.iterator(); iter.hasNext();) {
					ComponentResource resource = (ComponentResource) iter.next();
					if (aResourceType.equals(resource.getResourceType())) {
						IVirtualResource vres = createVirtualResource(resource);
						if (vres != null)
							foundResources.add(vres);
					}
				}
			}
			return (IVirtualResource[]) foundResources.toArray(new IVirtualResource[foundResources.size()]);
		} finally {
			if (core != null)
				core.dispose();
		}
	}

	private IVirtualResource createVirtualResource(ComponentResource aComponentResource) {
		IResource resource = StructureEdit.getEclipseResource(aComponentResource);
		switch (resource.getType()) {
			case IResource.FILE :
				return ComponentCore.createFile(getProject(), aComponentResource.getRuntimePath());
			case IResource.FOLDER :
				return ComponentCore.createFolder(getProject(), aComponentResource.getRuntimePath());
		}
		return null;
	}
}
