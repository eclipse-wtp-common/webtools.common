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
package org.eclipse.wst.common.frameworks.internal.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclispe.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

import org.eclipse.jem.util.RegistryReader;

public class OperationExtensionReader extends RegistryReader {
	protected static HashMap opExtensions = null;

	static final String ELEMENT_J2EEOPEXT = "operationExtension"; //$NON-NLS-1$
	static final String ATT_ID = "id"; //$NON-NLS-1$ 
	//static final String ATT_OP_TYPE = "operationType"; //$NON-NLS-1$
	static final String ATT_PRE_OP = "preOperationClass"; //$NON-NLS-1$
	static final String ATT_POST_OP = "postOperationClass"; //$NON-NLS-1$

	public OperationExtensionReader() {
		super(WTPCommonPlugin.PLUGIN_ID, "OperationExtension"); //$NON-NLS-1$
	}

	/**
	 * readElement() - parse and deal with an extension like: <operationExtension
	 * preOperationClass="com.ibm.etools....PreDeleteOperation"
	 * postOperationClass="com.ibm.etools....PostDeleteOperation"> </operationExtension>
	 */

	public boolean readElement(IConfigurationElement element) {
		if (!element.getName().equals(ELEMENT_J2EEOPEXT))
			return false;
		String id = element.getAttribute(ATT_ID);
		String preOp = element.getAttribute(ATT_PRE_OP);
		String postOp = element.getAttribute(ATT_POST_OP);
		OperationExtension extension = new OperationExtension(element, id, preOp, postOp);
		addExtensionPoint(extension);
		return true;

	}

	/**
	 * Sets the extension point.
	 * 
	 * @param extensions
	 *            The extensions to set
	 */
	private static void addExtensionPoint(OperationExtension newExtension) {
		if (opExtensions == null)
			opExtensions = new HashMap();
		Collection temp = null;
		Object holder = opExtensions.get(newExtension.getOpID());
		if (holder == null) {
			temp = new ArrayList();
			temp.add(newExtension);
			opExtensions.put(newExtension.getOpID(), temp);
		} else {
			temp = (Collection) holder;
			temp.add(newExtension);
		}
	}

	protected static HashMap getExtensionPoints() {
		return opExtensions;
	}

}