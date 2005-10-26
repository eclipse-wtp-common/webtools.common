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
package org.eclipse.wst.common.componentcore.internal.impl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.internal.emf.resource.EMF2DOMRendererFactory;
import org.eclipse.wst.common.internal.emf.resource.Renderer;
import org.eclipse.wst.common.internal.emf.resource.RendererFactory;
import org.eclipse.wst.common.internal.emf.resource.TranslatorResource;
import org.eclipse.wst.common.internal.emf.resource.TranslatorResourceFactory;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class WTPModulesResourceFactory extends TranslatorResourceFactory {
	
	
	public static final String WTP_MODULES_SHORT_NAME = ".component"; //$NON-NLS-1$
	public static final URI WTP_MODULES_URI_OBJ = URI.createURI(".settings/" + WTP_MODULES_SHORT_NAME);

	/**
	 * Method registerDtds.
	 */
	public static void registerDtds() {
		//do nothing
	}

	/**
	 * Constructor for ApplicationClientResourceFactory.
	 * @param aRendererFactory
	 */
	public WTPModulesResourceFactory(RendererFactory aRendererFactory) {
		super(aRendererFactory);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.RendererFactory.Listener#updateRendererFactory(com.ibm.etools.emf2xml.RendererFactory)
	 */
	public void updateRendererFactory(RendererFactory newRendererFactory) {
		//Do Nothing...   not allowed to switch
	}

	/**
	 * @see com.ibm.etools.emf2xml.impl.TranslatorResourceFactory#createResource(URI, Renderer)
	 */
	protected TranslatorResource createResource(URI uri, Renderer aRenderer) {
		return new WTPModulesResource(uri, aRenderer);
	}
	
	/**
	 * Register myself with the Resource.Factory.Registry
	 */
	public static void registerWith(RendererFactory aRendererFactory) {
		WTPResourceFactoryRegistry.INSTANCE.registerLastFileSegment(WTP_MODULES_SHORT_NAME, new WTPModulesResourceFactory(aRendererFactory));
	}
	/**
	 * register using the default renderer factory.
	 * @see #registerWith(RendererFactory)
	 */
	public static void register() {
		registerWith(EMF2DOMRendererFactory.INSTANCE);
	}

	
	public static Resource.Factory getRegisteredFactory() {
		return WTPResourceFactoryRegistry.INSTANCE.getFactory(WTP_MODULES_URI_OBJ);
	}

}
