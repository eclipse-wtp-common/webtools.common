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
 * Created on Apr 23, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.internal.common.emf.resource;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * @author DABERG
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class CompatibilityResourceFactory extends XMIResourceFactoryImpl {
	/**
	 *  
	 */
	public CompatibilityResourceFactory() {
		super();
	}

	protected Map prefixToPackageURIs;

	protected Map packageURIsToPrefixes;

	public Map getPrefixToPackageURI() {
		return CompatibilityPackageMappingRegistry.INSTANCE.getPrefixToPackageURIs();
	}

	public Map getPackageURIsToPrefixes() {
		return CompatibilityPackageMappingRegistry.INSTANCE.getPackageURIsToPrefixes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl#createResource(org.eclipse.emf.common.util.URI)
	 */
	public Resource createResource(URI uri) {
		return new CompatibilityXMIResourceImpl(uri);
	}


}