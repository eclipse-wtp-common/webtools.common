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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentDescription;

public class VirtualFile extends VirtualResource implements IFile {
	
	private final Set realFiles = new HashSet();
	
	public VirtualFile(IFile aRealFile, String aComponentName, IPath aRuntimePath) {
		super(aRealFile.getProject(), aComponentName, aRuntimePath);
		realFiles.add(aRealFile);		
	}

	protected VirtualFile(ComponentHandle aComponentHandle, IPath aRuntimePath) {
		super(aComponentHandle, aRuntimePath);
	}

	public void appendContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void appendContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void create(InputStream source, boolean force, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void create(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

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
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public String getCharset() throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return null;
	}

	public String getCharset(boolean checkImplicit) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return null;
	}

	public IContentDescription getContentDescription() throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return null;
	}

	public InputStream getContents() throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return null;
	}

	public InputStream getContents(boolean force) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return null;
	}

	public int getEncoding() throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return 0;
	}

	public IFileState[] getHistory(IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$
		//return null;
	}

	public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void setCharset(String newCharset) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void setCharset(String newCharset, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void setContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void setContents(IFileState source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void setContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}

	public void setContents(IFileState source, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException("Method not supported"); //$NON-NLS-1$

	}
	
	protected void doDeleteRealResources(int updateFlags, IProgressMonitor monitor) throws CoreException {
		for (Iterator iter = realFiles.iterator(); iter.hasNext();) {
			IFile realFile = (IFile ) iter.next();
			realFile.delete(updateFlags, monitor);			
		}
	}

}
