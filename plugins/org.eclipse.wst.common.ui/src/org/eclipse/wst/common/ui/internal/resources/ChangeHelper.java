/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.common.ui.internal.resources;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.common.ui.UIPlugin;


public class ChangeHelper 
{
  public static final String copyright = "(c) Copyright IBM Corporation 2000, 2002.";
  protected IEditorPart editor;

  public ChangeHelper(IEditorPart editor)
  {
    this.editor = editor;
  }

  boolean handlingEditorInput = false;
  
  public void handleEditorInputChanged()
  {
    if (handlingEditorInput)
      return;
      
    handlingEditorInput = true;
    
    if (isDeleted(editor.getEditorInput())) 
    {
      // if saveas is allowed, give the user an opportunity to 
      // save the existing contents to another file
      if (editor.isDirty() && editor.isSaveAsAllowed())
      {
      	boolean isDone = false;
      	while (isDone == false)
      	{
      	  String[] buttons= { UIPlugin.getResourceString("_UI_SAVE_BUTTON"),
		              UIPlugin.getResourceString("_UI_CLOSE_BUTTON") };
         			
          final MessageDialog msg = new MessageDialog(editor.getSite().getShell(),
                                                      UIPlugin.getResourceString("_UI_FILE_CHANGED_TITLE"),
                                                      null,
                                                      UIPlugin.getResourceString("_UI_FILE_DELETED_SAVE_CHANGES"),
                                                      MessageDialog.QUESTION,
                                                      buttons,
                                                      0);
              
          int rc = msg.open();
          if (rc == 0) 
          {
            editor.doSaveAs();
          }
          else 
          {
            close(false);
            break;
          }
          if (!editor.isDirty())//!isDeleted(editor.getEditorInput()))
          {
            isDone = true;
          }
      	}
      	// refresh it so it gets removed from the workspace
//      	WorkbenchUtility.refreshLocalWorkspaceFile(((IFileEditorInput)editor.getEditorInput()).getFile(), new NullProgressMonitor());
      }
      else 
      {
      	// otherwise time to shutdown the editor.
//        MessageDialog.openConfirm(editor.getSite().getShell(), B2BGUIPlugin.getGUIString("_UI_FILE_CHANGED_TITLE"), B2BGUIPlugin.getGUIString("_UI_FILE_DELETED_EDITOR_CLOSED"));
        close(false);
      }
    }
    else 
    {
      // The file has just had its contents modified
      if (editor instanceof IExternalChangeEditorListener)
      {
        
        boolean rc = MessageDialog.openQuestion(editor.getSite().getShell(), UIPlugin.getResourceString("_UI_FILE_CHANGED_TITLE"), UIPlugin.getResourceString("_UI_FILE_CHANGED_LOAD_CHANGES"));
        if (rc == true)
        {
          ((IExternalChangeEditorListener)editor).reload();
        }
      }
    }
    handlingEditorInput = false;
  }

  // close the editor
  public void close(final boolean save)
  {
    Display display= editor.getSite().getShell().getDisplay();
    display.asyncExec(new Runnable()
    {
      public void run()
      {
        editor.getSite().getPage().closeEditor(editor, save);
      }
    });
  }
  	
  /*
   * check if file is deleted
   */
  public boolean isDeleted(Object element)
  {
    if (element instanceof IFileEditorInput)
    {
      IFileEditorInput input= (IFileEditorInput) element;
      
      IPath path= input.getFile().getLocation();
      if (path == null)
      {
        return true;
      }
      return !path.toFile().exists();
    }
    return false;
  }
}// ChangeHelper
