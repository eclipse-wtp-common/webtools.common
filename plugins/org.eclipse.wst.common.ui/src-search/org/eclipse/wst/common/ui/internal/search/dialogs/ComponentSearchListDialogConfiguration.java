/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.ui.internal.search.dialogs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

public class ComponentSearchListDialogConfiguration
{
  IComponentSearchListProvider searchListProvider; 
  IComponentDescriptionProvider descriptionProvider;
  INewComponentHandler newComponentHandler;
  ComponentSearchListDialog dialog;

  public void init(ComponentSearchListDialog dialog)
  {
    this.dialog = dialog;
  }
  
  public void createWidgetAboveQualifierBox(Composite parent)
  {    
  }
  public void createWidgetBelowQualifierBox(Composite parent)
  {    
  }
  
  public void createToolBarItems(ToolBar toolBar)
  {    
  }

  public IComponentDescriptionProvider getDescriptionProvider()
  {
    return descriptionProvider;
  }

  public void setDescriptionProvider(IComponentDescriptionProvider descriptionProvider)
  {
    this.descriptionProvider = descriptionProvider;
  }

  public IComponentSearchListProvider getSearchListProvider()
  {
    return searchListProvider;
  }

  public void setSearchListProvider(IComponentSearchListProvider searchListProvider)
  {
    this.searchListProvider = searchListProvider;
  }

  public ComponentSearchListDialog getDialog()
  {
    return dialog;
  }

  public INewComponentHandler getNewComponentHandler()
  {
    return newComponentHandler;
  }

  public void setNewComponentHandler(INewComponentHandler newComponentHandler)
  {
    this.newComponentHandler = newComponentHandler;
  }
}
