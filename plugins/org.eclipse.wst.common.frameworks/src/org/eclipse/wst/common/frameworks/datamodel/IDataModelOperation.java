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

import org.eclipse.core.commands.operations.IUndoableOperation;

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
	 * @param id the unique operation id
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

}
