/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operations;

/**
 * replace with {@link org.eclipse.wst.common.frameworks.datamodel.IDataModelListener}
 * Clients wishing to register with a WTPOperationData to receive WTPOperationDataModelEvents need
 * to implement this interface.
 * 
 * This class is EXPERIMENTAL and is subject to substantial changes.
 * 
 * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#addListener(WTPOperationDataModelListener)
 */
public interface WTPOperationDataModelListener {
	//TODO delete this
	/**
	 * This will be deleted before WTP M4. Use WTPOperationDataModelEvent.PROPERTY_CHG instead.
	 * 
	 * @deprecated Use WTPOperationDataModelEvent.PROPERTY_CHG instead.
	 */
	final int PROPERTY_CHG = WTPOperationDataModelEvent.PROPERTY_CHG;

	//TODO delete this
	/**
	 * This will be deleted before WTP M4. Use WTPOperationDataModelEvent.VALID_VALUES_CHG instead.
	 * 
	 * @deprecated Use WTPOperationDataModelEvent.VALID_VALUES_CHG instead.
	 */
	final int VALID_VALUES_CHG = WTPOperationDataModelEvent.VALID_VALUES_CHG;

	//TODO delete this
	/**
	 * This will be deleted before WTP M4. Use WTPOperationDataModelEvent.ENABLE_CHG instead.
	 * 
	 * @deprecated Use WTPOperationDataModelEvent.ENABLE_CHG instead.
	 */
	final int ENABLE_CHG = WTPOperationDataModelEvent.ENABLE_CHG;

	/**
	 * This method is invoked on listening clients when a property changes.
	 * 
	 * @param event
	 *            the event carrying the notification.
	 */
	void propertyChanged(WTPOperationDataModelEvent event);

}