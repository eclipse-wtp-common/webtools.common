/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.operations.tests.manager;

import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;

public class BaseOperation extends AbstractDataModelOperation {
	private Vector resultList;
	private Vector undoList;
	private IStatus status;

	public BaseOperation(String id, Vector resultList, Vector undoList) {
		setID(id);
		this.resultList = resultList;
		this.undoList = undoList;
		status = Status.OK_STATUS;
	}

	public void setStatus(IStatus status) {
		this.status = status;
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		resultList.add(this);
		return status;
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return Status.OK_STATUS;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		resultList.remove(resultList.size() - 1);
		undoList.add(this);
		return Status.OK_STATUS;
	}
}
