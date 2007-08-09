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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Hashtable;

import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
   * The agree button for the dialog.
   */
  private Button agreeButton = null;
  
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
    shell.setText(CacheMessages._UI_CACHE_DIALOG_TITLE);
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
	agreeButton = createButton(buttonBar, LicenseAcceptanceDialog.OK, 
			CacheMessages._UI_CACHE_DIALOG_AGREE_BUTTON, false);
	agreeButton.setEnabled(false);
	
	// Create the disagree button.
	createButton(buttonBar, LicenseAcceptanceDialog.CANCEL, 
			CacheMessages._UI_CACHE_DIALOG_DISAGREE_BUTTON, false);
	
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
	licenseText1.setText(CacheMessages._UI_CACHE_DIALOG_LICENSE_STATEMENT1);
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
	  final Composite licenseTextComposite = new Composite(composite, SWT.NONE);
	  final StackLayout stackLayout = new StackLayout();
	  licenseTextComposite.setLayout(stackLayout);
	  gd = new GridData(SWT.FILL, SWT.FILL, true, true);
	  gd.heightHint = 400;
	  licenseTextComposite.setLayoutData(gd);
	  // Create the loading progress monitor composite and part.
	  Composite monitorComposite = new Composite(licenseTextComposite, SWT.NONE);
	  monitorComposite.setLayout(new GridLayout());
	  gd = new GridData(SWT.FILL, SWT.FILL, true, true);
	  gd.heightHint = 400;
	  monitorComposite.setLayoutData(gd);
	  final ProgressMonitorPart monitor = new ProgressMonitorPart(monitorComposite, new GridLayout());
	  gd = new GridData(SWT.FILL, SWT.BOTTOM, true, true);
	  monitor.setLayoutData(gd);
	  monitor.beginTask(CacheMessages._UI_LOADING_LICENSE, 100);
	  stackLayout.topControl = monitorComposite;
	  // Create the browser.
	  final Browser browser = new Browser(licenseTextComposite, SWT.BORDER);
	  gd = new GridData(SWT.FILL, SWT.FILL, true, true);
	  
	  // It's important that the license URL is set even if we read 
	  // the contents of the license file ourselves (see below) as
	  // otherwise the progress monitor will not be called on certain
	  // linux systems with certain browsers.
	  browser.setUrl(licenseURL);
	  
	  // The browser widget has problems loading files stored in jars
	  // so we read from the jar and set the browser text ourselves.
	  // See bug 154721.
	  if(licenseURL.startsWith("jar:"))
	  {
		  InputStream licenseStream = null;
		  InputStreamReader isreader = null;
		  BufferedReader breader = null;
		  try
		  {
		  URL browserURL = new URL(licenseURL);
		  licenseStream = browserURL.openStream();
		  isreader = new InputStreamReader(licenseStream);
		  breader = new BufferedReader(isreader);
		  String str;
		  StringBuffer sb = new StringBuffer();
		  while((str = breader.readLine())!=null){
		      sb.append(str);
		  }
		  browser.setText(sb.toString());
		  }
		  finally
		  {
			  if(licenseStream != null)
			  {
				  licenseStream.close();
			  }
			  if(isreader != null)
			  {
				  isreader.close();
			  }
			  if(breader != null)
			  {
				  breader.close();
			  }
		  }
	  }
	  
	  browser.setLayoutData(gd);
	  browser.addProgressListener(new ProgressListener(){

		/* (non-Javadoc)
		 * @see org.eclipse.swt.browser.ProgressListener#changed(org.eclipse.swt.browser.ProgressEvent)
		 */
		public void changed(ProgressEvent event) 
		{
		  if (event.total != 0)
		  {
		    monitor.internalWorked(event.current * 100 / event.total);
		  }
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.browser.ProgressListener#completed(org.eclipse.swt.browser.ProgressEvent)
		 */
		public void completed(ProgressEvent event) 
		{
		  monitor.done();
			
		  stackLayout.topControl = browser;
		  agreeButton.setEnabled(true);
		  licenseTextComposite.layout();
		}
	  });
	  
	  licenseText2.setText(CacheMessages._UI_CACHE_DIALOG_LICENSE_STATEMENT2);
	}
	catch(Throwable e)
	{
	  // The browser throws an exception on platforms that do not support it. 
	  // In this case we need to create an external browser.
	  try
	  {
	    CachePlugin.getDefault().getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(licenseURL));
	    licenseText2.setText(CacheMessages._UI_CACHE_DIALOG_LICENSE_STATEMENT2_NO_INTERNAL);
	  }
	  catch(Exception ex)
	  {
		// In this case the license cannot be display. Inform the user of this and give them the license location.
		licenseText2.setText(MessageFormat.format(CacheMessages._UI_CACHE_DIALOG_LICENSE_STATEMENT2_NO_BROWSER, new Object[]{licenseURL}));
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
	  InputStream is = null;

	  try
	  {
	    URL urlObj = new URL(licenseURL);
	    is = urlObj.openStream();

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

  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#close()
   */
  public boolean close() 
  {
	if(agreeButton != null)
	{
	  agreeButton.dispose();
	  agreeButton = null;
	}
	return super.close();
  }
  
}
