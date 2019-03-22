/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;

public class ResourceDependencyRegister {
	protected static Map GLOBAL_DEPENDENCIES = new HashMap();
	private static String RESOURCE_DEPENDENCY_TYPE = "ResourceDependencyAdapter"; //$NON-NLS-1$
	protected Map localDependencies = new HashMap();

	class ResourceDependencyAdapter extends AdapterImpl {
		Resource dependentResource;

		ResourceDependencyAdapter(Resource aDependentResource) {
			dependentResource = aDependentResource;
			dependentResource.eAdapters().add(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#isAdapterForType(java.lang.Object)
		 */
		@Override
		public boolean isAdapterForType(Object type) {
			return RESOURCE_DEPENDENCY_TYPE.equals(type);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
		 */
		@Override
		public void notifyChanged(Notification msg) {
			//Listen for unloads and removes
			switch (msg.getFeatureID(null)) {
				case Resource.RESOURCE__IS_LOADED :
					if (msg.getNotifier() != dependentResource && msg.getOldBooleanValue() && !msg.getNewBooleanValue())
						dependentResource.unload();
					break;
				case Resource.RESOURCE__RESOURCE_SET :
					if (msg.getOldValue() != null && msg.getNewValue() == null) {
						if (msg.getNotifier() == dependentResource)
							((Resource) getTarget()).eAdapters().remove(this);
						else {
							ResourceSet set = dependentResource.getResourceSet();
							if (set != null)
								set.getResources().remove(dependentResource);
						}
					}
					break;
			}
		}
	}

	class ResourceSetListener extends AdapterImpl {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
		 */
		@Override
		public void notifyChanged(Notification msg) {
			if (msg.getEventType() == Notification.ADD)
				proccessAddedResource((ResourceSet) msg.getNotifier(), (Resource) msg.getNewValue());
		}
	}

	/**
	 * Register a dependency between two URIs. The first parameter, aURIString, is the one that
	 * dependentUriString depends on.
	 * 
	 * @param aUriString
	 *            java.lang.String
	 * @param dependentUriString
	 *            java.lang.String
	 */
	public static void registerDependency(URI targetURI, URI dependentURI) {
		if (targetURI != null && dependentURI != null) {
			GLOBAL_DEPENDENCIES.put(dependentURI, targetURI);
		}
	}

	public ResourceDependencyRegister(ResourceSet aResourceSet) {
		initialize(aResourceSet);
	}


	ResourceDependencyRegister() {
		super();
	}

	/**
	 * @param aResourceSet
	 */
	protected void initialize(ResourceSet aResourceSet) {
		if (aResourceSet == null)
			throw new NullPointerException("The ResourceSet cannot be null."); //$NON-NLS-1$
		initializeLocalDependencies(aResourceSet);
		setupDependencyAdapters(aResourceSet);
		setupResourceSetListener(aResourceSet);
	}

	/**
	 * @param aResourceSet
	 */
	protected void setupResourceSetListener(ResourceSet aResourceSet) {
		aResourceSet.eAdapters().add(new ResourceSetListener());
	}

	/**
	 * @param aResourceSet
	 * @return
	 */
	protected void setupDependencyAdapters(ResourceSet aResourceSet) {
		if (!aResourceSet.getResources().isEmpty()) {
			Iterator it = localDependencies.entrySet().iterator();
			Map.Entry entry;
			URI dependentURI, targetURI;
			while (it.hasNext()) {
				entry = (Map.Entry) it.next();
				dependentURI = (URI) entry.getKey();
				targetURI = (URI) entry.getValue();
				setupDependencyAdapter(aResourceSet, dependentURI, targetURI);
			}
		}
	}

	/**
	 * @param aResourceSet
	 * @param dependentURI
	 * @param targetURI
	 * @return
	 */
	protected void setupDependencyAdapter(ResourceSet aResourceSet, URI dependentURI, URI targetURI) {
		Resource dependent = aResourceSet.getResource(dependentURI, false);
		if (dependent != null)
			setupDependencyAdapter(dependent, targetURI, aResourceSet);
	}

	protected void setupDependencyAdapter(Resource dependent, URI targetURI, ResourceSet aResourceSet) {
		Resource target = aResourceSet.getResource(targetURI, false);
		if (target == null)
			target = aResourceSet.createResource(targetURI);
		target.eAdapters().add(new ResourceDependencyAdapter(dependent));
	}

	/**
	 * @param aResourceSet
	 */
	protected void initializeLocalDependencies(ResourceSet aResourceSet) {
		URIConverter converter = aResourceSet.getURIConverter();
		initializeLocalDependencies(converter);
	}

	/**
	 * @param aResourceSet
	 */
	protected void initializeLocalDependencies(URIConverter aConverter) {
		Iterator it = GLOBAL_DEPENDENCIES.entrySet().iterator();
		Map resolved = new HashMap();
		Map.Entry entry;
		URI key, value;
		while (it.hasNext()) {
			entry = (Map.Entry) it.next();
			key = (URI) entry.getKey();
			value = (URI) entry.getValue();
			key = normalize(key, aConverter, resolved);
			value = normalize(value, aConverter, resolved);
			localDependencies.put(key, value);
		}
	}

	/**
	 * @param relativeURI
	 * @param converter
	 * @param resolved
	 * @return
	 */
	protected URI normalize(URI relativeURI, URIConverter converter, Map resolved) {
		URI result = (URI) resolved.get(relativeURI);
		if (result == null) {
			result = converter.normalize(relativeURI);
			resolved.put(relativeURI, result);
		}
		return result;
	}

	/**
	 * @param set
	 * @param resource
	 */
	protected void proccessAddedResource(ResourceSet set, Resource resource) {
		URI targetURI = (URI) localDependencies.get(resource.getURI());
		if (targetURI != null)
			setupDependencyAdapter(resource, targetURI, set);
	}
}