/*******************************************************************************
 * Copyright (c) 2015, 2019 IBM Corporation and others.
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
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


public class TestCollectorPlugin extends Plugin {
	public static String PLUGIN_ID = "org.eclipse.wst.common.tests.collector";
	/**
	 * 	@deprecated - no one is known to use this, and no one should
	 */
	public IExtensionPoint dataModelVerifierExt = null;
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		dataModelVerifierExt = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID, "DataModelVerifier");
	}
}
