/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.internet.cache.internal;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * The ToCacheRegistryReaders reads Eclipse extensions which specify
 * resources to cache. An extension point looks like the following.
 * 
 *  <extension point="org.eclipse.wst.internet.cache.cacheresource">
 *    <cacheresource uri="URI_TO_CACHE" license="URI_OF_LICENSE"/>
 *  </extension> 
 *
 */
public class ToCacheRegistryReader
{
  protected static final String PLUGIN_ID = "org.eclipse.wst.internet.cache";
  protected static final String EXTENSION_POINT_ID = "cacheresource";
  protected static final String ATT_URL = "url";
  protected static final String ATT_LICENSE = "license";
 
  private static ToCacheRegistryReader registryReader = null;
  
  private Hashtable resourcesToCache = new Hashtable();

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
        ToCacheResource toCacheResource = readElement(elements[i]);
        if(toCacheResource != null)
        {
          resourcesToCache.put(toCacheResource.getURL(), toCacheResource);
          LicenseRegistry.getInstance().addLicense(toCacheResource.getLicense());
        }
      }
    }
  }

  /**
   * Parse and deal with the extension points.
   * 
   * @param element The extension point element.
   */
  protected ToCacheResource readElement(IConfigurationElement element)
  {
	
    if(element.getName().equals(EXTENSION_POINT_ID))
    {
      String url = element.getAttribute(ATT_URL);
      if(url != null)
      {
    	String license = element.getAttribute(ATT_LICENSE);
    	
    	// If the license is relative resolve relative to the
    	// plug-in that declares it.
    	try
    	{
    	  URI licenseURI = new URI(license);
    	  if(!licenseURI.isAbsolute())
    	  {
    		Bundle pluginBundle = Platform.getBundle(element.getDeclaringExtension().getContributor().getName());
    		URL licenseURL = pluginBundle.getEntry(license);
    		if(licenseURL != null)
    		{
    	      license = FileLocator.resolve(licenseURL).toExternalForm();  
    		}
    	  }
    	}
    	catch(URISyntaxException e)
    	{
    	  // Use the license as specified.
    	}
    	catch(IOException e)
    	{
    	  // Use the license as specified.
    	}
    	return new ToCacheResource(url, license);
      }
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
    return (String[])resourcesToCache.keySet().toArray(new String[resourcesToCache.size()]);
  }
  
  /**
   * Get the resource to cache if one has been specified.
   * 
   * @param url The URL of the resource to cache.
   * @return A ToCacheResource object representing the URL or null if none has been specified.
   */
  public ToCacheResource getResourceToCache(String url)
  {
	return (ToCacheResource)resourcesToCache.get(url);
  }
}
