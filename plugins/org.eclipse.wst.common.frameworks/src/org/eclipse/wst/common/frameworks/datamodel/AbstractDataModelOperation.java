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
package org.eclipse.wst.common.frameworks.datamodel;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.common.environment.Environment;

/**
 * Abstract implementation for an IDataModelOperation.
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation
 * 
 * @plannedfor 1.0
 */
public abstract class AbstractDataModelOperation extends AbstractOperation implements IDataModelOperation {

	/**
	 * Convenience IStatus.OK.
	 */
	protected static final IStatus OK_STATUS = IDataModelProvider.OK_STATUS;

	private String id;
	private Environment environment;

	/**
	 * The IDataModel used by this IDataModelOperation
	 */
	protected IDataModel model;

	/**
	 * Default constructor.
	 */
	public AbstractDataModelOperation() {
		super(""); //$NON-NLS-1$
		this.id = getClass().getName();
	}

	/**
	 * Constructor taking an IDataModel
	 * 
	 * @param model
	 *            the IDataModel used to drive this operation
	 */
	public AbstractDataModelOperation(IDataModel model) {
		super(""); //$NON-NLS-1$ // TODO add a label property to IDataModel???
		this.model = model;
		this.id = getClass().getName();
	}

	/**
	 * Default implementation of setID().
	 * 
	 * @see IDataModelOperation#setID(String)
	 */
	public void setID(String id) {
		this.id = id;
	}

	/**
	 * Default implementation of getID().
	 * 
	 * @see IDataModelOperation#getID()
	 */
	public String getID() {
		return id;
	}

	/**
	 * Default implementation of setDataModel()
	 * 
	 * @see IDataModelOperation#setDataModel(IDataModel)
	 */
	public void setDataModel(IDataModel model) {
		this.model = model;
	}

	/**
	 * Default implementation of getDataModel()
	 * 
	 * @see IDataModelOperation#getDataModel()
	 */
	public IDataModel getDataModel() {
		return model;
	}

	public String getDataModelID() {
		return null;
	}

	public ISchedulingRule getSchedulingRule() {
		return null;
	}

	public int getOperationExecutionFlags() {
		return IWorkspace.AVOID_UPDATE;
	}

	public void setEnvironment(Environment env) {
		environment = env;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return Status.OK_STATUS;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return Status.OK_STATUS;
	}
}
