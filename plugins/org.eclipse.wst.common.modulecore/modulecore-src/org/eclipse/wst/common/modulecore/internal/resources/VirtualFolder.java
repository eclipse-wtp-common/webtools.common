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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.modulecore.ComponentResource;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.WorkbenchComponent;

public class VirtualFolder extends VirtualContainer implements IFolder {

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

		for (Iterator realFoldersIterator = realFolders.iterator(); realFoldersIterator.hasNext();) {
			IFolder realFolder = (IFolder) realFoldersIterator.next();
			if (!realFolder.exists()) {
				realFolder.create(updateFlags, local, monitor);
			}
		}

	}

	/**
	 * @see IFolder#createLink(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void createLink(IPath aProjectRelativeLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {

		ModuleCore moduleCore = null;
		try {
			IFolder resource = getProject().getFolder(aProjectRelativeLocation);

			moduleCore = ModuleCore.getModuleCoreForWrite(getProject());
			WorkbenchComponent component = moduleCore.findWorkbenchModuleByDeployName(getComponentHandle().getName());

			ComponentResource componentResource = moduleCore.createWorkbenchModuleResource(resource);
			componentResource.setRuntimePath(URI.createURI(getRuntimePath().toString()));
			component.getResources().add(componentResource);

			if (!resource.exists())  
				resource.create(updateFlags, true, monitor); 

		} finally {
			if (moduleCore != null) {
				moduleCore.saveIfNecessary(monitor);
				moduleCore.dispose();
			}
		}
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
	public IResource findMember(String name) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}

	// TODO WTP:Implement this method
	public IResource findMember(String name, boolean includePhantoms) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}

	// TODO WTP:Implement this method
	public IResource findMember(IPath path) {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}

	// TODO WTP:Implement this method
	public IResource findMember(IPath path, boolean includePhantoms) {
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

	public IFile[] findDeletedMembersWithHistory(int depth, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		// return null;
	}

	public void setDefaultCharset(String charset) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void setDefaultCharset(String charset, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	protected void doDeleteRealResources(int updateFlags, IProgressMonitor monitor) throws CoreException {
		for (Iterator iter = realFolders.iterator(); iter.hasNext();) {
			IFolder realFolder = (IFolder) iter.next();
			realFolder.delete(updateFlags, monitor);
		}
	}

}
