/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
/*
 * Created on Nov 3, 2003
 * 
 * To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code
 * Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.framework.operation;

import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclispe.wst.common.framework.plugin.WTPCommonPlugin;

import com.ibm.wtp.common.RegistryReader;


public class OperationExtensibilityReader extends RegistryReader {
	static final String ELEMENT_J2EEOPEXT = "extendableOperation"; //$NON-NLS-1$
	static final String OPERATION_ATTRIBUTE = "class"; //$NON-NLS-1$
	static final String OPERATION_ID = "id"; //$NON-NLS-1$
	protected static HashMap extensibleOperations = null;

	public OperationExtensibilityReader() {
		super(WTPCommonPlugin.PLUGIN_ID, "ExtendableOperation"); //$NON-NLS-1$
	}

	/**
	 * readElement() - parse and deal with an extension like: <extendableOperation class =
	 * "com.ibm.etools.foo.Operation" id ' "" />
	 */
	public boolean readElement(IConfigurationElement element) {
		if (!element.getName().equals(ELEMENT_J2EEOPEXT))
			return false;
		String op = element.getAttribute(OPERATION_ATTRIBUTE);
		String id = element.getAttribute(OPERATION_ID);
		addExtendableOperation(op, id);
		return true;
	}

	/**
	 * Sets the Extendable Operation
	 * 
	 * @param op
	 * @param id
	 */
	private static void addExtendableOperation(String op, String id) {
		if (extensibleOperations == null)
			extensibleOperations = new HashMap();
		extensibleOperations.put(op, id);
	}

	protected static HashMap getExtendableOperations() {
		return extensibleOperations;
	}
}