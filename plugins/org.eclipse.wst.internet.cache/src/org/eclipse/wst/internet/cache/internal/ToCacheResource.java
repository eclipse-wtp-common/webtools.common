/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.cache.internal;

/**
 * A resource that is specified to be cached. The resource has a URL and an optional
 * license.
 */
public class ToCacheResource 
{
  private String url;
  private String license;
  
  /**
   * Constructor.
   * 
   * @param url The URL of the resource to be cached.
   * @param license The URL of the optional license for this resource.
   */
  public ToCacheResource(String url, String license)
  {
	this.url = url;
	this.license = license;
  }
  
  /**
   * Get the URL of the resource to be cached.
   * 
   * @return The URL of the resource to be cached.
   */
  public String getURL()
  {
	return url;
  }
  
  /**
   * Get the license URL of the resource to be cached.
   * 
   * @return The license URL of the resource to be cached.
   */
  public String getLicense()
  {
	return license;
  }
}
