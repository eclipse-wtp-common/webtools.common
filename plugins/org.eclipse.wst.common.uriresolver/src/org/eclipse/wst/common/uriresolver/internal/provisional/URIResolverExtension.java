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
package org.eclipse.wst.common.uriresolver.internal.provisional;

import org.eclipse.core.resources.IFile;

/**
 * An extension to augment the behaviour of a URIResolver.  Extensions are project aware
 * so that they can apply specialized project specific resolving rules. 
 */
public interface URIResolverExtension {
	/**
	 * @param file the in-workspace base resource, if one exists
	 * @param baseLocation - the location of the resource that contains the uri
	 * @param publicId - an optional public identifier (i.e. namespace name), or null if none
	 * @param systemId - an absolute or relative URI, or null if none 
	 * 
	 * @return an absolute URI or null if this extension can not resolve this reference
	 */
	public String resolve(IFile file, String baseLocation, String publicId, String systemId);
}
