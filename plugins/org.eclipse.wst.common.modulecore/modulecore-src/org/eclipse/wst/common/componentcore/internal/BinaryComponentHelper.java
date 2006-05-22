/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

public abstract class BinaryComponentHelper {

	private IVirtualComponent component;

	protected BinaryComponentHelper(IVirtualComponent component) {
		this.component = component;
	}

	public IVirtualComponent getComponent() {
		return component;
	}

	public abstract void releaseAccess(ArtifactEdit edit);

	public abstract EObject getPrimaryRootObject();

	public abstract Resource getResource(URI uri);
	
	public void dispose(){
	}

}
