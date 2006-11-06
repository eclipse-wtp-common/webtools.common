/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 * David Schneider, david.schneider@unisys.com - [142500] WTP properties pages fonts don't follow Eclipse preferences
 *******************************************************************************/

package org.eclipse.wst.validation.internal.ui;

import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.delegates.ValidatorDelegateDescriptor;
import org.eclipse.wst.validation.internal.delegates.ValidatorDelegatesRegistry;

/**
 * Dialog used to allow the user to select a validator delegate from the list of
 * registered delegates for a given delegating validator.
 */
public class DelegatingValidatorPreferencesDialog extends Dialog
{
  /**
   * The delegating validator's descriptor.
   */
  private ValidatorMetaData delegatingValidatorDescriptor;

  /**
   * The selected validator delegate ID.
   */
  private String delegateID;

  /**
   * Constructs the dialog on the given shell.
   * 
   * @param parentShell
   *          the dialog's parent. Must not be null.
   * @param targetID
   *          the delegating validator's id
   * 
   * @param delegateID
   *          the ID of the currently selected validator delegate.
   */
  protected DelegatingValidatorPreferencesDialog(Shell parentShell, ValidatorMetaData vmd, String delegateID)
  {
    super(parentShell);

    delegatingValidatorDescriptor = vmd;
    this.delegateID = delegateID;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  protected Control createDialogArea(Composite parent)
  {
    super.createDialogArea(parent);

    getShell().setText(ResourceHandler.getExternalizedMessage(ResourceConstants.DELEGATES_DIALOG_TITLE));

    GridLayout layout = new GridLayout();
    parent.setLayout(layout);

    Label label = new Label(parent, SWT.NONE);
    GridData labelData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    labelData.widthHint = 250;
    label.setLayoutData(labelData);
    label.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
    String delegatingValidatorName = delegatingValidatorDescriptor.getValidatorDisplayName();
    label.setText(delegatingValidatorName);

    Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
    GridData separatorData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    separator.setLayoutData(separatorData);

    Composite group = new Composite(parent, SWT.NONE);
    GridData groupGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    group.setLayoutData(groupGridData);
    GridLayout groupLayout = new GridLayout(2, false);
    group.setLayout(groupLayout);

    Label comboLabel = new Label(group, SWT.NONE);
    comboLabel.setLayoutData(new GridData());
    comboLabel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.DELEGATES_COMBO_LABEL));
    
    Combo combo = new Combo(group, SWT.READ_ONLY);
    GridData comboGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    combo.setLayoutData(comboGridData);

    final ComboViewer comboViewer = new ComboViewer(combo);
    comboViewer.setContentProvider(new DelegatesContentProvider());
    comboViewer.setLabelProvider(new DelegatesLabelProvider());
    String targetID = delegatingValidatorDescriptor.getValidatorUniqueName();

    comboViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {

      public void selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent event)
      {
        IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
        setDelegateID(((ValidatorDelegateDescriptor) selection.getFirstElement()).getId());
      }
    });

    comboViewer.setInput(targetID);

    ValidatorDelegateDescriptor selected = ValidatorDelegatesRegistry.getInstance().getDescriptor(targetID, delegateID);

    if (selected != null)
    {
      comboViewer.setSelection(new StructuredSelection(new Object[] { selected }));
    }

    Label endSeparator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
    GridData endSeparatorData = new GridData(SWT.FILL, SWT.CENTER, true, false); 
    endSeparator.setLayoutData(endSeparatorData);
    Dialog.applyDialogFont(parent);
    
    return parent;
  }

  /**
   * Provides contents for the delegate validators combo box.
   */
  private final class DelegatesContentProvider implements IStructuredContentProvider
  {
    public void dispose()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement)
    {
      String targetID = (String) inputElement;
      Map delegatesByID = ValidatorDelegatesRegistry.getInstance().getDelegateDescriptors(targetID);
      
      if (delegatesByID == null)
      {
        return new Object[] {};
      }

      return delegatesByID.values().toArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }
  }

  /**
   * Provides the labels/images for the delegate validator combo box
   * 
   * @author vbaciul
   * 
   */
  private final class DelegatesLabelProvider implements ILabelProvider
  {
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener listener)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    public Image getImage(Object element)
    {
      return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    public String getText(Object element)
    {
      return ((ValidatorDelegateDescriptor) element).getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
     *      java.lang.String)
     */
    public boolean isLabelProperty(Object element, String property)
    {
      return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener listener)
    {
    }
  }

  /*
   * Provides the ID of the currently selected validator delegate ID.
   */
  public String getDelegateID()
  {
    return delegateID;
  }

  /**
   * Sets the currently selected validator delegate ID.
   * 
   * @param delegateID
   */
  private void setDelegateID(String delegateID)
  {
    this.delegateID = delegateID;
  }
}
