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

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;
import org.eclipse.wst.common.modulecore.DependencyType;
import org.eclipse.wst.common.modulecore.DependentModule;
import org.eclipse.wst.common.modulecore.ModuleURIUtil;
import org.eclipse.wst.common.modulecore.WorkbenchModule;
import org.eclipse.wst.common.modulecore.impl.UnresolveableURIException;
import org.eclipse.wst.common.modulecore.util.ModuleCore;

public class DependentDeployableModuleDataModel extends WTPOperationDataModel {
    /**
	 * Required, type IProject
	 */
	public static final String CONTAINING_WBMODULE = "DependentDeployableModuleDataModel.CONTAINING_WBMODULE"; //$NON-NLS-1$
	
    /**
	 * Required, type DependentModule
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
	public static final String DEPENDENT_WBMODULE = "DependentDeployableModuleDataModel.WORKBENCH_MODULE_RESOURCES"; //$NON-NLS-1$
	/**
	 * Calc, type boolean
	 */
	public static final String DOES_CONSUME = "DependentDeployableModuleDataModel.DOES_CONSUME"; //$NON-NLS-1$

	/**
	 * Calc, type boolean
	 */
	public static final String NEEDS_PREPROCESSING = "DependentDeployableModuleDataModel.NEEDS_PREPROCESSING"; //$NON-NLS-1$

    ModuleCore moduleCore = null;
	
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
		addValidBaseProperty(DOES_CONSUME);
		addValidBaseProperty(DEPENDENT_WBMODULE);
		addValidBaseProperty(NEEDS_PREPROCESSING);
		addValidBaseProperty(CONTAINING_WBMODULE);
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
        if(propertyName.equals(DEPENDENT_MODULE)){
            setProperty(HANDLE, getHandleValue());
            setProperty(OUTPUT_CONTAINER, getOutputContainerValue());
            setProperty(DEPENDENT_WBMODULE, getWorkBenchModuleValue());
            setProperty(NEEDS_PREPROCESSING, getNeedsPreprocessingValue());
            setProperty(DOES_CONSUME, getDoesConsumeValue());
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
        return depModule.getDeployedPath(); 
    }

    private URI getHandleValue() {
        if(!isSet(DEPENDENT_MODULE)) return null;
        return getDependentModule().getHandle();
    }
    /**
     * @return
     */
    private Boolean getDoesConsumeValue() {
        if(!isSet(DEPENDENT_MODULE)) return null;
        DependentModule depModule = (DependentModule)getProperty(DEPENDENT_MODULE);
        DependencyType depType = depModule.getDependencyType();
        if(depType.getValue() == DependencyType.CONSUMES)
            return Boolean.TRUE;
        return Boolean.FALSE;
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
			    WorkbenchModule containingWBModule = getContainingWorkbenchModule();
				// TODO THIS SHOULD BE THE CONTAINING PROJECT OF THE WORKBENCHMODULE, NOT THE DEPENDENT MODULE
				IProject container = ModuleCore.getContainingProject(containingWBModule.getHandle());
				moduleCore = ModuleCore.getModuleCoreForRead(container);
			} catch (UnresolveableURIException e) {
			}
		}
		return moduleCore;
	}
	
	private DependentModule getDependentModule() {
		return (DependentModule)getProperty(DEPENDENT_MODULE);
	}
	private WorkbenchModule getContainingWorkbenchModule() {
		return (WorkbenchModule)getProperty(CONTAINING_WBMODULE);
	}
    /* (non-Javadoc)
     * @see org.eclipse.wst.common.modulecore.builder.DeployableModuleDataModel#getDefaultOperation()
     */
    public WTPOperation getDefaultOperation() {
        return new DependentDeployableModuleOperation(this);
    }

}
