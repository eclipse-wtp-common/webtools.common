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
 * Created on Aug 19, 2003
 *
 */
package org.eclipse.wst.internal.common.emf.resource;


/**
 * Used to create instances of the EMF2SAXRenderer
 * 
 * @author mdelder
 */
public class EMF2SAXRendererFactory extends RendererFactory {

	public static final EMF2SAXRendererFactory INSTANCE = new EMF2SAXRendererFactory();

	protected EMF2SAXRendererFactory() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.RendererFactory#createRenderer()
	 */
	public Renderer createRenderer() {
		EMF2SAXRenderer renderer = new EMF2SAXRenderer();
		renderer.setValidating(isValidating());
		return renderer;
	}

}