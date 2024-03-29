/*******************************************************************************
 * Copyright (c) 2003, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.datamodel;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;

/**
 * <p>
 * A default implementation of <code>IDataModelProvider</code>.
 * </p>
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider
 * @since 1.0
 */
public abstract class AbstractDataModelProvider implements IDataModelProvider {

	/**
	 * <p>
	 * The IDataModel for this provider.
	 * </p>
	 */
	protected IDataModel model = null;

	@Override
	public Set<String> getPropertyNames() {
		return new HashSet<String>();
	}
	
	/**
	 * <p>
	 * A default implementation of init(). Subclasses should override as necessary.
	 * </p>
	 * 
	 * @see IDataModelProvider#init()
	 */
	@Override
	public void init() {
	}

	/**
	 * </p>
	 * An implementation of setDataModel().
	 * </p>
	 * 
	 * @see IDataModelProvider#setDataModel(IDataModel)
	 */
	@Override
	public final void setDataModel(IDataModel dataModel) {
		this.model = dataModel;
	}

	/**
	 * <p>
	 * An implementation of getDataModel().
	 * </p>
	 * 
	 * @see IDataModelProvider#getDataModel()
	 */
	@Override
	public final IDataModel getDataModel() {
		return model;
	}


	/**
	 * </p>
	 * A default implementation of propertySet(). Subclasses should override as necessary.
	 * </p>
	 * 
	 * @param propertyName
	 *            the name of the property that has been set
	 * @param propertyValue
	 *            the value the property has been set
	 * 
	 * @see IDataModelProvider#propertySet(String, Object)
	 */
	@Override
	public boolean propertySet(String propertyName, Object propertyValue) {
		return true;
	}

	/**
	 * <p>
	 * A default implementation of getDefaultProperty(). Subclasses should override as necessary.
	 * </p>
	 * 
	 * @param propertyName
	 *            the specified property
	 * 
	 * @see IDataModelProvider#getDefaultProperty(String)
	 */
	@Override
	public Object getDefaultProperty(String propertyName) {
		if(ALLOW_EXTENSIONS.equals(propertyName)){
			return Boolean.TRUE;
		} else if(RESTRICT_EXTENSIONS.equals(propertyName)){
			return Collections.EMPTY_LIST;
		}
		return null;
	}

	/**
	 * <p>
	 * A default implementation of isPropertyEnabled(). Subclasses should override as necessary.
	 * </p>
	 * 
	 * @param propertyName
	 *            the specified property
	 * @see IDataModelProvider#isPropertyEnabled(String)
	 */
	@Override
	public boolean isPropertyEnabled(String propertyName) {
		return true;
	}

	/**
	 * <p>
	 * A default implementation of validate(). Subclasses should override as necessary.
	 * </p>
	 * 
	 * @param name
	 *            the name of the property or nested IDataModel being validated.
	 * 
	 * @see IDataModelProvider#validate(String)
	 */
	@Override
	public IStatus validate(String name) {
		return null;
	}

	/**
	 * <p>
	 * A default implementation of getPropertyDescriptor(). Subclasses should override as necessary.
	 * </p>
	 * 
	 * @param propertyName
	 *            the specified property
	 * @see IDataModelProvider#getPropertyDescriptor(String)
	 */
	@Override
	public DataModelPropertyDescriptor getPropertyDescriptor(String propertyName) {
		return null;
	}

