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

package org.eclipse.wst.internet.cache.internal.preferences;

import java.util.Arrays;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wst.internet.cache.internal.Cache;
import org.eclipse.wst.internet.cache.internal.CachePlugin;

/**
 * This class represents a preference page that is contributed to the 
 * Preferences dialog. This page contains options for the cache. The cache
 * can be disabled, the list of entries in the cache can be viewed, and 
 * individual entries or the entire cache can be deleted.
 */

public class CachePreferencePage extends PreferencePage implements IWorkbenchPreferencePage 
{
	private static final String _UI_CONFIRM_CLEAR_CACHE_DIALOG_TITLE = "_UI_CONFIRM_CLEAR_CACHE_DIALOG_TITLE";
	private static final String _UI_CONFIRM_CLEAR_CACHE_DIALOG_MESSAGE = "_UI_CONFIRM_CLEAR_CACHE_DIALOG_MESSAGE";
	private static final String _UI_BUTTON_CLEAR_CACHE = "_UI_BUTTON_CLEAR_CACHE";
	private static final String _UI_BUTTON_DELETE_ENTRY = "_UI_BUTTON_DELETE_ENTRY";
	private static final String _UI_PREF_CACHE_ENTRIES_TITLE = "_UI_PREF_CACHE_ENTRIES_TITLE";
  private static final String _UI_PREF_CACHE_OPTIONS_TITLE = "_UI_PREF_CACHE_OPTIONS_TITLE";
  private static final String _UI_PREF_CACHE_CACHE_OPTION = "_UI_PREF_CACHE_CACHE_OPTION";
  private static final String _UI_CONFIRM_DELETE_CACHE_ENTRY_DIALOG_TITLE = "_UI_CONFIRM_DELETE_CACHE_ENTRY_DIALOG_TITLE";
  private static final String _UI_CONFIRM_DELETE_CACHE_ENTRY_DIALOG_MESSAGE = "_UI_CONFIRM_DELETE_CACHE_ENTRY_DIALOG_MESSAGE";
	
	protected Button clearButton;
	protected Button deleteButton;
  protected Button enabledButton;
	protected List entries;
  protected Composite composite = null;
  protected Group entriesGroup;

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
	 */
	public void dispose()
  {
    if(composite != null)
    {
      composite.dispose();
    }
    super.dispose();
  }

