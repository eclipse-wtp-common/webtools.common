/*******************************************************************************
 * Copyright (c) 2003, 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class ProjectComponentsBuilderOperation extends AbstractDataModelOperation implements IProjectComponentsBuilderDataModelProperties {
    /**
     * @param model
     */
    public ProjectComponentsBuilderOperation(IDataModel model) {
        super(model);
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.commands.operations.IUndoableOperation#execute(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
     */
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) {
        try {
            List deployableModuleDM = (List)model.getProperty(MODULE_BUILDER_DM_LIST);
            IUndoableOperation op = null;
            if(deployableModuleDM == null) return OK_STATUS;
            for(int i = 0; i < deployableModuleDM.size(); i++){
                IDataModel moduleDM = (IDataModel)deployableModuleDM.get(i);
                
                List depModuleList = (List)moduleDM.getProperty(IWorkbenchComponentBuilderDataModelProperties.DEPENDENT_MODULES_DM_LIST);
                for(int j = 0; j < depModuleList.size(); j++){
                	IDataModel depModuleDM = (IDataModel)depModuleList.get(j);
                	ReferencedComponentBuilderDelayedDataModelCache.getInstance().addToCache(depModuleDM);
                }
                op = moduleDM.getDefaultOperation();
                op.execute(monitor, null);
            }
        } catch (ExecutionException e) {
            Logger.getLogger().log(e.getMessage());
        }
        return OK_STATUS;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.commands.operations.IUndoableOperation#redo(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
     */
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.commands.operations.IUndoableOperation#undo(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
     */
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) {
        // TODO Auto-generated method stub
        return null;
    }

}
