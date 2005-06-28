/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *******************************************************************************/
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
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverInput;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverResult;


public class ExtensibleURIResolver implements URIResolver
{
	public ExtensibleURIResolver()
	{
	}
  
  public URIResolverResult resolve(URIResolverInput input)
  {

    URIResolverResult result = new URIResolverResult();
    result.setLogicalURI(input.getReferenceURI());
    
    // if the  input's baseURI exists outside of the workspace
    // the baseFile will be null here
    //
    IFile file = input.getBaseFile();    
    IProject project =  file != null ? file.getProject() : null;

    URIResolverExtensionRegistry resolverRegistry = URIResolverExtensionRegistry.getIntance();
    List list = resolverRegistry.getExtensionDescriptors(project, input.getReferenceType());

    try
    {    
    // get the list of applicable pre-normalized resolvers from the extension registry
    //
    for (Iterator i = resolverRegistry.getMatchingURIResolvers(list, URIResolverExtensionRegistry.STAGE_PRENORMALIZATION).iterator(); i.hasNext();)
    {
      URIResolverExtension resolver = (URIResolverExtension) i.next();
      resolver.resolve(input, result); 
    }

    // normalize the logical URI
    //
    if (result.getLogicalURI() != null)
    {  
      result.setLogicalURI(normalize(input.getBaseURI(), result.getLogicalURI()));
    }  

    // get the list of applicable post-normalized resolvers from the extension registry
    //    
    for (Iterator i = resolverRegistry.getMatchingURIResolvers(list, URIResolverExtensionRegistry.STAGE_POSTNORMALIZATION).iterator(); i.hasNext();)
    {
      URIResolverExtension resolver = (URIResolverExtension) i.next();
      resolver.resolve(input, result); 
    }
    
    // get the list of applicable physicalURI resolvers from the extension registry
    //    
    for (Iterator i = resolverRegistry.getMatchingURIResolvers(list, URIResolverExtensionRegistry.STAGE_PHYSICAL_RESOLUTION).iterator(); i.hasNext();)
    {
      URIResolverExtension resolver = (URIResolverExtension) i.next();
      resolver.resolve(input, result); 
    }

    // if no physical URI has been set we assume the physical URI is the same as the logical URI
    //
    if (result.getPhysicalURI() == null)
    {
      result.setPhysicalURI(result.getLogicalURI());
    }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return result;
  }

	public String resolve(String baseLocation, String publicId, String systemId)
	{
    URIResolverInput input = new URIResolverInput(baseLocation, systemId);
    input.setPublicId(publicId);
    URIResolverResult result = resolve(input);
    return result.getPhysicalURI();	
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
