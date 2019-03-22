/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;


public class EMF2DOMRendererFactory extends RendererFactory {

	public static final EMF2DOMRendererFactory INSTANCE = new EMF2DOMRendererFactory();

	public EMF2DOMRendererFactory() {
		super();
	}

	/**
	 * @see com.ibm.etools.emf2xml.RendererFactory#createRenderer()
	 */
	@Override
	public Renderer createRenderer() {
		EMF2DOMRenderer renderer = new EMF2DOMRenderer();
		renderer.setValidating(isValidating());
		return renderer;
	}

}