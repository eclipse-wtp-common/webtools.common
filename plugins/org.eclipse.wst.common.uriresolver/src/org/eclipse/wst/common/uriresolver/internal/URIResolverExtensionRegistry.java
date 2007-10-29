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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * The URI resolver extension registry contains information about
 * all of the extension URI resolvers.
 */
public class URIResolverExtensionRegistry
{
	protected HashMap map = new HashMap();

	public static final int STAGE_PRENORMALIZATION = 1;

	public static final int STAGE_POSTNORMALIZATION = 2;

	public static final int STAGE_PHYSICAL = 3;

	public static final String PRIORITY_LOW = "low";

	public static final String PRIORITY_MEDIUM = "medium";

	public static final String PRIORITY_HIGH = "high";

	protected final static String NULL_PROJECT_NATURE_ID = "";

	protected static URIResolverExtensionRegistry instance;

	private URIResolverExtensionRegistry()
	{
	}

	/**
	 * Get the one and only instance of the registry.
	 * 
	 * @return The one and only instance of the registry.
	 */
	public synchronized static URIResolverExtensionRegistry getIntance()
	{
		if (instance == null)
		{
			instance = new URIResolverExtensionRegistry();
			new URIResolverExtensionRegistryReader(instance).readRegistry();
		}
		return instance;
	}

	/**
	 * Add an extension resolver to the registry.
	 * 
	 * @param className The name of the extension URI resolver class.
	 * @param pluginId The ID of the plugin that contains the extension URI resolver class.
	 * @param projectNatureIds A list of project natures IDs for which the resolver should run.
	 * @param resourceType The type of resoure for which an extension resource should run.
	 * @param stage The stage to run. Either prenormalization or postnormalization.
	 * @param priority The priority of the resolver. Valid values are high, medium, and low.
	 */
	public void put(String className, String pluginId, List projectNatureIds,
			String resourceType, int stage, String priority)
	{
		if (projectNatureIds == null)
			projectNatureIds = new ArrayList();
		if (projectNatureIds.isEmpty())
		{
			projectNatureIds.add(NULL_PROJECT_NATURE_ID);
		}
		URIResolverExtensionDescriptor info = new URIResolverExtensionDescriptor(
				className, pluginId, projectNatureIds, resourceType, stage, priority);

		Iterator idsIter = projectNatureIds.iterator();
		while (idsIter.hasNext())
		{
			String key = (String) idsIter.next();

			HashMap priorityMap = (HashMap) map.get(key);
			if (priorityMap == null)
			{
				priorityMap = new HashMap();
				map.put(key, priorityMap);
				priorityMap.put(PRIORITY_HIGH, new ArrayList());
				priorityMap.put(PRIORITY_MEDIUM, new ArrayList());
				priorityMap.put(PRIORITY_LOW, new ArrayList());
			}
			List list = (List) priorityMap.get(priority);
			list.add(info);
		}
	}

	/**
	 * Return a list of URIResolverExtensionDescriptor objects that apply to this
	 * project. The list is in the priority order high, medium, low.
	 * 
	 * @param project The project for which you are requesting resolvers.
	 * @return A list of URIResolverExtensionDescriptor objects.
	 */
	public List getExtensionDescriptors(IProject project)
	{
	  List result = new ArrayList();
	  List lowPriorityList = new ArrayList();
	  List mediumPriorityList = new ArrayList();
	  List highPriorityList = new ArrayList();          
	  for (Iterator i = map.keySet().iterator(); i.hasNext();)
	  {
	    String key = (String) i.next();
	    try
	    {
	      if (key == NULL_PROJECT_NATURE_ID || project == null || project.hasNature(key))
	      {
	        highPriorityList.addAll((List) ((HashMap) map.get(key)).get(PRIORITY_HIGH));
	        mediumPriorityList.addAll((List) ((HashMap) map.get(key)).get(PRIORITY_MEDIUM));
	        lowPriorityList.addAll((List) ((HashMap) map.get(key)).get(PRIORITY_LOW));
	      }
	    } catch (CoreException e)
	    {
	    }
	  }
	  result.addAll(highPriorityList);
	  result.addAll(mediumPriorityList);
	  result.addAll(lowPriorityList);
	  return result;
	}

	/**
	 * Return a list of URIResolver objects that match the stage.
	 * TODO: This seems like an odd method to house here. It may need to be moved
	 *       or removed if the stage attribute dissapears.
	 * 
	 * @param resolverInfoList A list of resolvers to prune.
	 * @param stage The stage requested.
	 * @return A list of URIResolver objects that match the stage.
	 */
	public List getMatchingURIResolvers(List resolverInfoList, int stage)
	{
		List result = new ArrayList();
		for (Iterator i = resolverInfoList.iterator(); i.hasNext();)
		{
			URIResolverExtensionDescriptor info = (URIResolverExtensionDescriptor) i
					.next();
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
}
