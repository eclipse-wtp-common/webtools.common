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
package org.eclipse.wst.common.ui.resource;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.wst.common.ui.UIPlugin;


public class ResourceDeleteListener implements IResourceChangeListener, IResourceDeltaVisitor
{
  public static final String copyright = "(c) Copyright IBM Corporation 2000, 2002.";
  protected ArrayList recognizedEditorParts = new ArrayList();

  public void addEditorPartClass(Class editorPartClass)
  {
    recognizedEditorParts.add(editorPartClass);
  }

  public void resourceChanged(IResourceChangeEvent event)
  {
    IResourceDelta resourceDelta = event.getDelta();

    try
    {
      if (resourceDelta != null) 
      {
        resourceDelta.accept(this);
      }
    }
    catch (Exception e)
    {
//      B2BGUIPlugin.getPlugin().getMsgLogger().write("Exception caught during resource change" + e);
//      B2BGUIPlugin.getPlugin().getMsgLogger().writeCurrentThread(); 
    }      
  }

  public boolean visit(IResourceDelta delta)
  {
    if (delta.getKind() == IResourceDelta.REMOVED)
    {
      // handle removed resource
      //closeEditors(delta.getResource());
      /*
       * defect 235374
       * Without ayncExec, RuntimeException is raised:
       *   "The resource tree is locked for modifications."
       *   from Resource.refreshLocal().
       */
      Display.getDefault().asyncExec
        (new CloseEditors(recognizedEditorParts,delta.getResource()));
    }
    return true;
  }

  public static class CloseEditors implements Runnable
  {
    private IResource resource;
    private ArrayList recognizedEditorParts;

    public CloseEditors(ArrayList editorParts, IResource res)
    {
      recognizedEditorParts = editorParts;
      resource = res;
    }

    public void run()
    {
      // close all associated editors that are editing this resource,
      // if it is one of the recognizedEditorParts
      IWorkbenchWindow windows[] = 
        UIPlugin.getDefault().getWorkbench().getWorkbenchWindows();
    
      for (int i = 0; i < windows.length; i++) 
      {
        IWorkbenchPage pages[] = windows[i].getPages();
      
        for (int j = 0; j < pages.length; j++ ) 
        {
          IEditorPart editors[] = pages[j].getEditors();
          for (int k = 0; k < editors.length; k++) 
          {
            IEditorPart editor = editors[k];
            IEditorInput editorInput = editor.getEditorInput();
          
            if (editorInput instanceof IFileEditorInput) 
            {
              if (((IFileEditorInput)editorInput).getFile().equals(resource) &&
                  recognizedEditorParts.contains(editor.getClass())) 
              {
                IWorkbenchPage page = pages[j];
                page.closeEditor(editor, false);
              }
            }
          }
        }
      }
    }
  }
            
  /*
  private void closeEditors(IResource deletedResource)
  {
    // close all associated editors that are editing this resource,
    // if it is one of the recognizedEditorParts
    IWorkbenchWindow windows[] = B2BGUIPlugin.getInstance().getWorkbench().getWorkbenchWindows();
    
    for (int i = 0; i < windows.length; i++) 
    {
      IWorkbenchPage pages[] = windows[i].getPages();
      
      for (int j = 0; j < pages.length; j++ ) 
      {
        IEditorPart editors[] = pages[j].getEditors();
        for (int k = 0; k < editors.length; k++) 
        {
          IEditorPart editor = editors[k];
          IEditorInput editorInput = editor.getEditorInput();
          
          if (editorInput instanceof IFileEditorInput) 
          {
            if (((IFileEditorInput)editorInput).getFile().equals(deletedResource) &&
                recognizedEditorParts.contains(editor.getClass())) 
            {
              IWorkbenchPage page = pages[j];
              org.eclipse.swt.widgets.
                Display.getDefault().asyncExec(new CloseEditor(page, editor));
            }
          }
        }
      }
    }
  }
  */

  public static class CloseEditor implements Runnable
  {
    protected IWorkbenchPage page;
    protected IEditorPart editor;
    public CloseEditor(IWorkbenchPage page, IEditorPart editor)
    {
      this.page = page;
      this.editor = editor;
    }
    public void run()
    {
      page.closeEditor(editor, false);
    }
  }
}
