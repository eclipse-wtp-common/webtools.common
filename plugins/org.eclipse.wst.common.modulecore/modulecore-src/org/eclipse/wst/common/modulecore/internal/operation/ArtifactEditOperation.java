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
package org.eclipse.wst.common.modulecore.internal.operation;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.frameworks.operations.WTPOperation;

public class ArtifactEditOperation extends WTPOperation {

    /**
     * @param operationDataModel
     */
    public ArtifactEditOperation(ArtifactEditOperationDataModel operationDataModel) {
        super(operationDataModel);
    }

    //TODO: move functionality from edit model operation to artifact edit operation
    protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
        // TODO Auto-generated method stub

    }

}
