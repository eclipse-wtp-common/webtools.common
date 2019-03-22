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

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for the Cache class.
 */
public class CacheTest extends TestCase
{
	private Cache cache;
	
	/**
	  * Create a tests suite from this test class.
	  * 
	  * @return A test suite containing this test class.
	  */
	  public static Test suite()
	  {
	    return new TestSuite(CacheTest.class);
	  }

	protected void setUp() throws Exception {
		super.setUp();
		cache = Cache.getInstance();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	/**
	 * Test trying to cache a resource that doesn't exist
	 * on a server that does exist.
	 */
	public void testGetNonExistantResource()
	{
		String resource = "http://www.eclipse.org/webtools/nonexistantfile";
		String result = cache.getResource(resource);
		cache.clear();
		assertNull("The result returned for resource " + resource + " is not null.", result);
	}
	
	/**
	 * Test trying to cache a resource that doesn't exist
	 * because the server doesn't exist.
	 */
//	public void testGetNonExistantServer()
//	{
//		String resource = "http://www.eclipse.bad/webtools/nonexistantfile";
//		String result = cache.getResource(resource);
//		cache.clear();
//		assertNull("The result returned for resource " + resource + " is not null.", result);
//	}
	
	/**
	 * Test trying to get a resource specified by null.
	 */
	public void testGetNullResource()
	{
		String resource = null;
		String result = cache.getResource(resource);
		cache.clear();
		assertNull("The result returned for resource " + resource + " is not null.", result);
	}
	
	/**
	 * Test trying to cache a resource that does exist.
	 */
	public void testGetResourceThatExists()
	{
		String resource = "http://www.eclipse.org/webtools";
		String result = cache.getResource(resource);
		cache.clear();
		assertNotNull("The result returned for resource " + resource + " was null.", result);
		assertTrue("The result and resource are the same.", !resource.equals(result));
	}
	
	/**
	 * Test to ensure the result that is returned starts with file:///
	 */
	public void testResultStartsWithFile()
	{
		String resource = "http://www.eclipse.org/webtools";
		String result = cache.getResource(resource);
		cache.clear();
		assertTrue("The result does not start with file:///.", result.startsWith("file:///"));
	}
	
	/**
	 * Test to ensure deleting a cache entry deletes it from the cache and
	 * from the file system.
	 */
	public void testDeleteCacheEntry()
	{
		String resource = "http://www.eclipse.org/webtools";
		String result = cache.getResource(resource);
		assertNotNull("The local cache file is null.", result);
		// Remove file:/// from the result.
		result = result.substring(8);
		assertTrue("The cache file " + result + " does not exist.", new File(result).exists());
		cache.deleteEntry(resource);
		assertFalse("The cache file was not deleted.", new File(result).exists());
		assertTrue("The cache still contains the deleted entry.", cache.getCachedURIs().length == 0);
		cache.clear();
	}
	
	/**
	 * Test to ensure deleting a null cache entry simply returns.
	 */
	public void testDeleteNullCacheEntry()
	{
		String resource = "http://www.eclipse.org/webtools";
		cache.getResource(resource);
		cache.deleteEntry(null);
		assertFalse("The cache no longer contains the entry after deleting null.", cache.getCachedURIs().length == 0);
		cache.clear();
	}
	
	/**
	 * Test to ensure clearing the cache with a single entry deletes the entry
	 * from the cache and deletes the file from the file system.
	 */
	public void testClearCacheWithSingleEntry()
	{
		String resource1 = "http://www.eclipse.org/webtools";
		String result1 = cache.getResource(resource1);
		assertNotNull("The local cache file is null for resource1.", result1);
		// Remove file:/// from the result.
		result1 = result1.substring(8);
		assertTrue("The cache file " + result1 + " does not exist.", new File(result1).exists());
		cache.clear();
		assertFalse("The cache file for resource1 was not deleted.", new File(result1).exists());
		assertTrue("The cache still contains the deleted entries.", cache.getCachedURIs().length == 0);
	}
	
	/**
	 * Test to ensure clearing the cache with multiple entries deletes the entries
	 * from the cache and deletes the files from the file system.
	 */
	public void testClearCacheWithMultipleEntries()
	{
		String resource1 = "http://www.eclipse.org/webtools";
		String resource2 = "http://www.eclipse.org";
		String result1 = cache.getResource(resource1);
		String result2 = cache.getResource(resource2);
		assertNotNull("The local cache file is null for resource1.", result1);
		assertNotNull("The local cache file is null for resource2.", result2);
		// Remove file:/// from the result.
		result1 = result1.substring(8);
		result2 = result2.substring(8);
		assertTrue("The cache file " + result1 + " does not exist.", new File(result1).exists());
		assertTrue("The cache file " + result2 + " does not exist.", new File(result2).exists());
		cache.clear();
		assertFalse("The cache file for resource1 was not deleted.", new File(result1).exists());
		assertFalse("The cache file for resource2 was not deleted.", new File(result2).exists());
		assertTrue("The cache still contains the deleted entries.", cache.getCachedURIs().length == 0);
	}
	
	/**
	 * Test to ensure deleting a null cache entry simply returns.
	 */
	public void testGetInstance()
	{
		assertNotNull("The cache object is null.", cache);
	}
	
	/**
	 * Test to ensure getCacheEntries returns all of the cache entries and not entries that 
   * haven't been cached.
	 */
	public void testGetCacheEntries()
	{
		String resource1 = "http://www.eclipse.org/webtools";
		String resource2 = "http://www.eclipse.org";
		String resource3 = "http://www.eclipse.org/webtools/nonexistantfile";
		cache.getResource(resource1);
		cache.getResource(resource2);
		cache.getResource(resource3);
		String[] uris = cache.getCachedURIs();
		assertTrue("There are not 2 entries in the cache.", uris.length == 2);
		
		for(int i = 0; i < uris.length -1; i++)
		{
		  String uri = uris[i];
		  if(!(uri.equals(resource1) || uri.equals(resource2) || uri.equals(resource3)))
		  {
			fail("The URI " + uri + " is not equal to any of the resources put in the cache.");  
		  }
		}
		cache.clear();
		
	}
	
	
	
	

}
