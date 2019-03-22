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
 * Created on Oct 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class ConfigurationElementWrapper {

	protected IConfigurationElement element;

	/**
	 *  
	 */
	public ConfigurationElementWrapper(IConfigurationElement element) {
		super();
		this.element = element;
	}



	/**
	 * @return Returns the element.
	 */
	protected IConfigurationElement getElement() {
		return element;
	}

	/**
	 * Return the plugin id of the configuration element
	 * 
	 * @return
	 */
	public String getPluginId() {
		String pluginId = null;

		if (element != null) {
			IExtension extension = element.getDeclaringExtension();

			if (extension != null) {
				pluginId = extension.getNamespace();
				// TODO jsholl is this correct???
			}
		}

		return pluginId;
	}

}