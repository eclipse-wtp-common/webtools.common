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
package org.eclipse.wst.common.modulecore.builder;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.modulecore.WorkbenchModule;
import org.eclipse.wst.common.modulecore.impl.UnresolveableURIException;
import org.eclipse.wst.common.modulecore.util.ModuleCore;

public class DependentDeployableModuleOperation extends WTPOperation {
    private DependentDeployableModuleDataModel depDataModel = null;
    /**
     * @param operationDataModel
     */
    public DependentDeployableModuleOperation(DependentDeployableModuleDataModel operationDataModel) {
        super(operationDataModel);
        depDataModel = (DependentDeployableModuleDataModel)operationDataModel;
    }
    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
        IPath absoluteOutputContainer = getAbsoluteOutputContainer();    
       	// create output container folder if it does not exist
		IFolder outputContainerFolder = createFolder(absoluteOutputContainer);	
		IPath absoluteInputContainer = getAbsoluteInputContainer();
		
		if(absoluteOutputContainer == null || absoluteInputContainer == null) return;
		
		if(depDataModel.getBooleanProperty(DependentDeployableModuleDataModel.DOES_CONSUME)){
		    //if consumes simply copy resources to output directory
			IResource sourceResource = getResource(absoluteInputContainer);
			if(sourceResource == null) return;
			sourceResource.copy(absoluteInputContainer, true, new NullProgressMonitor());
		} else {
		    String zipName = getZipFileName();
		    //commona
		
		}    
    }
	/**
     * @return
     */
    private IPath getAbsoluteOutputContainer() {
        try {
            WorkbenchModule workbenchModule = (WorkbenchModule)depDataModel.getProperty(DependentDeployableModuleDataModel.CONTAINING_WBMODULE);
    		IProject currentModuleProject = ModuleCore.getContainingProject(workbenchModule.getHandle());
    		IPath currentModuleProjectPath = currentModuleProject.getFullPath();
    		URI outputContainerURI = (URI)depDataModel.getProperty(DependentDeployableModuleDataModel.OUTPUT_CONTAINER);
    		return currentModuleProjectPath.append(outputContainerURI.toString());
        } catch (UnresolveableURIException e) {
        }
        return null;
    }
    
	/**
     * @return
     */
    private IPath getAbsoluteInputContainer() {
        try {
            WorkbenchModule depWBModule = (WorkbenchModule)depDataModel.getProperty(DependentDeployableModuleDataModel.DEPENDENT_WBMODULE);
            IProject depModuleProject = ModuleCore.getContainingProject(depWBModule.getHandle());
    		IPath depModuleProjectPath = depModuleProject.getFullPath();
    		URI dependentModuleContainerURI = ModuleCore.getOutputContainerRoot(depWBModule);
    		return depModuleProjectPath.append(dependentModuleContainerURI.toString());
        } catch (UnresolveableURIException e) {
        }
        return null;
    }
    
    private String getZipFileName(){
        WorkbenchModule depWBModule = (WorkbenchModule)depDataModel.getProperty(DependentDeployableModuleDataModel.DEPENDENT_WBMODULE);
        return depWBModule.getDeployedName();
    }
    /**
	 * Get resource for given absolute path
	 * 
	 * @exception com.ibm.itp.core.api.resources.CoreException
	 */
	private IResource getResource(IPath absolutePath) throws CoreException {
		IResource resource = null;
		if (absolutePath != null && !absolutePath.isEmpty()) {
			resource = getWorkspace().getRoot().getFolder(absolutePath);
			if (resource == null || !(resource instanceof IFolder)) {
				resource = getWorkspace().getRoot().getFile(absolutePath);
			}
		}
		return resource;
	}

	/**
	 * Create a folder for given absolute path
	 * 
	 * @exception com.ibm.itp.core.api.resources.CoreException
	 */
	public IFolder createFolder(IPath absolutePath) throws CoreException {
		if (absolutePath == null || absolutePath.isEmpty())
			return null;
		IFolder folder = getWorkspace().getRoot().getFolder(absolutePath);
		// check if the parent is there
		IContainer parent = folder.getParent();
		if (parent != null && !parent.exists() && (parent instanceof IFolder))
			createFolder(parent.getFullPath());
		if (!folder.exists())
			folder.create(true, true, new NullProgressMonitor());
		return folder;
	}
}
