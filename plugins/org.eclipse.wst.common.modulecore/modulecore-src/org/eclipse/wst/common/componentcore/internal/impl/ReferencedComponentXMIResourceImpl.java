/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.impl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.internal.emf.resource.MappedXMIHelper;
import org.eclipse.wst.common.internal.emf.resource.ReferencedXMIResourceImpl;

public class ReferencedComponentXMIResourceImpl extends
		ReferencedXMIResourceImpl implements Resource {

	public ReferencedComponentXMIResourceImpl() {
		super();
	}

	public ReferencedComponentXMIResourceImpl(URI uri) {
		super(uri);
	}
	protected MappedXMIHelper doCreateXMLHelper() {
		return new MappedComponentXMIHelper(this, getPrefixToPackageURIs());
	}

}
