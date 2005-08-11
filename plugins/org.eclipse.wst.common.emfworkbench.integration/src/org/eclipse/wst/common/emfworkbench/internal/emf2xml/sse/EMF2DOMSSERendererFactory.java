/*
 * Created on Oct 10, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.emfworkbench.internal.emf2xml.sse;

import org.eclipse.wst.common.internal.emf.resource.Renderer;
import org.eclipse.wst.common.internal.emf.resource.RendererFactory;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class EMF2DOMSSERendererFactory extends RendererFactory {

	public static final EMF2DOMSSERendererFactory INSTANCE = new EMF2DOMSSERendererFactory();

	public EMF2DOMSSERendererFactory() {
		//Default constructor
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.RendererFactory#createRenderer()
	 */
	public Renderer createRenderer() {
		return new EMF2DOMSSERenderer();
	}

}