/*******************************************************************************
 * Copyright (c) 2003, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.URIMappingRegistryImpl;
import org.eclipse.jem.util.emf.workbench.ResourceSetWorkbenchSynchronizer;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.internal.emfworkbench.CompatibilityWorkbenchURIConverterImpl;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;

public class ComponentCoreURIConverter extends CompatibilityWorkbenchURIConverterImpl {
	
	private IProject containingProject;
//	private IFolder archiveRoot;
	/**
	 *  
	 */
	public ComponentCoreURIConverter() {
		super();
	}

	/**
	 * @param anInputContainer
	 */
	public ComponentCoreURIConverter(IProject aContainingProject) {
		super();
		containingProject = aContainingProject;
	}

	/**
	 * @param aContainer
	 * @param aSynchronizer
	 */
	public ComponentCoreURIConverter(IProject aContainingProject, ResourceSetWorkbenchSynchronizer aSynchronizer) {
		super(aContainingProject, aSynchronizer);
		containingProject = aContainingProject;
	} 
 
	public IVirtualComponent findComponent(IResource res) {
		
		if (res != null && res.exists())
			return ComponentCore.createComponent(res.getProject());
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverterImpl#normalize(org.eclipse.emf.common.util.URI)
	 */
	public URI normalize(URI aURI) {
		URI normalizedURI = null;
		if(PlatformURLModuleConnection.MODULE.equals(aURI.scheme())) { 		
			try {
				normalizedURI = PlatformURLModuleConnection.resolve(aURI);
			} catch(IOException e) {
				ModulecorePlugin.logError(e);
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
//			String componentName = StructureEdit.getDeployedName(aNewURI);
			IVirtualComponent component = ComponentCore.createComponent(containingProject);

			URI deployPathSegment = ModuleURIUtil.trimToDeployPathSegment(aNewURI);
			
			//IVirtualFile newFile = component.getFile(new Path(deployPathSegment.path()));			
			IVirtualFolder rootFolder = component.getRootFolder();
			IVirtualFile newFile = rootFolder.getFile(new Path(deployPathSegment.path()));
			
			return URI.createPlatformResourceURI(newFile.getWorkspaceRelativePath().toString());
			 
		} catch(Exception e) {
			ModulecorePlugin.logError(e);
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
				IVirtualComponent component = ComponentCore.createComponent(getContainingProject());
				
				if (component != null) {
					IProject fileProject = getContainingProject();
					
					if (resourceSetSynchronizer.getProject() == fileProject){
						List list = Arrays.asList(component.getRootFolder().getUnderlyingFolders());
						IPath path = WorkbenchResourceHelperBase.getPathFromContainers(list, aFile.getFullPath());
						if (path != null)
							return URI.createURI(path.toString());
						return null;
					}
				} else
					return super.deNormalize(uri);
			}
		}
		return uri;
	}
	
	protected URI getContainerRelativeURI(IFile aFile) {
		IPath path = WorkbenchResourceHelperBase.getPathFromContainers(inputContainers, aFile.getFullPath());
		if (path != null)
			return URI.createURI(path.toString());
		return null;
	}
	protected URI getArchiveRelativeURI(IFile aFile, IContainer aContainer) {
		IPath path = WorkbenchResourceHelperBase.getPathFromContainers(Collections.singletonList(aContainer), aFile.getFullPath());
		if (path != null)
			return URI.createURI(path.toString());
		return null;
	}

	
	
	protected IProject getContainingProject() {
		return containingProject;
	}

	@Override
	protected URIMap getInternalURIMap() {

	    if (uriMap == null)
	    {
	      URIMappingRegistryImpl mappingRegistryImpl = 
	        new URIMappingRegistryImpl()
	        {
	          private static final long serialVersionUID = 1L;

	          @Override
	          protected URI delegatedGetURI(URI uri)
	          {
	        	if (ModuleURIUtil.hasContentTypeName(uri))
	        		return newPlatformURI(uri);
	            return URIMappingRegistryImpl.INSTANCE.getURI(uri);
	          }
	        };

	      uriMap = (URIMap)mappingRegistryImpl.map();
	    }

	    URIMap uriConverterImplURIMap = null;
	    if (uriMap instanceof URIMap)
	    {
	    	uriConverterImplURIMap = (URIMap)uriMap;
	    }
	    return uriConverterImplURIMap;
	  
	}


}
