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

import java.util.Map;

import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.common.uriresolver.internal.ExtensibleURIResolver;
import org.eclipse.wst.common.uriresolver.internal.URIResolverExtensionRegistry;
import org.eclipse.wst.common.uriresolver.internal.URIResolverExtensionRegistryReader;


public class URIResolverPlugin extends Plugin {
	protected static URIResolverPlugin instance;	
	protected URIResolverExtensionRegistry xmlResolverExtensionRegistry;

	public static URIResolverPlugin getInstance()
	{
		return instance;
	}
	
	public URIResolverPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		instance = this;
	}	
	
					
	public static URIResolver createResolver()
	{
		return createResolver(null);
	}
	
	public static URIResolver createResolver(Map properties)
	{
		// TODO... utilize properties
		return new ExtensibleURIResolver();
	}	
	
	//public static URIResolver createResolver(IProject project)
	//{
	//	return new ExtensibleURIResolver(project);
	//}	
	
	public URIResolverExtensionRegistry getXMLResolverExtensionRegistry()
	{
		if (xmlResolverExtensionRegistry == null)
		{
			xmlResolverExtensionRegistry = new URIResolverExtensionRegistry();
			new URIResolverExtensionRegistryReader(xmlResolverExtensionRegistry).readRegistry();	
		}	
		return xmlResolverExtensionRegistry; 
	}
}
