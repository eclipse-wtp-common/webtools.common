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

import java.util.List;
import java.util.Set;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.common.environment.IEnvironment;

/**
 * <p>
 * IDataModelOperation defines an IDataModel driven undoable operation. Every IDataModelOperation
 * may be extended by third party clients using the extended operation framework.
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

	public Set getDataModelIDs();

	public ISchedulingRule getSchedulingRule();

	public int getOperationExecutionFlags();

  /**
   * The framework will set the environment on this operation 
   * before it is executed.  The operation can then use the
   * environment to report status, log information, and access
   * resources in an environment neutral way.
   */
  public void setEnvironment( IEnvironment environment );
  
  /**
   * An operation can specify a list of operations that should run
   * before this operation.  Subclasses can override this method to 
   * provide these operations.  The operations provided will be
   * executed in the order specified in the list.
   * @return returns a list of data model operations or null.  
   * Null indicates that there are no pre operations.
   */
  public List getPreOperations();
  
  /**
   * An operation can specify a list of operations that should run
   * after this operation.  Subclasses can override this method to 
   * provide these operations.  The operations provided will be
   * executed in the order specified in the list.
   * @return returns a list of data model operations or null.
   * Null indicates that there are no post operations.
   */
  public List getPostOperations();
}
