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

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.common.environment.IEnvironment;

/**
 * <p>
 * IDataModelOperation defines an IDataModel driven undoable operation. Every IDataModelOperation
 * may be extended by third party clients using the extended operation framework.
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients. Clients should subclass
 * {@link AbstractDataModelOperation}.
 * </p>
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModel
 * 
 * @since 1.0
 */
public interface IDataModelOperation extends IUndoableOperation {

	/**
	 * <p>
	 * Sets the unique operation id. Typically, clients should not invoke this method.
	 * </p>
	 * 
	 * @param id
	 *            the unique operation id
	 */
	public void setID(String id);

	/**
	 * <p>
	 * Returns the unique operation id.
	 * </p>
	 * 
	 * @return the unique operation id
	 */
	public String getID();

	/**
	 * <p>
	 * Sets the IDataModel for this operation.
	 * </p>
	 * 
	 * @param model
	 *            the IDataModel used to run this operation
	 */
	public void setDataModel(IDataModel model);

	/**
	 * <p>
	 * Returns this operation's IDataModel.
	 * </p>
	 * 
	 * @return this operation's IDataModel.
	 */
	public IDataModel getDataModel();

	/**
	 * <p>
	 * Returns the ISchedulingRule used for executing this job using
	 * {@link org.eclipse.core.resources.IWorkspace#run(org.eclipse.core.resources.IWorkspaceRunnable, ISchedulingRule, int, org.eclipse.core.runtime.IProgressMonitor)}.
	 * If <code>null</code> is returned, then IWorkspace.getRoot() is used as the ISchedulingRule
	 * during execution.
	 * </p>
	 * 
	 * @return the ISchedulingRule
	 * 
	 * @see #getOperationExecutionFlags()
	 * @see org.eclipse.core.resources.IWorkspace#run(org.eclipse.core.resources.IWorkspaceRunnable, ISchedulingRule, int,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public ISchedulingRule getSchedulingRule();

	/**
	 * <p>
	 * Returns the OperationExecutionFlags used for executing this Operation as a workspace job.
	 * {@link org.eclipse.core.resources.IWorkspace#run(org.eclipse.core.resources.IWorkspaceRunnable, ISchedulingRule, int, org.eclipse.core.runtime.IProgressMonitor)}.
	 * </p>
	 * 
	 * @return the OperationExecutionFlags
	 * 
	 * @see #getSchedulingRule()
	 * @seeorg.eclipse.core.resources.IWorkspace#run(org.eclipse.core.resources.IWorkspaceRunnable, ISchedulingRule, int,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public int getOperationExecutionFlags();

	/**
	 * <p>
	 * The framework will set the environment on this operation before it is executed. The operation
	 * can then use the environment to report status, log information, and access resources in an
	 * environment neutral way.
	 * <p>
	 * 
	 * @param environment
	 *            the IEnvironment to set.
	 */
	public void setEnvironment(IEnvironment environment);

	/**
	 * Returns the IEvironment set in {@link #setEnvironment(IEnvironment)}}
	 * 
	 * @return the set IEnvironment
	 * 
	 * @see #setEnvironment(IEnvironment)
	 */
	public IEnvironment getEnvironment();

}
