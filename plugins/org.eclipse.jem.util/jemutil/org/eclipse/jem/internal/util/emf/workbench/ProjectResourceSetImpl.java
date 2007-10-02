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
 *  $$RCSfile: ProjectResourceSetImpl.java,v $$
 *  $$Revision: 1.12 $$  $$Date: 2007/10/02 12:57:59 $$ 
 */
package org.eclipse.jem.internal.util.emf.workbench;

import java.io.IOException;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.resource.impl.URIConverterImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;

import org.eclipse.jem.util.emf.workbench.*;
import org.eclipse.jem.util.emf.workbench.nature.EMFNature;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jem.util.plugin.JEMUtilPlugin;

public class ProjectResourceSetImpl extends ResourceSetImpl implements FlexibleProjectResourceSet {
	public static interface ModuleURI {
		public static final int SUB_PROTOCOL_INDX = 0;
		public static final int PROJECT_NAME_INDX = 1;
		public static final int MODULE_NAME_INDX = 2;
		public static final int CONTENT_TYPE_INDX = 3;
	}

	private boolean isReleasing = false;
	private IProject project;
	protected List resourceHandlers = new ArrayList();
	protected ResourceSetWorkbenchSynchronizer synchronizer;
	protected ProjectResourceSetImpl() {
		setURIResourceMap(new HashMap(10));	// Tell it to cache uri->resource access.
		getLoadOptions().put(XMLResource.OPTION_USE_PARSER_POOL, EMFNature.SHARED_PARSER_POOL);
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
		boolean isMapped = detectURIMapping(uri);
		boolean hasContentType = (getContentTypeName(uri) != null);
		URI converted = uri;
		if (!isMapped)
			converted = getURIConverter().normalize(uri);
		else if (hasContentType)
			converted = getURIConverter().normalize(uri);
		
		Resource result = createResourceFromHandlers(converted);
		if (result == null) {
		    Resource.Factory resourceFactory = getResourceFactoryRegistry().getFactory(uri);
		    if (resourceFactory != null)
		    {//We got the right factory, now use the right URI
		      result = resourceFactory.createResource(converted);
		      getResources().add(result);
		    }
		}
			
		
		return result;
	}
	private boolean detectURIMapping(URI uri) {
		return !(((URIConverterImpl.URIMap)getURIConverter().getURIMap()).getURI(uri).equals(uri));
	}
	/**
	 * Return the IFile for the <code>uri</code> within the Workspace. This URI is assumed to be
	 * absolute in the following format: platform:/resource/....
	 */
	private IFile getPlatformFile(URI uri) {
		if (WorkbenchResourceHelperBase.isPlatformResourceURI(uri)) {
			String fileString = URI.decode(uri.path());
			fileString = fileString.substring(JEMUtilPlugin.PLATFORM_RESOURCE.length() + 1);
			return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileString));
		}
		return null;
	}
	public Resource createResource(URI uri, Resource.Factory resourceFactory) {
		if (isReleasing) return null;
		//Check the map first when creating the resource and do not
		//normalize if a value is found.
		boolean isMapped = detectURIMapping(uri);
		boolean hasContentType = (getContentTypeName(uri) != null);
		URI converted = uri;
		if (!isMapped)
			converted = getURIConverter().normalize(uri);
		else if (hasContentType)
			converted = getURIConverter().normalize(uri);
		Resource result = createResourceFromHandlers(converted);
		if (result == null) {

		    if (resourceFactory != null)
		    {
		      result = resourceFactory.createResource(converted);
		      getResources().add(result);
		      getURIResourceMap().put(uri, result);
		      return result;
		    }
		    else
		    {
		      return null;
		    }
		  
		}
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
		// Send out notification of release.
		if (eNotificationRequired()) {
			eNotify(new NotificationImpl(SPECIAL_NOTIFICATION_TYPE, null, null, Notification.NO_INDEX, false) {
				/* (non-Javadoc)
				 * @see org.eclipse.emf.common.notify.impl.NotificationImpl#getFeatureID(java.lang.Class)
				 */
				public int getFeatureID(Class expectedClass) {
					return PROJECTRESOURCESET_ABOUT_TO_RELEASE_ID;
				}
				
				/* (non-Javadoc)
				 * @see org.eclipse.emf.common.notify.impl.NotificationImpl#getNotifier()
				 */
				public Object getNotifier() {
					return ProjectResourceSetImpl.this;
				}
			});
		}
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
	 * @see org.eclipse.emf.ecore.resource.ResourceSet#setResourceFactoryRegistry(Resource.Factory.Registry)
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

	    Map<URI, Resource> map = getURIResourceMap();
	    if (map != null)
	    {
	      Resource resource = map.get(uri);
	      if (resource != null)
	      {
	        if (loadOnDemand && !resource.isLoaded())
	        {
	          demandLoadHelper(resource);
	        }        
	        return resource;
	      }
	    }
	    
	    URIConverter theURIConverter = getURIConverter();
	    URI normalizedURI = theURIConverter.normalize(uri);
	    for (Resource resource : getResources())
	    {
	      if (theURIConverter.normalize(resource.getURI()).equals(normalizedURI)) {
	    		  
	    	if (getContentTypeName(uri) == null) { // loading from legacy archive api or non-typed resource
		        if (loadOnDemand && !resource.isLoaded())
		        {
		          demandLoadHelper(resource);
		        }
		        
		        if (map != null)
		        {
		          map.put(uri, resource);
		        } 
		        return resource;
	    	} else  {// content type is known
	    		if((!map.containsValue(resource) || ((map.get(uri) != null) && map.get(uri).equals(resource))) // existing resource  with alternate mapping doesn't exist in map
	    			|| getContentTypeName(findKey(resource)) == null || ((getContentTypeName(resource) != null && getContentTypeName(resource).equals(getContentTypeName(uri))))) {
	    		if (loadOnDemand && !resource.isLoaded())
		        {
		          demandLoadHelper(resource);
		        }
		        
		        if (map != null && (map.get(uri) == null))
		        {
		          map.put(uri, resource);
		        } 
		        return resource;
	    		}
	    	} //return null;  // Returning null because We can't handle multiple resources based on the same DOM model
	      }
	    }
	    
	    Resource delegatedResource = delegatedGetResource(uri, loadOnDemand);
	    if (delegatedResource != null)
	    {
	      if (map != null)
	      {
	        map.put(uri, delegatedResource);
	      }
	      return delegatedResource;
	    }

	    if (loadOnDemand)
	    {
	      Resource resource = demandCreateResource(uri);
	      if (resource == null)
	      {
	        throw new RuntimeException("Cannot create a resource for '" + uri + "'; a registered resource factory is needed");
	      }

	      demandLoadHelper(resource);

	      if (map != null)
	      {
	        map.put(uri, resource);
	      }      
	      return resource;
	    }

	    return null;
	  
	}
	private IFile getPlatformFile(Resource res) {
		IFile file = null;
		file = getPlatformFile(res.getURI());
		if (file == null) {
			if (res.getResourceSet() != null) {
				URIConverter converter = res.getResourceSet().getURIConverter();
				URI convertedUri = converter.normalize(res.getURI());
				if (!res.getURI().equals(convertedUri))
					file = getPlatformFile(convertedUri);
			}
		}
		return file;
	}
	
	private String getContentTypeName(Resource resource) {
		IFile file = getPlatformFile(resource);
		IContentDescription desc = null;
		try {
			desc = file.getContentDescription();
		} catch (CoreException e) {
		}
		if (desc != null && desc.getContentType() != null)
			return desc.getContentType().getName();
		return null;
	}
	private URI findKey(Resource resource) {
		Map aMap = getURIResourceMap();
		Set keys = aMap.keySet();
		for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
			URI name = (URI) iterator.next();
			if (aMap.get(name).equals(resource))
				return name;
		}
		return null;
	}
	protected static String getContentTypeName(URI uri) {
		
		if (WorkbenchResourceHelperBase.isPlatformResourceURI(uri) || !isValidFullyQualifiedModuleURI(uri))
			return null;
		String contentTypeIdentifier = (uri.segmentCount() > 3 ? uri.segment(ModuleURI.CONTENT_TYPE_INDX) : null);
		if (contentTypeIdentifier != null && Platform.getContentTypeManager().getContentType(uri.segment(ModuleURI.CONTENT_TYPE_INDX)) != null)
			return contentTypeIdentifier;
		else
			return null;
	}
	public static boolean isValidFullyQualifiedModuleURI(URI aModuleURI) {
		if (aModuleURI.segmentCount() < 3) {
			return false;
		}
		return true;
	}
	/*
	 * Javadoc copied from interface.
	 */
	public Resource getResource(URI uri, boolean loadOnDemand, Resource.Factory resourceFactory) {
		if (isReleasing) return null;
		

	    Map<URI, Resource> map = getURIResourceMap();
	    if (map != null)
	    {
	      Resource resource = map.get(uri);
	      if (resource != null)
	      {
	        if (loadOnDemand && !resource.isLoaded())
	        {
	          demandLoadHelper(resource);
	        }        
	        return resource;
	      }
	    }
	    
	    URIConverter theURIConverter = getURIConverter();
	    URI normalizedURI = theURIConverter.normalize(uri);
	    for (Resource resource : getResources())
	    {
	      if (theURIConverter.normalize(resource.getURI()).equals(normalizedURI))
	      {
	        if (loadOnDemand && !resource.isLoaded())
	        {
	          demandLoadHelper(resource);
	        }
	        
	        if (map != null)
	        {
	          map.put(uri, resource);
	        } 
	        return resource;
	      }
	    }
	    
	    Resource delegatedResource = delegatedGetResource(uri, loadOnDemand);
	    if (delegatedResource != null)
	    {
	      if (map != null)
	      {
	        map.put(uri, delegatedResource);
	      }
	      return delegatedResource;
	    }

	    if (loadOnDemand)
	    {
	      Resource resource = demandCreateResource(uri,resourceFactory);
	      if (resource == null)
	      {
	        throw new RuntimeException("Cannot create a resource for '" + uri + "'; a registered resource factory is needed");
	      }

	      demandLoadHelper(resource);

	      if (map != null)
	      {
	        map.put(uri, resource);
	      }      
	      return resource;
	    }

	    return null;
	  
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jem.util.emf.workbench.ProjectResourceSet#resetNormalizedURICache()
	 */
	public void resetNormalizedURICache() {
		if (getURIResourceMap() != null)
			getURIResourceMap().clear();
	}
	
	protected Resource demandCreateResource(URI uri, Factory resourceFactory) {
		// TODO Auto-generated method stub
		return createResource(uri,resourceFactory);
	}

}
