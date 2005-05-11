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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

public class WindowUtility
{
	public static final String copyright = "(c) Copyright IBM Corporation 2000, 2002.";
	public static Object getSelection(ISelection selection)
	{
		if (selection == null)
		{
			return null;
		} // end of if ()

		Object result = null;
		if (selection instanceof IStructuredSelection)
		{
			 IStructuredSelection es= (IStructuredSelection)selection;
			 Iterator i= es.iterator();
			 if (i.hasNext())
			 {
				 result= i.next();
			 }
		}                     
		return result;
	}

	public static List getSelectionList(ISelection selection)
	{
		List result = null;
		if (selection != null)
		{
			if (selection instanceof IStructuredSelection)
			{
				IStructuredSelection es= (IStructuredSelection)selection;
				result = new Vector();
				for (Iterator i= es.iterator(); i.hasNext(); )
				{
					result.add(i.next());
				}
			}
		}                     
		return result;
	} 

	public static void openErrorCreatingFile(Shell shell, IResource resource)
	{                   
		 String title = null;
		 String briefMessage = null;
		 String reason = null;
		 String details = null;

//	KCPort TODO
//		 title = B2BGUIPlugin.getInstance().getString("_UI_ERROR_CREATING_FILE_TITLE");
//		 briefMessage = B2BGUIPlugin.getInstance().getString("_UI_ERROR_CREATING_FILE_SHORT_DESC", resource.getName());
//		 details = B2BGUIPlugin.getInstance().getString("_UI_ERROR_CREATING_FILE_LONG_DESC", resource.getLocation().toOSString()); 
//      
//		 IResource parent = resource.getParent();     
//		 if (parent != null) 
//		 {                   
//			 if (parent.isReadOnly())
//			 {
//				 reason = B2BGUIPlugin.getInstance().getString("_UI_PARENT_FOLDER_IS_READ_ONLY", parent.getName());     
//			 }
//			 else
//			 {
//				 // on windows the isReadOnly() = false for read only shared directory... so we give a hint
//				 reason = B2BGUIPlugin.getInstance().getString("_UI_UNKNOWN_ERROR_WITH_HINT", parent.getName());  
//			 }
//		 } 
//
//		 if (reason == null)
//		 {
//			 reason = B2BGUIPlugin.getInstance().getString("_UI_UNKNOWN_ERROR");
//		 }

		 openError(shell, title, briefMessage, reason, details);
	}      

	public static void openError(Shell shell, String title, String briefMessage, String reason, String detailedMessage)
	{
		ErrorDialog.openError(shell, title, briefMessage, createStatus(reason, detailedMessage));                                  
	}      

	private static IStatus createStatus(String reason, String msg)
	{
// KCPort TODO
//	  String pluginId = B2BGUIPlugin.getInstance().getDescriptor().getUniqueIdentifier();
	  String pluginId = "";
		MultiStatus multiStatus = new MultiStatus(pluginId, 0, reason, null);
		Status status = new Status(IStatus.ERROR, pluginId, 0, msg, null);
		multiStatus.add(status);
		return multiStatus;
	}
}
