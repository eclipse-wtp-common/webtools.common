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
 * Created on Dec 1, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.internal.emf.resource;


/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EMF2DOMRendererFactoryDefaultHandler implements RendererFactoryDefaultHandler {

	public static final EMF2DOMRendererFactoryDefaultHandler INSTANCE = new EMF2DOMRendererFactoryDefaultHandler();

	/**
	 *  
	 */
	protected EMF2DOMRendererFactoryDefaultHandler() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.RendererFactoryDefaultHandler#getDefaultRendererFactory()
	 */
	public RendererFactory getDefaultRendererFactory() {
		return EMF2DOMRendererFactory.INSTANCE;
	}

}