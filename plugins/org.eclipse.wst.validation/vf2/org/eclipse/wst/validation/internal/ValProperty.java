/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.util.BitSet;

/**
 * A resource session property, that is used to improve the performance of the validation framework. This is placed
 * on each resource and it enables the framework to quickly determine if the resource needs to be processed.
 * @author karasiuk
 *
 */
public class ValProperty {
/*
 * I did some performance measurements on the IResource#setSessionProperty() and IResource#getSessionProperty()
 * methods, and they were very fast. I used a very large workspace (over 17,000) resources, and you could set (or get)
 * a property on all the resources in under 100ms. 
 */
	
	private int 	_configNumber;
	private BitSet	_configSet = new BitSet(100);
	
	private int		_validationNumber;
	private BitSet	_validationSet = new BitSet(100);
	
	public int getConfigNumber() {
		return _configNumber;
	}
	public void setConfigNumber(int configNumber) {
		_configNumber = configNumber;
	}
	public BitSet getConfigSet() {
		return _configSet;
	}
	public int getValidationNumber() {
		return _validationNumber;
	}
	public void setValidationNumber(int validationNumber) {
		_validationNumber = validationNumber;
	}
	public BitSet getValidationSet() {
		return _validationSet;
	}
	
}
