/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.internet.cache.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The cache holds references to remote resources. The cache can store resources,
 * retrieve resources, and delete resources.
 *
 */
public class Cache 
{
  /**
   * String instances.
   */
  private static final String URI = "uri";
  private static final String LOCATION ="location";
  private static final String ENTRY = "entry";
  private static final String CACHE = "cache";
  private static final String LAST_MODIFIED = "lastModified";
  private static final String EXPIRATION_TIME = "expirationTime";
  private static final String FILE_PROTOCOL = "file:///";
  private static final String CACHE_FILE = "cache.xml";
  private static final String CACHE_EXTENSION = ".cache";
  private static final String CACHE_PREFIX = "wtpcache";
  private static final String CACHE_SUFFIX = null;
  /**
   * The default timeout for a cache entry is 1 day.
   */
  private static final long TIMEOUT = 86400000;
	
  /**
   * The one and only instance of the cache.
   */
  private static Cache cacheInstance = null;
  
  /**
   * The cache is stored in a hashtable.
   */
  private Hashtable cache;
  
  /**
   * A set of uncached resources. The cache was not able to cache resources
   * in this list. This list allows quickly skipping over these resources in
   * future requests. 
   */
  private Set uncached;
  
  /**
   * The location of the cache
   */
  private File cacheLocation = null;
  
  /**
   * Private constructor.
   */
  protected Cache(IPath cacheLocation)
  {
	  this.cacheLocation = cacheLocation.toFile();//Platform.getPluginStateLocation(CachePlugin.getDefault()).toFile();
    cache = new Hashtable();
    uncached = new HashSet();
  }
  
  /**
   * Get the one and only instance of the cache.
   * 
   * @return The one and only instance of the cache.
   */
  public static Cache getInstance()
  {
//	  if(cacheInstance == null)
//	  {
//		  cacheInstance = new Cache(cacheLocation);
//		  cacheInstance.open(cacheLocation);
//	  }
	  return cacheInstance;
  }
  
  /**
   * Return the local resource for the specified uri. If there is no resource
   * the cache will attempt to download and cache the resource before returning.
   * If a remote resource cannot be cached this method will return null.
   * 
   * @param uri The URI for which a resource is requested.
   * @return The local resource for the specified URI or null if a remote resource cannot be cached.
   */
  public String getResource(String uri)
  {
	  if(uri == null) return null;
	  CacheEntry result = (CacheEntry)cache.get(uri);
	  
	  // If no result is in the cache and the URI is of an allowed type
	  // retrieve it and store it in the cache.
	  if(result == null)
	  {
      
        if(!uncached.contains(uri))
	    {
          result = cacheResource(uri); 
        }
	  }
	  // Retreive a fresh copy of the result if it has timed out.
	  else if(result.hasExpired())
	  {
		result = refreshCacheEntry(result);
	  }
	  if(result == null || result.getLocalFile() == null)
	  {
		return null;
	  }
	  return FILE_PROTOCOL + cacheLocation.toString() + "/" + result.getLocalFile();
  }
  
  /**
   * Get the list of uncached resources.
   * 
   * @return The list of uncached resources.
   */
  protected String[] getUncachedURIs()
  {
    return (String[])uncached.toArray(new String[uncached.size()]);
  }
  
  /**
   * Clear the list of uncached resources.
   */
  protected void clearUncachedURIs()
  {
    uncached.clear();
  }
  
  /**
   * Add an uncached resource to the list and start the 
   * uncached job if not already started.
   */
  protected void addUncachedURI(String uri)
  {
	CacheJob.startJob();
    uncached.add(uri);
  }
  
