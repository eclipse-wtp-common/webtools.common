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
package org.eclipse.wst.common.componentcore.internal.builder;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;

public class ComponentStructuralProjectBuilderOperation extends WTPOperation {

    /**
     * @param operationDataModel
     */
    public ComponentStructuralProjectBuilderOperation(ComponentStructuralProjectBuilderDataModel operationDataModel) {
        super(operationDataModel);
    }

    /**
     * 
     */
    public ComponentStructuralProjectBuilderOperation() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
        ComponentStructuralProjectBuilderDataModel deployProjectDM = (ComponentStructuralProjectBuilderDataModel)operationDataModel;
        List deployableModuleDM = (List)deployProjectDM.getProperty(ComponentStructuralProjectBuilderDataModel.MODULE_BUILDER_DM_LIST);
    
        WTPOperation op = null;
        if(deployableModuleDM == null) return;
        for(int i = 0; i < deployableModuleDM.size(); i++){
            ComponentStructuralBuilderDataModel moduleDM = (ComponentStructuralBuilderDataModel)deployableModuleDM.get(i);
            
            List depModuleList = (List)moduleDM.getProperty(ComponentStructuralBuilderDataModel.DEPENDENT_MODULES_DM_LIST);
            WTPOperation opDep = null;
            for(int j = 0; j < depModuleList.size(); j++){
            	ComponentStructuralDependentBuilderDataModel depModuleDM = (ComponentStructuralDependentBuilderDataModel)depModuleList.get(j);
            	ComponentStructuralBuilderDelayedDataModelCache.getInstance().addToCache(depModuleDM);
            }
            op = moduleDM.getDefaultOperation();
            op.doRun(monitor);
        }
    }

}
