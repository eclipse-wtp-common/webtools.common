/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: ProjectResourceSetImpl.java,v $$
 *  $$Revision: 1.4 $$  $$Date: 2005/01/26 18:45:51 $$ 
 */
package org.eclipse.jem.internal.util.emf.workbench;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.resource.impl.URIConverterImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.jem.util.emf.workbench.ProjectResourceSet;
import org.eclipse.jem.util.emf.workbench.ResourceHandler;
import org.eclipse.jem.util.emf.workbench.ResourceSetWorkbenchSynchronizer;
import org.eclipse.jem.util.emf.workbench.nature.EMFNature;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jem.util.plugin.JEMUtilPlugin;

public class ProjectResourceSetImpl extends ResourceSetImpl implements ProjectResourceSet {
	private boolean isReleasing = false;
	private IProject project;
	protected List resourceHandlers = new ArrayList();
	protected ResourceSetWorkbenchSynchronizer synchronizer;
	protected ProjectResourceSetImpl() {
		HashMap resourceMap = new HashMap(10);
		resourceMap.put(XMLResource.OPTION_USE_PARSER_POOL, EMFNature.SHARED_PARSER_POOL);
		loadOptions = resourceMap;	// Tell it to cache uri->resource access.
	}
	public ProjectResourceSetImpl(IProject aProject) {
		this();
		setProject(aProject);
		initializeSharedCacheListener();
	}
	protected void initializeSharedCacheListener() {
		JEMUtilPlugin.getSharedCache().beginListening(this);
	}
	protected boolean isReleasing() {
		return isReleasing;
	}
	/**
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#delegatedGetResource(URI, boolean)
	 */
	protected Resource delegatedGetResource(URI uri, boolean loadOnDemand) {
		Resource res = super.delegatedGetResource(uri, loadOnDemand);
		if (res == null)
			res = getResourceFromHandlers(uri);
		return res;
	}
	public Resource createResource(URI uri) {
		if (isReleasing) return null;
		//Check the map first when creating the resource and do not
		//normalize if a value is found.
		boolean isMapped = !(((URIConverterImpl.URIMap)getURIConverter().getURIMap()).getURI(uri).equals(uri));
		URI converted = uri;
		if (!isMapped)
			converted = getURIConverter().normalize(uri);
		Resource result = createResourceFromHandlers(converted);
		if (result == null)
			result = super.createResource(converted);
		
		return result;
	}
	/**
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#demandLoad(Resource)
	 */
	protected void demandLoad(Resource resource) throws IOException {
		if (!isReleasing)
			super.demandLoad(resource);
	}
	
