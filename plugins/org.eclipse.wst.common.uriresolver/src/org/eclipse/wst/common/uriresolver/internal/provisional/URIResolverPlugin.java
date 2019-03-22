/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *******************************************************************************/
package org.eclipse.wst.common.uriresolver.internal.provisional;

import java.util.Map;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.common.uriresolver.internal.ExtensibleURIResolver;
import org.eclipse.wst.common.uriresolver.internal.URIResolverExtensionRegistry;


public class URIResolverPlugin extends Plugin {
	protected static URIResolverPlugin instance;	
	protected URIResolverExtensionRegistry xmlResolverExtensionRegistry;

	public static URIResolverPlugin getInstance()
	{
		return instance;
	}
	
	public URIResolverPlugin() {
		super();
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
}
