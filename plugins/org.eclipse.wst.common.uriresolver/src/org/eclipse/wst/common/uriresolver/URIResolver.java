/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.common.uriresolver;

/**
 * A URIResolver is used to resolve URI references to resources.
 *  
 */
public interface URIResolver {
	
	/**
	 * @param baseLocation - the location of the resource that contains the uri 
	 * @param publicId - an optional public identifier (i.e. namespace name), or null if none
	 * @param systemId - an absolute or relative URI, or null if none 
	 * @return an absolute URI
	 */
	public String resolve(String baseLocation, String publicId, String systemId);
}
