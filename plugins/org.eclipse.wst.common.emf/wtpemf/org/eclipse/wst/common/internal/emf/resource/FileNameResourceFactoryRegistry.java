/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.wst.common.internal.emf.utilities.DefaultOverridableResourceFactoryRegistry;


public abstract class FileNameResourceFactoryRegistry extends DefaultOverridableResourceFactoryRegistry {

	private Map/*<String shortName, ResourceFactoryDescriptor>*/ descriptors = new HashMap(); 
	private Map/*<ResourceFactoryDescriptor, Resource.Factory>*/ factories = new HashMap();
	
	public FileNameResourceFactoryRegistry() {
		super();
	}

	/**
	 * Return a Resource.Factory that is registered with the last segment of the URI's file name.
	 * 
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl#getFactory(URI)
	 */
	protected Object getFileNameFactory(URI uri) {

		if(uri != null) {
			ResourceFactoryDescriptor descriptor = getDescriptor(uri);
			
			if(descriptor != null) {
				return getFactory(descriptor);	
			}	
		}
		return null;
		
	}

	public synchronized Resource.Factory getFactory(URI uri) {
		return getFactory(uri, null);
	}

	public Resource.Factory getFactory(URI uri, String contentType)
	{
		Resource.Factory resourceFactory = null;
		if(uri != null && uri.lastSegment() != null) {
			ResourceFactoryDescriptor descriptor = getDescriptor(uri);
			
			if(descriptor != null) {
				resourceFactory = getFactory(descriptor);	
			}	
		}
		if(resourceFactory == null)
			resourceFactory = super.getFactory(uri, contentType);
		return resourceFactory; 
	}

	/**
	 * Register a file name representing the last segment of a URI with the corresponding
	 * Resource.Factory.
	 */
	public synchronized void registerLastFileSegment(String aSimpleFileName, Resource.Factory aFactory) {
		URI uri = URI.createURI(aSimpleFileName);
		String lastSegment = uri.lastSegment(); 
		addDescriptor(new StaticResourceFactoryDescriptor(lastSegment, aFactory));		
	} 

	protected synchronized ResourceFactoryDescriptor getDescriptor(URI uri) {
		return (ResourceFactoryDescriptor) descriptors.get(uri.lastSegment());
	}
	protected synchronized Map getDescriptors() {
		return descriptors;
	}

	protected final synchronized Resource.Factory getFactory(ResourceFactoryDescriptor descriptor) {  
		Resource.Factory factory = (Factory) factories.get(descriptor);
		if(factory == null) {
			factories.put(descriptor, (factory = descriptor.createFactory()));
		}
		return factory;
	}

	protected void addDescriptor(ResourceFactoryDescriptor descriptor) { 
		descriptors.put(descriptor.getShortSegment(), descriptor);
	}
 
}
