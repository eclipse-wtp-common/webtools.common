/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.common.environment.IEnvironment;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

public class WrappedOperation implements IDataModelOperation {

	protected IDataModelOperation rootOperation;
	
	public WrappedOperation(IDataModelOperation rootOperation) {
		this.rootOperation = rootOperation;
		if (null == rootOperation) {
			throw new NullPointerException();
		}
	}

	public boolean canExecute() {
		return rootOperation.canExecute();
	}

	public boolean canRedo() {
		return rootOperation.canRedo();
	}

	public boolean canUndo() {
		return rootOperation.canUndo();
	}

	public void setID(String id) {
		rootOperation.setID(id);
	}

	public String getID() {
		return rootOperation.getID();
	}

	public void setDataModel(IDataModel model) {
		rootOperation.setDataModel(model);
	}

	public IDataModel getDataModel() {
		return rootOperation.getDataModel();
	}

	public void setEnvironment(IEnvironment environment) {
		rootOperation.setEnvironment(environment);
	}

	public IEnvironment getEnvironment() {
		return rootOperation.getEnvironment();
	}

	public ISchedulingRule getSchedulingRule() {
		return rootOperation.getSchedulingRule();
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return rootOperation.execute(monitor, info);
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return rootOperation.undo(monitor, info);
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return rootOperation.redo(monitor, info);
	}

	public int getOperationExecutionFlags() {
		return rootOperation.getOperationExecutionFlags();
	}

	public String getLabel() {
		return rootOperation.getLabel();
	}

	public IUndoContext[] getContexts() {
		return rootOperation.getContexts();
	}

	public boolean hasContext(IUndoContext context) {
		return rootOperation.hasContext(context);
	}

	public void addContext(IUndoContext context) {
		rootOperation.addContext(context);
	}

	public void removeContext(IUndoContext context) {
		rootOperation.removeContext(context);
	}

	public void dispose() {
		rootOperation.dispose();
	}

}
