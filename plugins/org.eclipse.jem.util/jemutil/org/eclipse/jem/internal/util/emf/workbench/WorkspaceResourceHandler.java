/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: WorkspaceResourceHandler.java,v $$
 *  $$Revision: 1.2 $$  $$Date: 2005/02/15 23:04:14 $$ 
 */
package org.eclipse.jem.internal.util.emf.workbench;

import org.eclipse.core.resources.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.URIConverterImpl;

import org.eclipse.jem.util.emf.workbench.ResourceHandler;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.jem.util.plugin.JEMUtilPlugin;

/**
 * The main purpose of this class is to redirect, if necessary, to another 
 * ResourceSet.  This class should be used in conjunction with the WorkbenchURIConverter
 * so that the URIs passed will use the platform protocol.  Anything else will be considered
 * to be ambiguous and we will not be able to redirect.
 */
public class WorkspaceResourceHandler implements ResourceHandler {
	/**
	 * Constructor for WorkspaceResourceHandler.
	 */
	public WorkspaceResourceHandler() {
		super();
	}
	/*
	* @see IResourceHandler#getResource(ResourceSet, URI)
	*/
	public Resource getResource(ResourceSet originatingResourceSet, URI uri) {
		if (WorkbenchResourceHelperBase.isPlatformResourceURI(uri))
			return getResourceForPlatformProtocol(originatingResourceSet, uri);
		URI mappedURI = ((URIConverterImpl.URIMap)originatingResourceSet.getURIConverter().getURIMap()).getURI(uri);
		if (isGlobalPluginLoad(mappedURI))
			return getResourceForPlatformPluginProtocol(originatingResourceSet, uri);
		return null;
	}
	/**
	 * Redirect to the correct project based on the project name in the <code>uri</code>.
	 * The <code>uri</code> will be in the following format:   platform:/resource/[project name].
	 */
	protected Resource createResourceForPlatformProtocol(ResourceSet originatingResourceSet, URI uri) {
		String projectName = uri.segment(1);
		IProject project = getProject(projectName);
		if (project != null && project.isAccessible()) {
			ResourceSet set = WorkbenchResourceHelperBase.getResourceSet(project);
			if (originatingResourceSet != set)
				return createResource(uri, set);
		}
		return null;
	}
	/**
		 * Redirect to the correct project based on the project name in the <code>uri</code>.
		 * The <code>uri</code> will be in the following format:   platform:/resource/[project name].
		 */
	protected Resource createResourceForPlatformPluginProtocol(ResourceSet originatingResourceSet, URI uri) {
			
		ResourceSet set = JEMUtilPlugin.getPluginResourceSet();
		return createResource(uri, set);
		}
	protected Resource createResource(URI uri, ResourceSet redirectedResourceSet) {
		return redirectedResourceSet.createResource(uri);
	}
	/**
	 * Redirect to the correct project based on the first segment in the file name.
	 * This is for compatability purposes for people using the platform:/resource protocol.
	 */
	protected Resource getResourceForPlatformProtocol(ResourceSet originatingResourceSet, URI uri) {
		String projectName = uri.segment(1);
		IProject project = getProject(projectName);
		if (project != null && project.isAccessible()) {
			ResourceSet set = WorkbenchResourceHelperBase.getResourceSet(project);
			if (originatingResourceSet != set)
				return getResource(uri, set);
		}
		return null;
	}
	/**
		 * Redirect to the correct project based on the first segment in the file name.
		 * This is for compatability purposes for people using the platform:/resource protocol.
		 */
	protected Resource getResourceForPlatformPluginProtocol(ResourceSet originatingResourceSet, URI uri) {
			
		ResourceSet set = JEMUtilPlugin.getPluginResourceSet();
		return getResource(uri, set);
			
	}
	protected Resource getResource(URI uri, ResourceSet redirectedResourceSet) {
		return redirectedResourceSet.getResource(uri, false);
	}
	
	protected IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	protected IProject getProject(String projectName) {
		IWorkspace ws = getWorkspace();
		if (ws == null)
			return null;
		return ws.getRoot().getProject(projectName);
	}
	protected IProject getProject(ResourceSet resourceSet) {
		return WorkbenchResourceHelperBase.getProject(resourceSet);
	}
	/**
	 * @see org.eclipse.jem.util.ResourceHandler#createResource(ResourceSet, URI)
	 */
	public Resource createResource(ResourceSet originatingResourceSet, URI uri) {
		if (WorkbenchResourceHelperBase.isPlatformResourceURI(uri))
			return createResourceForPlatformProtocol(originatingResourceSet, uri);
		URI mappedURI = ((URIConverterImpl.URIMap)originatingResourceSet.getURIConverter().getURIMap()).getURI(uri);
		if (isGlobalPluginLoad(mappedURI))
			return createResourceForPlatformPluginProtocol(originatingResourceSet, uri);
		return null;
	}
	/**
	 * @see org.eclipse.jem.util.ResourceHandler#getEObjectFailed(ResourceSet, URI, boolean)
	 * Subclasses may override.
	 */
	public EObject getEObjectFailed(ResourceSet originatingResourceSet, URI uri, boolean loadOnDemand) {
		return null;
	}
	
	protected boolean isGlobalPluginLoad(URI aURI) {
		if (WorkbenchResourceHelperBase.isPlatformPluginResourceURI(aURI)) {
			String[] globalPlugins = JEMUtilPlugin.getGlobalLoadingPluginNames();
			for (int i=0;i<globalPlugins.length;i++) {
				if (aURI.segment(1).startsWith(globalPlugins[i]))
					return true;
			}
		}
		return false;
	}
}
