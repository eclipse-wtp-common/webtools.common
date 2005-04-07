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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.componentcore.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.enablement.DataModelEnablementFactory;

public abstract class WorkbenchComponentBuilderDataModelProvider extends AbstractDataModelProvider implements IWorkbenchComponentBuilderDataModelProperties{
    
     /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider#getPropertyNames()
     */
    public String[] getPropertyNames() {
        return new String[]{PROJECT, OUTPUT_CONTAINER, PROJECT, WORKBENCH_MODULE, DEPENDENT_MODULES_DM_LIST, MODULE_CORE};
    }
    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#doSetProperty(java.lang.String, java.lang.Object)
     */
    public boolean propertySet(String propertyName, Object propertyValue) {
        if(propertyName.equals(WORKBENCH_MODULE)) {
        	model.setProperty(OUTPUT_CONTAINER, populateOutputContainer());
        	model.setProperty(DEPENDENT_MODULES_DM_LIST, populateDependentModulesDM());
        }
        return true;
    }

    private Object populateDependentModulesDM() {
        WorkbenchComponent wbModule = (WorkbenchComponent)model.getProperty(WORKBENCH_MODULE);
        List depModules = wbModule.getReferencedComponents();
        List depModulesDataModels = new ArrayList();
        IDataModel dependentDataModel = null;
        StructureEdit moduleCore = (StructureEdit)model.getProperty(MODULE_CORE);
        IProject project = (IProject)model.getProperty(PROJECT);
        for(int i = 0; i<depModules.size(); i++){
            dependentDataModel = DataModelEnablementFactory.createDataModel(IModuleConstants.DEPENDENT_MODULE + ".builder", project);
            if(dependentDataModel != null) {
                dependentDataModel.setProperty(IReferencedComponentBuilderDataModelProperties.MODULE_CORE, moduleCore);
                dependentDataModel.setProperty(IReferencedComponentBuilderDataModelProperties.CONTAINING_WBMODULE, getProperty(WORKBENCH_MODULE));
                dependentDataModel.setProperty(IReferencedComponentBuilderDataModelProperties.DEPENDENT_MODULE, depModules.get(i));
                depModulesDataModels.add(dependentDataModel);
            }
        }
        return depModulesDataModels;
    }

    /**
     * @return
     */
    private Object populateOutputContainer() {
        WorkbenchComponent wbModule = (WorkbenchComponent)model.getProperty(WORKBENCH_MODULE);
        IFolder outputContainer = null;
        if(wbModule != null)
        	outputContainer = StructureEdit.getOutputContainerRoot(wbModule);
        return outputContainer;
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#getDefaultOperation()
     */
    public abstract IDataModelOperation getDefaultOperation();

}
