/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.plugin;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.internal.emf.resource.CompatibilityPackageMappingRegistry;

/**
 * This reader will read the packageURIMap extension point and populate the
 * CompatibilityPackageMappingRegistry.
 * 
 * The packageURIMap will have the following configuration element.
 * 
 * <map prefix="somePackagePrefix" uri="somePackageURI"/>
 *  
 */
public class PackageURIMapReader {
	private static final String URI_ATT_NAME = "uri"; //$NON-NLS-1$
	private static final String PREFIX_ATT_NAME = "prefix"; //$NON-NLS-1$
	private static final String EXTENSION_POINT_NAME = "packageURIMap"; //$NON-NLS-1$

	/**
	 *  
	 */
	public PackageURIMapReader() {
		super();
	}

	private IExtensionPoint getExtensionPoint() {
		return Platform.getExtensionRegistry().getExtensionPoint(EcoreUtilitiesPlugin.ID, EXTENSION_POINT_NAME);
	}

	/**
	 * Call this method to read and process all of the packageURIMap extensions. *
	 */
	public void processExtensions() {
		CompatibilityPackageMappingRegistry reg = CompatibilityPackageMappingRegistry.INSTANCE;
		IExtension[] extensions = getExtensionPoint().getExtensions();
		for (int i = 0; i < extensions.length; i++)
			processExtension(extensions[i], reg);
	}

	/**
	 * @param extension
	 */
	private void processExtension(IExtension extension, CompatibilityPackageMappingRegistry reg) {
		IConfigurationElement[] configs = extension.getConfigurationElements();
		for (int i = 0; i < configs.length; i++)
			processConfiguration(configs[i], reg);
	}

	/**
	 * @param element
	 */
	private void processConfiguration(IConfigurationElement element, CompatibilityPackageMappingRegistry reg) {
		String prefix = element.getAttribute(PREFIX_ATT_NAME);
		String uri = element.getAttribute(URI_ATT_NAME);
		reg.registerPrefixToPackageURI(prefix, uri);
	}
}
