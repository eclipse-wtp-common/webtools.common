/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operations;

public interface WTPOperationDataModelListener {
	/**
	 * Flags associated with the propertyChanged event. <code>PROPERTY_CHG</code>= A simple
	 * property change. <code>VALID_VALUES_CHG</code>= The valid values for the given property
	 * have changed. <code>ENABLE_CHG</code>= The enablement for the given property has changed.
	 */
	final int PROPERTY_CHG = WTPOperationDataModelEvent.PROPERTY_CHG;
	final int VALID_VALUES_CHG = WTPOperationDataModelEvent.VALID_VALUES_CHG;
	final int ENABLE_CHG = WTPOperationDataModelEvent.ENABLE_CHG;

	/**
	 * A property has changed on the model with the given propertyName. Use the flag to detect the
	 * type of change.
	 * 
	 * @param propertyName
	 * @param flag
	 * @param oldValue
	 * @param newValue
	 * 
	 * @see WTPOperationDataModelListener#PROPERTY_CHG
	 * @see WTPOperationDataModelListener#VALID_VALUES_CHG
	 */
	void propertyChanged(WTPOperationDataModelEvent event);

}