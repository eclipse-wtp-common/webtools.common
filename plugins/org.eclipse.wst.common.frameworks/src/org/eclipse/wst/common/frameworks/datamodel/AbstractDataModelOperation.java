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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.common.environment.IEnvironment;

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
	private IEnvironment environment;

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

	public Set getDataModelIDs() {
		return new HashSet();
	}

	public ISchedulingRule getSchedulingRule() {
		return null;
	}

	public int getOperationExecutionFlags() {
		return IWorkspace.AVOID_UPDATE;
	}

  /**
   * The framework will set the environment on this operation 
   * before it is executed.  The operation can then use the
   * environment to report status, log information, and access
   * resources in an environment neutral way.
   */
	public final void setEnvironment(IEnvironment env) {
		environment = env;
	}

  /**
   * An operation can call this method to get the environment
   * that has been set by the operations framework.
   * @return returns an environment.
   */
	public final IEnvironment getEnvironment() {
		return environment;
	}

  /**
   * An operation can specify a list of operations that should run
   * before this operation.  Subclasses can override this method to 
   * provide these operations.  The operations provided will be
   * executed in the order specified in the list.
   * @return returns a list of data model operations or null.  
   * Null indicates that there are no pre operations.
   */
  public List getPreOperations()
  {
    return null; 
  }
  
  /**
   * An operation can specify a list of operations that should run
   * after this operation.  Subclasses can override this method to 
   * provide these operations.  The operations provided will be
   * executed in the order specified in the list.
   * @return returns a list of data model operations or null.
   * Null indicates that there are no post operations.
   */
  public List getPostOperations()
  {
    return null; 
  }
  
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return Status.OK_STATUS;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return Status.OK_STATUS;
	}
}
