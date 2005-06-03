/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operations;

/**
 * replace with {@link org.eclipse.wst.common.frameworks.datamodel.DataModelEvent}
 * This event is used to communicate property changes from WTPOperationDataModels to their
 * WTPOperationDataModelListeners.
 * 
 * This class may be instantiated; it is not intended to be subclassed.
 * 
 * This class is EXPERIMENTAL and is subject to substantial changes.
 * 
 * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#addListener(WTPOperationDataModelListener)
 * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModelListener
 */
public class WTPOperationDataModelEvent {

	/**
	 * A flag used to specify the property's value has changed.
	 * 
	 * @see WTPOperationDataModel#getProperty(String)
	 * @see #getFlag()
	 */
	public static final int PROPERTY_CHG = 1;

	/**
	 * A flag used to specify the property's valid values have changed.
	 * 
	 * @see WTPOperationDataModel#getValidPropertyDescriptors(String)
	 * @see #getFlag()
	 */
	public static final int VALID_VALUES_CHG = 4;

	/**
	 * A flag used to specify the property's enablment has changed.
	 * 
	 * @see WTPOperationDataModel#isEnabled(String)
	 * @see #getFlag()
	 */
	public static final int ENABLE_CHG = 3;

	private WTPOperationDataModel dataModel;
	private String propertyName;
	private int flag;

	/**
	 * Constructor for WTPOperationDataModelEvent
	 * 
	 * @param dataModel
	 *            the dataModel whose property has changed
	 * @param propertyName
	 *            the name of the changed property
	 * @param flag
	 *            contains a flag specifiying the event type
	 */
	public WTPOperationDataModelEvent(WTPOperationDataModel dataModel, String propertyName, int flag) {
		this.dataModel = dataModel;
		this.propertyName = propertyName;
		this.flag = flag;
	}

	/**
	 * Returns the dataModel whose property has changed.
	 * 
	 * @return the dataModel whose property has changed.
	 */
	public WTPOperationDataModel getDataModel() {
		return dataModel;
	}

	/**
	 * Returns the name of the changed property.
	 * 
	 * @return the name of the changed property.
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Returns the flag indicating the event type. Valid types are:
	 * <ul>
	 * <li><code>PROPERTY_CHG</code></li>
	 * <li><code>VALID_VALUES_CHG</code></li>
	 * <li><code>ENABLE_CHG</code></li>
	 * </ul>
	 * 
	 * @return the flag indicating the event type.
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * Convenience method to return the dataModel's property. This is equavalent to:
	 * <code>getDataModel().getProperty(getPropertyName())</code>.
	 * 
	 * @return the dataModel's property.
	 * @see WTPOperationDataModel#getProperty(String)
	 */
	public Object getProperty() {
		return dataModel.getProperty(propertyName);
	}

	/**
	 * Convenience method to return the dataModel property's enablement state. This is equavalent
	 * to: <code>getDataModel().isEnabled(getPropertyName())</code>.
	 * 
	 * @return the dataModel property's enablement state.
	 * @see WTPOperationDataModel#isEnabled(String)
	 */
	public Boolean isEnabled() {
		return dataModel.isEnabled(propertyName);
	}

	/**
	 * Convenience method to return the dataModel property's valid property descriptors. This is
	 * equavalent to: <code>getDataModel().getValidPropertyDescriptors(getPropertyName())</code>.
	 * 
	 * @return the dataModel property's valid property descriptors.
	 * @see WTPOperationDataModel#getValidPropertyDescriptors(String)
	 */
	public WTPPropertyDescriptor[] getValidPropertyDescriptors() {
		return dataModel.getValidPropertyDescriptors(propertyName);
	}
}