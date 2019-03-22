/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class RunnableOperationWrapper implements IRunnableWithProgress {

	private IUndoableOperation undoableOperation;
	private IStatus status;

	public RunnableOperationWrapper(IUndoableOperation undoableOperation) {
		this.undoableOperation = undoableOperation;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			status = undoableOperation.execute(monitor, null);
			if(status.getSeverity() == IStatus.ERROR){
				throw new InvocationTargetException(status.getException());
			}
		} catch (ExecutionException e) {
			throw new InvocationTargetException(e);
		}
	}

	public IStatus getStatus() {
		return status;
	}

}
