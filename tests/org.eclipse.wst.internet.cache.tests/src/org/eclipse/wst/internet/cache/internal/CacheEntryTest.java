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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for the CacheEntry class.
 */
public class CacheEntryTest extends TestCase
{
	/**
	  * Create a tests suite from this test class.
	  * 
	  * @return A test suite containing this test class.
	  */
	  public static Test suite()
	  {
	    return new TestSuite(CacheEntryTest.class);
	  }
	
	/**
	 * Test that the entry is not considered expired when -1 is specified.
	 */
	public void testNotExpiredWhenMinusOne()
	{
		CacheEntry cacheEntry = new CacheEntry(null, null, 0, -1);
		assertFalse("The cache entry is expired when -1 is specified.", cacheEntry.hasExpired());
	}
	
	/**
	 * Test that the entry is not considered expired when the set expiration
	 * time is greater than the current system time.
	 */
	public void testNotExpiredWhenGreaterThanSystemTime()
	{
		CacheEntry cacheEntry = new CacheEntry(null, null, 0, System.currentTimeMillis() + 60000);
		assertFalse("The cache entry is expired when greater than the currnet system time.", cacheEntry.hasExpired());
	}
	
	/**
	 * Test that the entry is considered expired when the set expiration
	 * time is less than the current system time.
	 */
	public void testExpiredWhenLessThanSystemTime()
	{
		CacheEntry cacheEntry = new CacheEntry(null, null, 0, System.currentTimeMillis() - 60000);
		assertTrue("The cache entry is not expired when less than the currnet system time.", cacheEntry.hasExpired());
	}
}
