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
package org.eclipse.wst.common.frameworks.datamodel.provisional;

import java.util.List;

import org.eclipse.core.runtime.IStatus;

/**
 * A default implementation of <code>IDataModelProvider</code>.
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.provisional.IDataModelProvider
 */
public abstract class BasicDataModelProvider implements IDataModelProvider {

	protected IDataModel model = null;

	public void init() {
	}

	public final void setDataModel(IDataModel dataModel) {
		this.model = dataModel;
	}

	public final IDataModel getDataModel() {
		return model;
	}

	/**
	 * <p>
	 * Convenience method for getting a property from the backing IDataModel. This is equavalent to
	 * <code>getDataModel().getProperty(propertyName)</code>.
	 * </p>
	 * 
	 * @param propertyName
	 * @return the property value
	 */
	protected final Object getProperty(String propertyName) {
		return model.getProperty(propertyName);
	}

	protected final boolean getBooleanProperty(String propertyName) {
		return model.getBooleanProperty(propertyName);
	}

	protected final int getIntProperty(String propertyName) {
		return model.getIntProperty(propertyName);
	}


	public boolean setProperty(String propertyName, Object propertyValue) {
		return true;
	}

	public Object getDefaultProperty(String propertyName) {
		return null;
	}

	public boolean isPropertyEnabled(String propertyName) {
		return true;
	}

	public IStatus validateProperty(String propertyName) {
		return null;
	}

	public DataModelPropertyDescriptor getPropertyDescriptor(String propertyName) {
		return null;
	}

	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		return null;
	}

	public List getExtendedContext() {
		return null;
	}

	public DataModelOperation getDefaultOperation() {
		return null;
	}
}
