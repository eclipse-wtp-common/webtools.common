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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.internet.cache.internal.Cache;
import org.eclipse.wst.internet.cache.internal.CacheMessages;
import org.eclipse.wst.internet.cache.internal.CachePlugin;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. This page contains options for the cache. The cache can
 * be disabled, the list of entries in the cache can be viewed, and individual
 * entries or the entire cache can be deleted.
 */

public class CachePreferencePage extends PreferencePage implements
    IWorkbenchPreferencePage
{
  protected Button clearButton;

  protected Button deleteButton;

  protected Button enabledButton;
  
  protected Button disagreedLicensesButton;

  protected List entries;

  protected Composite composite = null;

  /**
   * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
   */
  public void dispose()
  {
    if (composite != null)
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
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    layout.horizontalSpacing = convertHorizontalDLUsToPixels(4);
    layout.verticalSpacing = convertVerticalDLUsToPixels(3);
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    composite.setLayout(layout);
    GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
    composite.setLayoutData(gd);
    
    Label aboutLabel = new Label(composite, SWT.WRAP);
    aboutLabel.setText(CacheMessages._UI_PREF_CACHE_ABOUT);
    GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
    gridData.horizontalSpan = 2;
    aboutLabel.setLayoutData(gridData);
    new Label(composite, SWT.None);
    try
    {
      // Created the disable cache option.
      enabledButton = new Button(composite, SWT.CHECK | SWT.LEFT);
      gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
      gridData.horizontalSpan = 2;
      enabledButton.setLayoutData(gridData);
      enabledButton.setText(CacheMessages._UI_PREF_CACHE_CACHE_OPTION);
      enabledButton.setSelection(!CachePlugin.getDefault().getPreferenceStore()
          .getBoolean(PreferenceConstants.CACHE_ENABLED));
      enabledButton.addSelectionListener(new SelectionListener()
      {

        public void widgetDefaultSelected(SelectionEvent e)
        {
          widgetSelected(e);

        }

        public void widgetSelected(SelectionEvent e)
        {
          boolean disabled = enabledButton.getSelection();
          CachePlugin.getDefault().setCacheEnabled(!disabled);
        }

      });
      
      disagreedLicensesButton = new Button(composite, SWT.CHECK | SWT.LEFT);
      gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
      gridData.horizontalSpan = 2;
      disagreedLicensesButton.setLayoutData(gridData);
      disagreedLicensesButton.setText(CacheMessages._UI_PREF_PROMPT_FOR_DISAGREED_LICENSES);
      disagreedLicensesButton.setSelection(CachePlugin.getDefault().getPreferenceStore()
          .getBoolean(PreferenceConstants.PROMPT_DISAGREED_LICENSES));
      disagreedLicensesButton.addSelectionListener(new SelectionListener()
      {

        public void widgetDefaultSelected(SelectionEvent e)
        {
          widgetSelected(e);

        }

        public void widgetSelected(SelectionEvent e)
        {
          boolean prompt = disagreedLicensesButton.getSelection();
          CachePlugin.getDefault().setPromptDisagreedLicenses(prompt);
        }

      });

      // Create the entities group.
      Label entriesLabel = new Label(composite, SWT.WRAP);
      entriesLabel.setText(CacheMessages._UI_PREF_CACHE_ENTRIES_TITLE);
      gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
      gridData.horizontalSpan = 2;
      entriesLabel.setLayoutData(gridData);

      entries = new List(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
      PlatformUI.getWorkbench().getHelpSystem().setHelp(entries,
          ContextIds.PREF_ENTRIES);
      String[] cacheEntries = Cache.getInstance().getCachedURIs();
      Arrays.sort(cacheEntries);
      entries.setItems(cacheEntries);
      gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
          | GridData.VERTICAL_ALIGN_FILL);
      gridData.grabExcessHorizontalSpace = true;
      gridData.grabExcessVerticalSpace = true;
      entries.setLayoutData(gridData);
      entries.addSelectionListener(new SelectionAdapter()
      {
        public void widgetSelected(SelectionEvent event)
        {
          setPreferenceWidgets();
        }
      });

      Composite buttonComposite = new Composite(composite, SWT.NULL);
      GridLayout gridLayout = new GridLayout();
      gridLayout.horizontalSpacing = 0;
      gridLayout.verticalSpacing = convertVerticalDLUsToPixels(3);
      gridLayout.marginWidth = 0;
      gridLayout.marginHeight = 0;
      gridLayout.numColumns = 1;
      buttonComposite.setLayout(gridLayout);
      gridData = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_FILL | SWT.TOP);
      buttonComposite.setLayoutData(gridData);
      // Create the Delete button
      deleteButton = new Button(buttonComposite, SWT.PUSH);
      deleteButton.setText(CacheMessages._UI_BUTTON_DELETE_ENTRY);
      gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
      gridData.grabExcessHorizontalSpace = true;
      deleteButton.setLayoutData(gridData);
      deleteButton.addSelectionListener(new SelectionAdapter()
      {
        public void widgetSelected(SelectionEvent event)
        {
          if (MessageDialog
              .openConfirm(
                  Display.getDefault().getActiveShell(),
                  CacheMessages._UI_CONFIRM_DELETE_CACHE_ENTRY_DIALOG_TITLE,
                  CacheMessages._UI_CONFIRM_DELETE_CACHE_ENTRY_DIALOG_MESSAGE))
          {
            String[] selectedEntries = entries.getSelection();
            int numSelectedEntries = selectedEntries.length;

            Cache cache = Cache.getInstance();
            for (int i = 0; i < numSelectedEntries; i++)
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
      clearButton.setText(CacheMessages._UI_BUTTON_CLEAR_CACHE);
      gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
      gridData.grabExcessHorizontalSpace = true;
      clearButton.setLayoutData(gridData);
      clearButton.addSelectionListener(new SelectionAdapter()
      {
        public void widgetSelected(SelectionEvent event)
        {
          if (MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
              CacheMessages._UI_CONFIRM_CLEAR_CACHE_DIALOG_TITLE,
              CacheMessages._UI_CONFIRM_CLEAR_CACHE_DIALOG_MESSAGE))
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

    } catch (Throwable e)
    {
      //TODO: Log error
    }
    setPreferenceWidgets();
    applyDialogFont(composite);

    return composite;
  }

  /**
   * Set the preference page widgets. There are a few rules. 1. If disabled, all
   * of the widgets are diabled except for the disabled check box. 2. If
   * enabled, all widgets are enabled except a. The delete button is enabled
   * only if there is a selection in the list. b. The clear button is enabled
   * only if there are items in the list.
   */
  public void setPreferenceWidgets()
  {
    if (composite != null && composite.getEnabled())
    {
      if (entries.getSelectionCount() > 0)
      {
        deleteButton.setEnabled(true);
      } 
      else
      {
        deleteButton.setEnabled(false);
      }
      if (entries.getItemCount() > 0)
      {
        clearButton.setEnabled(true);
      } 
      else
      {
        clearButton.setEnabled(false);
      }
      
    }
  }

  /*
   * @see PreferencePage#createControl(Composite)
   */
  public void createControl(Composite parent)
  {
    super.createControl(parent);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), ContextIds.PREF); //$NON-NLS-1$
  }
}