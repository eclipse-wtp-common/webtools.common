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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog that prompts the user to accept a license agreement.
 */
public class LicenseAcceptanceDialog extends IconAndMessageDialog 
{
  /**
   * Externalized string keys.
   */
  private static final String _UI_CACHE_DIALOG_LICENSE_STATEMENT1 = "_UI_CACHE_DIALOG_LICENSE_STATEMENT1";
  private static final String _UI_CACHE_DIALOG_LICENSE_STATEMENT2 = "_UI_CACHE_DIALOG_LICENSE_STATEMENT2";
  private static final String _UI_CACHE_DIALOG_LICENSE_STATEMENT2_NO_INTERNAL = "_UI_CACHE_DIALOG_LICENSE_STATEMENT2_NO_INTERNAL";
  private static final String _UI_CACHE_DIALOG_LICENSE_STATEMENT2_NO_BROWSER = "_UI_CACHE_DIALOG_LICENSE_STATEMENT2_NO_BROWSER";
  private static final String _UI_CACHE_DIALOG_AGREE_BUTTON = "_UI_CACHE_DIALOG_AGREE_BUTTON";
  private static final String _UI_CACHE_DIALOG_DISAGREE_BUTTON = "_UI_CACHE_DIALOG_DISAGREE_BUTTON";
  private static final String _UI_CACHE_DIALOG_TITLE = "_UI_CACHE_DIALOG_TITLE";

  /**
   * Holds all the dialogs that are currently displayed keyed by the license URL.
   */
  private static Hashtable dialogsInUse = new Hashtable();
  
  /**
   * The URL of the resource.
   */
  private String url;

  /**
   * The URL of the license.
   */
  private String licenseURL;
  
  /**
   * Constructor.
   * 
   * @param parent The parent of this dialog.
   * @param url The license URL.
   */
  protected LicenseAcceptanceDialog(Shell parent, String url, String licenseURL) 
  {
    super(parent);
	this.url = url;
	this.licenseURL = licenseURL;
  }
  
  /**
   * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
   */
  protected void configureShell(Shell shell) 
  {
    super.configureShell(shell);
    shell.setText(CachePlugin.getResourceString(_UI_CACHE_DIALOG_TITLE));
    shell.setImage(null);
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
   */
  protected Control createButtonBar(Composite parent) 
  {
	Composite buttonBar = new Composite(parent, SWT.NONE);
	GridLayout layout = new GridLayout();
	layout.numColumns = 0;
	layout.makeColumnsEqualWidth = true;
	buttonBar.setLayout(layout);
	GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
	buttonBar.setLayoutData(gd);
	
	// Create the agree button.
	createButton(buttonBar, LicenseAcceptanceDialog.OK, 
			CachePlugin.getResourceString(_UI_CACHE_DIALOG_AGREE_BUTTON), false);

	// Create the disagree button.
	createButton(buttonBar, LicenseAcceptanceDialog.CANCEL, 
			CachePlugin.getResourceString(_UI_CACHE_DIALOG_DISAGREE_BUTTON), false);
	
	return buttonBar;
  }

  /**
   * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
   */
  protected Control createContents(Composite parent) 
  {
	Composite composite = new Composite(parent, SWT.NONE);
	GridLayout layout = new GridLayout();
	composite.setLayout(layout);
	GridData gd = new GridData(SWT.FILL);
	gd.widthHint = 500;
	composite.setLayoutData(gd);

	// Display a statement about the license.
	Label licenseText1 = new Label(composite, SWT.NONE);
	licenseText1.setText(CachePlugin.getResourceString(_UI_CACHE_DIALOG_LICENSE_STATEMENT1));
	Label urlText = new Label(composite, SWT.WRAP);
	gd = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
	urlText.setLayoutData(gd);
	urlText.setText(url);
	new Label(composite, SWT.NONE); // Spacing label.
	Label licenseText2 = new Label(composite, SWT.WRAP);
	gd = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
	licenseText2.setLayoutData(gd);
	
	// Display the license in a browser.
	try
	{
	  Browser browser = new Browser(composite, SWT.BORDER);
	  gd = new GridData(GridData.FILL_BOTH);
	  gd.heightHint = 400;
	  browser.setUrl(licenseURL);
	  browser.setLayoutData(gd);
	  licenseText2.setText(CachePlugin.getResourceString(_UI_CACHE_DIALOG_LICENSE_STATEMENT2));
	}
	catch(Throwable e)
	{
	  // The browser throws an exception on platforms that do not support it. 
	  // In this case we need to create an external browser.
	  try
	  {
	    CachePlugin.getDefault().getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(licenseURL));
	    licenseText2.setText(CachePlugin.getResourceString(_UI_CACHE_DIALOG_LICENSE_STATEMENT2_NO_INTERNAL));
	  }
	  catch(Exception ex)
	  {
		// In this case the license cannot be display. Inform the user of this and give them the license location.
		licenseText2.setText(CachePlugin.getResourceString(_UI_CACHE_DIALOG_LICENSE_STATEMENT2_NO_BROWSER, licenseURL));
	  }
	}

	createButtonBar(composite);
		
	return composite;
  }

  /**
   * @see org.eclipse.jface.dialogs.IconAndMessageDialog#getImage()
   */
  protected Image getImage() 
  {
	return getInfoImage();
  }

  /**
   * Prompt the user to accept the specified license. This method creates the
   * dialog and returns the result.
   * 
   * @param parent The parent of this dialog.
   * @param url The URL of the resource for which the license must be accepted.
   * @param licenseURL The license URL.
   * @return True if the license is accepted, false otherwise.
   */
  public static boolean promptForLicense(Shell parent, String url, String licenseURL) throws IOException
  {
	boolean agreedToLicense = false;
	boolean newDialog = true;
	LicenseAcceptanceDialog dialog = null;
	// If the dialog is already displayed for this license use it instead of 
	// displaying another dialog.
	if(dialogsInUse.containsKey(licenseURL))
	{
	  newDialog = false;
	  dialog = (LicenseAcceptanceDialog)dialogsInUse.get(licenseURL);
	}
	else
	{
	  //BufferedReader bufreader = null;
	  InputStream is = null;
//	  StringBuffer source = new StringBuffer();
	  try
	  {
	    URL urlObj = new URL(licenseURL);
	    is = urlObj.openStream();
//        if (urlObj != null)
//        {
//          bufreader = new BufferedReader(new InputStreamReader(urlObj.openStream()));
//
//          if (bufreader != null)
//          {
//            while (bufreader.ready())
//            {
//              source.append(bufreader.readLine());
//            }
//          }
//        } 
	    dialog = new LicenseAcceptanceDialog(parent, url, licenseURL);
	    dialogsInUse.put(licenseURL, dialog);
	    dialog.setBlockOnOpen(true);
	  }
	  catch(Exception e)
	  {
		throw new IOException("The license cannot be opened.");
	  }
	  finally
	  {
//		if(bufreader != null)
//		{
//		  bufreader.close();
//		}
		if(is != null)
		{
		  try
		  {
			is.close();
		  }
		  catch(IOException e)
		  {
		    // Do nothing.
		  }
		}
	  }
	}
	if(dialog != null)
	{
	  dialog.open();
	  
	  if (dialog.getReturnCode() == LicenseAcceptanceDialog.OK) 
	  {
		agreedToLicense = true;
      }
		
	  if(newDialog)
	  {
       dialogsInUse.remove(licenseURL);
	  }
	}
	
	
	 
	return agreedToLicense;
  }
}
