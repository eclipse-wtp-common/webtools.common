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
package org.eclipse.wst.common.ui.viewers;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;

public class NavigableTableViewer extends TableViewer
{
  public static final String copyright = "(c) Copyright IBM Corporation 2000, 2002.";

   TableNavigator navigator;

   public NavigableTableViewer(Table parent)
   {
      super(parent);
      navigator = new TableNavigator(getTable(), this);
   }

   //override setCellEditors to put in call to moveAboveCellEditors for TableNavigator
   public void setCellEditors(CellEditor[] editors)
   {
     super.setCellEditors(editors);
     navigator.moveCellEditorsAbove(editors);

   }

   //override refresh so that TableNavigator is refreshed for all model changes
   public void refresh()
   {
   	if( !this.getTable().isDisposed() )
   	{
      	super.refresh();
      	navigator.refresh();
   	}
   }

}
