/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *******************************************************************************/
package org.eclipse.wst.common.uriresolver.internal;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverExtension;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.osgi.framework.Bundle;


/**
 * @author csalter
 * 
 */
public class ExtensibleURIResolver implements URIResolver
{
	private static final boolean logExceptions = false;

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
    
    public String resolvePhysicalLocation(String baseLocation, String publicId, String logicalLocation)
    {
      String result = logicalLocation;
      URIResolverExtensionRegistry resolverRegistry = URIResolverExtensionRegistry.getIntance();
      IFile file = computeFile(baseLocation);
      
      // compute the project that holds the resource
      //      
      IProject project =  file != null ? file.getProject() : null;            
      List list = resolverRegistry.getExtensionDescriptors(project);      
      for (Iterator i = resolverRegistry.getMatchingURIResolvers(list, URIResolverExtensionRegistry.STAGE_PHYSICAL).iterator(); i.hasNext(); )
      {        
        // get the list of applicable physical resolvers from the extension registry
        //
        while (i.hasNext())
        {
          URIResolverExtension resolver = (URIResolverExtension) i.next();
          String tempresult = resolver.resolve(file, baseLocation, publicId, result);
          if(tempresult != null)
          {
            result = tempresult;
          }
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
			baseLocation = baseLocation.replace('\\','/');
			URI baseURI = URI.createURI(baseLocation);
			try
			{
			  result = systemURI.resolve(baseURI).toString();
			}
			catch (IllegalArgumentException e) {
				Bundle bundle = URIResolverPlugin.getInstance().getBundle();
				IStatus statusObj = null;
				java.net.URI baseURI2 = null;
				try {
					baseURI2 = java.net.URI.create(baseLocation);
				}
				catch (IllegalArgumentException e2) {
					if(logExceptions) {
					    statusObj = new Status(IStatus.ERROR, bundle.getSymbolicName(), IStatus.ERROR, "Problem in creating java.net.URI in ExtensibleURIResolver:" + e2.getMessage(), e2); //$NON-NLS-1$
					    Platform.getLog(bundle).log(statusObj);
					}
				}
				try {
					if(baseURI2 != null) {
						java.net.URI resultURI = baseURI2.resolve(systemId);
						result = resultURI.toString();
					}
				}
				catch (IllegalArgumentException e2) {
					if(logExceptions) {
					    statusObj = new Status(IStatus.ERROR, bundle.getSymbolicName(), IStatus.ERROR, "Problem in resolving with java.net.URI in ExtensibleURIResolver:" + e2.getMessage(), null); //$NON-NLS-1$
					    Platform.getLog(bundle).log(statusObj);
					}
				}
			}
		}
		return result;
	}

  protected IFile computeFile(String baseLocation)
  {
    IFile file = null;
    if (baseLocation != null)
    {
      String pattern = "file:///"; //$NON-NLS-1$
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
