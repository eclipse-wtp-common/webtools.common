package org.eclipse.wst.common.modulecore.builder;

import org.eclipse.core.internal.resources.Project;
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
	 * Required, type ModuleStructuralModel
	 */
	public static final String MODULE_STRUCTURAL_MODEL = "DeployableModuleDataModel.MODULE_STRUCTURAL_MODEL"; //$NON-NLS-1$
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
		addValidBaseProperty(MODULE_STRUCTURAL_MODEL);
		addValidBaseProperty(OUTPUT_CONTAINER);
		addValidBaseProperty(WORKBENCH_MODULE);
		addValidBaseProperty(DEPENDENT_MODULES_DM_LIST);
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#getDefaultProperty(java.lang.String)
     */
    protected Object getDefaultProperty(String propertyName) {
        if(propertyName.equals(OUTPUT_CONTAINER)){
            if(isSet(WORKBENCH_MODULE))
                return populateOutputContainer();
        }
        else if(propertyName.equals(DEPENDENT_MODULES_DM_LIST)){
            if(isSet(WORKBENCH_MODULE))
                return populateDependentModulesDM();
        }
        return super.getDefaultProperty(propertyName);
    }
    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#doSetProperty(java.lang.String, java.lang.Object)
     */
    protected boolean doSetProperty(String propertyName, Object propertyValue) {
        boolean status = super.doSetProperty(propertyName, propertyValue);
        if(propertyName.equals(WORKBENCH_MODULE)) {
            notifyDefaultChange(OUTPUT_CONTAINER);
            notifyDefaultChange(DEPENDENT_MODULES_DM_LIST);
        }
        return status;
    }
    /**
     * 
     */
    public DeployableModuleBuilderDataModel() {
        super();
        // TODO Auto-generated constructor stub
    }    /**
     * @return
     */
    private Object populateDependentModulesDM() {
        //TODO:
        return null;
    }

    /**
     * @return
     */
    private Object populateOutputContainer() {
        WorkbenchModule wbModule = (WorkbenchModule)getProperty(WORKBENCH_MODULE);
        URI uri = null;
        if(wbModule != null){
            uri = ModuleCore.INSTANCE.getOutputContainerRoot(wbModule, (Project)getProperty(PROJECT));
        }
        return uri;
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#getDefaultOperation()
     */
    public abstract WTPOperation getDefaultOperation();

}
