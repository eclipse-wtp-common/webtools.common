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
 * Created on Jun 9, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.internal.emf.plugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.common.internal.emf.resource.RendererFactory;

/**
 * @author DABERG
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class EcoreUtilitiesPlugin extends Plugin {
	public static final String ID = "org.eclipse.wst.common.emf"; //$NON-NLS-1$

	/**
	 * @param descriptor
	 */
	public EcoreUtilitiesPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#startup()
	 */
	public void startup() throws CoreException {
		super.startup();
		RendererFactory.setDefaultHandler(PluginRendererFactoryDefaultHandler.INSTANCE);
		PackageURIMapReader reader = new PackageURIMapReader();
		reader.processExtensions();
	}
}