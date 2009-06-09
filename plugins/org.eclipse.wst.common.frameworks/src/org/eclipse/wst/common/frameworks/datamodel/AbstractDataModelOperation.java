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
package org.eclipse.wst.common.frameworks.datamodel;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.common.environment.IEnvironment;

/**
 * <p>
 * Abstract implementation for an IDataModelOperation.
 * </p>
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation
 * 
 * @since 1.0
 */
public abstract class AbstractDataModelOperation extends AbstractOperation implements IDataModelOperation {

	/**
	 * <p>
	 * Convenience IStatus.OK.
	 * </p>
	 */
	protected static final IStatus OK_STATUS = IDataModelProvider.OK_STATUS;

	private String id;
	private IEnvironment environment;

	/**
	 * <p>
	 * The IDataModel used by this IDataModelOperation
	 * </p>
	 */
	protected IDataModel model;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public AbstractDataModelOperation() {
		super(""); //$NON-NLS-1$
		this.id = getClass().getName();
	}

	/**
	 * <p>
	 * Constructor taking an IDataModel
	 * </p>
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
	 * <p>
	 * Default implementation of setID().
	 * <p>
	 * 
	 * @see IDataModelOperation#setID(String)
	 */
	public void setID(String id) {
		this.id = id;
	}

	/**
	 * <p>
	 * Default implementation of getID().
	 * </p>
	 * 
	 * @see IDataModelOperation#getID()
	 */
	public String getID() {
		return id;
	}

	/**
	 * <p>
	 * Default implementation of setDataModel()
	 * </p>
	 * 
	 * @see IDataModelOperation#setDataModel(IDataModel)
	 */
	public void setDataModel(IDataModel model) {
		this.model = model;
	}

	/**
	 * <p>
	 * Default implementation of getDataModel()
	 * </p>
	 * 
	 * @see IDataModelOperation#getDataModel()
	 */
	public IDataModel getDataModel() {
		return model;
	}

	/**
	 * <p>
	 * Default implementation of getSchedulingRule() returns
	 * <code>ResourcesPlugin.getWorkspace().getRoot()</code>.
	 * </p>
	 * 
	 * @see IDataModelOperation#getSchedulingRule()
	 */
	public ISchedulingRule getSchedulingRule() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * <p>
	 * Default implementation of getOperationExecutionFlags() returns
	 * <code>IWorkspace.AVOID_UPDATE</code>.
	 * </p>
	 * 
	 * @see IDataModelOperation#getOperationExecutionFlags()
	 */
	public int getOperationExecutionFlags() {
		return IWorkspace.AVOID_UPDATE;
	}

	/**
	 * <p>
	 * The framework will set the environment on this operation before it is executed. The operation
	 * can then use the environment to report status, log information, and access resources in an
	 * environment neutral way.
	 * </p>
	 * 
	 * @param env
	 *            the environment.
	 * 
	 */
	public final void setEnvironment(IEnvironment env) {
		environment = env;
	}

	/**
	 * <p>
	 * An operation can call this method to get the environment that has been set by the operations
	 * framework.
	 * </p>
	 * 
	 * @return returns an environment.
	 */
	public final IEnvironment getEnvironment() {
		return environment;
	}

	/**
	 * <p>
	 * Default empty implementation of redo.
	 * </p>
	 */
	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return Status.OK_STATUS;
	}

	/**
	 * <p>
	 * Default empty implementation of undo.
	 * </p>
	 */
	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return Status.OK_STATUS;
	}
}
