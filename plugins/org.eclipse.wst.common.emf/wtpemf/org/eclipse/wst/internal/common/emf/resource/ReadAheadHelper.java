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
 * Created on Apr 2, 2003
 *
 */
package org.eclipse.wst.internal.common.emf.resource;

/**
 * @author schacher
 */
public class ReadAheadHelper {

	protected String parentDOMName;
	protected String[] values;
	protected String childDOMName;

	public ReadAheadHelper(String parentDOMName, String[] values, String childDOMName) {
		super();
		this.parentDOMName = parentDOMName;
		this.values = values;
		this.childDOMName = childDOMName;
	}

	/**
	 * @return
	 */
	public String getChildDOMName() {
		return childDOMName;
	}

	/**
	 * @return
	 */
	public String getParentDOMName() {
		return parentDOMName;
	}

	/**
	 * @return
	 */
	public String[] getValues() {
		return values;
	}

	/**
	 * @param string
	 */
	public void setChildDOMName(String string) {
		childDOMName = string;
	}

	/**
	 * @param string
	 */
	public void setParentDOMName(String string) {
		parentDOMName = string;
	}

	/**
	 * @param strings
	 */
	public void setValues(String[] strings) {
		values = strings;
	}


	public boolean nodeValueIsReadAheadName(String nodeName) {

		return (childDOMName != null) ? childDOMName.equals(nodeName) : false;
	}

	public boolean nodeNameIsReadAheadName(String nodeName) {
		boolean result = false;
		for (int i = 0; i < values.length; i++) {
			if (nodeName.equals(values[i])) {
				result = true;
				break;
			}
		}
		return result;
	}

}