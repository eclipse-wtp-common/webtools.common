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

import java.util.List;

import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverExtension;


/**
 * @author csalter
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class URIResolverExtensionDescriptor
{
	protected URIResolverExtension resolver;
	//protected String projectNature;
	protected String fileType;
	protected String className;
	public List projectNatureIds;
	protected String resourceType;
	protected int stage = URIResolverExtensionRegistry.STAGE_POSTNORMALIZATION;
  protected String priority = URIResolverExtensionRegistry.PRIORITY_MEDIUM;
	protected ClassLoader classLoader;
	protected boolean error;

	public URIResolverExtensionDescriptor(String className, ClassLoader classLoader, List projectNatureIds, String resourceType, int stage, String priority)
	{
		this.className = className;
		this.classLoader = classLoader;
		this.projectNatureIds = projectNatureIds;
		this.resourceType = resourceType;
		this.stage = stage;
    this.priority = priority;
	}

	public URIResolverExtension getResolver()
	{

		if (resolver == null && className != null && !error)
		{
			try
			{
				Class theClass = classLoader != null ? classLoader.loadClass(className) : Class.forName(className);
				resolver = (URIResolverExtension) theClass.newInstance();
			}
			catch (Exception e)
			{
				error = true;
				e.printStackTrace();
			}
		}
		return resolver;
	}

	public boolean matches(String projectNatureId, String resourceType, int stage)
	{
	  if(projectNatureIds.contains(projectNatureId))
	  {
		return matches(this.resourceType, resourceType) && this.stage == stage;
	  }
	  return false;
	}

	public boolean matches(String a, String b)
	{
		return (a != null) ? a.equals(b) : a == b;
	}
}
