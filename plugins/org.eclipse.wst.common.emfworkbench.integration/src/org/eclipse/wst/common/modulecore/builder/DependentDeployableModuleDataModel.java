package org.eclipse.wst.common.modulecore.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;
import org.eclipse.wst.common.modulecore.DependentModule;
import org.eclipse.wst.common.modulecore.ModuleURIUtil;
import org.eclipse.wst.common.modulecore.WorkbenchModule;
import org.eclipse.wst.common.modulecore.impl.UnresolveableURIException;
import org.eclipse.wst.common.modulecore.util.ModuleCore;

public class DependentDeployableModuleDataModel extends WTPOperationDataModel {
	/**
	 * Required, type IProject
	 */
	public static final String DEPENDENT_MODULE = "DependentDeployableModuleDataModel.DEPENDENT_MODULE"; //$NON-NLS-1$
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
	
	private ModuleCore moduleCore;

    public DependentDeployableModuleDataModel() {
        super();
    }
    
	/* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#addValidBaseProperty(java.lang.String)
     */
	protected void initValidBaseProperties() {
		addValidBaseProperty(DEPENDENT_MODULE);
		addValidBaseProperty(HANDLE);
		addValidBaseProperty(OUTPUT_CONTAINER);
		addValidBaseProperty(WORKBENCH_MODULE);
		addValidBaseProperty(NEEDS_PREPROCESSING);
	}
	/* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#getDefaultProperty(java.lang.String)
     */
    protected Object getDefaultProperty(String propertyName) {
        if(propertyName.equals(HANDLE))
            return getHandleValue();
        if(propertyName.equals(OUTPUT_CONTAINER))
            return getOutputContainerValue();
        if(propertyName.equals(WORKBENCH_MODULE))
            return getWorkBenchModuleValue();
        if(propertyName.equals(NEEDS_PREPROCESSING))
            return getNeedsPreprocessingValue();
        return super.getDefaultProperty(propertyName);
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#doSetProperty(java.lang.String, java.lang.Object)
     */
    protected boolean doSetProperty(String propertyName, Object propertyValue) {
        boolean status = super.doSetProperty(propertyName, propertyValue);
        if(propertyName.equals(DEPENDENT_MODULE)){
            notifyDefaultChange(HANDLE);
            notifyDefaultChange(OUTPUT_CONTAINER);
            notifyDefaultChange(WORKBENCH_MODULE);
            notifyDefaultChange(NEEDS_PREPROCESSING);
        }
        return status;
    }
    
    private Object getNeedsPreprocessingValue() {
        if(!isSet(DEPENDENT_MODULE)) return null; 
        ModuleCore localCore = getModuleCore();
        if(localCore != null)
        	return Boolean.valueOf(localCore.isLocalDependency(getDependentModule())); 
        return null;
    }

    private WorkbenchModule getWorkBenchModuleValue() {
        if(!isSet(DEPENDENT_MODULE)) return null;
        ModuleCore localCore = getModuleCore();
        try {
			if(localCore != null)
				return localCore.findWorkbenchModuleByModuleURI(getDependentModule().getHandle());
		} catch (UnresolveableURIException e) {
		}
        return null;
    }

    private Object getOutputContainerValue() {
        if(!isSet(DEPENDENT_MODULE)) return null;
        DependentModule depModule = getDependentModule();
        URI moduleRoot = ModuleCore.getOutputContainerRoot(getWorkBenchModuleValue());
        return ModuleURIUtil.concat(moduleRoot, depModule.getDeployedPath()); 
    }

    private URI getHandleValue() {
        if(!isSet(DEPENDENT_MODULE)) return null;
        return getDependentModule().getHandle();
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#dispose()
	 */
	public void dispose() { 
		super.dispose();
		if(moduleCore != null)
			moduleCore.dispose();
	}
	
	private ModuleCore getModuleCore() {
		if(!isSet(DEPENDENT_MODULE)) return null;
		if(moduleCore == null) {
			try {
				DependentModule dependentModule = getDependentModule();
				// TODO THIS SHOULD BE THE CONTAINING PROJECT OF THE WORKBENCHMODULE, NOT THE DEPENDENT MODULE
				IProject container = ModuleCore.getContainingProject(dependentModule.getHandle());
				moduleCore = ModuleCore.getModuleCoreForRead(container);
			} catch (UnresolveableURIException e) {
			}
		}
		return moduleCore;
	}
	
	private DependentModule getDependentModule() {
		return (DependentModule)getProperty(DEPENDENT_MODULE);
	}

    /* (non-Javadoc)
     * @see org.eclipse.wst.common.modulecore.builder.DeployableModuleDataModel#getDefaultOperation()
     */
    public WTPOperation getDefaultOperation() {
        return null;
    }

}
