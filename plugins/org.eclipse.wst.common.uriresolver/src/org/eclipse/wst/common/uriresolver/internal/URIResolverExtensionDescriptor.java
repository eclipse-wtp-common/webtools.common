/**
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

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverExtension;
import org.osgi.framework.Bundle;

/**
 * A URI resolver extension descriptor contains all the information about
 * an extension URI resolver. The information contained allows for the
 * extension resolver to be instantiated and called at the correct times.
 */
public class URIResolverExtensionDescriptor
{
	protected URIResolverExtension resolver;

	protected String fileType;

	protected String className;

	public List projectNatureIds;

	protected String resourceType;

	protected int stage = URIResolverExtensionRegistry.STAGE_POSTNORMALIZATION;

	protected String priority = URIResolverExtensionRegistry.PRIORITY_MEDIUM;

	protected String pluginId;

	protected boolean error;

	/**
	 * Constructor.
	 * 
	 * @param className The extension URI resolver class name.
	 * @param pluginId The ID of the plugin that contains the extension URI resolver class.
	 * @param projectNatureIds The project nature IDs for which the resolver should run.
	 * @param resourceType The type of resource for which the resolver should run.
	 * @param stage The stage of the resolver. Either prenormalization or postnormalization.
	 * @param priority The resolver's priority. high, medium, or low.
	 */
	public URIResolverExtensionDescriptor(String className, String pluginId,
			List projectNatureIds, String resourceType, int stage, String priority)
	{
		this.className = className;
		this.pluginId = pluginId;
		this.projectNatureIds = projectNatureIds;
		this.resourceType = resourceType;
		this.stage = stage;
		this.priority = priority;
	}

	/**
	 * Get the extension URI resolver.
	 * 
	 * @return The extension URI resolver.
	 */
	public URIResolverExtension getResolver()
	{

		if (resolver == null && className != null && !error)
		{
			try
			{
				// Class theClass = classLoader != null ?
				// classLoader.loadClass(className) : Class.forName(className);
				Bundle bundle = Platform.getBundle(pluginId);
				Class theClass = bundle.loadClass(className);
				resolver = (URIResolverExtension) theClass.newInstance();
			} catch (Exception e)
			{
				error = true;
				e.printStackTrace();
			}
		}
		return resolver;
	}

	/**
	 * Determines if the resolver should run in the current scenario given
	 * the project nature ID, resource type, and stage.
	 * 
	 * @param projectNatureId The project nature ID to check against.
	 * @param resourceType The resource type to check against.
	 * @param stage The stage to check against.
	 * @return True if the resolver should run, false otherwise.
	 */
	public boolean matches(String projectNatureId, String resourceType, int stage)
	{
		if (projectNatureIds.contains(projectNatureId))
		{
			return matches(this.resourceType, resourceType) && this.stage == stage;
		}
		return false;
	}

	/**
	 * Determines if string a matches string b.
	 * TODO: Why is this required instead of just using String.equals?
	 * 
	 * @param a String for comparison.
	 * @param b String for comparison.
	 * @return True if the strings match, false otherwise.
	 */
	private boolean matches(String a, String b)
	{
		return (a != null) ? a.equals(b) : a == b;
	}
}
