/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.builder;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.componentcore.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;

public class ComponentStructuralBuilderDependencyResolver extends IncrementalProjectBuilder implements IModuleConstants {
    /**
     * Builder id of this incremental project builder.
     */
    public static final String BUILDER_ID = COMPONENT_STRUCTURAL_DEPENDENCY_RESOLVER_ID;

    /**
     *  
     */
    public ComponentStructuralBuilderDependencyResolver() {
        super();
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
     *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
        StructureEdit moduleCore = null;
        try {
            List delayedOperationDMs = ComponentStructuralBuilderDelayedDataModelCache.getInstance().getCacheList();
            if (delayedOperationDMs.size() > 0) {
                moduleCore = StructureEdit.getStructureEditForRead(getProject());
            }
            ComponentStructuralDependentBuilderDataModel dataModel = null;
            WTPOperation op = null;
            for (int i = 0; i < delayedOperationDMs.size(); i++) {
                dataModel = (ComponentStructuralDependentBuilderDataModel) delayedOperationDMs.get(i);
                dataModel.setProperty(ComponentStructuralDependentBuilderDataModel.MODULE_CORE, moduleCore);
                op = dataModel.getDefaultOperation();
                if (op != null) {
                    try {
                        op.run(monitor);
                    } catch (InvocationTargetException ex) {
                    } catch (InterruptedException ex2) {
                    }
                }
            }
        } finally {
            if (moduleCore != null) {
                moduleCore.dispose();
            }
            ComponentStructuralBuilderDelayedDataModelCache.getInstance().clearCache();
        }
        return null;
    }
}