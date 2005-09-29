package org.eclipse.wst.common.componentcore.internal.operation;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFlexibleProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.ProjectComponents;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.WTPProjectUtilities;

public class FlexibleProjectCreationOperation extends AbstractDataModelOperation {

    public FlexibleProjectCreationOperation(IDataModel model) {
        super(model);
        // TODO Auto-generated constructor stub
    }

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        try {
			createProject(monitor);
		    WTPProjectUtilities.addNatureToProjectLast(getProject(), IModuleConstants.MODULE_NATURE_ID);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        createInitialWTPModulesFile();
		return OK_STATUS;
	}

    private void createProject(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException, ExecutionException {
        IDataModel projModel = model.getNestedModel(IFlexibleProjectCreationDataModelProperties.NESTED_MODEL_PROJECT_CREATION);
        IDataModelOperation op = projModel.getDefaultOperation();
        op.execute(monitor, null);
    }
    
    private void createInitialWTPModulesFile() {
    	StructureEdit moduleCore = null;
		try {
			IProject containingProject = getProject();
			moduleCore = StructureEdit.getStructureEditForWrite(containingProject);
			moduleCore.prepareProjectComponentsIfNecessary(); 
			ProjectComponents projectModules = moduleCore.getComponentModelRoot();
			moduleCore.saveIfNecessary(null); 
		} finally {
			if(moduleCore != null)
				moduleCore.dispose();
		}     
    }
    
    protected IProject getProject() {
        String name = model.getStringProperty(IFlexibleProjectCreationDataModelProperties.PROJECT_NAME);
        if (name != null && name.length() > 0)
            return ResourcesPlugin.getWorkspace().getRoot().getProject(name);
        return null;
    }

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}
}