	/**
	 * <p>
	 * A default implementation of getValidPropertyDescriptors(). Subclasses should override as
	 * necessary.
	 * </p>
	 * 
	 * @param propertyName
	 *            the specified property
	 * @see IDataModelProvider#getValidPropertyDescriptors(String)
	 */
	@Override
	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		return null;
	}

	/**
	 * <p>
	 * A default implementation of getExtendedContext(). Subclasses should override as necessary.
	 * </p>
	 * 
	 * @see IDataModelProvider#getExtendedContext()
	 */
	@Override
	public List getExtendedContext() {
		return null;
	}

	/**
	 * <p>
	 * A default implementation of getDefaultOperation(). Subclasses should override as necessary.
	 * </p>
	 * 
	 * @see IDataModelProvider#getDefaultOperation()
	 */
	@Override
	public IDataModelOperation getDefaultOperation() {
		return null;
	}

	/**
	 * <p>
	 * A default implementation of getName(). Subclasses should override as necessary.
	 * </p>
	 * 
	 * @see IDataModelProvider#getID()
	 */
	@Override
	public String getID() {
		return this.getClass().getName();
	}

	/**
	 * <p>
	 * A default implementation of dispose(). Subclasses should override as necessary.
	 * </p>
	 * 
	 * @see IDataModelProvider#dispose()
	 */
	@Override
	public void dispose() {
	}

	/**
	 * <p>
	 * Convenience method for getting a property from the backing IDataModel.
	 * </p>
	 * 
	 * @param propertyName
	 *            the property name
	 * @return the property value
	 * 
	 * @see IDataModel#getProperty(String)
	 */
	protected final Object getProperty(String propertyName) {
		return model.getProperty(propertyName);
	}

	/**
	 * <p>
	 * Convenience method for setting a property on the backing IDataModel.
	 * </p>
	 * 
	 * @param propertyName
	 *            the property name
	 * @param propertyValue
	 *            the property value
	 * 
	 * @see IDataModel#setProperty(String, Object)
	 */
	protected final void setProperty(String propertyName, Object propertyValue) {
		model.setProperty(propertyName, propertyValue);
	}

	/**
	 * <p>
	 * Convenience method for getting a boolean property from the backing IDataModel.
	 * </p>
	 * 
	 * @param propertyName
	 *            the property name
	 * @return the boolean value of the property
	 * 
	 * @see IDataModel#getBooleanProperty(String)
	 */
	protected final boolean getBooleanProperty(String propertyName) {
		return model.getBooleanProperty(propertyName);
	}

	/**
	 * <p>
	 * Convenience method for setting a boolean property on the backing IDataModel.
	 * </p>
	 * 
	 * @param propertyName
	 *            the property name
	 * @param propertyValue
	 *            the boolean property value
	 * 
	 * @see IDataModel#setBooleanProperty(String, boolean)
	 */
	protected final void setBooleanProperty(String propertyName, boolean propertyValue) {
		model.setBooleanProperty(propertyName, propertyValue);
	}

	/**
	 * <p>
	 * Convenience method for getting an int property from the backing IDataModel.
	 * </p>
	 * 
	 * @param propertyName
	 *            the property name
	 * @return the int value of the property
	 * 
	 * @see IDataModel#getIntProperty(String)
	 */
	protected final int getIntProperty(String propertyName) {
		return model.getIntProperty(propertyName);
	}

	/**
	 * <p>
	 * Convenience method for setting an int property on the backing IDataModel.
	 * </p>
	 * 
	 * @param propertyName
	 *            the property name
	 * @param propertyValue
	 *            the int property value
	 * 
	 * @see IDataModel#setIntProperty(String, int)
	 */
	protected final void setIntProperty(String propertyName, int propertyValue) {
		model.setIntProperty(propertyName, propertyValue);
	}

	/**
	 * <p>
	 * Convenience method for getting a String property from the backing IDataModel.
	 * </p>
	 * 
	 * @param propertyName
	 *            the property name
	 * @return the String value of the property
	 * 
	 * @see IDataModel#getStringProperty(String)
	 */
	protected final String getStringProperty(String propertyName) {
		return model.getStringProperty(propertyName);
	}

	/**
	 * <p>
	 * Convenience method for checking if a property is set from the backing IDataModel.
	 * </p>
	 * 
	 * @param propertyName
	 *            the property name
	 * @return <code>true</code> if the property is set, <code>false</code> otherwise.
	 */
	protected final boolean isPropertySet(String propertyName) {
		return model.isPropertySet(propertyName);
	}

}
