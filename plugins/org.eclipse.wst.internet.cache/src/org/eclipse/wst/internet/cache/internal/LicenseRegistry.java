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

import java.util.Hashtable;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;

/**
 * The license registry holds all of the registered licenses and whether or not they
 * have been accepted.
 */
public class LicenseRegistry 
{
  protected static Integer LICENSE_UNSPECIFIED = new Integer(0); // This needs to be 0 for the plugin prefs.
  protected static Integer LICENSE_AGREE = new Integer(1);
  protected static Integer LICENSE_DISAGREE = new Integer(2);
  
  protected final static String _LOG_INFO_WTP_NO_USER_INTERACTION = "_LOG_INFO_WTP_NO_USER_INTERACTION";
  
  protected final static String WTP_NO_USER_INTERACTION_SYSTEM_PROP = CachePlugin.getResourceString("WTP_NO_USER_INTERACTION_SYSTEM_PROP");
  
  /**
   * There is only one instance of the license registry.
   */
  protected static LicenseRegistry instance = null;
  
  /**
   * If set to true, the do not prompt flag will prevent user prompting. Used for automated testing.
   */
  private boolean DO_NOT_PROMPT = false;
  
  /**
   * The licenses hashtable contains licenses and whether or not they've been accepted.
   */
  protected Hashtable licenses;
  
  protected LicenseRegistry()
  {
	licenses = new Hashtable();
	
	// If the wtp quiet system property is set the DO_NOT_PROMPT flag is set to true.
	// This is used for automated testing.
	if(System.getProperty(WTP_NO_USER_INTERACTION_SYSTEM_PROP, "false").equals("true"))
	{
	  CachePlugin.getDefault().getLog().log(new Status(IStatus.INFO, CachePlugin.PLUGIN_ID, IStatus.OK, CachePlugin.getResourceString(_LOG_INFO_WTP_NO_USER_INTERACTION, WTP_NO_USER_INTERACTION_SYSTEM_PROP), null));
	  DO_NOT_PROMPT = true;
	}
  }
  
  /**
   * Get the one and only instance of the license registry.
   * 
   * @return The one and only instance of the license registry.
   */
  public static LicenseRegistry getInstance()
  {
	if(instance == null)
	{
	  instance = new LicenseRegistry();
	}
	return instance;
  }
  
  /**
   * Add the specified license to the license registry. A license can only be added to the
   * registry once.
   * 
   * @param url The URL of the license to add to the registry.
   */
  public void addLicense(String url)
  {
	if(url != null && !licenses.containsKey(url))
	{
	  licenses.put(url, LICENSE_UNSPECIFIED);
	}
  }
  
  /**
   * Agree to the specified license. The license is agreed to only if it has already
   * been registered with the registry.
   * 
   * @param url The URL of the license to accept.
   */
  protected void agreeLicense(String url)
  {
	if(licenses.containsKey(url))
	{
	  licenses.put(url, LICENSE_AGREE);
	}
  }
  
  /**
   * Disagree to the specified license. The license is disagreed to only if it has already
   * been registered with the registry.
   * 
   * @param url The URL of the license to accept.
   */
  protected void disagreeLicense(String url)
  {
	if(licenses.containsKey(url))
	{
	  licenses.put(url, LICENSE_DISAGREE);
	}
  }
  
  /**
   * Determine if the license has been accepted. This method will return true if the license
   * has been accepted or is not registered with the registry and false if it has not been accepted. 
   * 
   * @param url The URL of the resource for which we are checking the license.
   * @param licenseURL The URL of the license that should be checked to see if it has been accepted.
   * @return True if the license has been accepted or is not registered with the registry, false otherwise.
   */
  public boolean hasLicenseBeenAccepted(String url, String licenseURL)
  {
	if(DO_NOT_PROMPT)
	{
	  return true;
	}
	
	if(licenses.containsKey(licenseURL))
	{
	  Integer agreed = (Integer)licenses.get(licenseURL);
	  if(agreed == LICENSE_AGREE)
	  {
		return true;
	  }
	  else if(agreed == LICENSE_DISAGREE)
	  {
		if(!CachePlugin.getDefault().shouldPrompt())
		  return false;
	  }
	  
	  // Prompt the user to accept the license.
	  if(promptToAcceptLicense(url, licenseURL))
	  {
		agreeLicense(licenseURL);
		return true;
	  }
	  disagreeLicense(licenseURL);
	  return false;
	}
	
	// The license is not registred with the registry.
	return true;
  }
  
  /**
   * Prompt the user to accept the license. This method creates a LicenseAcceptanceDialog.
   * 
   * @param url The URL of the resource for which the license needs to be accepted.
   * @param licenseURL The URL of the license to be accepted. 
   * @return True if the license is accepted, false otherwise.
   */
  protected boolean promptToAcceptLicense(final String url, final String licenseURL)
  {
	final Accepted accepted = new Accepted();
	  Display.getDefault().syncExec(new Runnable() {
			public void run() {
				accepted.accepted = LicenseAcceptanceDialog.promptForLicense(null, url, licenseURL);
			}
		});
	return accepted.accepted;
  }
  
  /**
   * Get an array containing all the licenses in the registry.
   * 
   * @return As array containing all the licenses in the registry. 
   */
  protected String[] getLicenses()
  {
	return (String[])licenses.keySet().toArray(new String[licenses.keySet().size()]);
  }
  
  /**
   * Get the state of the license.
   * 
   * @param url The URL of the license.
   * @return The state of the license.
   */
  protected Integer getLicenseState(String url)
  {
	return (Integer)licenses.get(url);
  }
  
  
  /**
   * A class to hold acceptance information for the prompt method.
   */
  private class Accepted
  {
	public boolean accepted = false;
  }

}
