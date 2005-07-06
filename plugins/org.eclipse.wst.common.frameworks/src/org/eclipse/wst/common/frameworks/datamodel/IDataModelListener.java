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

/**
 * <p>
 * The interface required when listening for DataModelEvents from an IDataModel.
 * </p>
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModel#addListener(IDataModelListener)
 * @see org.eclipse.wst.common.frameworks.datamodel.DataModelEvent
 * 
 * @plannedfor 1.0
 */
public interface IDataModelListener {

	/**
	 * <p>
	 * Sent when DataModelEvents occur.
	 * </p>
	 * 
	 * @param event
	 *            the DataModelEvent
	 */
	public void propertyChanged(DataModelEvent event);
}
