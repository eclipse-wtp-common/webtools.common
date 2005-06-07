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
package org.eclipse.wst.common.uriresolver.internal;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverExtension;


/**
 * @author csalter
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ExtensibleURIResolver implements URIResolver
{

	//protected IProject project;

	//TODO... consider ctor that takes a project arg
	//public ExtensibleURIResolver(IProject project)
	//{
	//	this.project = project;
	//}

	public ExtensibleURIResolver()
	{
	}

	public String resolve(String baseLocation, String publicId, String systemId)
	{
		String result = systemId;

		// compute the project that holds the resource
		//
    IFile file = computeFile(baseLocation);
		IProject project =  file != null ? file.getProject() : null;
		String fileName = null; // todo.. get the file name for systemId

		URIResolverExtensionRegistry resolverRegistry = URIResolverExtensionRegistry.getIntance();
		List list = resolverRegistry.getExtensionDescriptors(project);

		// get the list of applicable pre-normalized resolvers from the
		// extension registry
		//
		for (Iterator i = resolverRegistry.getMatchingURIResolvers(list, URIResolverExtensionRegistry.STAGE_PRENORMALIZATION).iterator(); i.hasNext();)
		{
			URIResolverExtension resolver = (URIResolverExtension) i.next();
			String tempresult = resolver.resolve(file, baseLocation, publicId, result);
			if(tempresult != null)
			{
			  result = tempresult;
			}
		}

		// normalize the uri
		//
		result = normalize(baseLocation, result);

		// get the list of applicable post-normalized resolvers from the
		// extension registry
		//		
		for (Iterator i = resolverRegistry.getMatchingURIResolvers(list, URIResolverExtensionRegistry.STAGE_POSTNORMALIZATION).iterator(); i.hasNext();)
		{
			URIResolverExtension resolver = (URIResolverExtension) i.next();
			String tempresult = resolver.resolve(file, baseLocation, publicId, result);
			if(tempresult != null)
			{
			  result = tempresult;
			}
		}

		return result;
	}

	protected String normalize(String baseLocation, String systemId)
	{
	  // If no systemId has been specified there is nothing to do
	  // so return null;
	  if(systemId == null)
	    return null;
		String result = systemId;
		// normalize the URI
		URI systemURI = URI.createURI(systemId);
		if (systemURI.isRelative())
		{
			URI baseURI = URI.createURI(baseLocation);
			try
			{
			  result = systemURI.resolve(baseURI).toString();
			}
			catch(IllegalArgumentException e)
			{}
			
		}
		return result;
	}

  protected IFile computeFile(String baseLocation)
  {
    IFile file = null;
    if (baseLocation != null)
    {
      String pattern = "file:///";
      if (baseLocation.startsWith(pattern))
      {
        baseLocation = baseLocation.substring(pattern.length());
      }
      IPath path = new Path(baseLocation);
      file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
    }
    return file;    
  }
}
