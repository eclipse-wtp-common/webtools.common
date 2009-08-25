/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.plugin;

import org.eclipse.wst.common.core.util.UIContextDetermination;
import org.eclipse.wst.common.internal.emf.resource.EMF2DOMRendererFactoryDefaultHandler;
import org.eclipse.wst.common.internal.emf.resource.RendererFactory;

public class PluginRendererFactoryDefaultHandler extends EMF2DOMRendererFactoryDefaultHandler {

	static final PluginRendererFactoryDefaultHandler INSTANCE = new PluginRendererFactoryDefaultHandler();
	static final String EXT_POINT_NAME = "rendererFactory"; //$NON-NLS-1$

	/**
	 *  
	 */
	protected PluginRendererFactoryDefaultHandler() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.EMF2DOMRendererFactoryDefaultHandler#getDefaultRendererFactory()
	 */
	@Override
	public RendererFactory getDefaultRendererFactory() {
		RendererFactory aFactory = (RendererFactory) UIContextDetermination.createInstance(EXT_POINT_NAME);
		return aFactory == null ? super.getDefaultRendererFactory() : aFactory;
	}


}
