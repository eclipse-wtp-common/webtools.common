package org.eclipse.wst.common.modulecore.builder;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;

public class DependentDeployableModuleOperation extends WTPOperation {

    /**
     * @param operationDataModel
     */
    public DependentDeployableModuleOperation(DependentDeployableModuleDataModel operationDataModel) {
        super(operationDataModel);
        // TODO Auto-generated constructor stub
    }
    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {


    }

}
