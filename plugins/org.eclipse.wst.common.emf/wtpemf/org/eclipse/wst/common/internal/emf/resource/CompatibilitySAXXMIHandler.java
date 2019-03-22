/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Apr 30, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.internal.emf.resource;

import java.util.Map;

import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.SAXXMIHandler;

/**
 * @author DABERG
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class CompatibilitySAXXMIHandler extends SAXXMIHandler {
	protected final static String NULL_ATTRIB = XMLResource.XSI_NS + ":null"; //$NON-NLS-1$

	/**
	 * @param xmiResource
	 * @param helper
	 * @param options
	 */
	public CompatibilitySAXXMIHandler(XMLResource xmiResource, XMLHelper helper, Map options) {
		super(xmiResource, helper, options);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#isNull()
	 */
	@Override
	protected boolean isNull() {
		boolean isnull = super.isNull();
		if (!isnull)
			isnull = attribs.getValue(NULL_ATTRIB) != null;
		return isnull;
	}

}