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

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

public class ResourceChangeListener implements IPartListener, IResourceChangeListener, IResourceDeltaVisitor
{
  public static final String copyright = "(c) Copyright IBM Corporation 2000, 2002.";
  protected ArrayList recognizedEditorParts = new ArrayList();
  protected IEditorPart editor;
  protected long fileTimestamp;

  public ResourceChangeListener(IEditorPart editPart)
  {
    editor = editPart;
    // add a part activate listener
    editor.getSite().getWorkbenchWindow().getPartService().addPartListener(this);
    // add resource change listener
    getFile().getWorkspace().addResourceChangeListener(this);
    setTimestamp();
  }
  
  protected void setTimestamp()
  {
    fileTimestamp = computeModificationStamp(getFile());
  }
  
  /**
   * Computes the initial modification stamp for the given resource.
   * 
   * @param resource the resource
   * @return the modification stamp
   */
  protected long computeModificationStamp(IResource resource) {
    long modificationStamp= resource.getModificationStamp();
    
    IPath path= resource.getLocation();
    if (path == null)
     return modificationStamp;
    
    modificationStamp= path.toFile().lastModified();
    return modificationStamp;
  }
  
  protected IFile getFile()
  {
    IFileEditorInput fileInput = (IFileEditorInput) editor.getEditorInput();
    return fileInput.getFile();
  }
  
  protected void checkChanged(final IEditorPart editorPart)
  {
    if (!getFile().exists() ||
         fileTimestamp != computeModificationStamp(getFile()))
    {
      if (editorPart instanceof IExternalChangeEditorListener)
      {
        editorPart.getSite().getShell().getDisplay().asyncExec(new Runnable()
        {
          public void run()
          {
            ((IExternalChangeEditorListener)editorPart).handleEditorInputChanged();
            setTimestamp();
          }
        });
      }
    }
  }
  
  /**
   * @see IPartListener#partActivated(IWorkbenchPart)
   */
  public void partActivated(IWorkbenchPart part) 
  {
    if (part == editor)
      checkChanged(editor);
  }
  
  /**
   * @see IPartListener#partBroughtToTop(IWorkbenchPart)
   */
  public void partBroughtToTop(IWorkbenchPart arg0) {
//    if (arg0 == editor)
//     B2BGUIPlugin.getPlugin().getMsgLogger().write("part brought to top");
  }
  
  /**
   * @see IPartListener#partClosed(IWorkbenchPart)
   */
  public void partClosed(IWorkbenchPart part) {
    if (part == editor)
    {
      // add a part activate listener
      editor.getSite().getWorkbenchWindow().getPartService().removePartListener(this);
      // add resource change listener
      getFile().getWorkspace().removeResourceChangeListener(this);      
//      B2BGUIPlugin.getPlugin().getMsgLogger().write("part closed");
    }
  }
  
  /**
   * @see IPartListener#partDeactivated(IWorkbenchPart)
   */
  public void partDeactivated(IWorkbenchPart part) {}
  
  /**
   * @see IPartListener#partOpened(IWorkbenchPart)
   */
  public void partOpened(IWorkbenchPart part) {}
  
  // IResourceChangeListener interface
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

  // IResourceDeltaVisitor
  public boolean visit(IResourceDelta delta)
  {
    if (delta.getResource().equals(getFile()))
    {
      switch (delta.getKind())
      {
        case IResourceDelta.CHANGED:
         setTimestamp();
         break;
        case IResourceDelta.REMOVED:
         if ((IResourceDelta.MOVED_TO & delta.getFlags()) != 0)
         {
           final IPath movedToPath = delta.getMovedToPath();

           editor.getSite().getShell().getDisplay().asyncExec(new Runnable()
           {
             public void run()
             {
               // if the resource has moved or been renamed, let the editor do some work now
               ((IExternalChangeEditorListener)editor).handleEditorPathChanged(movedToPath);  
               setTimestamp();
             }
           });
         }
         else
         {
           editor.getSite().getShell().getDisplay().asyncExec(new Runnable()
           {
             public void run()
             {
               // if the resource is deleted, let the editor do some work now
               ((IExternalChangeEditorListener)editor).handleEditorInputChanged();  
               setTimestamp();
             }
           });
         
         }
         break;
        
      default:
       break;
      }
    }
    return true;
  }
}
 