  /**
   * Cache the specified resource. This method creates a local version of the
   * remote resource and adds the resource reference to the cache. If the resource
   * cannot be accessed it is not added and null is returned.
   * 
   * @param uri The remote URI to cache.
   * @return A new CacheEntry representing the cached resource or null if the remote
   *         resource could not be retrieved.
   */
  protected CacheEntry cacheResource(String uri)
  {
	  CacheEntry cacheEntry = null;
	  URLConnection conn = null;
	  InputStream is = null;
	  OutputStream os = null;
	  try
	  {
		  URL url = new URL(uri);
		  conn = url.openConnection();
		  conn.connect();
		  // Determine if this resource can be cached.
		  if(conn.getUseCaches())
		  {
		    is = conn.getInputStream();//url.openStream();
		  
		    Random rand = new Random();
			String fileName = rand.nextInt() + CACHE_EXTENSION;
		    File file = new File(cacheLocation, fileName);
		    // If the file already exists we need to change the file name.
		    while(!file.createNewFile())
		    {
			  fileName = rand.nextInt() + CACHE_EXTENSION;
			  file = new File(cacheLocation,fileName);
		    }
		    os = new FileOutputStream(file);
		    byte[] bytes = new byte[1024];
		    int bytelength;
		    while((bytelength = is.read(bytes)) != -1)
		    {
			  os.write(bytes, 0, bytelength);
		    }
			long lastModified = conn.getLastModified();
		    long expiration = conn.getExpiration();
			if(expiration == 0)
			{
			  expiration = System.currentTimeMillis() + TIMEOUT;
			}
		    cacheEntry = new CacheEntry(uri, fileName, lastModified, expiration);
		    cache.put(uri,cacheEntry);
		  }

	  }
	  catch(Exception t)
	  {
		  // Put the entry in the uncached list so the resolution work will not be performed again.
      // TODO: Add in a timeout for the non-located uris.
      addUncachedURI(uri);
	  }
	  finally
	  {
		  if(is != null)
		  {
			  try
			  {
			    is.close();
			  }
			  catch(IOException e)
			  {
			    // Do nothing if the stream cannot be closed.
			  }
		  }
		  if(os != null)
		  {
			  try
			  {
			    os.close();
			  }
			  catch(IOException e)
			  {
				// Do nothing if the stream cannot be closed. 
			  }
		  }
	  }
	  return cacheEntry;
  }
  
  /**
   * Refresh the cache entry if necessary. The cache entry will be refreshed
   * if the remote resource is accessible and the last modified time of the
   * remote resource is greater than the last modified time of the cached
   * resource.
   * 
   * @param cacheEntry The cache entry to refresh.
   * @return The refreshed cache entry.
   */
  protected CacheEntry refreshCacheEntry(CacheEntry cacheEntry)
  {
	  URLConnection conn = null;
	  InputStream is = null;
	  OutputStream os = null;
	  try
	  {
		  URL url = new URL(cacheEntry.getURI());
		  conn = url.openConnection();
		  conn.connect();
		  
		  long lastModified = conn.getLastModified();
	      if(lastModified > cacheEntry.getLastModified())
		  {
			long expiration = conn.getExpiration();
		    if(expiration == 0)
			{
			  expiration = System.currentTimeMillis() + TIMEOUT;
			}
			
		    is = conn.getInputStream();
			
			String localFile = cacheEntry.getLocalFile();
  
		    File tempFile = File.createTempFile(CACHE_PREFIX, CACHE_SUFFIX);
			tempFile.deleteOnExit();

		    os = new FileOutputStream(tempFile);
		    byte[] bytes = new byte[1024];
		    int bytelength;
		    while((bytelength = is.read(bytes)) != -1)
		    {
			  os.write(bytes, 0, bytelength);
		    }
			is.close();
			os.close();
			deleteFile(cacheEntry.getURI());
			File f = new File(cacheLocation, localFile);
			tempFile.renameTo(f);
			cacheEntry.setExpiration(expiration);
			cacheEntry.setLastModified(lastModified);
		  }
		  // The cache entry hasn't changed. Just update the expiration time.
	      else
		  {
			long expiration = conn.getExpiration();
			if(expiration == 0)
			{
			  expiration = System.currentTimeMillis() + TIMEOUT;
			}
			cacheEntry.setExpiration(expiration);
		  }

	  }
	  catch(Exception e)
	  {
      // Do nothing.
	  }
	  finally
	  {
		  if(is != null)
		  {
			  try
			  {
			    is.close();
			  }
			  catch(IOException e)
			  {
			    // Do nothing if the stream cannot be closed.
			  }
		  }
		  if(os != null)
		  {
			  try
			  {
			    os.close();
			  }
			  catch(IOException e)
			  {
				// Do nothing if the stream cannot be closed. 
			  }
		  }
	  }
	  return cacheEntry;
  }
  
  /**
   * Get an array of the cached URIs.
   * 
   * @return An array of the cached URIs.
   */
  public String[] getCachedURIs()
  {
	Set keyset = cache.keySet();
	return (String[])keyset.toArray(new String[keyset.size()]);
  }
  
