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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.wst.common.ui.UIPlugin;


public class PropertyResourceChangeListener extends ResourceChangeListener implements IPropertyListener
{
  public static final String copyright = "(c) Copyright IBM Corporation 2002.";
  public PropertyResourceChangeListener(IEditorPart editPart)
  {
    super(editPart);
    editPart.addPropertyListener(this); 
  }

  boolean dirtyState = false;
  IFile[] iFileList = null;
  IEditorPart editorPart = null;
  Runnable currentRunnable = null;
  boolean inValidateEditCall = false;
  
  public void propertyChanged(Object source, int propId) 
  {
    if (propId == IEditorPart.PROP_DIRTY)
    {
      if ((source instanceof IEditorPart) && (currentRunnable == null))
      {
        editorPart = (IEditorPart)source;
        if (editorPart.isDirty() && (editorPart.getEditorInput() instanceof IFileEditorInput))
        {
		  IFileEditorInput editorInput = (IFileEditorInput)(editorPart.getEditorInput());
          // Only call validateEdit if the file is read only
          if (editorInput.getFile().isReadOnly()) 
          {
			iFileList = new IFile[1];
           	iFileList[0] = editorInput.getFile();

       	    currentRunnable = new Runnable()
      	    { 
              public void run()
              {
              	inValidateEditCall = true;                  
            	IStatus status = ResourcesPlugin.getWorkspace().validateEdit(iFileList, editorPart.getSite().getShell());          
            	if (status.getCode() != IStatus.OK)
            	{ 
	      	  	  ((IValidateEditEditor)editorPart).undoChange();
              	  setDirtyState(editorPart.isDirty());
              	  
              	  if (status.getCode() == IStatus.ERROR)
				    MessageDialog.openError(Display.getCurrent().getActiveShell() , 
				    						UIPlugin.getResourceString("_UI_ERROR_VALIDATE_EDIT_FAIL_ONE_FILE"), 
				    						status.getMessage());
            	}
            	else
            	{
            	  checkChanged(editorPart);
            	}
            	  
            	inValidateEditCall = false;
            	currentRunnable = null;
              }   
            };
            
            // we need to ensure that this is run via 'asyncExec' since these 
            // notifications can come from a non-ui thread
            if (Display.getCurrent() != null)
              Display.getCurrent().timerExec(100,currentRunnable);
            else
              Display.getDefault().timerExec(100,currentRunnable);
          }
        }
      }
    }
  }

public boolean inValidateEditCall()
{
  return inValidateEditCall;	
}

/**
 * Gets the dirtyState.
 * @return Returns a boolean
 */
public boolean getDirtyState() {
	return dirtyState;
}

/**
 * Sets the dirtyState.
 * @param dirtyState The dirtyState to set
 */
public void setDirtyState(boolean dirtyState) {
	this.dirtyState = dirtyState;
}

}
