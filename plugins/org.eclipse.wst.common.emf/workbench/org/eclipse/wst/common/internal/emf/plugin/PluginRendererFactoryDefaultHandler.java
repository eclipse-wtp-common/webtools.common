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
 * Created on Dec 1, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.internal.emf.plugin;

import org.eclipse.wst.common.internal.emf.resource.EMF2DOMRendererFactoryDefaultHandler;
import org.eclipse.wst.common.internal.emf.resource.RendererFactory;

import org.eclipse.jem.util.UIContextDetermination;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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
	public RendererFactory getDefaultRendererFactory() {
		RendererFactory aFactory = (RendererFactory) UIContextDetermination.createInstance(EXT_POINT_NAME);
		return aFactory == null ? super.getDefaultRendererFactory() : aFactory;
	}


}