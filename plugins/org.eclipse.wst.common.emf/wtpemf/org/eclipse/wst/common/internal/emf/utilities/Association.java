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
package org.eclipse.wst.common.internal.emf.utilities;

/**
 * Insert the type's description here. Creation date: (12/17/2000 7:38:15 PM)
 * 
 * @author: Administrator
 */
public class Association {
	protected Object key;
	protected Object value;

	/**
	 * Association constructor comment.
	 */
	public Association(Object aKey, Object aValue) {
		setKey(aKey);
		setValue(aValue);
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 7:38:48 PM)
	 * 
	 * @return java.lang.Object
	 */
	public java.lang.Object getKey() {
		return key;
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 7:38:48 PM)
	 * 
	 * @return java.lang.Object
	 */
	public java.lang.Object getValue() {
		return value;
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 7:38:48 PM)
	 * 
	 * @param newKey
	 *            java.lang.Object
	 */
	public void setKey(java.lang.Object newKey) {
		key = newKey;
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 7:38:48 PM)
	 * 
	 * @param newValue
	 *            java.lang.Object
	 */
	public void setValue(java.lang.Object newValue) {
		value = newValue;
	}
}