  /**
   * Constructor.
   */
  public CachePreferencePage() 
	{
		setPreferenceStore(CachePlugin.getDefault().getPreferenceStore());
	}
	
	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) 
	{
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) 
	{
		noDefaultAndApplyButton();
		composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
	    composite.setLayoutData(gd);
      try
      {
		GridLayout gridLayout = new GridLayout();
    
    // Created the disable cache option.
    enabledButton = new Button(composite, SWT.CHECK | SWT.LEFT);
    enabledButton.setText(CachePlugin.getResourceString(_UI_PREF_CACHE_CACHE_OPTION));
    enabledButton.setSelection(!CachePlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.CACHE_ENABLED));
    enabledButton.addSelectionListener(new SelectionListener(){

      public void widgetDefaultSelected(SelectionEvent e)
      {
        widgetSelected(e);
        
      }

      public void widgetSelected(SelectionEvent e)
      {
        boolean disabled = enabledButton.getSelection();
        CachePlugin.getDefault().setCacheEnabled(!disabled);
        setPreferenceWidgets();
      }
      
      
      
    });
		
		// Create the entities group.
		entriesGroup = new Group(composite, SWT.NONE);
	  entriesGroup.setText(CachePlugin.getResourceString(_UI_PREF_CACHE_ENTRIES_TITLE));
	  gridLayout = new GridLayout();
	  entriesGroup.setLayout(gridLayout);
	  GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
	  entriesGroup.setLayoutData(gridData);
    
		entries = new List(entriesGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		String[] cacheEntries = Cache.getInstance().getCachedURIs();
		Arrays.sort(cacheEntries);
		entries.setItems(cacheEntries);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
	    entries.setLayoutData(gridData);
		entries.addSelectionListener(new SelectionAdapter() {
	          public void widgetSelected(SelectionEvent event) {
              setPreferenceWidgets();
	          }
	       });
		
		Composite buttonComposite = new Composite(composite, SWT.NULL);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		buttonComposite.setLayout(gridLayout);
		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL |GridData.HORIZONTAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_END);
	    composite.setLayoutData(gridData);
		// Create the Delete button
		deleteButton = new Button(buttonComposite, SWT.PUSH);
	    deleteButton.setText(CachePlugin.getResourceString(_UI_BUTTON_DELETE_ENTRY));
	    gridData = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_END);
	    deleteButton.setLayoutData(gridData);
	    deleteButton.addSelectionListener(new SelectionAdapter() {
	          public void widgetSelected(SelectionEvent event) {
              if(MessageDialog.openConfirm(Display.getDefault().getActiveShell(), CachePlugin.getResourceString(_UI_CONFIRM_DELETE_CACHE_ENTRY_DIALOG_TITLE), CachePlugin.getResourceString(_UI_CONFIRM_DELETE_CACHE_ENTRY_DIALOG_MESSAGE)))
              {
	              String[] selectedEntries = entries.getSelection();
				  int numSelectedEntries = selectedEntries.length;
				  
				  Cache cache = Cache.getInstance();
				  for(int i = 0; i < numSelectedEntries; i++)
				  {
				    cache.deleteEntry(selectedEntries[i]);
				  }
				  String[] cacheEntries = cache.getCachedURIs();
				  Arrays.sort(cacheEntries);
				  entries.setItems(cacheEntries);
          setPreferenceWidgets();
	          }
            }
	       });
		
		// Create the Clear Cache button
		clearButton = new Button(buttonComposite, SWT.PUSH);
	    clearButton.setText(CachePlugin.getResourceString(_UI_BUTTON_CLEAR_CACHE));
	    gridData = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_END);
	    clearButton.setLayoutData(gridData);
	    clearButton.addSelectionListener(new SelectionAdapter() {
	          public void widgetSelected(SelectionEvent event) {
				  if(MessageDialog.openConfirm(Display.getDefault().getActiveShell(), CachePlugin.getResourceString(_UI_CONFIRM_CLEAR_CACHE_DIALOG_TITLE), CachePlugin.getResourceString(_UI_CONFIRM_CLEAR_CACHE_DIALOG_MESSAGE)))
				  {
					  Cache cache = Cache.getInstance();
					  cache.clear();
					  String[] cacheEntries = cache.getCachedURIs();
					  Arrays.sort(cacheEntries);
					  entries.setItems(cacheEntries);
            setPreferenceWidgets();
				  }
	          }
	       });
      
      }
      catch(Throwable e)
      {
        System.out.println(e);
      }
      setPreferenceWidgets();
      
		return composite;
	}
  
  /**
   * Set the preference page widgets. There are a few rules.
   * 1. If disabled, all of the widgets are diabled except for the disabled check box.
   * 2. If enabled, all widgets are enabled except
   *  a. The delete button is enabled only if there is a selection in the list.
   *  b. The clear button is enabled only if there are items in the list.
   */
  public void setPreferenceWidgets()
  {
    if(composite != null && composite.getEnabled())
    {
      // Cache is disabled.
      if(enabledButton.getSelection())
      {
        deleteButton.setEnabled(false);
        clearButton.setEnabled(false);
        entriesGroup.setEnabled(false);
        entries.setEnabled(false);
      }
      else
      {
        entriesGroup.setEnabled(true);
        entries.setEnabled(true);
        if(entries.getSelectionCount() > 0)
        {
          deleteButton.setEnabled(true);
        }
        else
        {
          deleteButton.setEnabled(false);
        }
        if(entries.getItemCount() > 0)
        {
          clearButton.setEnabled(true);
        }
        else
        {
          clearButton.setEnabled(false);
        }
      }
    }
  }
	
	
}