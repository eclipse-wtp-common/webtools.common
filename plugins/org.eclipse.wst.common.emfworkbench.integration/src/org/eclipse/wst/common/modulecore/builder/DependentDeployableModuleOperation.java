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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.internal.emfworkbench.integration.EMFWorkbenchEditPlugin;
import org.eclipse.wst.common.modulecore.WorkbenchModule;
import org.eclipse.wst.common.modulecore.impl.UnresolveableURIException;
import org.eclipse.wst.common.modulecore.util.ModuleCore;
import org.eclipse.wst.common.modulecore.util.ZipFileExporter;

public class DependentDeployableModuleOperation extends WTPOperation {
	private static String ERROR_EXPORTING_MSG = "Zip Error Message"; //$NON-NLS-1$
    private DependentDeployableModuleDataModel depDataModel = null;
    private ZipFileExporter exporter = null;

	private List errorTable = new ArrayList(1); //IStatus
	private boolean useCompression = true;
	private boolean createLeadupStructure = true;
	private boolean generateManifestFile = false;
	private IProgressMonitor monitor;
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
		this.monitor = monitor; 
        IPath absoluteOutputContainer = getAbsoluteOutputContainer();    
       	// create output container folder if it does not exist
		IFolder outputContainerFolder = createFolder(absoluteOutputContainer);	
		IPath absoluteInputContainer = getAbsoluteInputContainer();
		
		if(absoluteOutputContainer == null || absoluteInputContainer == null) return;
		
		if(depDataModel.getBooleanProperty(DependentDeployableModuleDataModel.DOES_CONSUME)){
		    //if consumes simply copy resources to output directory
			IResource sourceResource = getResource(absoluteInputContainer);
			if(sourceResource == null) return;
			smartCopy(sourceResource, absoluteOutputContainer, new NullProgressMonitor());
			//sourceResource.copy(absoluteOutputContainer, true, new NullProgressMonitor());
		} else {
		    String zipName = getZipFileName();
		    zipAndCopyResource(getResource(absoluteInputContainer), absoluteOutputContainer.append(zipName).toString());
		}    
    }
	/**
	 * @param sourceResource
	 * @param absoluteInputContainer
	 * @param monitor
	 * @throws CoreException
	 */
    //TODO this is a bit sloppy; there must be existing API somewhere.
	private void smartCopy(IResource sourceResource, IPath absoluteOutputContainer, NullProgressMonitor monitor) throws CoreException {
		Resource targetResource =((Workspace)ResourcesPlugin.getWorkspace()).newResource(absoluteOutputContainer, sourceResource.getType()); 
		if(!targetResource.exists()){
			sourceResource.copy(absoluteOutputContainer, true, monitor);
		} else if(sourceResource.getType() == Resource.FOLDER){
			IFolder folder = (IFolder)sourceResource;
			IResource [] members = folder.members();
			for(int i=0;i<members.length;i++){
				smartCopy(members[i],  absoluteOutputContainer.append(IPath.SEPARATOR+members[i].getName()), monitor);
			}
		}
	}
	/**
     * @param resource
     * @param zipName
     * @return
     */
    private void zipAndCopyResource(IResource resource, String zipNameDestination ) throws InterruptedException{
        try {
            exporter = new ZipFileExporter(zipNameDestination,	true, true);
            exportResource(resource);
		    exporter.finished();
        } catch (IOException ioEx) {
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
    		URI outputContainerURI = ModuleCore.getOutputContainerRoot(workbenchModule);
    		URI deployPath = (URI)depDataModel.getProperty(DependentDeployableModuleDataModel.OUTPUT_CONTAINER);
    		return currentModuleProjectPath.append(outputContainerURI.toString()).append(deployPath.toString());
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
	/**
	 *  Export the passed resource to the destination .zip. Export with
	 * no path leadup
	 *
	 *  @param resource org.eclipse.core.resources.IResource
	 */
	protected void exportResource(IResource resource)
		throws InterruptedException {
		exportResource(resource, 1);
	}

	/**
	 *  Export the passed resource to the destination .zip
	 *
	 *  @param resource org.eclipse.core.resources.IResource
	 *  @param depth - the number of resource levels to be included in
	 *   				the path including the resourse itself.
	 */
	protected boolean exportResource(IResource resource, int leadupDepth)
		throws InterruptedException {
		if (!resource.isAccessible())
			return false;

		if (resource.getType() == IResource.FILE) {
			return writeResource(resource, leadupDepth);
		} else {
			IResource[] children = null;

			try {
				children = ((IContainer) resource).members();
			} catch (CoreException e) {
				// this should never happen because an #isAccessible check is done before #members is invoked
				addError(format(ERROR_EXPORTING_MSG, new Object[] { resource.getFullPath()}), e); //$NON-NLS-1$
			}
			
			boolean writeFolder = true;
			for (int i = 0; i < children.length; i++) {
				writeFolder = !exportResource(children[i], leadupDepth + 1) && writeFolder;
			}
			if (writeFolder) {
				writeResource(resource, leadupDepth);
			}
			return true;

		}
	}
	
	private boolean writeResource(IResource resource, int leadupDepth) throws InterruptedException {
		if (resource.isDerived())
			return false;
		String destinationName;
		IPath fullPath = resource.getFullPath();
		if (createLeadupStructure)
			destinationName = fullPath.makeRelative().toString();
		else
			destinationName =
				fullPath
					.removeFirstSegments(
						fullPath.segmentCount() - leadupDepth)
					.toString();
		monitor.subTask(destinationName);

		try {
			if (resource.getType() == IResource.FILE)
				exporter.write((IFile) resource, destinationName);
			else 
				exporter.writeFolder(destinationName);
		} catch (IOException e) {
			addError(format(ERROR_EXPORTING_MSG, //$NON-NLS-1$
			new Object[] {
				resource.getFullPath().makeRelative(),
				e.getMessage()}),
				e);
			return false;
		} catch (CoreException e) {
			addError(format(ERROR_EXPORTING_MSG, //$NON-NLS-1$
			new Object[] {
				resource.getFullPath().makeRelative(),
				e.getMessage()}),
				e);
			return false;
		}

		monitor.worked(1);
		return true;
	}
	/**
	 * @param ERROR_EXPORTING_MSG
	 * @param objects
	 * @return
	 */
	private String format(String pattern, Object[] arguments) {
		return MessageFormat.format(pattern, arguments);
	}
	/**
	 * Add a new entry to the error table with the passed information
	 */
	protected void addError(String message, Throwable e) {
		errorTable.add(
			new Status(IStatus.ERROR, EMFWorkbenchEditPlugin.ID, 0, message, e));
	}
}