  /**
   * Close the cache. Closing the cache involves serializing the data to an XML file
   * in the plugin state location.
   */
  protected void close()
  {
	  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      try {
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document cachedoc = builder.newDocument();
		Element rootelem = cachedoc.createElement(CACHE);
		cachedoc.appendChild(rootelem);
		
	  Enumeration uris = cache.keys();
	  while(uris.hasMoreElements())
	  {
		  String key = (String)uris.nextElement();
		  CacheEntry cacheEntry = (CacheEntry)cache.get(key);
		  if(cacheEntry != null)
		  {
			  Element entry = cachedoc.createElement(ENTRY);
			  entry.setAttribute(URI, key);
			  entry.setAttribute(LOCATION, cacheEntry.getLocalFile());
			  entry.setAttribute(EXPIRATION_TIME, String.valueOf(cacheEntry.getExpirationTime()));
			  entry.setAttribute(LAST_MODIFIED, String.valueOf(cacheEntry.getLastModified()));
			  rootelem.appendChild(entry);
		  }
	  }
	  
	  // Write the cache entry.
	  TransformerFactory tFactory  = TransformerFactory.newInstance();
      Transformer transformer = tFactory.newTransformer();
      Source input = new DOMSource(cachedoc);
	  IPath stateLocation = Platform.getStateLocation(CachePlugin.getDefault().getBundle());
      Result output = new StreamResult(stateLocation.toString() + "/" + CACHE_FILE);
      transformer.transform(input, output);

      }catch(Exception e)
	  {
		  System.out.println("Unable to store internet cache.");
	  }
	  cacheInstance = null;
  }
  
  /**
   * Open the cache. Opening the cache involves parsing the cache XML file in
   * the plugin state location if it can be read.
   */
  protected static void open(IPath cacheLocation)
  {
    cacheInstance = new Cache(cacheLocation);
	  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      try {
		  
		IPath stateLocation = cacheLocation;
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document cachedoc = builder.parse(stateLocation.toString() + "/" + CACHE_FILE);
		Element rootelem = cachedoc.getDocumentElement();
		NodeList entries = rootelem.getChildNodes();
		int numEntries = entries.getLength();
		for(int i = 0; i < numEntries; i++)
		{
			Node entry = entries.item(i);
			if(entry instanceof Element)
			{
				Element e = (Element)entry;
				if(e.getNodeName().equals(ENTRY))
				{
					String uri = e.getAttribute(URI);
					String location = e.getAttribute(LOCATION);
					String lm = e.getAttribute(LAST_MODIFIED);
					String et = e.getAttribute(EXPIRATION_TIME);
					long lastModified = -1;
					long expirationTime = -1;
					try
					{
						lastModified = Long.parseLong(lm);
					}
					catch(NumberFormatException nfe)
					{
					}
					try
					{
						expirationTime = Long.parseLong(et);
					}
					catch(NumberFormatException nfe)
					{
					}
					if(uri != null && location != null)
					{
					  cacheInstance.cache.put(uri, new CacheEntry(uri, location, lastModified, expirationTime));
					}
				}
			}
		}
      }
	  catch(FileNotFoundException e)
	  {
		// If the file doesn't exist in the correct location there is nothing to load. Do nothing.
	  }
	  catch(Exception e)
	  {
		  System.out.println("Unable to load cache.");
	  }
  }
  
  /**
   * Clear all of the entries from the cache. This method also deletes the cache files.
   */
  public void clear()
  {
	Enumeration keys = cache.keys();
	while(keys.hasMoreElements())
	{
	  String key = (String)keys.nextElement();
	  
	  deleteFile(key);
	}
	cache.clear();
  }
  
  /**
   * Delete the cache entry specified by the given URI.
   * @param uri The URI entry to remove from the catalog.
   */
  public void deleteEntry(String uri)
  {
	  if(uri == null) return;
	  
	  deleteFile(uri);
	  cache.remove(uri);
  }
  
  /**
   * Delete the file specified by the URI.
   * 
   * @param uri The URI of the file to delete.
   */
  protected void deleteFile(String uri)
  {
	  CacheEntry cacheEntry = (CacheEntry)cache.get(uri);
	  if(cacheEntry != null)
	  {
		String location = cacheLocation.toString() + "/" + cacheEntry.getLocalFile();
	    File file = new File(location);
	    if(!file.delete())
	    {
	      System.out.println("Unable to delete file " + location + " from cache.");
	    } 
	  }
  }
}