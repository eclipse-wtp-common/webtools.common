package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.componentcore.datamodel.properties.IWorkbenchComponentBuilderDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class WorkbenchComponentBuilderOperation extends AbstractDataModelOperation {

	public WorkbenchComponentBuilderOperation(IDataModel model) {
		super(model);
	}
	
	 /* (non-Javadoc)
     * @see org.eclipse.core.commands.operations.IUndoableOperation#execute(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
     */
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) {
        StructureEdit sEdit = null;
		try {
            IVirtualComponent vComponent = (IVirtualComponent)model.getProperty(IWorkbenchComponentBuilderDataModelProperties.VIRTUAL_COMPONENT);
            sEdit = StructureEdit.getStructureEditForRead(vComponent.getProject());
            WorkbenchComponent wbComponent = sEdit.getComponent();
            
            // create output container folder if it does not exist
            IFolder outputContainer = (IFolder)model.getProperty(IWorkbenchComponentBuilderDataModelProperties.OUTPUT_CONTAINER);
            if(!outputContainer.exists())
            	createFolder(outputContainer);
            
            IPath outputContainerPath = outputContainer.getFullPath();

            // copy resources except the java source folder
            List resourceList = wbComponent.getResources();
            for (int i = 0; i < resourceList.size(); i++) {
            	ComponentResource wmr = (ComponentResource)resourceList.get(i);  
            	IResource sourceResource =  StructureEdit.getEclipseResource(wmr);
            	if (sourceResource == null)
            		continue; 
            	IPath deployPath = outputContainerPath.append(wmr.getRuntimePath());
            	// create parent folders for deploy folder if not exist
            	IPath parentPath = deployPath.removeLastSegments(1);
            	createFolder(parentPath);
            	ComponentStructuralBuilder.smartCopy(sourceResource, deployPath, new NullProgressMonitor());
            }
        } catch (CoreException e) {
            Logger.getLogger().log(e.getMessage());
        } finally{
        	if (sEdit != null)
        		sEdit.dispose();
        }
		return OK_STATUS;
    }
    
    /**
	 * @param outputContainer
	 */
	protected void createFolder(IFolder outputContainer) {
		IContainer parentContainer = outputContainer.getParent();
		if(parentContainer != null && !parentContainer.exists() && parentContainer.getType() == IResource.FOLDER) {			
			createFolder((IFolder)outputContainer.getParent());
		}
		try {
			if(!outputContainer.exists())
				outputContainer.create(true, true, null);
		} catch (CoreException e) { 
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Create a folder for given absolute path
	 * 
	 * @exception com.ibm.itp.core.api.resources.CoreException
	 */
	protected IFolder createFolder(IPath absolutePath) throws CoreException {
		if (absolutePath == null || absolutePath.isEmpty())
			return null;
		IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(absolutePath);
		// check if the parent is there
		IContainer parent = folder.getParent();
		if (parent != null && !parent.exists() && (parent instanceof IFolder))
			createFolder(parent.getFullPath());
		if (!folder.exists())
			folder.create(true, true, new NullProgressMonitor());
		return folder;
	}
	
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

}
