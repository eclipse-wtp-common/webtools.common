package org.eclipse.wst.common.modulecore.builder;

import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;

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
	 * Required, type PATH
	 */
	public static final String OUTPUT_CONTAINER = "DeployableModuleDataModel.OUTPUT_CONTAINER"; //$NON-NLS-1$
	/**
	 * Required, type WorkbenchModuleResource
	 */
	public static final String WORKBENCH_MODULE_RESOURCES = "DeployableModuleDataModel.WORKBENCH_MODULE_RESOURCES"; //$NON-NLS-1$
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
		addValidBaseProperty(WORKBENCH_MODULE_RESOURCES);
		addValidBaseProperty(DEPENDENT_MODULES_DM_LIST);
	}
    /**
     * 
     */
    public DeployableModuleBuilderDataModel() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#getDefaultOperation()
     */
    public abstract WTPOperation getDefaultOperation();

}
