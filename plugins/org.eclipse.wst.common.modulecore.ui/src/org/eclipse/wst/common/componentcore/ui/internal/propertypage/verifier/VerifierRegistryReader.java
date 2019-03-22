/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.componentcore.ui.internal.propertypage.verifier;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.componentcore.ui.ModuleCoreUIPlugin;
import org.eclipse.wst.common.core.util.RegistryReader;


public class VerifierRegistryReader extends RegistryReader {
	/**
	 * @param registry
	 * @param plugin
	 * @param extensionPoint
	 */
	static final String ASSEMBLY_VERIFIER_EXTENSION_POINT = "deploymentAssemblyVerifier"; //$NON-NLS-1$
	static final String TARGET_SERVER_RUNTIME_ID = "runtime_server_id"; //$NON-NLS-1$
	static final String RUNTIME = "runtime"; //$NON-NLS-1$
	static final String VERIFIER = "verifier"; //$NON-NLS-1$
	public static final String VERIFIER_CLASS = "class"; //$NON-NLS-1$
	static final String COMPONENT_TYPE_ID = "component_type"; //$NON-NLS-1$
	static final String COMPONENT = "component"; //$NON-NLS-1$

	public VerifierRegistryReader() {
		super(ModuleCoreUIPlugin.PLUGIN_ID, ASSEMBLY_VERIFIER_EXTENSION_POINT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public boolean readElement(IConfigurationElement element) {
		if (!element.getName().equals(VERIFIER))
			return false;
		List runtimeList = new ArrayList();
		List comps = new ArrayList();
		IConfigurationElement[] runtimes = element.getChildren(RUNTIME);
		for (int i = 0; i < runtimes.length; i++) {
			IConfigurationElement runtime = runtimes[i];
			String serverTarget = runtime.getAttribute(TARGET_SERVER_RUNTIME_ID);
			runtimeList.add(serverTarget);
		}
		IConfigurationElement[] components = element.getChildren(COMPONENT);
		for (int i = 0; i < components.length; i++) {
			IConfigurationElement component = components[i];
			String compType = component.getAttribute(COMPONENT_TYPE_ID);
			comps.add(compType);
		}

		String deployer = element.getAttribute(VERIFIER_CLASS);
		if (deployer != null) {
			VerifierRegistry.instance().register(element, runtimeList, comps);
			return true;
		}
		return false;
	}

}
