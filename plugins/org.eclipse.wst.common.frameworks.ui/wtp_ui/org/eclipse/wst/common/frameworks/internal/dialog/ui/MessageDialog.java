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

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.environment.Choice;


/**
 * A dialog to display one or more errors to the user, as contained in an
 * <code>Status</code> object. If an error contains additional detailed
 * information then a Details button is automatically supplied, which shows or
 * hides an error details viewer when pressed by the user.
 *  
 */
public class MessageDialog extends Dialog
{
  protected Composite parent;

  /**
   * Reserve room for this many list items.
   */
  protected static final int LIST_ITEM_COUNT = 7;

  /**
   * The Details button.
   */
  protected Button detailsButton;

  /**
   * The title of the dialog.
   */
  protected String title;

  /**
   * The message to display.
   */
  protected String message;

  /**
   * The SWT list control that displays the error details.
   */
  protected List list;
  protected Text details;

  /**
   * Indicates whether the error details viewer is currently created.
   */
  protected boolean listCreated = false;
  protected boolean detailsCreated = false;

  /**
   * Filter mask for determining which status items to display.
   */
  protected int displayMask = 0xFFFF;

  /**
   * The main status object.
   */
  protected IStatus status;
  
  private Point savedSize = null;

  /**
   * List of the main error object's detailed errors (element type: <code>Status</code>).
   */
  protected java.util.List statusList;
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
  public MessageDialog(
    Shell parentShell,
    String dialogTitle,
    String message,
    IStatus status,
    int displayMask)
  {
    super(parentShell);
      this.title = dialogTitle == null ? JFaceResources.getString("Problem_Occurred") : //$NON-NLS-1$
  dialogTitle;
    this.message = message == null ? status.getMessage() : JFaceResources.format("Reason", new Object[] { message, status.getMessage()}); //$NON-NLS-1$
    this.status = status;
    statusList = Arrays.asList(status.getChildren());
    this.displayMask = displayMask;
    setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
  }
  /*
   * (non-Javadoc) Method declared on Dialog. Handles the pressing of the Ok or
   * Details button in this dialog. If the Ok button was pressed then close
   * this dialog. If the Details button was pressed then toggle the displaying
   * of the error details area. Note that the Details button will only be
   * visible if the error being displayed specifies child details.
   */
  protected void buttonPressed(int id)
  {
    if (id == StatusDialogConstants.DETAILS_ID)
    { // was the details button pressed?
      toggleDetailsArea();
    }
    else
    {
      super.buttonPressed(id);
    }
  }
  /*
   * (non-Javadoc) Method declared in Window.
   */
  protected void configureShell(Shell shell)
  {
    super.configureShell(shell);
    shell.setText(title);
  }
  /*
   * (non-Javadoc) This should also be overwritten Method declared on Dialog.
   */
  protected void createButtonsForButtonBar(Composite parent)
  {
    // create OK and Details buttons
    createButton(
      parent,
      StatusDialogConstants.OK_ID,
      IDialogConstants.OK_LABEL,
      true);
    if (status.isMultiStatus() || status.getException() != null )
    {
      detailsButton =
        createButton(
          parent,
          StatusDialogConstants.DETAILS_ID,
          IDialogConstants.SHOW_DETAILS_LABEL,
          false);
    }
    
    parent.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
  }

  /*
   * This is one of the few methods that needs to be overwritten by the
   * subclasses. The image names can be found in the Dialog class
   */
  protected Image getDialogImage()
  {
    // create image
    return PlatformUI.getWorkbench().getDisplay().getSystemImage(SWT.ICON_INFORMATION);
  }

