/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.modulecore.internal.resources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.modulecore.resources.IVirtualFile;

public class VirtualFile extends VirtualResource implements IVirtualFile {

	protected VirtualFile(ComponentHandle aComponentHandle, IPath aRuntimePath) {
		super(aComponentHandle, aRuntimePath); 
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
	
	public int getType() {
		return IResource.FILE;
	}
	
	public IFile getRealFile() {
		return getProject().getFile(getProjectRelativePath());
	}
	
	public IFile[] getRealFiles() {
		return new IFile[] {getRealFile()};
	}

	protected void doDeleteMetaModel(int updateFlags,IProgressMonitor monitor) {
		
	}	
	
	protected void doDeleteRealResources(int updateFlags, IProgressMonitor monitor) throws CoreException {

	}

}
