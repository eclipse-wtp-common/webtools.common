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

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.modulecore.impl.PlatformURLModuleConnection;
import org.eclipse.wst.common.modulecore.impl.UnresolveableURIException;
import org.eclipse.wst.common.modulecore.util.ModuleCore;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ArtifactEditModel extends EditModel {

	private final URI moduleURI;
	private final IPath modulePath;

	public ArtifactEditModel(String anEditModelId, EMFWorkbenchContext aContext, boolean toMakeReadOnly, URI aModuleURI) {
		this(anEditModelId, aContext, toMakeReadOnly, true, aModuleURI);
	}

	public ArtifactEditModel(String anEditModelId, EMFWorkbenchContext aContext, boolean toMakeReadOnly, boolean toAccessUnknownResourcesAsReadOnly, URI aModuleURI) {
		super(anEditModelId, aContext, toMakeReadOnly, toAccessUnknownResourcesAsReadOnly);
		moduleURI = aModuleURI;
		modulePath = new Path(moduleURI.path());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emfworkbench.integration.EditModel#getResource(org.eclipse.emf.common.util.URI)
	 */
	public Resource getResource(URI aUri) {
		IPath requestPath = modulePath.append(new Path(aUri.path()));
		URI resourceURI = URI.createURI(requestPath.toString());
		return super.getResource(resourceURI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emfworkbench.integration.EditModel#getOrCreateResource(org.eclipse.emf.common.util.URI)
	 */
	public Resource getOrCreateResource(URI aUri) {

		IPath requestPath = modulePath.append(new Path(aUri.path()));
		URI resourceURI = URI.createURI(requestPath.toString());
		return super.getOrCreateResource(resourceURI);
	}

	protected boolean processResourcesIfInterrested(List someResources) {
		int size = someResources.size();
		Resource resourceToProcess;
		boolean processed = false;
		ModuleStructuralModel structuralModel = null;
		try {
			structuralModel = ModuleCore.INSTANCE.getModuleStructuralModelForRead(moduleURI, this);
			WorkbenchModuleResource[] relevantModuleResources = null;
			for (int i = 0; i < size; i++) {
				resourceToProcess = (Resource) someResources.get(i);
				relevantModuleResources = ModuleCore.INSTANCE.findWorkbenchModuleResourcesBySourcePath(structuralModel, resourceToProcess.getURI());
				for(int resourcesIndex=0; resourcesIndex < relevantModuleResources.length; resourcesIndex++) {
					if(moduleURI.equals(relevantModuleResources[resourcesIndex].getModule().getHandle())) {
						processResource(resourceToProcess);
						processed = true;
					}
				}
			}
		} catch (UnresolveableURIException uurie) {

		} finally {
			if (structuralModel != null)
				structuralModel.releaseAccess(this);
		}
		return processed;
	}
}
