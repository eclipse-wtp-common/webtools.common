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

import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.componentcore.StructureEdit;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.DependencyType;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

public class ReferencedComponentBuilderDataModelProvider extends AbstractDataModelProvider implements IReferencedComponentBuilderDataModelProperties {

    /* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider#getPropertyNames()
     */
    public String[] getPropertyNames() {
        return new String[]{CONTAINING_WBMODULE, DEPENDENT_MODULE, DEPENDENT_WBMODULE, DOES_CONSUME, HANDLE, MODULE_CORE, OUTPUT_CONTAINER};
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider#doSetProperty(java.lang.String,
     *      java.lang.Object)
     */
    public boolean propertySet(String propertyName, Object propertyValue) {
        if (propertyName.equals(DEPENDENT_MODULE)) {
            model.setProperty(HANDLE, getHandleValue());
            model.setProperty(OUTPUT_CONTAINER, getOutputContainerValue());
            model.setProperty(DOES_CONSUME, getDoesConsumeValue());
            model.setProperty(DEPENDENT_WBMODULE, getWorkBenchModuleValue());
        }
        return true;
    }

    private WorkbenchComponent getWorkBenchModuleValue() {
        if (!model.isPropertySet(DEPENDENT_MODULE))
            return null;
        StructureEdit localCore = (StructureEdit) model.getProperty(MODULE_CORE);
        try {
            if (localCore != null)
                return localCore.findComponentByURI(((ReferencedComponent) model.getProperty(DEPENDENT_MODULE)).getHandle());
        } catch (UnresolveableURIException e) {
        }
        return null;
    }

    private Object getOutputContainerValue() {
        if (!model.isPropertySet(DEPENDENT_MODULE))
            return null;
        ReferencedComponent depModule = (ReferencedComponent) model.getProperty(DEPENDENT_MODULE);;
        return depModule.getRuntimePath();
    }

    private URI getHandleValue() {
        if (!model.isPropertySet(DEPENDENT_MODULE))
            return null;
        return ((ReferencedComponent) model.getProperty(DEPENDENT_MODULE)).getHandle();
    }

    /**
     * @return
     */
    private Boolean getDoesConsumeValue() {
        if (!model.isPropertySet(DEPENDENT_MODULE))
            return null;
        ReferencedComponent depModule = (ReferencedComponent) model.getProperty(DEPENDENT_MODULE);
        DependencyType depType = depModule.getDependencyType();
        if (depType.getValue() == DependencyType.CONSUMES)
            return Boolean.TRUE;
        return Boolean.FALSE;
    }
	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider#getDefaultOperation()
	 */
	public IDataModelOperation getDefaultOperation() {
	    return new ReferencedComponentBuilderOperation(model);
	}
}
