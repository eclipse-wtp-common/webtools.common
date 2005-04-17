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
 * Tests for the CacheURIResolverExtension class.
 */
public class CacheURIResolverExtensionTest extends TestCase
{
	private CacheURIResolverExtension cacheResolver;
	
	/**
	  * Create a tests suite from this test class.
	  * 
	  * @return A test suite containing this test class.
	  */
	  public static Test suite()
	  {
	    return new TestSuite(CacheURIResolverExtensionTest.class);
	  }
	  
	  protected void setUp() throws Exception 
	  {
		super.setUp();
		cacheResolver = new CacheURIResolverExtension();
	  }

	  /**
	   * Test that the result returned when a null systemId is given is null.
	   */
	  public void testResolveNullSystemId()
	  {
	    String result = cacheResolver.resolve(null,"http://www.eclipse.org/webtools", null, null);
		assertNull("The result is not null.", result);
	  }
	  
	  /**
	   * Test that the result returned when a null systemId and a null baselocation
	   * are given is null.
	   */
	  public void testResolveNullSystemIdAndBaselocation()
	  {
	    String result = cacheResolver.resolve(null, null, null, null);
		assertNull("The result is not null.", result);
	  }
	  
	  /**
	   * Test that the result is sucessfully cached when an absolute systemId
	   * is given.
	   */
	  public void testAbsoluteSystemId()
	  {
	    String result = cacheResolver.resolve(null,"http://www.eclipse.org/webtools", null, "http://www.eclipse.org");
		assertNotNull("The result is null.", result);
	  }
	  
	  /**
	   * Test that the result is sucessfully cached when an absolute systemId
	   * is given and a null base location is given
	   */
	  public void testAbsoluteSystemIdNullBaselocation()
	  {
	    String result = cacheResolver.resolve(null, null, null, "http://www.eclipse.org");
		assertNotNull("The result is null.", result);
	  }
	  
	  /**
	   * Test that the result is sucessfully cached when a relative systemId
	   * is given.
	   */
	  public void testRelativeSystemId()
	  {
	    String result = cacheResolver.resolve(null,"http://www.eclipse.org/webtools/community/somefile.xml", null, "community.html");
		assertNotNull("The result is null.", result);
	  }
	  
	  /**
	   * Test that the result is unsucessfully cached when a relative systemId
	   * is given and a null base location is given.
	   */
	  public void testRelativeSystemIdWillNullBaselocation()
	  {
	    String result = cacheResolver.resolve(null, null, null, "community.html");
		assertNull("The result is not null.", result);
	  }
    
    /**
     * Test that null is returned when the cache is disabled.
     */
    public void testReturnsNullWhenDisabled()
    {
      CachePlugin.getDefault().setCacheEnabled(false);
      String result = cacheResolver.resolve(null, "http://www.eclipse.org/webtools/", null, "http://www.eclipse.org/webtools/");
      assertNull("The result is not null.", result);
      CachePlugin.getDefault().setCacheEnabled(true);
    }
}