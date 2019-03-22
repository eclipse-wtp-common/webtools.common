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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LicenseRegistryTest extends TestCase
{
  /**
   * Create a tests suite from this test class.
   *  
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(LicenseRegistryTest.class);
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
	LicenseRegistryWrapper registry = new LicenseRegistryWrapper();
	registry.addLicense(licenseURL);
	assertEquals("The registry does not have 1 license.", 1, registry.getLicensesHashtable().size());
	assertTrue("The registry does not conain the license " + licenseURL, registry.getLicensesHashtable().containsKey(licenseURL));
	assertEquals("The registry does not have the correct value for the license.", LicenseRegistryWrapper.LICENSE_UNSPECIFIED, registry.getLicensesHashtable().get(licenseURL));
  }
  
  /**
   * Test adding a license to the registry twice. The registry should only have a single entry
   * for the license.
   */
  public void testAddLicenseToRegistryTwice()
  {
	String licenseURL = "http://somelicense";
	LicenseRegistryWrapper registry = new LicenseRegistryWrapper();
	registry.addLicense(licenseURL);
	registry.addLicense(licenseURL);
	assertEquals("The registry contains more than one license.", 1, registry.getLicensesHashtable().size());
  }
  
  /**
   * Test agree to license method. The registry should have 'agree' registered for the license.
   */
  public void testAgreeToLicense()
  {
    String licenseURL = "http://somelicense";
	LicenseRegistryWrapper registry = new LicenseRegistryWrapper();
	registry.addLicense(licenseURL);
	registry.agreeLicense(licenseURL);
	assertEquals("The registry does not have the correct value for the license.", LicenseRegistryWrapper.LICENSE_AGREE, registry.getLicensesHashtable().get(licenseURL));
  }
  
  /**
   * Test agree to license that is not in the registry. The registry should not create
   * an entry for the license.
   */
  public void testAgreeToLicenseNotInRegistry()
  {
    String licenseURL = "http://somelicense";
	LicenseRegistryWrapper registry = new LicenseRegistryWrapper();
	registry.agreeLicense(licenseURL);
	assertEquals("The registry registered the license but it should not have.", 0, registry.getLicensesHashtable().size());
  }
  
  /**
   * Test disagree to license method. The registry should have 'disagree' registered for the license.
   */
  public void testDisgreeToLicense()
  {
    String licenseURL = "http://somelicense";
	LicenseRegistryWrapper registry = new LicenseRegistryWrapper();
	registry.addLicense(licenseURL);
	registry.disagreeLicense(licenseURL);
	assertEquals("The registry does not have the correct value for the license.", LicenseRegistryWrapper.LICENSE_DISAGREE, registry.getLicensesHashtable().get(licenseURL));
  }
  
  /**
   * Test disagree to license that is not in the registry. The registry should not create
   * an entry for the license.
   */
  public void testDisagreeToLicenseNotInRegistry()
  {
    String licenseURL = "http://somelicense";
	LicenseRegistryWrapper registry = new LicenseRegistryWrapper();
	registry.agreeLicense(licenseURL);
	assertEquals("The registry registered the license but it should not have.", 0, registry.getLicensesHashtable().size());
  }
  
  /**
   * Test prompt for agree for license not in registry.
   * TODO: This test does not test what it is supposed to.
   * This test should be corrected if possible.
   */
//  public void testPromptForLicenseNotInRegistry()
//  {
//    String licenseURL = "http://somelicense";
//	LicenseRegistryWrapper registry = new LicenseRegistryWrapper();
//	assertTrue("The prompt for agreement method does not return true for a license not in the registry.", registry.promptToAcceptLicense(licenseURL, licenseURL));
//  }
}
