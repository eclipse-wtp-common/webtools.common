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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jem.util.emf.workbench.ResourceSetWorkbenchSynchronizer;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.wst.common.internal.emfworkbench.CompatibilityWorkbenchURIConverterImpl;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.resources.IVirtualContainer;
import org.eclipse.wst.common.modulecore.resources.IVirtualFile;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ModuleCoreURIConverter extends CompatibilityWorkbenchURIConverterImpl {
	
	private IProject containingProject;
	
	/**
	 *  
	 */
	public ModuleCoreURIConverter() {
		super();
	}

	/**
	 * @param anInputContainer
	 */
	public ModuleCoreURIConverter(IProject aContainingProject) {
		super();
		containingProject = aContainingProject;
	}

	/**
	 * @param aContainer
	 * @param aSynchronizer
	 */
	public ModuleCoreURIConverter(IProject aContainingProject, ResourceSetWorkbenchSynchronizer aSynchronizer) {
		super(aContainingProject, aSynchronizer);
		containingProject = aContainingProject;
	} 
 
	
	/* (non-Javadoc)
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverterImpl#normalize(org.eclipse.emf.common.util.URI)
	 */
	public URI normalize(URI aURI) {
		URI normalizedURI = null;
		if(PlatformURLModuleConnection.MODULE.equals(aURI.scheme())) { 		
			try {
				normalizedURI = PlatformURLModuleConnection.resolve(aURI);
			} catch(IOException ioe) {
				ioe.printStackTrace();
			} 
		} else {
			normalizedURI = super.normalize(aURI);
		}
		if(normalizedURI == null) {
			normalizedURI = newPlatformURI(aURI);
		}
		else if(normalizedURI.scheme() == null || normalizedURI.scheme().length() == 0) {
			normalizedURI = URI.createPlatformResourceURI(getInputContainer().getFullPath().append(normalizedURI.toString()).toString());	
		}
		return normalizedURI;
	}
	
	private URI newPlatformURI(URI aNewURI) {
		
		try {
			String componentName = ModuleCore.getDeployedName(aNewURI);
			IVirtualContainer component = ModuleCore.createContainer(containingProject, componentName);

			URI deployPathSegment = ModuleURIUtil.trimToDeployPathSegment(aNewURI);
			IVirtualFile newFile = component.getFile(new Path(deployPathSegment.path()));
			
			return URI.createPlatformResourceURI(newFile.getWorkspaceRelativePath().toString());
			 
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
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
