package org.eclipse.wst.common.modulecore.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;
import org.eclipse.wst.common.modulecore.WorkbenchModule;
import org.eclipse.wst.common.modulecore.util.ModuleCore;

public abstract class DeployableModuleBuilderDataModel extends WTPOperationDataModel {
	/**
	 * Required, type IProject
	 */
	public static final String PROJECT = "DeployableModuleDataModel.PROJECT"; //$NON-NLS-1$
	/**
	 * Required, type project relative URI
	 */
	public static final String OUTPUT_CONTAINER = "DeployableModuleDataModel.OUTPUT_CONTAINER"; //$NON-NLS-1$
	/**
	 * Required, type WorkbenchModule
	 */
	public static final String WORKBENCH_MODULE = "DeployableModuleDataModel.WORKBENCH_MODULE_RESOURCES"; //$NON-NLS-1$
	/**
	 * Required, type List of DependentdeployableModuleDataModel
	 */
	public static final String DEPENDENT_MODULES_DM_LIST = "DeployableModuleDataModel.DEPENDENT_MODULES_DM_LIST"; //$NON-NLS-1$

	
	/* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#addValidBaseProperty(java.lang.String)
     */
	protected void initValidBaseProperties() {
		addValidBaseProperty(PROJECT);
		addValidBaseProperty(OUTPUT_CONTAINER);
		addValidBaseProperty(WORKBENCH_MODULE);
		addValidBaseProperty(DEPENDENT_MODULES_DM_LIST);
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#getDefaultProperty(java.lang.String)
     */
    protected Object getDefaultProperty(String propertyName) {
        return super.getDefaultProperty(propertyName);
    }
    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#doSetProperty(java.lang.String, java.lang.Object)
     */
    protected boolean doSetProperty(String propertyName, Object propertyValue) {
        boolean status = super.doSetProperty(propertyName, propertyValue);
        if(propertyName.equals(WORKBENCH_MODULE)) {
        	setProperty(OUTPUT_CONTAINER, populateOutputContainer());
        	setProperty(DEPENDENT_MODULES_DM_LIST, populateDependentModulesDM());
        }
        return status;
    }
    /**
     * 
     */
    public DeployableModuleBuilderDataModel() {
        super();
    }    
    /**
     * @return
     */
    private Object populateDependentModulesDM() {
        WorkbenchModule wbModule = (WorkbenchModule)getProperty(WORKBENCH_MODULE);
        List depModules = wbModule.getModules();
        List depModulesDataModels = new ArrayList();
        DependentDeployableModuleDataModel dependentDataModel;
        for(int i = 0; i<depModules.size(); i++){
            dependentDataModel = new DependentDeployableModuleDataModel();
            dependentDataModel.setProperty(DependentDeployableModuleDataModel.CONTAINING_WBMODULE, getProperty(WORKBENCH_MODULE));
            dependentDataModel.setProperty(DependentDeployableModuleDataModel.DEPENDENT_MODULE, depModules.get(i));
            depModulesDataModels.add(dependentDataModel);
        }
        return depModulesDataModels;
    }

    /**
     * @return
     */
    private Object populateOutputContainer() {
        WorkbenchModule wbModule = (WorkbenchModule)getProperty(WORKBENCH_MODULE);
        URI uri = null;
        if(wbModule != null)
            uri = ModuleCore.getOutputContainerRoot(wbModule);
        return uri;
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#getDefaultOperation()
     */
    public abstract WTPOperation getDefaultOperation();

}
