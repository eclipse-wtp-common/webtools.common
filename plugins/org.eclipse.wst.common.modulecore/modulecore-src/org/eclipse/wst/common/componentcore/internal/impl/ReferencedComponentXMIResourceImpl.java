package org.eclipse.wst.common.componentcore.internal.impl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.internal.emf.resource.MappedXMIHelper;
import org.eclipse.wst.common.internal.emf.resource.ReferencedXMIResourceImpl;

public class ReferencedComponentXMIResourceImpl extends
		ReferencedXMIResourceImpl implements Resource {

	public ReferencedComponentXMIResourceImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ReferencedComponentXMIResourceImpl(URI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}
	protected MappedXMIHelper doCreateXMLHelper() {
		return new MappedComponentXMIHelper(this, getPrefixToPackageURIs());
	}

}