  /*
   * (non-Javadoc) Method declared on Dialog. Creates and returns the contents
   * of the upper part of the dialog (above the button bar).
   */
  protected Control createDialogArea(Composite parent)
  {
    this.parent = parent;

    // create composite
    Composite composite = (Composite) super.createDialogArea(parent);
    Composite imageAndLabel = new Composite(composite, SWT.NONE);
    GridLayout gl = new GridLayout();
    gl.numColumns = 2;
    imageAndLabel.setLayout(gl);
    composite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ));
    
    // create image
    Image image = getDialogImage();
    if (image != null)
    {
      Label label = new Label(imageAndLabel, 0);
      image.setBackground(label.getBackground());
      label.setImage(image);
      label.setLayoutData(
        new GridData(
          GridData.HORIZONTAL_ALIGN_CENTER
            | GridData.VERTICAL_ALIGN_BEGINNING));
    }

    // create message
    if (message != null)
    {
      Text text = new Text(imageAndLabel, SWT.READ_ONLY|SWT.WRAP);
      text.setText(message);
      GridData data =
        new GridData(
          GridData.GRAB_HORIZONTAL
            | GridData.GRAB_VERTICAL
            | GridData.HORIZONTAL_ALIGN_FILL
            | GridData.VERTICAL_ALIGN_CENTER);
      data.widthHint =
        convertHorizontalDLUsToPixels(
          IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
     
      text.setLayoutData(data);
      text.setFont(parent.getFont());
    }

    return composite;
  }

  protected List createDropDownList(Composite parent)
  {
    // create the list
    list = new List(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

    // fill the list
    populateList(list);

    GridData data =
      new GridData(
        GridData.HORIZONTAL_ALIGN_FILL
          | GridData.GRAB_HORIZONTAL
          | GridData.VERTICAL_ALIGN_FILL
          | GridData.GRAB_VERTICAL);
    data.heightHint = list.getItemHeight() * LIST_ITEM_COUNT;
    list.setLayoutData(data);
    listCreated = true;
    return list;
  }
  protected Text createDropDownDetails(Composite parent)
  {
    details = new Text(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
    //details.setEditable(false);
    Color color = new Color(parent.getShell().getDisplay(), 255, 255, 255);
    details.setBackground(color);
    populateDetails(details, status, 0);
    GridData data =
      new GridData(
        GridData.HORIZONTAL_ALIGN_FILL
          | GridData.GRAB_HORIZONTAL
          | GridData.VERTICAL_ALIGN_FILL
          | GridData.GRAB_VERTICAL);
    details.setLayoutData(data);
    details.setSelection(0);
    detailsCreated = true;
    return details;
  }
  /*
   * (non-Javadoc) Method declared on Window.
   */
  /**
   * Extends <code>Window.open()</code>. Opens an error dialog to display
   * the error. If you specified a mask to filter the displaying of these
   * children, the error dialog will only be displayed if there is at least one
   * child status matching the mask.
   */
  public int open()
  {
    if (shouldDisplay(status, displayMask))
    {
      return super.open();
    }
    return 0;
  }
  /**
   * This essentially does the work of a factory Opens an error dialog to
   * display the given error. Use this method if the error object being
   * displayed does not contain child items, or if you wish to display all such
   * items without filtering.
   * 
   * @param parent
   *            the parent shell of the dialog, or <code>null</code> if none
   * @param dialogTitle
   *            the title to use for this dialog, or <code>null</code> to
   *            indicate that the default title should be used
   * @param message
   *            the message to show in this dialog, or <code>null</code> to
   *            indicate that the error's message should be shown as the
   *            primary message
   * @param status
   *            the error to show to the user
   * @return the code of the button that was pressed that resulted in this
   *         dialog closing. This will be <code>Dialog.OK</code> if the OK
   *         button was pressed, or <code>Dialog.CANCEL</code> if this
   *         dialog's close window decoration or the ESC key was used.
   */
  public static int openMessage(
    Shell parent,
    String dialogTitle,
    String message,
    IStatus status)
  {

    switch (status.getSeverity())
    {
      case IStatus.INFO :
        return openInfo(
          parent,
          dialogTitle,
          message,
          status,
          IStatus.OK | IStatus.INFO | IStatus.WARNING | IStatus.ERROR);
      case IStatus.WARNING :
        return openWarning(
          parent,
          dialogTitle,
          message,
          status,
          IStatus.OK | IStatus.INFO | IStatus.WARNING | IStatus.ERROR);
      default :
        return openError(
          parent,
          dialogTitle,
          message,
          status,
          IStatus.OK | IStatus.INFO | IStatus.WARNING | IStatus.ERROR);
    }

  }

  public static int openMessage(
    Shell parent,
    String dialogTitle,
    String message,
    IStatus status,
    Choice[] options)
  {

    return openOptions(
      parent,
      dialogTitle,
      message,
      status,
      IStatus.OK | IStatus.INFO | IStatus.WARNING | IStatus.ERROR,
      options);
  }

  /**
   * Opens an error dialog to display the given error. Use this method if the
   * error object being displayed contains child items <it>and</it> you wish
   * to specify a mask which will be used to filter the displaying of these
   * children. The error dialog will only be displayed if there is at least one
   * child status matching the mask.
   * 
   * @param parentShell
   *            the parent shell of the dialog, or <code>null</code> if none
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
   * @return the code of the button that was pressed that resulted in this
   *         dialog closing. This will be <code>Dialog.OK</code> if the OK
   *         button was pressed, or <code>Dialog.CANCEL</code> if this
   *         dialog's close window decoration or the ESC key was used.
   */
  public static int openError(
    Shell parentShell,
    String title,
    String message,
    IStatus status,
    int displayMask)
  {
    ErrorDialog dialog =
      new ErrorDialog(parentShell, title, message, status, displayMask);
    return dialog.open();
  }

  public static int openInfo(
    Shell parentShell,
    String title,
    String message,
    IStatus status,
    int displayMask)
  {
    InfoDialog dialog =
      new InfoDialog(parentShell, title, message, status, displayMask);
    return dialog.open();
  }

  public static int openWarning(
    Shell parentShell,
    String title,
    String message,
    IStatus status,
    int displayMask)
  {
    WarningDialog dialog =
      new WarningDialog(parentShell, title, message, status, displayMask);
    return dialog.open();
  }

  public static int openOptions(
    Shell parentShell,
    String title,
    String message,
    IStatus status,
    int displayMask,
    Choice[] options)
  {
    OptionsDialog dialog =
      new OptionsDialog(
        parentShell,
        title,
        message,
        status,
        displayMask,
        options);
    dialog.open();
    return dialog.getReturnCode();
  }

  /**
   * Populates the list using this error dialog's status object. This walks the
   * child stati of the status object and displays them in a list. The format
   * for each entry is status_path : status_message If the status's path was
   * null then it (and the colon) are omitted.
   */
  private void populateList(List list)
  {
    Iterator enumeration = statusList.iterator();
    while (enumeration.hasNext())
    {
      IStatus childStatus = (IStatus) enumeration.next();
      populateList(list, childStatus, 0);
    }
  }
  private void populateList(List list, IStatus status, int nesting)
  {
    if (!status.matches(displayMask))
    {
      return;
    }
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < nesting; i++)
    {
      sb.append("  "); //$NON-NLS-1$
    }
    sb.append(status.getMessage());
    list.add(sb.toString());
    IStatus[] children = status.getChildren();
    for (int i = 0; i < children.length; i++)
    {
      populateList(list, children[i], nesting + 1);
    }
  }
  private void populateDetails(Text text, IStatus status, int nesting)
  {
    if (!status.matches(displayMask))
    {
      return;
    }
        
    String    tabChars    = repeat( ' ', nesting * 2 );
    String    messageLine = tabChars + status.getMessage() + System.getProperty("line.separator"); //$NON-NLS-1$
    Throwable except      = status.getException();
    
    text.append( messageLine );
    
    if( except != null )
    {
      String[] trace = getStackTrace( except );
      
      for( int index = 0; index < trace.length; index++ )
      {
        text.append( tabChars + "    " + trace[index] + System.getProperty("line.separator") ); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
    
    IStatus[] children = status.getChildren();
    for (int i = 0; i < children.length; i++)
    {
      populateDetails(text, children[i], nesting + 1);
    }
  }
  /**
   * Returns whether the given status object should be displayed.
   * 
   * @param status
   *            a status object
   * @param mask
   *            a mask as per <code>Status.matches</code>
   * @return <code>true</code> if the given status should be displayed, and
   *         <code>false</code> otherwise
   */
  protected static boolean shouldDisplay(IStatus status, int mask)
  {
    IStatus[] children = status.getChildren();
    if (children == null || children.length == 0)
    {
      return status.matches(mask);
    }
    for (int i = 0; i < children.length; i++)
    {
      if (children[i].matches(mask))
        return true;
    }
    return false;
  }
  /**
   * Toggles the unfolding of the details area. This is triggered by the user
   * pressing the details button.
   */
  private void toggleDetailsArea()
  {
    Point windowSize = getShell().getSize();
    int   newHeight  = -1;
    
    if (detailsCreated)
    {
      details.dispose();
      detailsCreated = false;
      detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
      
      // Without the following computeSize call the setSize call below throws an array out of bounds exception.
      // Very weird.
      getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT);
      
      newHeight = savedSize.y;
    }
    else
    {
      if( savedSize == null ) savedSize = windowSize;
        
      details = createDropDownDetails((Composite) getContents());
      detailsButton.setText(IDialogConstants.HIDE_DETAILS_LABEL);
      newHeight = getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
    }


    newHeight = newHeight > 400 ? 400 : newHeight;
    
    getShell().setSize( new Point(windowSize.x, newHeight) );
  }
  
  private String[] getStackTrace( Throwable exc )
  {
    Vector       lines        = new Vector();
    StringWriter stringWriter = new StringWriter();
    PrintWriter  printWriter  = new PrintWriter( stringWriter );
    
    exc.printStackTrace( printWriter );
    
    try
    {
      printWriter.close();
      stringWriter.close();
    }
    catch( Exception nestedExc )
    {
      return new String[0];
    }
    
    StringReader stringReader = new StringReader( stringWriter.toString() );
    BufferedReader reader     = new BufferedReader( stringReader );
    String         line       = null;
    
    try
    {
      line = reader.readLine();
    
      while( line != null )
      {
        lines.add( line.trim() );
        line = reader.readLine();
      }
    }
    catch( Exception nestedExc )
    {
      return new String[0];
    }
    
    return (String[])lines.toArray( new String[0] );
  }
  
  private String repeat( char the_char, int count )
  {
    StringBuffer buf = new StringBuffer( count );

    for( int index = 0; index < count; index++ )
    {
      buf.append( the_char );
    }

    return buf.toString();
  }
  
}
