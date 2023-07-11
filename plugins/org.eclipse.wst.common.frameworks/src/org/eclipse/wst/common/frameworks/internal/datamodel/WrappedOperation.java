/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
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

	@Override
	public boolean canExecute() {
		return rootOperation.canExecute();
	}

	@Override
	public boolean canRedo() {
		return rootOperation.canRedo();
	}

	@Override
	public boolean canUndo() {
		return rootOperation.canUndo();
	}

	@Override
	public void setID(String id) {
		rootOperation.setID(id);
	}

	@Override
	public String getID() {
		return rootOperation.getID();
	}

	@Override
	public void setDataModel(IDataModel model) {
		rootOperation.setDataModel(model);
	}

	@Override
	public IDataModel getDataModel() {
		return rootOperation.getDataModel();
	}

	@Override
	public void setEnvironment(IEnvironment environment) {
		rootOperation.setEnvironment(environment);
	}

	@Override
	public IEnvironment getEnvironment() {
		return rootOperation.getEnvironment();
	}

	@Override
	public ISchedulingRule getSchedulingRule() {
		return rootOperation.getSchedulingRule();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return rootOperation.execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return rootOperation.undo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return rootOperation.redo(monitor, info);
	}

	@Override
	public int getOperationExecutionFlags() {
		return rootOperation.getOperationExecutionFlags();
	}

	@Override
	public String getLabel() {
		return rootOperation.getLabel();
	}

	@Override
	public IUndoContext[] getContexts() {
		return rootOperation.getContexts();
	}

	@Override
	public boolean hasContext(IUndoContext context) {
		return rootOperation.hasContext(context);
	}

	@Override
	public void addContext(IUndoContext context) {
		rootOperation.addContext(context);
	}

	@Override
	public void removeContext(IUndoContext context) {
		rootOperation.removeContext(context);
	}

	@Override
	public void dispose() {
		rootOperation.dispose();
	}

}
