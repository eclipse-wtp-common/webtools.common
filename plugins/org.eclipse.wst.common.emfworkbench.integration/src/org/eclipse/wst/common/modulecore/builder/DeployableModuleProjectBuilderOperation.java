package org.eclipse.wst.common.modulecore.builder;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;

public class DeployableModuleProjectBuilderOperation extends WTPOperation {

    /**
     * @param operationDataModel
     */
    public DeployableModuleProjectBuilderOperation(DeployableModuleProjectBuilderDataModel operationDataModel) {
        super(operationDataModel);
    }

    /**
     * 
     */
    public DeployableModuleProjectBuilderOperation() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
        DeployableModuleProjectBuilderDataModel deployProjectDM = (DeployableModuleProjectBuilderDataModel)operationDataModel;
        List deployableModuleDM = (List)deployProjectDM.getProperty(DeployableModuleProjectBuilderDataModel.MODULE_BUILDER_DM_LIST);
    
        WTPOperation op = null;
        for(int i = 0; i < deployableModuleDM.size(); i++){
            DeployableModuleBuilderDataModel moduleDM = (DeployableModuleBuilderDataModel)deployableModuleDM.get(i);
            op = moduleDM.getDefaultOperation();
            op.doRun(monitor);
        }
    }

}
