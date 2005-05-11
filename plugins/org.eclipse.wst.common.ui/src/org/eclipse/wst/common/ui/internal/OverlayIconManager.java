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
package org.eclipse.wst.common.ui.internal;

import org.eclipse.core.resources.*;
//import org.eclipse.jface.resource.*;
import org.eclipse.jface.viewers.*;
//import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*; 
import org.eclipse.swt.widgets.Display;
import java.util.*;
 
/**         
 * This class used to track the icon overlays that should be used to decorate the icons in a view.  
 * Clients should implement the method 'locateObjectForOffset()' that will be used to associate a model object with each marker.
 * When markers are changed, a table of objects -> markers is maintained.  When the time comes to display the object in a view
 * the client can call 'getOverlayImageForObject()' to determine the annotation image that should be used when displaying the object.
 */
public abstract class OverlayIconManager
{ 
	public static final String copyright = "(c) Copyright IBM Corporation 2000, 2002.";
	protected IResource resource;
	protected IWorkspace workspace;    
	protected Hashtable hashtable = new Hashtable();
	protected ResourceChangeListener resourceChangeListener = new ResourceChangeListener();
	protected List viewerList = new Vector();     
 
	// todo... get images for these other kinds of markers
	//                                        
	protected static final Image taskOverlayImage = null; //B2BGUIPlugin.getInstance().getImage("icons/error-overlay.gif");
	protected static final Image bookmarkOverlayImage = null; //B2BGUIPlugin.getInstance().getImage("icons/error-overlay.gif");
	protected static final Image problemInfoOverlayImage = null; //B2BGUIPlugin.getInstance().getImage("icons/error-overlay.gif");
// TODO protected static final Image problemWarningOverlayImage = B2BGUIPlugin.getInstance().getImage("icons/warning-overlay.gif");
	protected static final Image problemWarningOverlayImage = null;
// TODO protected static final Image problemErrorOverlayImage = B2BGUIPlugin.getInstance().getImage("icons/error-overlay.gif");
	protected static final Image problemErrorOverlayImage = null;

	/**
	 * Internal resource change listener.
	 */
	class ResourceChangeListener implements IResourceChangeListener 
	{
		protected boolean needsUpdate;

		public void resourceChanged(IResourceChangeEvent e) 
		{	      
			//if (e.getResource() == resource)
			{
				if (!needsUpdate)
				{           
					needsUpdate = true;
					Runnable delayedUpdate = new Runnable()
					{
						public void run()
						{
							doUpdate();
						}
					}; 

					// It seems like this current dislay is actually coming back null sometimes.
					// Without this test we get an occasional NPE logged (defect 187099).
					if (Display.getCurrent() != null)
					{ 
						Display.getCurrent().asyncExec(delayedUpdate);
					}
				}             
			}
		}

		public void doUpdate()
		{              
			needsUpdate = false;
			update();
		}
	};   
                                                        

	public OverlayIconManager()
	{
	}       


	public void setResource(IResource newResource)
	{
		this.resource = newResource; 
		setWorkspace(newResource != null ? newResource.getWorkspace() : null);
	}     

              
	// I'm not sure why there can be more than 1 workspace.... do I misunderstand something?
	protected void setWorkspace(IWorkspace newWorkspace)
	{
		if (workspace != null && workspace != newWorkspace)
		{
			workspace.removeResourceChangeListener(resourceChangeListener);
		} 
		if (newWorkspace != null)
		{
			newWorkspace.addResourceChangeListener(resourceChangeListener);
		}
		workspace = newWorkspace;
	}

  
	//
	public IResource getResource()
	{
		return resource;
	}       

	public void addViewer(Viewer viewer)
	{
		viewerList.add(viewer);
	}                 

	public void removeViewer(Viewer viewer)
	{
		viewerList.remove(viewer);
	}      

	protected void updateListeners()
	{
		for (Iterator i = viewerList.iterator(); i.hasNext(); )
		{
			Viewer viewer = (Viewer)i.next();

			// Make sure the viewer is not disposed before calling refresh on it
			if ( viewer.getControl() != null && 
					!viewer.getControl().isDisposed() )
			{
				viewer.refresh();
			}
		}
	}
  
	public void update()
	{   
		// if resource is null, do nothing                        
		if (resource == null)
			return;
      
		try
		{                                                                         
			hashtable = new Hashtable();                  
			IMarker[] markers = resource.findMarkers(IMarker.MARKER, true, IResource.DEPTH_ZERO);
			for (int i = 0 ; i < markers.length; i++)
			{       
				IMarker marker = markers[i];
				Object object = locateObjectForMarker(marker);
				Object key = object != null ? getKeyForObject(object) : null;
				if (key != null)
				{                     
					hashtable.put(key, marker);
				}
			}   
		}
		catch (Exception e)
		{   
// TODO			B2BGUIPlugin.getPlugin().getMsgLogger().writeCurrentThread(); 
		}
		updateListeners();
	}


	protected abstract Object locateObjectForOffset(int offset);
                                                          
	protected Object locateObjectForMarker(IMarker marker)
	{ 
		Object result = null;
		int offset = marker.getAttribute(IMarker.CHAR_START, -1);
		if (offset != -1)
		{
			result = locateObjectForOffset(offset);
		}
		return result;
	}    


	protected Object getKeyForObject(Object object)
	{     
		return object;
	}    

                                                   
	public Image getOverlayImageForObject(Object object)
	{ 
		Image result = null;                                          
		try
		{
			IMarker marker = getMarkerForObject(object);
			if (marker != null)
			{          
				if (marker.isSubtypeOf(IMarker.TASK)) 
				{
					result= taskOverlayImage;
				} 
				else if (marker.isSubtypeOf(IMarker.BOOKMARK)) 
				{
					result = bookmarkOverlayImage;
				}
				else if (marker.isSubtypeOf(IMarker.PROBLEM))
				{
					switch (marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO)) 
					{                                 
						case IMarker.SEVERITY_INFO:     { result = problemInfoOverlayImage; break; }
						case IMarker.SEVERITY_WARNING:  { result = problemWarningOverlayImage; break; }
						case IMarker.SEVERITY_ERROR:    { result = problemErrorOverlayImage; break; }
					}			
				}     
			}
		}
		catch (Exception e)
		{
		}
		return result;
	}  


	public IMarker getMarkerForObject(Object object)
	{           
		IMarker result = null;
		Object key = object != null ? getKeyForObject(object) : null;
		if (key != null)
		{                     
			result = (IMarker)hashtable.get(key);
		}         
		return result;
	}       
}
