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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.uriresolver.URIResolverExtension;



public class URIResolverExtensionRegistry {
	protected HashMap map = new HashMap();
	public static final int STAGE_PRENORMALIZATION = 1;
	public static final int STAGE_POSTNORMALIZATION = 2;	
	protected final static String NULL_PROJECT_NATURE_ID = "";
	
	public URIResolverExtensionRegistry() {
	}

	public void put(String className, ClassLoader classLoader, List projectNatureIds, String resourceType, int stage) {
		if(projectNatureIds == null)
		  projectNatureIds = new ArrayList();
		if(projectNatureIds.isEmpty())
		{
		  projectNatureIds.add(NULL_PROJECT_NATURE_ID);
		}
		URIResolverExtensionDescriptor info = new URIResolverExtensionDescriptor(className, classLoader, projectNatureIds, resourceType, stage);
		
		Iterator idsIter = projectNatureIds.iterator();
		while(idsIter.hasNext())
		{
		  String key = (String)idsIter.next();
		  
		  List list = (List)map.get(key);   
		  if (list == null)
		  {			
			list = new ArrayList();
			map.put(key, list);
		  }
		
		  list.add(info);	
		}
	}
	
	
	/**
	 * Return a list of URIResolverExtensionDescriptor objects that apply to this project.
	 * 
	 */
	public List getExtensionDescriptors(IProject project)
	{
		List result = new ArrayList();
		for (Iterator i = map.keySet().iterator(); i.hasNext(); )
		{
			String key = (String)i.next();
			try{		
				if (key == NULL_PROJECT_NATURE_ID ||
				    project == null ||
					project.hasNature(key))
				{				
					result.addAll((List)map.get(key)); 
				}	
			}
			catch (CoreException e)
			{}
		}
		return result;
	}
	
	/**
	 * Return a list of URIResolver objects that match the stage.
	 * 
	 */	
	public List getMatchingURIResolvers(List resolverInfoList, int stage)
	{
		List result = new ArrayList();			
		for (Iterator i = resolverInfoList.iterator(); i.hasNext(); ){
			URIResolverExtensionDescriptor info = (URIResolverExtensionDescriptor)i.next();		
			if (info.stage == stage)
			{    
				Object resolver = info.getResolver();
				if (resolver != null)
				{ 									
					result.add(resolver);
				}					
			}
		}
		return result;
	}	

	public URIResolverExtension get(String key) {
		URIResolverExtensionDescriptor info = (URIResolverExtensionDescriptor) map.get(key);
		return info != null ? info.getResolver() : null;
	}


	
	/*
	protected URIResolverExtensionDescriptor getMatchingURIResolverExtensionDescriptor(List list, String projectNatureId, String resourceType, int stage)
	{
		URIResolverExtensionDescriptor result = null;
		for (Iterator i = list.iterator(); i.hasNext(); )
		{
			URIResolverExtensionDescriptor info = (URIResolverExtensionDescriptor)i.next();
			if (info.matches(projectNatureId, resourceType, stage))
			{
				result = info;
				break;
			}
		}
		return result;
	}*/	
}

