/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.componentcore.internal.resources;

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
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.impl.ResourceTreeNode;
import org.eclipse.wst.common.componentcore.internal.impl.ResourceTreeRoot;
import org.eclipse.wst.common.componentcore.resources.ComponentHandle;
import org.eclipse.wst.common.componentcore.resources.IVirtualContainer;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public class VirtualContainer extends VirtualResource implements
		IVirtualContainer {

	public VirtualContainer(IProject aProject, String aName, IPath aRuntimePath) {
		super(aProject, aName, aRuntimePath);
	}

	public VirtualContainer(ComponentHandle aComponentHandle, IPath aRuntimePath) {
		super(aComponentHandle, aRuntimePath);
	}

	// TODO WTP:Implement this method
	public boolean exists(IPath path) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return false;
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
		Set virtualResources = null;
		try {

			structureEdit = StructureEdit.getStructureEditForRead(getProject());
			WorkbenchComponent component = structureEdit
					.findComponentByName(getComponentHandle().getName());
			ResourceTreeRoot root = ResourceTreeRoot
					.getDeployResourceTreeRoot(component);
			ComponentResource[] resources = root.findModuleResources(aPath,
					ResourceTreeNode.CREATE_NONE);

			if (resources.length != 0) {

				IResource platformResource = StructureEdit
						.getEclipseResource(resources[0]);
				if (platformResource != null) {
					switch (platformResource.getType()) {
					case IResource.FOLDER:
						return new VirtualFolder(getComponentHandle(),
								getRuntimePath().append(aPath));
					case IResource.FILE:
						return new VirtualFile(getComponentHandle(),
								getRuntimePath().append(aPath));
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
		return new VirtualFile(getComponentHandle(), getRuntimePath().append(
				aPath));
	}

	/**
	 * @see IContainer#getFolder(org.eclipse.core.runtime.IPath)
	 */
	public IVirtualFolder getFolder(IPath aPath) {
		return new VirtualFolder(getComponentHandle(), getRuntimePath().append(
				aPath));
	}

	/**
	 * @see IFolder#getFile(java.lang.String)
	 */
	public IVirtualFile getFile(String name) {
		return new VirtualFile(getComponentHandle(), getRuntimePath().append(
				name));
	}

	/**
	 * @see IFolder#getFolder(java.lang.String)
	 */
	public IVirtualFolder getFolder(String name) {
		return new VirtualFolder(getComponentHandle(), getRuntimePath().append(
				name));
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
	public IVirtualResource[] members(boolean includePhantoms)
			throws CoreException {
		return members(includePhantoms ? IGNORE_EXCLUSIONS : IResource.NONE);
	}

	/**
	 * @see IContainer#members(int)
	 */
	public IVirtualResource[] members(int memberFlags) throws CoreException {

		StructureEdit moduleCore = null;
		Set virtualResources = new HashSet();
		try {

			moduleCore = StructureEdit
					.getStructureEditForRead(getComponentHandle().getProject());
			WorkbenchComponent component = moduleCore
					.findComponentByName(getComponentHandle().getName());
			ResourceTreeRoot root = ResourceTreeRoot
					.getDeployResourceTreeRoot(component);
			ComponentResource[] componentResources = root.findModuleResources(
					getRuntimePath(), ResourceTreeNode.CREATE_NONE);

			IResource realResource = null;
			IPath fullRuntimePath = null;
			IPath newRuntimePath = null;

			for (int componentResourceIndex = 0; componentResourceIndex < componentResources.length; componentResourceIndex++) {
				fullRuntimePath = componentResources[componentResourceIndex]
						.getRuntimePath();

				// exact match
				if (fullRuntimePath.equals(getRuntimePath())) {

					realResource = StructureEdit
							.getEclipseResource(componentResources[componentResourceIndex]);
					if (realResource.getType() == IResource.FOLDER) {
						IFolder realFolder = (IFolder) realResource;
						IResource[] realChildResources = realFolder
								.members(memberFlags);
						for (int realResourceIndex = 0; realResourceIndex < realChildResources.length; realResourceIndex++) {
							newRuntimePath = getRuntimePath().append(
									realChildResources[realResourceIndex]
											.getName());
							addVirtualResource(virtualResources,
									realChildResources[realResourceIndex],
									newRuntimePath);
						}
					} // An IResource.FILE would be an error condition (as
					// this is a container)

				} else { // fuzzy match
					newRuntimePath = getRuntimePath().append(
							fullRuntimePath.segment(getRuntimePath()
									.segmentCount()));

					if (fullRuntimePath.segmentCount() == 1) {
						realResource = StructureEdit
								.getEclipseResource(componentResources[componentResourceIndex]);
						if (realResource != null)
							addVirtualResource(virtualResources, realResource,
									newRuntimePath);
					} else if (fullRuntimePath.segmentCount() > 1) {
						virtualResources.add(new VirtualFolder(
								getComponentHandle(), newRuntimePath));
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
		return (IVirtualResource[]) virtualResources
				.toArray(new IVirtualResource[virtualResources.size()]);
	}

	public IVirtualFile[] findDeletedMembersWithHistory(int depth,
			IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}

	public void create(int updateFlags, IProgressMonitor aMonitor)
			throws CoreException {

		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(getProject());
			WorkbenchComponent component = moduleCore
					.findComponentByName(getComponentHandle().getName());
			if (component == null)
				moduleCore
						.createWorkbenchModule(getComponentHandle().getName());
		} finally {
			if (moduleCore != null) {
				moduleCore.saveIfNecessary(null);
				moduleCore.dispose();
			}
		}
	}

	/**
	 * @see IFolder#createLink(org.eclipse.core.runtime.IPath, int,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void createLink(IPath aProjectRelativeLocation, int updateFlags,
			IProgressMonitor monitor) throws CoreException {

		StructureEdit moduleCore = null;
		boolean isRootFolder = false;
		try {
			IContainer resource = null;
			if( aProjectRelativeLocation.isRoot()){				
				resource = getProject();
			}else{
				resource = getProject().getFolder(aProjectRelativeLocation);
			}

			moduleCore = StructureEdit.getStructureEditForWrite(getProject());
			WorkbenchComponent component = moduleCore
					.findComponentByName(getComponent().getName());

			ResourceTreeRoot root = ResourceTreeRoot
					.getDeployResourceTreeRoot(component);
			ComponentResource[] resources = root.findModuleResources(
					getRuntimePath(), ResourceTreeNode.CREATE_NONE);

			if (resources.length == 0) {
				ComponentResource componentResource = moduleCore
						.createWorkbenchModuleResource(resource);
				componentResource.setRuntimePath(getRuntimePath());
				component.getResources().add(componentResource);
			} else {
				boolean foundMapping = false;
				for (int resourceIndx = 0; resourceIndx < resources.length
						&& !foundMapping; resourceIndx++) {
					if (aProjectRelativeLocation.equals(resources[resourceIndx]
							.getSourcePath()))
						foundMapping = true;
				}
				if (!foundMapping) {
					ComponentResource componentResource = moduleCore
							.createWorkbenchModuleResource(resource);
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
			moduleCore = StructureEdit
					.getStructureEditForWrite(getComponentHandle().getProject());
			WorkbenchComponent component = moduleCore
					.findComponentByName(getComponentHandle().getName());
			moduleCore.getComponentModelRoot().getComponents()
					.remove(component);
		} finally {
			if (moduleCore != null) {
				moduleCore.saveIfNecessary(monitor);
				moduleCore.dispose();
			}
		}
	}

	protected void doDeleteRealResources(int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
	}

	/**
	 * @param virtualResources
	 * @param realResource
	 * @param newRuntimePath
	 */
	private void addVirtualResource(Set virtualResources,
			IResource realResource, IPath newRuntimePath) {
		if (realResource.getType() == IResource.FOLDER)
			virtualResources.add(new VirtualFolder(getComponentHandle(),
					newRuntimePath));
		else
			virtualResources.add(new VirtualFile(getComponentHandle(),
					newRuntimePath));
	}

}
