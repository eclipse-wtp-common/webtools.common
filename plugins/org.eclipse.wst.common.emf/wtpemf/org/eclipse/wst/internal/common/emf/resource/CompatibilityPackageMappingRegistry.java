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
package org.eclipse.wst.internal.common.emf.resource;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to register mappings for a package prefix to its current namespace URI. Also,
 * this registry is used to map the package
 */
public class CompatibilityPackageMappingRegistry {
	public static CompatibilityPackageMappingRegistry INSTANCE = new CompatibilityPackageMappingRegistry();
	private Map prefixToPackageURIs = new HashMap();
	private Map packageURIsToPrefixes = new HashMap();

	/**
	 *  
	 */
	private CompatibilityPackageMappingRegistry() {
		super();
	}

	/**
	 * @return
	 */
	public Map getPackageURIsToPrefixes() {
		return packageURIsToPrefixes;
	}

	/**
	 * @return
	 */
	public Map getPrefixToPackageURIs() {
		return prefixToPackageURIs;
	}

	public void registerPrefixToPackageURI(String prefix, String uri) {
		if (prefix != null && uri != null) {
			prefixToPackageURIs.put(prefix, uri);
			packageURIsToPrefixes.put(uri, prefix);
		}
	}

}