package org.eclipse.wst.common.modulecore.builder;

import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;

public class DependentDeployableModuleDataModel extends WTPOperationDataModel {
	/**
	 * Required, type IProject
	 */
	public static final String DEPENDENT_MODULE = "DependentDeployableModuleDataModel.DEPENDENT_MODULE"; //$NON-NLS-1$
	/**
	 * Required, type ModuleStructuralModel
	 */
	public static final String MODULE_STRUCTURAL_MODEL = "DependentDeployableModuleDataModel.MODULE_STRUCTURAL_MODEL"; //$NON-NLS-1$
	/**
	 * Calc, type project relative URI
	 */
	public static final String HANDLE = "DependentDeployableModuleDataModel.HANDLE"; //$NON-NLS-1$
	/**
	 * Calc, type project relative URI
	 */
	public static final String OUTPUT_CONTAINER = "DependentDeployableModuleDataModel.OUTPUT_CONTAINER"; //$NON-NLS-1$
	/**
	 * Calc, type WorkbenchModule
	 */
	public static final String WORKBENCH_MODULE = "DependentDeployableModuleDataModel.WORKBENCH_MODULE_RESOURCES"; //$NON-NLS-1$
	/**
	 * Calc, type boolean
	 */
	public static final String NEEDS_PREPROCESSING = "DependentDeployableModuleDataModel.NEEDS_PREPROCESSING"; //$NON-NLS-1$

    public DependentDeployableModuleDataModel() {
        super();
    }
    
	/* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#addValidBaseProperty(java.lang.String)
     */
	protected void initValidBaseProperties() {
		addValidBaseProperty(DEPENDENT_MODULE);
		addValidBaseProperty(MODULE_STRUCTURAL_MODEL);
		addValidBaseProperty(HANDLE);
		addValidBaseProperty(OUTPUT_CONTAINER);
		addValidBaseProperty(WORKBENCH_MODULE);
		addValidBaseProperty(NEEDS_PREPROCESSING);
	}

    /* (non-Javadoc)
     * @see org.eclipse.wst.common.modulecore.builder.DeployableModuleDataModel#getDefaultOperation()
     */
    public WTPOperation getDefaultOperation() {
        return null;
    }

}
