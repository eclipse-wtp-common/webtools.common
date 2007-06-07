/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * IDataModelProviders are used by the DataMdoelFactory to construct IDataModels.
 * 
 * <p>
 * This interface is not intended to be implemented by clients. Clients should subclass
 * {@link AbstractDataModelProvider}.
 * </p>
 * 
 * @since 1.0
 */
public interface IDataModelProvider extends IDataModelProperties {

	public static IStatus OK_STATUS = new Status(IStatus.OK, "org.eclipse.wst.common.frameworks.internal", 0, "OK", null); //$NON-NLS-1$ //$NON-NLS-2$

	public void setDataModel(IDataModel dataModel);

	/**
	 * <p>
	 * Returns the backing IDataModel for this provider.
	 * </p>
	 * 
	 * @return the backing IDataModel
	 */
	public IDataModel getDataModel();

	/**
	 * <p>
	 * Returns a list of property names for which this provider is responsible. This method is
	 * called only once during initialization.
	 * </p>
	 * 
	 * @return the array of valid property names.
	 */
	public Set getPropertyNames();

	/**
	 * <p>
	 * Providers should perform additional initialization here.
	 * </p>
	 */
	public void init();

	/**
	 * <p>
	 * This is where the provider should define how default properties should be computed.
	 * </p>
	 * 
	 * @param propertyName
	 *            the specified property
	 * @return the default property value
	 */
	public Object getDefaultProperty(String propertyName);


	/**
	 * <p>
	 * This is where the provider should define how property enablements are computed.
	 * </p>
	 * 
	 * @param propertyName
	 *            the specified property
	 * @return <code>true</code> if the property is enabled, <code>false</code> otherwise.
	 */
	public boolean isPropertyEnabled(String propertyName);

	/**
	 * IDataModelProviders should perform property validation here. All calls to
	 * IDataModel.validateProperty(String) are routed to the appropriate IDatModelProvider. When
	 * IDataModel.validate() or IDataModel.validate(boolean) are called to validate the entire
	 * IDataModel, any nested model names are also passed through to the IDataModelProvider for a
	 * chance to validate the nested IDataModel in an appropriate manner.
	 * 
	 * @param name
	 * @return an IStatus
	 */
	public IStatus validate(String name);

	/**
	 * <p>
	 * This is a special callback hook for the IDataModel provider to be notified of a setProperty()
	 * call invoked on the backing IDataModel. This method is called after the actual value has been
	 * stored by the IDataModel but before any general notifications are fired to its listeners.
	 * Thus this IDataModelProvider may react to updates apropriately by setting other properties,
	 * notifying default changes, notifying valid value changes, etc.
	 * </p>
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * @return <code>true</code> to fire a VALUE_CHG DataModelEvent.
	 */
	public boolean propertySet(String propertyName, Object propertyValue);

	public DataModelPropertyDescriptor getPropertyDescriptor(String propertyName);

	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName);

	public List getExtendedContext();

	public IDataModelOperation getDefaultOperation();

	public String getID();

	public void dispose();

}
