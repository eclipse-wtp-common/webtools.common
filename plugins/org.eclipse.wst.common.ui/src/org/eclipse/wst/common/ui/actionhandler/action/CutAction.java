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
package org.eclipse.wst.common.ui.actionhandler.action;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

public class CutAction extends EditAction
{
  public static final String copyright = "(c) Copyright IBM Corporation 2000, 2002.";

  /* (non-Javadoc)
   * @see org.eclipse.jface.action.IAction#getActionDefinitionId()
   */
  public String getActionDefinitionId()
  {
    return IWorkbenchActionDefinitionIds.CUT;
  }

  public void run()
  {
    Control control = getFocusControl();
    if (control instanceof Text)
    {
      ((Text)control).cut();
    }
    if (control instanceof StyledText)
    {
      ((StyledText)control).cut();
    }
    if (control instanceof Combo)
    {
      ((Combo)control).cut();
    }
  }  
}
