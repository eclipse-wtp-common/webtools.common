package org.eclipse.wst.common.modulecore.builder;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;

public class DeployableModuleBuilderOperation extends WTPOperation {

    /**
     * @param operationDataModel
     */
    public DeployableModuleBuilderOperation(WTPOperationDataModel operationDataModel) {
        super(operationDataModel);
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     */
    public DeployableModuleBuilderOperation() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
        // TODO Auto-generated method stub

    }

}
