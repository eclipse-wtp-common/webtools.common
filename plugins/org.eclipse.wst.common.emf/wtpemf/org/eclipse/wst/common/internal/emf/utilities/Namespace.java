/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
/*
 * Created on Aug 20, 2003
 *  
 */
package org.eclipse.wst.common.internal.emf.utilities;


public class Namespace {

	protected String prefix;
	protected String nsURI;


	public Namespace(String prefix, String uri) {
		this.prefix = prefix;
		this.nsURI = uri;
	}

	public String getNsURI() {
		return nsURI;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setNsURI(String string) {
		nsURI = string;
	}

	public void setPrefix(String string) {
		prefix = string;
	}
}