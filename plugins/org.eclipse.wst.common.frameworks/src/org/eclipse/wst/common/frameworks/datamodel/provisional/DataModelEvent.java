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
package org.eclipse.wst.common.frameworks.datamodel.provisional;

/**
 * This event is used to communicate property changes from IDataModels to their IDataModelListeners.
 * 
 * This class may be instantiated; it is not intended to be subclassed.
 * 
 * This class is EXPERIMENTAL and is subject to substantial changes.
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.provisional.IDataModel#addListener(IDataModelListener)
 * @see org.eclipse.wst.common.frameworks.datamodel.provisional.IDataModelListener
 */
public final class DataModelEvent {

	/**
	 * A flag used to specify the property's value has changed.
	 * 
	 * @see IDataModel#getProperty(String)
	 * @see #getFlag()
	 */
	public static final int VALUE_CHG = 1;

	/*
	 * Internal
	 */
	static final int DEFAULT_CHG = 2;

	/**
	 * A flag used to specify the property's enablment has changed.
	 * 
	 * @see IDataModel#isPropertyEnabled(String)
	 * @see #getFlag()
	 */
	public static final int ENABLE_CHG = 3;

	/**
	 * A flag used to specify the property's valid values have changed.
	 * 
	 * @see IDataModel#getValidPropertyDescriptors(String)
	 * @see #getFlag()
	 */
	public static final int VALID_VALUES_CHG = 4;


	private IDataModel dataModel;
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
	public DataModelEvent(IDataModel dataModel, String propertyName, int flag) {
		this.dataModel = dataModel;
		this.propertyName = propertyName;
		this.flag = flag;
	}

	/**
	 * Returns the dataModel whose property has changed.
	 * 
	 * @return the dataModel whose property has changed.
	 */
	public IDataModel getDataModel() {
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
	 * @see IDataModel#getProperty(String)
	 */
	public Object getProperty() {
		return dataModel.getProperty(propertyName);
	}

	/**
	 * Convenience method to return the dataModel property's enablement state. This is equavalent
	 * to: <code>getDataModel().isPropertyEnabled(getPropertyName())</code>.
	 * 
	 * @return the dataModel property's enablement state.
	 * @see IDataModel#isPropertyEnabled(String)
	 */
	public boolean isPropertyEnabled() {
		return dataModel.isPropertyEnabled(propertyName);
	}

	/**
	 * Convenience method to return the dataModel property's valid property descriptors. This is
	 * equavalent to: <code>getDataModel().getValidPropertyDescriptors(getPropertyName())</code>.
	 * 
	 * @return the dataModel property's valid property descriptors.
	 * @see IDataModel#getValidPropertyDescriptors(String)
	 */
	public DataModelPropertyDescriptor[] getValidPropertyDescriptors() {
		return dataModel.getValidPropertyDescriptors(propertyName);
	}
}