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
 * A cache entry contains a URI, a local file, and a timeout.
 */
public class CacheEntry 
{
  private String uri;
  private String localFile;
  private long lastModified;
  private long expirationTime;
  
  /**
   * Create a new cache entry.
   * 
   * @param uri The remote URI of the cache entry.
   * @param localFile The local file that contains the cached entry.
   * @param lastModifie The time this resource was last modified.
   * @param expirationTime The time in miliseconds that this cache entry will
   *                       expire.
   */
  public CacheEntry(String uri, String localFile, long lastModified, long expirationTime)
  {
	this.uri = uri;
	this.localFile = localFile;
	this.lastModified = lastModified;
	this.expirationTime = expirationTime;
  }
  
  /**
   * The cache entry is expired if its expiration time is less then the
   * current system time and not equal to -1.
   * 
   * @return True if this cached entry has expired, false otherwise.
   */
  public boolean hasExpired()
  {
	 if(expirationTime != -1 && System.currentTimeMillis() > expirationTime)
	 {
	   return true;
	 }
	 return false;
  }
  
  /**
   * Set the time in miliseconds that this cache entry will expire.
   * 
   * @param timeout The time in miliseconds that this cache entry will expire.
   *                -1 indicates that this entry will not expire.
   */
  public void setExpiration(long expirationTime)
  {
	this.expirationTime = expirationTime;
  }
  
  /**
   * Get the time at which this cache entry will expire.
   * 
   * @return The time at which this cache entry will expire.
   */
  public long getExpirationTime()
  {
	return expirationTime;
  }
  
  /**
   * Get the remote URI for this cache entry.
   * 
   * @return The remote URI for this cache entry.
   */
  public String getURI()
  {
	return uri;
  }
  
  /**
   * Get the local file for this cache entry.
   * 
   * @return The local file for this cache entry.
   */
  public String getLocalFile()
  {
	return localFile;
  }
  
  /**
   * Get the last time this cache entry was modified.
   * 
   * @return The last time this cache entry was modified.
   */
  public long getLastModified()
  {
	return lastModified;
  }
  
  /**
   * Set the last time this cache entry was modified.
   */
  public void setLastModified(long lastModified)
  {
	this.lastModified = lastModified;
  }
}
