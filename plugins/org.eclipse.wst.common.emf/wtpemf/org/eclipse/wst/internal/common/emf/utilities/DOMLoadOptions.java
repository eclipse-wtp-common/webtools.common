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
 * Created on Mar 12, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.internal.common.emf.utilities;

/**
 * @author schacher
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DOMLoadOptions {
	protected boolean validate;
	protected boolean allowJavaEncodings;
	protected boolean expandEntityRefererences;

	/**
	 * @return boolean
	 */
	public boolean isAllowJavaEncodings() {
		return allowJavaEncodings;
	}

	/**
	 * @return boolean
	 */
	public boolean isExpandEntityRefererences() {
		return expandEntityRefererences;
	}

	/**
	 * @return boolean
	 */
	public boolean isValidate() {
		return validate;
	}

	/**
	 * Sets the allowJavaEncodings.
	 * 
	 * @param allowJavaEncodings
	 *            The allowJavaEncodings to set
	 */
	public void setAllowJavaEncodings(boolean allowJavaEncodings) {
		this.allowJavaEncodings = allowJavaEncodings;
	}

	/**
	 * Sets the expandEntityRefererences.
	 * 
	 * @param expandEntityRefererences
	 *            The expandEntityRefererences to set
	 */
	public void setExpandEntityRefererences(boolean expandEntityRefererences) {
		this.expandEntityRefererences = expandEntityRefererences;
	}

	/**
	 * Sets the validate.
	 * 
	 * @param validate
	 *            The validate to set
	 */
	public void setValidate(boolean validate) {
		this.validate = validate;
	}

}