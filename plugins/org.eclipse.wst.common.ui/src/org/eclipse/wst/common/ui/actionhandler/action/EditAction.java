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

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class EditAction extends Action
{
  public static final String copyright = "(c) Copyright IBM Corporation 2000, 2002.";
  public Control getFocusControl()
  {
    return Display.getCurrent().getFocusControl();
  }
  
  public String getClipboardText()
  {
    Control focusControl = getFocusControl();
    if (focusControl != null)
    {
      Clipboard clipboard = new Clipboard(getFocusControl().getDisplay());
      TextTransfer textTransfer = TextTransfer.getInstance();
      String availableText = (String) clipboard.getContents(textTransfer);
      clipboard.dispose();
      return availableText;
    }
    return null;
//	String string = "";
//	if (OS.OpenClipboard (0)) {
//		int hMem = OS.GetClipboardData (OS.IsUnicode ? OS.CF_UNICODETEXT : OS.CF_TEXT);
//		if (hMem != 0) {
//			int byteCount = OS.GlobalSize (hMem);
//			int ptr = OS.GlobalLock (hMem);
//			if (ptr != 0) {
//				/* Use the character encoding for the default locale */
//				TCHAR buffer = new TCHAR (0, byteCount / TCHAR.sizeof);
//				OS.MoveMemory (buffer, ptr, byteCount);
//				string = buffer.toString (0, buffer.strlen ());
//				OS.GlobalUnlock (hMem);
//			}
//		}
//		OS.CloseClipboard ();
//	}
//	return string;
  }
  
  public String getSelectionText()
  {
    Control control = getFocusControl();
    if (control instanceof Text)
    {
      return ((Text)control).getSelectionText();
    }
    if (control instanceof StyledText)
    {
      return ((StyledText)control).getSelectionText();
    }
    if (control instanceof Combo)
    {
      Combo combo = (Combo)control;
      Point selection = combo.getSelection();
	  return combo.getText().substring(selection.x, selection.y);
    }
    return "";
  }
  
  public boolean isReadOnlyFocusControl()
  {
    Control control = getFocusControl();
    if (control instanceof Text)
    {
      return !((Text)control).getEditable();
    }
    if (control instanceof StyledText)
    {
      return !((StyledText)control).getEditable();
    }
    if (control instanceof Combo)
    {
      Combo combo = (Combo)control;
	  return (combo.getStyle() & SWT.READ_ONLY) == SWT.READ_ONLY;
    }
    return false;
  }
  
  public boolean isValidFocusControl()
  {
    Control control = getFocusControl();
    return (control instanceof Text ||
            control instanceof StyledText ||
            control instanceof Combo);
  }
}
