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
package org.eclipse.wst.common.frameworks.datamodel;

/**
 * <p>
 * This event is used to communicate property changes from IDataModels to their IDataModelListeners.
 * </p>
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModel#addListener(IDataModelListener)
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelListener
 * 
 * @plannedfor 1.0
 */
public final class DataModelEvent {

	/**
	 * <p>
	 * A flag used to specify the property's value has changed.
	 * </p>
	 * 
	 * @see IDataModel#getProperty(String)
	 * @see #getFlag()
	 */
	public static final int VALUE_CHG = 1;

	/**
	 * Internal. Clients should not use.
	 */
	static final int DEFAULT_CHG = 2;

	/**
	 * <p>
	 * A flag used to specify the property's enablment has changed.
	 * </p>
	 * 
	 * @see IDataModel#isPropertyEnabled(String)
	 * @see #getFlag()
	 */
	public static final int ENABLE_CHG = 3;

	/**
	 * <p>
	 * A flag used to specify the property's valid values have changed.
	 * </p>
	 * 
	 * @see IDataModel#getValidPropertyDescriptors(String)
	 * @see #getFlag()
	 */
	public static final int VALID_VALUES_CHG = 4;


	private IDataModel dataModel;
	private String propertyName;
	private int flag;

	/**
	 * <p>
	 * Constructor for DataModelEvent.
	 * </p>
	 * 
	 * @param dataModel
	 *            the IDataModel whose property has changed
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
	 * <p>
	 * Returns the IDataModel whose property has changed.
	 * </p>
	 * 
	 * @return the IDataModel whose property has changed.
	 */
	public IDataModel getDataModel() {
		return dataModel;
	}

	/**
	 * <p>
	 * Returns the name of the changed property.
	 * </p>
	 * 
	 * @return the name of the changed property.
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * <p>
	 * Returns the flag indicating the event type. Valid types are:
	 * <ul>
	 * <li><code>PROPERTY_CHG</code></li>
	 * <li><code>VALID_VALUES_CHG</code></li>
	 * <li><code>ENABLE_CHG</code></li>
	 * </ul>
	 * </p>
	 * 
	 * @return the flag indicating the event type.
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * <p>
	 * Convenience method to return the IDataModel's property. This is equavalent to:
	 * <code>getDataModel().getProperty(getPropertyName())</code>.
	 * </p>
	 * 
	 * @return the dataModel's property.
	 * @see IDataModel#getProperty(String)
	 */
	public Object getProperty() {
		return dataModel.getProperty(propertyName);
	}

	/**
	 * <p>
	 * Convenience method to return the IDataModel property's enablement state. This is equavalent
	 * to: <code>getDataModel().isPropertyEnabled(getPropertyName())</code>.
	 * </p>
	 * 
	 * @return the dataModel property's enablement state.
	 * @see IDataModel#isPropertyEnabled(String)
	 */
	public boolean isPropertyEnabled() {
		return dataModel.isPropertyEnabled(propertyName);
	}

	/**
	 * <p>
	 * Convenience method to return the IDataModel property's valid property descriptors. This is
	 * equavalent to: <code>getDataModel().getValidPropertyDescriptors(getPropertyName())</code>.
	 * </p>
	 * 
	 * @return the dataModel property's valid property descriptors.
	 * @see IDataModel#getValidPropertyDescriptors(String)
	 */
	public DataModelPropertyDescriptor[] getValidPropertyDescriptors() {
		return dataModel.getValidPropertyDescriptors(propertyName);
	}
}