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
package org.eclipse.wst.common.internal.emf.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

public abstract class ResourceFactoryDescriptor {
	
	
	/**
	 * Returns true if the current descriptor is applicable to the given fileURI.
	 * 
	 * @param fileURI The URI of the file to be loaded
	 * @return True if the current descriptor declares a resource factory applicable to the given URI.
	 */
	public abstract boolean isEnabledFor(URI fileURI);
	

	/**
	 * The short segment is one possible way that a ResourceFactory
	 * might apply to a URI. Clients should call {@link isEnabledFor} 
	 * instead of comparing the short segments when searching for an
	 * applicable descriptor from a set.  
	 * 
	 * <p><b>Subclasses may NOT return null.</b></p>
	 *  
	 * @return The short segment that this descriptor is applicable to.
	 */
	public abstract String getShortSegment();
	
	
	/**
	 * The correct instance of Resource.Factory. The instance returned
	 * may or may not be unique, so if you require the same instance 
	 * for each call, you should cache the value returned the first
	 * time from this method. 
	 * 
	 * @return An instance of Resource.Factory.
	 */
	public abstract Resource.Factory createFactory();
	
	public int hashCode() {
		return getShortSegment().hashCode();
	}
	
	public boolean equals(Object o) {
		if(o instanceof ResourceFactoryDescriptor)
			return getShortSegment().equals(((ResourceFactoryDescriptor)o).getShortSegment());
		return false;
	}
}
