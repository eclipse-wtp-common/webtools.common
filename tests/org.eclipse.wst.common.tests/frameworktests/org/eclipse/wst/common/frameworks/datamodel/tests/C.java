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
package org.eclipse.wst.common.frameworks.datamodel.tests;

import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;

public class C extends AbstractDataModelProvider {
	public static final String P = "C.P";

	public String[] getPropertyNames() {
		return new String[]{P};
	}

	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}
}
