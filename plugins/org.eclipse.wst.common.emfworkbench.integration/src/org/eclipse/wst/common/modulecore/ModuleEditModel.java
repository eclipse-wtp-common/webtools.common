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
package org.eclipse.wst.common.modulecore;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.modulecore.impl.PlatformURLModuleConnection;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ModuleEditModel extends EditModel {
	
	private final URI moduleURI;
	private final IPath modulePath;
	
	public ModuleEditModel(String anEditModelId, EMFWorkbenchContext aContext, boolean toMakeReadOnly, URI aModuleURI) {
		this(anEditModelId, aContext, toMakeReadOnly, true, aModuleURI); 
	}

	public ModuleEditModel(String anEditModelId, EMFWorkbenchContext aContext, boolean toMakeReadOnly, boolean toAccessUnknownResourcesAsReadOnly, URI aModuleURI) {
		super(anEditModelId, aContext, toMakeReadOnly, toAccessUnknownResourcesAsReadOnly);
		moduleURI = aModuleURI;
		modulePath = new Path(PlatformURLModuleConnection.MODULE_PROTOCOL + moduleURI.path());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.internal.emfworkbench.integration.EditModel#getResource(org.eclipse.emf.common.util.URI)
	 */
	public Resource getResource(URI aUri) { 

		IPath requestPath = modulePath.append(new Path(aUri.path()));
		URI resourceURI = URI.createURI(requestPath.toString());
		return super.getResource(resourceURI);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.internal.emfworkbench.integration.EditModel#getOrCreateResource(org.eclipse.emf.common.util.URI)
	 */
	public Resource getOrCreateResource(URI aUri) { 
		
		IPath requestPath = modulePath.append(new Path(aUri.path()));
		URI resourceURI = URI.createURI(requestPath.toString());
		return super.getOrCreateResource(resourceURI);
	}
	
	
}
