/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.tests.collector;

import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Plugin;


public class TestCollectorPlugin extends Plugin {
	public static String PLUGIN_ID = "org.eclipse.wst.common.tests.collector";
	public static TestCollectorPlugin instance = null;
	public IExtensionPoint dataModelVerifierExt = null;
	
	/**
	 * @param descriptor
	 */
	public TestCollectorPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		instance = this;
		dataModelVerifierExt = descriptor.getExtensionPoint("DataModelVerifier");
	}

}
