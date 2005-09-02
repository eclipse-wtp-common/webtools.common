/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.cache.internal;

import java.util.Hashtable;

/**
 * A wrapper for org.eclipse.wst.internet.cache.internal.LicenseRegistry
 * that allows for testing of protected methods.
 */
public class LicenseRegistryWrapper extends LicenseRegistry 
{
  protected static Integer LICENSE_UNSPECIFIED = LicenseRegistry.LICENSE_UNSPECIFIED;
  protected static Integer LICENSE_AGREE = LicenseRegistry.LICENSE_AGREE;
  protected static Integer LICENSE_DISAGREE = LicenseRegistry.LICENSE_DISAGREE;
  protected LicenseRegistryWrapper()
  {
	super();
  }
  
  protected Hashtable getLicensesHashtable()
  {
	return licenses;
  }

  protected void agreeLicense(String url) 
  {
	super.agreeLicense(url);
  }

  protected void disagreeLicense(String url) 
  {
	super.disagreeLicense(url);
  }

  protected boolean promptToAcceptLicense(String url, String licenseURL) 
  {
	return super.promptToAcceptLicense(url, licenseURL);
  }
}
