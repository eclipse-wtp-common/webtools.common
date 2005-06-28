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

import java.util.Map;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;


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
  
	/**  
   * @deprecated    
	 */
	public static URIResolver createResolver(Map properties)
	{
		// TODO... utilize properties
		return new ExtensibleURIResolver();
	}	
}
