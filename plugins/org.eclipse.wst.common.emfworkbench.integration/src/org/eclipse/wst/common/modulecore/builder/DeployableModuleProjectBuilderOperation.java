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
            
            List depModuleList = (List)moduleDM.getProperty(DeployableModuleBuilderDataModel.DEPENDENT_MODULES_DM_LIST);
            WTPOperation opDep = null;
            for(int j = 0; j < depModuleList.size(); j++){
            	DependentDeployableModuleDataModel depModuleDM = (DependentDeployableModuleDataModel)depModuleList.get(j);
            	opDep = depModuleDM.getDefaultOperation();
            	if(depModuleDM.getBooleanProperty(DependentDeployableModuleDataModel.NEEDS_PREPROCESSING)){
                	LocalDependencyDelayedOperationCache.getInstance().addOperationToCacheList(opDep);
                }else {
                	opDep.doRun(monitor);
                }
            }
            op = moduleDM.getDefaultOperation();
            op.doRun(monitor);
        }
    }

}
