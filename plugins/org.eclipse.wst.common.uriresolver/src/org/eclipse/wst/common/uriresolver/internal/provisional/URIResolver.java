/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *******************************************************************************/
package org.eclipse.wst.common.uriresolver.internal.provisional;

/**
 * A URIResolver is used to resolve URI references to resources.
 */
public interface URIResolver {
	
	/**
	 * @param baseLocation - the location of the resource that contains the uri 
	 * @param publicId - an optional public identifier (i.e. namespace name), or null if none
	 * @param systemId - an absolute or relative URI, or null if none 
	 * @return an absolute URI representation of the 'logical' location of the resource
	 */
	public String resolve(String baseLocation, String publicId, String systemId);
    
    /**
     * @param baseLocation - the location of the resource that contains the uri 
     * @param publicId - an optional public identifier (i.e. namespace name), or null if none
     * @param systemId - an absolute or relative URI, or null if none 
     * @return an absolute URI representation of the 'physical' location of the resource
     */
    public String resolvePhysicalLocation(String baseLocation, String publicId, String logicalLocation);    
}
