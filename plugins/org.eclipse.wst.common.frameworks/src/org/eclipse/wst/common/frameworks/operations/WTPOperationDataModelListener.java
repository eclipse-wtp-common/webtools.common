/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.operations;

/**
 * Clients wishing to register with a WTPOperationData to receive WTPOperationDataModelEvents need
 * to implement this interface.
 * 
 * @see org.eclipse.wst.common.frameworks.operations.WTPOperationDataModel#addListener(WTPOperationDataModelListener)
 */
public interface WTPOperationDataModelListener {
	/**
	 * Use WTPOperationDataModelEvent.PROPERTY_CHG instead.
	 * @deprecated
	 */
	final int PROPERTY_CHG = WTPOperationDataModelEvent.PROPERTY_CHG;
	
	/**
	 * Use WTPOperationDataModelEvent.VALID_VALUES_CHG instead.
	 * @deprecated
	 */
	final int VALID_VALUES_CHG = WTPOperationDataModelEvent.VALID_VALUES_CHG;
	
	/**
	 * Use WTPOperationDataModelEvent.ENABLE_CHG instead.
	 * @deprecated
	 */
	final int ENABLE_CHG = WTPOperationDataModelEvent.ENABLE_CHG;
	/**
	 * This method is invoked on listening clients when a property changes.
	 * 
	 * @param event
	 */
	void propertyChanged(WTPOperationDataModelEvent event);

}