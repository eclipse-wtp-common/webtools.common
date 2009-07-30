/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: RegistryReader.java,v $$
 *  $$Revision: 1.6 $$  $$Date: 2009/07/30 22:11:24 $$ 
 */
package org.eclipse.jem.util;
import org.eclipse.core.runtime.*;
import org.osgi.framework.Bundle;


/**
 * Class to read a registry. It is meant to be subclassed to provide specific function.
 * 
 * @deprecated Replaced by {@link org.eclipse.wst.common.core.util.RegistryReader)
 * @since 1.0.0
 */
public abstract class RegistryReader extends org.eclipse.wst.common.core.util.RegistryReader {

	public RegistryReader(String pluginID, String extensionPoint) {
		super(pluginID, extensionPoint);
	}

	/**
	 * Utility method to get the plugin id of a configuation element
	 * 
	 * @param configurationElement
	 * @return plugin id of configuration element
	 * @deprecated Replaced by (@link org.eclipse.wst.common.core.util.RegistryReader.getPluginId())
	 * @since 1.0.0
	 */
	public static String getPluginId(IConfigurationElement configurationElement) {
		return org.eclipse.wst.common.core.util.RegistryReader.getPluginId(configurationElement);
	}

	/**
	 * Tests to see if it is valid at this point in time to create an executable extension. A valid reason not to would be that the workspace is
	 * shutting donw.
	 * 
	 * @param element
	 * @return <code>true</code> if it is valid point to create an executable extension.
	 * 
	 * @deprecated Replaced by (@link org.eclipse.wst.common.core.util.RegistryReader.canCreateExecutableExtension())
	 * @since 1.0.0
	 */
	public static boolean canCreateExecutableExtension(IConfigurationElement element) {
		return org.eclipse.wst.common.core.util.RegistryReader.canCreateExecutableExtension(element);
	}

	/**
	 * Get the system bundle
	 * 
	 * @return system bundle.
	 * 
	 * @since 1.0.0
	 */
	protected static Bundle getSystemBundle() {
		return org.eclipse.wst.common.core.util.RegistryReader.getSystemBundle();
	}
}
