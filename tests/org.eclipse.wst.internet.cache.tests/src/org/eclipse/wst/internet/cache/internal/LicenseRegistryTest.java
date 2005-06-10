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

public class LicenseRegistryTest extends TestCase
{
  private LicenseRegistry registry = LicenseRegistry.getInstance();
  /**
   * Create a tests suite from this test class.
   *  
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(CacheTest.class);
  }

  protected void setUp() throws Exception 
  {
	super.setUp();
  }

  protected void tearDown() throws Exception 
  {
	super.tearDown();
  }
  
  /**
   * Test adding a license to the registry. When added the registry should contain the license
   * and the license should have the unspecified value.
   */
  public void testAddLicenseToRegistry()
  {
	String licenseURL = "http://somelicense";
	LicenseRegistry registry = new LicenseRegistry();
	registry.addLicense(licenseURL);
	assertEquals("The registry does not have 1 license.", 1, registry.licenses.size());
	assertTrue("The registry does not conain the license " + licenseURL, registry.licenses.containsKey(licenseURL));
	assertEquals("The registry does not have the correct value for the license.", LicenseRegistry.LICENSE_UNSPECIFIED, registry.licenses.get(licenseURL));
  }
  
  /**
   * Test adding a license to the registry twice. The registry should only have a single entry
   * for the license.
   */
  public void testAddLicenseToRegistryTwice()
  {
	String licenseURL = "http://somelicense";
	LicenseRegistry registry = new LicenseRegistry();
	registry.addLicense(licenseURL);
	registry.addLicense(licenseURL);
	assertEquals("The registry contains more than one license.", 1, registry.licenses.size());
  }
  
  /**
   * Test agree to license method. The registry should have 'agree' registered for the license.
   */
  public void testAgreeToLicense()
  {
    String licenseURL = "http://somelicense";
	LicenseRegistry registry = new LicenseRegistry();
	registry.addLicense(licenseURL);
	registry.agreeLicense(licenseURL);
	assertEquals("The registry does not have the correct value for the license.", LicenseRegistry.LICENSE_AGREE, registry.licenses.get(licenseURL));
  }
  
  /**
   * Test agree to license that is not in the registry. The registry should not create
   * an entry for the license.
   */
  public void testAgreeToLicenseNotInRegistry()
  {
    String licenseURL = "http://somelicense";
	LicenseRegistry registry = new LicenseRegistry();
	registry.agreeLicense(licenseURL);
	assertEquals("The registry registered the license but it should not have.", 0, registry.licenses.size());
  }
  
  /**
   * Test disagree to license method. The registry should have 'disagree' registered for the license.
   */
  public void testDisgreeToLicense()
  {
    String licenseURL = "http://somelicense";
	LicenseRegistry registry = new LicenseRegistry();
	registry.addLicense(licenseURL);
	registry.disagreeLicense(licenseURL);
	assertEquals("The registry does not have the correct value for the license.", LicenseRegistry.LICENSE_DISAGREE, registry.licenses.get(licenseURL));
  }
  
  /**
   * Test disagree to license that is not in the registry. The registry should not create
   * an entry for the license.
   */
  public void testDisagreeToLicenseNotInRegistry()
  {
    String licenseURL = "http://somelicense";
	LicenseRegistry registry = new LicenseRegistry();
	registry.agreeLicense(licenseURL);
	assertEquals("The registry registered the license but it should not have.", 0, registry.licenses.size());
  }
  
  /**
   * Test prompt for agree for license not in registry.
   */
  public void testPromptForLicenseNotInRegistry()
  {
    String licenseURL = "http://somelicense";
	LicenseRegistry registry = new LicenseRegistry();
	assertTrue("The prompt for agreement method does not return true for a license not in the registry.", registry.promptToAcceptLicense(licenseURL, licenseURL));
  }
}
