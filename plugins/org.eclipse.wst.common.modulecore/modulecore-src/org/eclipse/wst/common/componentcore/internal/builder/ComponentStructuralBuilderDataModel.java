package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.wst.common.componentcore.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;

public abstract class ComponentStructuralBuilderDataModel extends WTPOperationDataModel {
	/**
	 * Required, type IProject
	 */
	public static final String PROJECT = "DeployableModuleDataModel.PROJECT"; //$NON-NLS-1$
	/**
	 * Required, type project relative URI
	 */
	public static final String OUTPUT_CONTAINER = "DeployableModuleDataModel.OUTPUT_CONTAINER"; //$NON-NLS-1$
	/**
	 * Required, type WorkbenchComponent
	 */
	public static final String WORKBENCH_MODULE = "DeployableModuleDataModel.WORKBENCH_MODULE_RESOURCES"; //$NON-NLS-1$
	/**
	 * Required, type List of DependentDeployableModuleDataModel
	 */
	public static final String DEPENDENT_MODULES_DM_LIST = "DeployableModuleDataModel.DEPENDENT_MODULES_DM_LIST"; //$NON-NLS-1$

	public static final String MODULE_CORE = "DeployableModuleBuilderDataModel.MODULE_CORE";
	
	
	/* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#addValidBaseProperty(java.lang.String)
     */
	protected void initValidBaseProperties() {
		addValidBaseProperty(PROJECT);
		addValidBaseProperty(OUTPUT_CONTAINER);
		addValidBaseProperty(WORKBENCH_MODULE);
		addValidBaseProperty(DEPENDENT_MODULES_DM_LIST);
		addValidBaseProperty(MODULE_CORE);
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
    public ComponentStructuralBuilderDataModel() {
        super();
    }    
    /**
     * @return
     */
    private Object populateDependentModulesDM() {
        WorkbenchComponent wbModule = (WorkbenchComponent)getProperty(WORKBENCH_MODULE);
        List depModules = wbModule.getReferencedComponents();
        List depModulesDataModels = new ArrayList();
        ComponentStructuralDependentBuilderDataModel dependentDataModel;
        StructureEdit moduleCore = (StructureEdit)getProperty(MODULE_CORE);
        for(int i = 0; i<depModules.size(); i++){
            dependentDataModel = new ComponentStructuralDependentBuilderDataModel();
            dependentDataModel.setProperty(ComponentStructuralDependentBuilderDataModel.MODULE_CORE, moduleCore);
            dependentDataModel.setProperty(ComponentStructuralDependentBuilderDataModel.CONTAINING_WBMODULE, getProperty(WORKBENCH_MODULE));
            dependentDataModel.setProperty(ComponentStructuralDependentBuilderDataModel.DEPENDENT_MODULE, depModules.get(i));
            depModulesDataModels.add(dependentDataModel);
        }
        return depModulesDataModels;
    }

    /**
     * @return
     */
    private Object populateOutputContainer() {
        WorkbenchComponent wbModule = (WorkbenchComponent)getProperty(WORKBENCH_MODULE);
        IFolder outputContainer = null;
        if(wbModule != null)
        	outputContainer = StructureEdit.getOutputContainerRoot(wbModule);
        return outputContainer;
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#getDefaultOperation()
     */
    public abstract WTPOperation getDefaultOperation();

}
