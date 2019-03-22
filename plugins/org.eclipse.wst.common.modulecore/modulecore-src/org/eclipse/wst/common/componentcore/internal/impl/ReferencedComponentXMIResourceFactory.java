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
import org.eclipse.wst.common.internal.emf.resource.ReferencedXMIFactoryImpl;

public class ReferencedComponentXMIResourceFactory extends
		ReferencedXMIFactoryImpl {

	public ReferencedComponentXMIResourceFactory() {
		super();
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
