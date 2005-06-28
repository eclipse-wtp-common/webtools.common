package org.eclipse.wst.common.componentcore.internal.impl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.internal.emf.resource.ReferencedXMIFactoryImpl;

public class ReferencedComponentXMIResourceFactory extends
		ReferencedXMIFactoryImpl {

	public ReferencedComponentXMIResourceFactory() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * This is the method that subclasses can override to actually instantiate a new Resource
	 * 
	 * @param uri
	 * @return
	 */
	protected Resource doCreateResource(URI uri) {
		return new ReferencedComponentXMIResourceImpl(uri);
	}

}
