/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.builder;

import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.componentcore.StructureEdit;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.DependencyType;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;

public class ComponentStructuralDependentBuilderDataModel extends WTPOperationDataModel {
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

    public ComponentStructuralDependentBuilderDataModel() {
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
        StructureEdit localCore = (StructureEdit) getProperty(MODULE_CORE);
        try {
            if (localCore != null)
                return localCore.findComponentByURI(((ReferencedComponent) getProperty(DEPENDENT_MODULE)).getHandle());
        } catch (UnresolveableURIException e) {
        }
        return null;
    }

    private Object getOutputContainerValue() {
        if (!isSet(DEPENDENT_MODULE))
            return null;
        ReferencedComponent depModule = (ReferencedComponent) getProperty(DEPENDENT_MODULE);;
        return depModule.getRuntimePath();
    }

    private URI getHandleValue() {
        if (!isSet(DEPENDENT_MODULE))
            return null;
        return ((ReferencedComponent) getProperty(DEPENDENT_MODULE)).getHandle();
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
     * @see org.eclipse.wst.common.modulecore.builder.DeployableModuleDataModel#getDefaultOperation()
     */
    public WTPOperation getDefaultOperation() {
        return new ComponentStructuralDependentBuilderOperation(this);
    }

}