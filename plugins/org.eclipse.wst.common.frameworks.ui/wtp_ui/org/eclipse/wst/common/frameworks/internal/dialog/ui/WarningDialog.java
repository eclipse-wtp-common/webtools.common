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
package org.eclipse.wst.common.frameworks.internal.dialog.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;


/**
 * A dialog to display one or more errors to the user, as contained in an
 * <code>Status</code> object. If an error contains additional detailed
 * information then a Details button is automatically supplied, which shows or
 * hides an error details viewer when pressed by the user.
 *  
 */
public class WarningDialog extends MessageDialog
{
  /**
   * Creates an error dialog. Note that the dialog will have no visual
   * representation (no widgets) until it is told to open.
   * <p>
   * Normally one should use <code>openError</code> to create and open one of
   * these. This constructor is useful only if the error object being displayed
   * contains child items <it>and</it> you need to specify a mask which will
   * be used to filter the displaying of these children.
   * </p>
   * 
   * @param parentShell
   *            the shell under which to create this dialog
   * @param dialogTitle
   *            the title to use for this dialog, or <code>null</code> to
   *            indicate that the default title should be used
   * @param message
   *            the message to show in this dialog, or <code>null</code> to
   *            indicate that the error's message should be shown as the
   *            primary message
   * @param status
   *            the error to show to the user
   * @param displayMask
   *            the mask to use to filter the displaying of child items, as per
   *            <code>Status.matches</code>
   */
  public WarningDialog(
    Shell parentShell,
    String dialogTitle,
    String message,
    IStatus status,
    int displayMask)
  {
    super(parentShell, dialogTitle, message, status, displayMask);
  }

  /*
   * (non-Javadoc) This should also be overwritten Method declared on Dialog.
   */
  protected void createButtonsForButtonBar(Composite parent)
  {
    // create OK, Cancel and Details buttons
    createButton(
      parent,
      StatusDialogConstants.OK_ID,
      IDialogConstants.OK_LABEL,
      true);
    createButton(
      parent,
      StatusDialogConstants.CANCEL_ID,
      IDialogConstants.CANCEL_LABEL,
      false);
    if (status.isMultiStatus())
    {
      detailsButton =
        createButton(
          parent,
          StatusDialogConstants.DETAILS_ID,
          IDialogConstants.SHOW_DETAILS_LABEL,
          false);
    }
  }

  /*
   * This is one of the few methods that needs to be overwritten by the
   * subclasses. The image names can be found in the Dialog class
   */
  protected Image getDialogImage()
  {
    // create image
    return PlatformUI.getWorkbench().getDisplay().getSystemImage(SWT.ICON_WARNING);
  }

}
