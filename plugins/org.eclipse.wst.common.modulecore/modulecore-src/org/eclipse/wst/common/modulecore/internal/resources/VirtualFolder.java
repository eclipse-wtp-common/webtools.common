/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.modulecore.internal.resources;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.modulecore.ComponentResource;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.WorkbenchComponent;
import org.eclipse.wst.common.modulecore.internal.impl.ResourceTreeRoot;
import org.eclipse.wst.common.modulecore.resources.IVirtualContainer;
import org.eclipse.wst.common.modulecore.resources.IVirtualFile;
import org.eclipse.wst.common.modulecore.resources.IVirtualFolder;
import org.eclipse.wst.common.modulecore.resources.IVirtualResource;

public class VirtualFolder extends VirtualContainer implements IVirtualFolder {

	private final Set realFolders = new HashSet();

	public VirtualFolder(IFolder aRealFolder, String aComponentName, IPath aRuntimePath) {

		super(aRealFolder.getProject(), aComponentName, aRuntimePath);
		realFolders.add(aRealFolder);
	}

	/**
	 * <p>
	 * Creates an unassigned mapping contained by the component identified by <aContainingProject,
	 * aComponentName> with a runtime path of aRuntimePath.
	 * </p>
	 * 
	 * @param aContainingProject
	 * @param aComponentName
	 * @param aRuntimePath
	 */
	public VirtualFolder(IProject aContainingProject, String aComponentName, IPath aRuntimePath) {
		super(aContainingProject, aComponentName, aRuntimePath);
	}

	/**
	 * p> Creates an unassigned mapping contained by the component identified by aComponentHandle
	 * with a runtime path of aRuntimePath.
	 * </p>
	 * 
	 * @param aComponentHandle
	 * @param aRuntimePath
	 */
	protected VirtualFolder(ComponentHandle aComponentHandle, IPath aRuntimePath) {
		super(aComponentHandle, aRuntimePath);
	}

	// TODO WTP:Implement this method
	public void create(boolean force, boolean local, IProgressMonitor monitor) throws CoreException {
		create((force ? IResource.FORCE : IResource.NONE), local, monitor);
	}

	// TODO WTP:Implement this method
	public void create(int updateFlags, boolean local, IProgressMonitor monitor) throws CoreException {

		IVirtualContainer container = ModuleCore.create(getProject(), getComponentHandle().getName());
		IVirtualFolder root = container.getFolder(new Path("/"));  //$NON-NLS-1$		
		IFolder realFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(root.getWorkspaceRelativePath()); 
		IFolder newFolder = realFolder.getFolder(getRuntimePath()); 
		createResource(newFolder, updateFlags, monitor); 

	}

	/**
	 * @see IFolder#createLink(org.eclipse.core.runtime.IPath, int,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void createLink(IPath aProjectRelativeLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {

		ModuleCore moduleCore = null;
		try {
			IFolder resource = getProject().getFolder(aProjectRelativeLocation);

			moduleCore = ModuleCore.getModuleCoreForWrite(getProject());
			WorkbenchComponent component = moduleCore.findWorkbenchModuleByDeployName(getComponentHandle().getName());
			
			ResourceTreeRoot root = ResourceTreeRoot.getDeployResourceTreeRoot(component);
			ComponentResource[] resources = root.findModuleResources(getRuntimePath(), false);

			if(resources.length == 0) {
				ComponentResource componentResource = moduleCore.createWorkbenchModuleResource(resource);
				componentResource.setRuntimePath(URI.createURI(getRuntimePath().toString()));
				component.getResources().add(componentResource);
			} else {
				URI projectRelativeURI = URI.createURI(aProjectRelativeLocation.toString());
				boolean foundMapping = false;
				for (int resourceIndx = 0; resourceIndx < resources.length && !foundMapping; resourceIndx++) {
					if(projectRelativeURI.equals(resources[resourceIndx].getSourcePath()))
						foundMapping = true;
				}
				if(!foundMapping) {
					ComponentResource componentResource = moduleCore.createWorkbenchModuleResource(resource);
					componentResource.setRuntimePath(URI.createURI(getRuntimePath().toString()));
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

	private void createResource(IContainer resource, int updateFlags, IProgressMonitor monitor) throws CoreException {

		if (!resource.getParent().exists())
			createResource(resource.getParent(), updateFlags, monitor);
		if (!resource.exists())
			((IFolder) resource).create(updateFlags, true, monitor);

	}

	public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	// TODO WTP:Implement this method
	public boolean exists(IPath path) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return false;
	}

	// TODO WTP:Implement this method
	public IVirtualResource findMember(String name) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}

	// TODO WTP:Implement this method
	public IVirtualResource findMember(String name, boolean includePhantoms) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}

	// TODO WTP:Implement this method
	public IVirtualResource findMember(IPath path) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}

	// TODO WTP:Implement this method
	public IVirtualResource findMember(IPath path, boolean includePhantoms) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}

	public String getDefaultCharset() throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}

	public String getDefaultCharset(boolean checkImplicit) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}

	public IVirtualFile[] findDeletedMembersWithHistory(int depth, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}

	public void setDefaultCharset(String charset) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void setDefaultCharset(String charset, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void commit() throws CoreException {

		// ModuleCore moduleCore = null;
		// try {
		// moduleCore = ModuleCore.getModuleCoreForWrite(getProject());
		// WorkbenchComponent component =
		// moduleCore.findWorkbenchModuleByDeployName(getComponentHandle().getName());
		// if(component == null)
		// moduleCore.createWorkbenchModule(getComponentHandle().getName());
		// } finally {
		// if (moduleCore != null) {
		// moduleCore.saveIfNecessary(null);
		// moduleCore.dispose();
		// }
		// }
	}

	protected void doDeleteRealResources(int updateFlags, IProgressMonitor monitor) throws CoreException {
		for (Iterator iter = realFolders.iterator(); iter.hasNext();) {
			IFolder realFolder = (IFolder) iter.next();
			realFolder.delete(updateFlags, monitor);
		}
	}

}
