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
package org.eclipse.wst.common.modulecore.internal.impl;

import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.internal.emfworkbench.CompatibilityWorkbenchURIConverterImpl;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;

import org.eclipse.jem.util.emf.workbench.ResourceSetWorkbenchSynchronizer;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ModuleCoreURIConverter extends CompatibilityWorkbenchURIConverterImpl {
	/**
	 *  
	 */
	public ModuleCoreURIConverter() {
		super();
	}

	/**
	 * @param anInputContainer
	 */
	public ModuleCoreURIConverter(IContainer anInputContainer) {
		super(anInputContainer);
	}

	/**
	 * @param aContainer
	 * @param aSynchronizer
	 */
	public ModuleCoreURIConverter(IContainer aContainer, ResourceSetWorkbenchSynchronizer aSynchronizer) {
		super(aContainer, aSynchronizer);
	}

	/**
	 * @param anInputContainer
	 * @param anOutputContainer
	 */
	public ModuleCoreURIConverter(IContainer anInputContainer, IContainer anOutputContainer) {
		super(anInputContainer, anOutputContainer);
	}

	/**
	 * @param anInputContainer
	 * @param anOutputContainer
	 * @param aSynchronizer
	 */
	public ModuleCoreURIConverter(IContainer anInputContainer, IContainer anOutputContainer, ResourceSetWorkbenchSynchronizer aSynchronizer) {
		super(anInputContainer, anOutputContainer, aSynchronizer);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverterImpl#normalize(org.eclipse.emf.common.util.URI)
	 */
	public URI normalize(URI aURI) {
		URI normalizedURI = null;
		if(PlatformURLModuleConnection.MODULE.equals(aURI.scheme())) { 		
			try {
				normalizedURI = PlatformURLModuleConnection.resolve(aURI);
			} catch(IOException ioe) {} 
		} else {
			normalizedURI = super.normalize(aURI);
		}
		if(normalizedURI == null || normalizedURI.scheme() == null || normalizedURI.scheme().length() == 0) {
			normalizedURI = URI.createPlatformResourceURI(getInputContainer().getFullPath().append(normalizedURI.toString()).toString());
		}
		return normalizedURI;
	}

	/**
	 * @see com.ibm.etools.xmi.helpers.CompatibilityURIConverter#deNormalize(URI)
	 */
	public URI deNormalize(URI uri) {
		if (WorkbenchResourceHelperBase.isPlatformResourceURI(uri)) {
			IFile aFile = WorkbenchResourceHelper.getPlatformFile(uri);
			if (aFile != null) {
				IProject fileProject = aFile.getProject();
				//If it is not in the same project then just return the URI as is.
				if (resourceSetSynchronizer.getProject() == fileProject)
					return getContainerRelativeURI(aFile);
			}
		}
		return uri;
	}

}
