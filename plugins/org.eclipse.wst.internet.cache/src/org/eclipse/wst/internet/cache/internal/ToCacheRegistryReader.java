/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.internet.cache.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * The ToCacheRegistryReaders reads Eclipse extensions which specify
 * resources to cache. An extension point looks like the following.
 * 
 *  <extension point="org.eclipse.wst.internet.cache.cacheresource">
 *    <cacheresource uri="URI_TO_CACHE"/>
 *  </extension> 
 *
 */
public class ToCacheRegistryReader
{
  protected static final String PLUGIN_ID = "org.eclipse.wst.internet.cache";
  protected static final String EXTENSION_POINT_ID = "cacheresource";
  protected static final String ATT_URI = "uri";
 
  private static ToCacheRegistryReader registryReader = null;
  
  private Set resourcesToCache = new HashSet();

  /**
   * Get the one and only instance of this registry reader.
   * 
   * @return The one and only instance of this registry reader.
   */
  public static ToCacheRegistryReader getInstance()
  {
    if(registryReader == null)
    {
      registryReader = new ToCacheRegistryReader();
    }
    return registryReader;
  }
  /**
   * Read from plugin registry and handle the configuration elements that match
   * the spedified elements.
   */
  public void readRegistry()
  {
    IExtensionRegistry pluginRegistry = Platform.getExtensionRegistry();
    IExtensionPoint point = pluginRegistry.getExtensionPoint(PLUGIN_ID, EXTENSION_POINT_ID);
    if (point != null)
    {
      IConfigurationElement[] elements = point.getConfigurationElements();
      for (int i = 0; i < elements.length; i++)
      {
        String uri = readElement(elements[i]);
        if(uri != null)
        {
          resourcesToCache.add(uri);
        }
      }
    }
  }

  /**
   * Parse and deal with the extension points.
   * 
   * @param element The extension point element.
   */
  protected String readElement(IConfigurationElement element)
  {
    if(element.getName().equals(EXTENSION_POINT_ID))
    {
      return element.getAttribute(ATT_URI);
    }
    return null;
  }
  
  /**
   * Get the list of URIs that have been specified for caching.
   * 
   * @return The list of URIs that have been specified for caching.
   */
  public String[] getURIsToCache()
  {
    return (String[])resourcesToCache.toArray(new String[resourcesToCache.size()]);
  }
}
