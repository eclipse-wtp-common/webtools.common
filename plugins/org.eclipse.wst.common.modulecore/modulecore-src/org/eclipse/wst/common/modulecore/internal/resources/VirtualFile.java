/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.modulecore.internal.resources;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.wst.common.modulecore.resources.IVirtualFile;

public class VirtualFile extends VirtualResource implements IVirtualFile {
	 
	private final IFile realFile;
	
//	public VirtualFile(IFile aRealFile, String aComponentName, IPath aRuntimePath) {
//		super(aRealFile.getProject(), aComponentName, aRuntimePath); 
//	}

	protected VirtualFile(ComponentHandle aComponentHandle, IPath aRuntimePath) {
		super(aComponentHandle, aRuntimePath);
		realFile = getProject().getFile(getProjectRelativePath());
	}

	public void appendContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {		
		realFile.create(source, force, monitor);
	}

	public void appendContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
		realFile.appendContents(source, updateFlags, monitor);
	}

	public void create(InputStream source, boolean force, IProgressMonitor monitor) throws CoreException {
		realFile.create(source, force, monitor);
	}

	public void create(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
		realFile.create(source, updateFlags, monitor);

	}
	public void createLink(IPath aProjectRelativeLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
//		
//		ModuleCore moduleCore = null;
//		try {
//			IResource resource = (getType() == IResource.FOLDER) ? 
//									(IResource) getProject().getFolder(aProjectRelativeLocation) : 
//									(IResource) getProject().getFile(aProjectRelativeLocation);
//									
//			moduleCore = ModuleCore.getModuleCoreForWrite(getProject());
//			WorkbenchComponent component = moduleCore.findWorkbenchModuleByDeployName(getComponentHandle().getName());
//			
//			ComponentResource componentResource = moduleCore.createWorkbenchModuleResource(resource);
//			componentResource.setRuntimePath(URI.createURI(getRuntimePath().toOSString()));
//			
//			if(!resource.exists()) {
//				resource.
//			}
//			
//		} finally {
//			if(moduleCore != null) {
//				moduleCore.saveIfNecessary(monitor);
//				moduleCore.dispose();
//			}
//		}
		
		
	}

	public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException { 
		realFile.delete(force, keepHistory, monitor);

	}

	public String getCharset() throws CoreException {
		return realFile.getCharset();
	}

	public String getCharset(boolean checkImplicit) throws CoreException {
		return realFile.getCharset(checkImplicit);
	}

	public IContentDescription getContentDescription() throws CoreException { 
		return realFile.getContentDescription();
	}

	public InputStream getContents() throws CoreException { 
		return realFile.getContents();
	}

	public InputStream getContents(boolean force) throws CoreException {
		return realFile.getContents(force);
	}
 

	public IFileState[] getHistory(IProgressMonitor monitor) throws CoreException { 
		return realFile.getHistory(monitor);
	}

	public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void setCharset(String newCharset, IProgressMonitor monitor) throws CoreException { 
		realFile.setCharset(newCharset, monitor);

	}

	public void setContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		realFile.setContents(source, force, keepHistory, monitor);
	}

	public void setContents(IFileState source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		realFile.setContents(source, force, keepHistory, monitor);
	}

	public void setContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
		realFile.setContents(source, updateFlags, monitor);
	}

	public void setContents(IFileState source, int updateFlags, IProgressMonitor monitor) throws CoreException {
		realFile.setContents(source, updateFlags, monitor);
	}
	
	public IFile getRealFile() {
		return realFile;
	}
	
	public IFile[] getRealFiles() {
		return new IFile[] {realFile};
	}
	
	protected void doDeleteRealResources(int updateFlags, IProgressMonitor monitor) throws CoreException {
		realFile.delete(updateFlags, monitor);
	}

}
