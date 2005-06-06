/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.internet.cache.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverExtension;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;

/**
 * A cache URI resolver. This resolver will cache remote resources and return
 * the local copy if they can be cached. If a resource cannot be cached the
 * resource returns null.
 */
public class CacheURIResolverExtension implements URIResolverExtension 
{
	/**
	 * @see org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverExtension#resolve(org.eclipse.core.resources.IProject, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String resolve(IProject project, String baseLocation, String publicId, String systemId)
	{ 
    if(CachePlugin.getDefault().isCacheEnabled())
    {
		  String resource = null;
		  if(systemId != null)
		  {
		    resource = URIHelper.normalize(systemId, baseLocation, null);
		  } 
		
		  if(resource != null && (resource.startsWith("http:") || resource.startsWith("ftp:")))
		  {
		    return Cache.getInstance().getResource(resource);
		  }
    }
		return null;
	  }
}
