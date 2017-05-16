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
package org.eclipse.wst.internet.cache.tests.internal;
import junit.framework.Test;

import org.eclipse.wst.internet.cache.internal.CacheEntryTest;
import org.eclipse.wst.internet.cache.internal.CacheTest;
import org.eclipse.wst.internet.cache.internal.CacheURIResolverExtensionTest;
import org.eclipse.wst.internet.cache.internal.LicenseRegistryTest;
/**
 * The root test suite that contains all other Cache test suites.
 */
public class AllCacheTests extends junit.framework.TestSuite
{
  /**
   * Create this test suite.
   * 
   * @return This test suite.
   */
  public static Test suite()
  {
    return new AllCacheTests();
  }
  
  /**
   * Constructor
   */
  public AllCacheTests()
  {
    super("AllCacheTests");
	addTest(CacheTest.suite());
	addTest(CacheEntryTest.suite());
	addTest(CacheURIResolverExtensionTest.suite());
	addTest(LicenseRegistryTest.suite());
  }
}