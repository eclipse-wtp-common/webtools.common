/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.wst.common.modulecore.internal.builder;

import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;
import org.eclipse.wst.common.modulecore.DependencyType;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.ReferencedComponent;
import org.eclipse.wst.common.modulecore.UnresolveableURIException;
import org.eclipse.wst.common.modulecore.WorkbenchComponent;

public class DependentDeployableModuleDataModel extends WTPOperationDataModel {
    /**
     * Required, type IProject
     */
    public static final String CONTAINING_WBMODULE = "DependentDeployableModuleDataModel.CONTAINING_WBMODULE"; //$NON-NLS-1$

    /**
     * Required, type ReferencedComponent
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
     * Calc, type WorkbenchComponent
     */
    public static final String DEPENDENT_WBMODULE = "DependentDeployableModuleDataModel.WORKBENCH_MODULE_RESOURCES"; //$NON-NLS-1$

    /**
     * Calc, type boolean
     */
    public static final String DOES_CONSUME = "DependentDeployableModuleDataModel.DOES_CONSUME"; //$NON-NLS-1$

    public static final String MODULE_CORE = "DependentDeployableModuleDataModel.MODULE_CORE"; //$NON-NLS-1$

    public DependentDeployableModuleDataModel() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#addValidBaseProperty(java.lang.String)
     */
    protected void initValidBaseProperties() {
        addValidBaseProperty(DEPENDENT_MODULE);
        addValidBaseProperty(HANDLE);
        addValidBaseProperty(OUTPUT_CONTAINER);
        addValidBaseProperty(DOES_CONSUME);
        addValidBaseProperty(DEPENDENT_WBMODULE);
        addValidBaseProperty(CONTAINING_WBMODULE);
        addValidBaseProperty(MODULE_CORE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#getDefaultProperty(java.lang.String)
     */
    protected Object getDefaultProperty(String propertyName) {
        if (DEPENDENT_WBMODULE.equals(propertyName)) {
            return getWorkBenchModuleValue();
        }
        return super.getDefaultProperty(propertyName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#doSetProperty(java.lang.String,
     *      java.lang.Object)
     */
    protected boolean doSetProperty(String propertyName, Object propertyValue) {
        boolean status = super.doSetProperty(propertyName, propertyValue);
        if (propertyName.equals(DEPENDENT_MODULE)) {
            setProperty(HANDLE, getHandleValue());
            setProperty(OUTPUT_CONTAINER, getOutputContainerValue());
            setProperty(DOES_CONSUME, getDoesConsumeValue());
            notifyDefaultChange(DEPENDENT_WBMODULE);
        }
        return status;
    }

    private WorkbenchComponent getWorkBenchModuleValue() {
        if (!isSet(DEPENDENT_MODULE))
            return null;
        ModuleCore localCore = getModuleCore();
        try {
            if (localCore != null)
                return localCore.findWorkbenchModuleByModuleURI(getDependentModule().getHandle());
        } catch (UnresolveableURIException e) {
        }
        return null;
    }

    private Object getOutputContainerValue() {
        if (!isSet(DEPENDENT_MODULE))
            return null;
        ReferencedComponent depModule = getDependentModule();
        return depModule.getRuntimePath();
    }

    private URI getHandleValue() {
        if (!isSet(DEPENDENT_MODULE))
            return null;
        return getDependentModule().getHandle();
    }

    /**
     * @return
     */
    private Boolean getDoesConsumeValue() {
        if (!isSet(DEPENDENT_MODULE))
            return null;
        ReferencedComponent depModule = (ReferencedComponent) getProperty(DEPENDENT_MODULE);
        DependencyType depType = depModule.getDependencyType();
        if (depType.getValue() == DependencyType.CONSUMES)
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#dispose()
     */
    public void dispose() {
        super.dispose();
    }

    /**
     * Convenience Method for property DEPENDENT_MODULE
     * 
     * @return
     */
    public ReferencedComponent getDependentModule() {
        return (ReferencedComponent) getProperty(DEPENDENT_MODULE);
    }

    /**
     * Convenience Method for property CONTAINING_WBMODULE
     * 
     * @return
     */
    public WorkbenchComponent getContainingWorkbenchModule() {
        return (WorkbenchComponent) getProperty(CONTAINING_WBMODULE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.common.modulecore.builder.DeployableModuleDataModel#getDefaultOperation()
     */
    public WTPOperation getDefaultOperation() {
        return new DependentDeployableModuleOperation(this);
    }

    private ModuleCore getModuleCore() {
        return (ModuleCore) getProperty(MODULE_CORE);
    }
}