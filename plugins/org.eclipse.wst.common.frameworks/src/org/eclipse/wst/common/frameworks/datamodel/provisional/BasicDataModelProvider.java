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

public abstract class BasicDataModelProvider implements IDataModelProvider {

	protected IDataModel model = null;

	public final void setDataModel(IDataModel dataModel) {
		this.model = dataModel;
	}

	public final IDataModel getDataModel() {
		return model;
	}

	public Object getProperty(String propertyName){
		return model.getProperty(propertyName);
	}
	
	public boolean setProperty(String propertyName, Object propertyValue) {
		return true;
	}

	public Object getDefaultProperty(String propertyName) {
		return null;
	}

	public void init() {
	}

	public boolean isPropertyEnabled(String propertyName) {
		return true;
	}

	public IStatus validateProperty(String propertyName) {
		return OK_STATUS;
	}

	public IDataModelPropertyDescriptor getPropertyDescriptor(String propertyName) {
		return null;
	}

	public IDataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		return null;
	}

	public List getExtendedContext() {
		return null;
	}
	
	public DataModelOperation getDefaultOperation(){
		return null;
	}

}