	/**
	 * See if any resource handlers from the WorkbenchContext
	 * decide to create the Resource in another manner.
	 */
	protected Resource createResourceFromHandlers(URI uri) {
		Resource resource = null;
		ResourceHandler handler = null;
		for (int i = 0; i < resourceHandlers.size(); i++) {
			handler = (ResourceHandler) resourceHandlers.get(i);
			resource = handler.createResource(this, uri);
			if (resource != null)
				return resource;
		}
		return null;
	}
	/**
	 * See if any resource handlers from the WorkbenchContext
	 * can return a Resource from a <code>uri</code>.
	 */
	protected Resource getResourceFromHandlers(URI uri) {
		if (isReleasing) return null;
		for (int i = 0; i < resourceHandlers.size(); i++) {
			Resource resource = ((ResourceHandler) resourceHandlers.get(i)).getResource(this, uri);
			if (resource != null)
				return resource;
		}
		return null;
	}
	public void release() {
		setIsReleasing(true);
		if (synchronizer != null)
			synchronizer.dispose();
		synchronizer = null;
		removeAndUnloadAllResources();
		resourceHandlers = null;
		eAdapters().clear();
		setProject(null);
		JEMUtilPlugin.getSharedCache().stopListening(this);
	}
	protected void removeAndUnloadAllResources() {
		boolean caughtException = false;
		if (getResources().isEmpty()) return;
		List list = new ArrayList(getResources());
		getResources().clear();
		Resource res;
		int size = list.size();
		for (int i = 0; i < size; i++) {
			res = (Resource) list.get(i);
			try {
				res.unload();
			} catch (RuntimeException ex) {
				Logger.getLogger().logError(ex);
				caughtException = true;
			}
		}
		if (caughtException)
			throw new RuntimeException("Exception(s) unloading resources - check log files"); //$NON-NLS-1$
	}
	protected void setIsReleasing(boolean aBoolean) {
		isReleasing = aBoolean;
	}
	/**
	 * Gets the project.
	 * @return Returns a IProject
	 */
	public IProject getProject() {
		return project;
	}
	/**
	 * Sets the project.
	 * @param project The project to set
	 */
	protected void setProject(IProject project) {
		this.project = project;
	}
	/*
	 * Javadoc copied from interface.
	 */
	public EObject getEObject(URI uri, boolean loadOnDemand) {
		if (isReleasing) return null;
		Resource resource = getResource(uri.trimFragment(), loadOnDemand);
		EObject result = null;
		if (resource != null && resource.isLoaded())
			result = resource.getEObject(uri.fragment());
		if (result == null)
			result = getEObjectFromHandlers(uri, loadOnDemand);
		return result;
	}
	/**
	 * See if any resource handlers from the WorkbenchContext
	 * can return a EObject from a <code>uri</code> after
	 * failing to find it using the normal mechanisms.
	 */
	protected EObject getEObjectFromHandlers(URI uri, boolean loadOnDemand) {
		EObject obj = null;
		ResourceHandler handler = null;
		for (int i = 0; i < resourceHandlers.size(); i++) {
			handler = (ResourceHandler) resourceHandlers.get(i);
			obj = handler.getEObjectFailed(this, uri, loadOnDemand);
			if (obj != null)
				return obj;
		}
		return null;
	}
	
	public boolean add(ResourceHandler resourceHandler) {
		return resourceHandlers.add(resourceHandler);
	}
	public void addFirst(ResourceHandler resourceHandler) {
		resourceHandlers.add(0, resourceHandler);
	}
	public boolean remove(ResourceHandler resourceHandler) {
		return resourceHandlers.remove(resourceHandler);
	}
	/**
	 * Returns the synchronizer.
	 * @return ResourceSetWorkbenchSynchronizer
	 */
	public ResourceSetWorkbenchSynchronizer getSynchronizer() {
		return synchronizer;
	}
	/**
	 * Sets the synchronizer.
	 * @param synchronizer The synchronizer to set
	 */
	public void setSynchronizer(ResourceSetWorkbenchSynchronizer synchronizer) {
		this.synchronizer = synchronizer;
	}
	/**
	 * @see org.eclipse.emf.ecore.resource.ResourceSet#setResourceFactoryRegistry(Registry)
	 */
	public void setResourceFactoryRegistry(Resource.Factory.Registry factoryReg) {
		if (resourceFactoryRegistry != null && factoryReg != null) {
			preserveEntries(factoryReg.getExtensionToFactoryMap(), resourceFactoryRegistry.getExtensionToFactoryMap());
			preserveEntries(factoryReg.getProtocolToFactoryMap(), resourceFactoryRegistry.getProtocolToFactoryMap());
		}
		super.setResourceFactoryRegistry(factoryReg);
	}
	/*
	 * Preserve the entries from map2 in map1 if no collision.
	 */
	protected void preserveEntries(Map map1, Map map2) {
		if (map2.isEmpty())
			return;
		Iterator it = map2.entrySet().iterator();
		Map.Entry entry;
		while (it.hasNext()) {
			entry = (Map.Entry) it.next();
			if (!map1.containsKey(entry.getKey()))
				map1.put(entry.getKey(), entry.getValue());
		}
	}
	/*
	 * Javadoc copied from interface.
	 */
	public Resource getResource(URI uri, boolean loadOnDemand) {
		if (isReleasing) return null;
		return super.getResource(uri, loadOnDemand);
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jem.util.emf.workbench.ProjectResourceSet#resetNormalizedURICache()
	 */
	public void resetNormalizedURICache() {
		if (getURIResourceMap() != null)
			getURIResourceMap().clear();
	}

}
