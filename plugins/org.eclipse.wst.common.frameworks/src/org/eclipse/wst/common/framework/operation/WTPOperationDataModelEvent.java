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
/*
 * Created on Feb 6, 2004
 * 
 * To change the template for this generated file go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
package org.eclipse.wst.common.framework.operation;

/**
 * @author jsholl
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public class WTPOperationDataModelEvent {

	/**
	 * Flags associated with the propertyChanged event. <code>PROPERTY_CHG</code>= A simple
	 * property change. <code>VALID_VALUES_CHG</code>= The valid values for the given property
	 * have changed. <code>ENABLE_CHG</code>= The enablement for the given property has changed.
	 */
	public static final int PROPERTY_CHG = 1;
	public static final int VALID_VALUES_CHG = 2;
	public static final int ENABLE_CHG = 3;

	private WTPOperationDataModel dataModel;
	private String propertyName;
	private Object oldValue;
	private Object newValue;
	private int flag;

	public WTPOperationDataModelEvent(WTPOperationDataModel dataModel, String propertyName, Object oldValue, Object newValue) {
		this(dataModel, propertyName, oldValue, newValue, PROPERTY_CHG);
	}

	public WTPOperationDataModelEvent(WTPOperationDataModel dataModel, String propertyName, Object oldValue, Object newValue, int flag) {
		this.dataModel = dataModel;
		this.propertyName = propertyName;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.flag = flag;
	}

	public WTPOperationDataModel getDataModel() {
		return dataModel;
	}

	public int getFlag() {
		return flag;
	}

	public Object getNewValue() {
		return newValue;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public String getPropertyName() {
		return propertyName;
	}

}