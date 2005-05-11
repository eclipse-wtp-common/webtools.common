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
package org.eclipse.wst.common.ui.internal.actionhandler;

import java.util.ArrayList;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.wst.common.ui.internal.actionhandler.action.CopyAction;
import org.eclipse.wst.common.ui.internal.actionhandler.action.CutAction;
import org.eclipse.wst.common.ui.internal.actionhandler.action.EditAction;
import org.eclipse.wst.common.ui.internal.actionhandler.action.PasteAction;



public class ActionHandlerListener implements IPartListener, MenuListener
{
  public static final String copyright = "(c) Copyright IBM Corporation 2000, 2002.";
  
  /**
   * Constructor for ActionHandlerListener.
   */
  public ActionHandlerListener()
  {
    super();
    createEditActions();
  }

  /**
   * Method connectPart.  This call registers your part to the actionhandler listener
   * if it hasn't been registered already and hooks up the generic cut/copy/paste action handlers.
   * Invoke this method from the init() routine of your #IViewPart or #IEditorPart if you do not
   * have any custom cut/copy/paste action handlers.
   * 
   * If you have a customized cut/copy/paste action handlers, invoke this method after you
   * have finished your custom processing to add/remove your actionhandlers.
   * 
   * @param part - the workbench part that you would like to connect the generic cut/copy/paste handlers
   */
  static public void connectPart(IWorkbenchPart part)
  {
    getInstance().connectWorkbenchPart(part);
  }

  static protected ActionHandlerListener actionHandlerListener;
  static protected ActionHandlerListener getInstance()
  {
		if (actionHandlerListener == null)
		{
			actionHandlerListener = new ActionHandlerListener();
		}
    return actionHandlerListener;
  }
  
  
  
  protected ArrayList workbenchWindows = new ArrayList();
  public void listenToWorkbenchWindow(IWorkbenchWindow workbenchWindow)
  {
    if (!workbenchWindows.contains(workbenchWindow))
    {
      workbenchWindows.add(workbenchWindow);
      workbenchWindow.getPartService().addPartListener(this);
      MenuManager editMenu = (MenuManager) ((ApplicationWindow)workbenchWindow).getMenuBarManager().findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
      if (editMenu != null)
        editMenu.getMenu().addMenuListener(this);
    }
  }
  
  protected void createEditActions()
  {
    cut = new CutAction();
    copy = new CopyAction();
    paste = new PasteAction();
  }
  
  protected IActionBars getActionBars(IWorkbenchPart part)
  {
    IActionBars actionBars = null;
    if (part != null)
    {
      IWorkbenchPartSite partSite = part.getSite();
      if (partSite instanceof PartSite)
      {
        actionBars = ((PartSite)partSite).getActionBars();
      }
    }
    return actionBars;
  }

  /**
   * Method connectWorkbenchPart.  Ensure we are already listening to the workbenchwindow,
   * register the part's id and then connect the cut/copy/paste actions
   * @param part
   */
  public void connectWorkbenchPart(IWorkbenchPart part)
  {
    IWorkbenchWindow wbw = part.getSite().getWorkbenchWindow();
    listenToWorkbenchWindow(wbw);
    registerPartId(part);
    connectCutCopyPasteActions(part);
  }

  /**
   * Method isRegisteredPart.  Returns whether the part has already been
   * registered as being a candidate for the generic cut/copy/paste actions.
   * 
   * @param part
   * @return boolean
   */
  public boolean isRegisteredPart(IWorkbenchPart part)
  {
    String partId = part.getSite().getId();
    return registeredParts.contains(partId);
  }
    
  protected ArrayList registeredParts = new ArrayList();
  protected void registerPartId(IWorkbenchPart part)
  {
    if (!isRegisteredPart(part))
    {
      String partId = part.getSite().getId();
      registeredParts.add(partId);
    }
  }
  
  /**
   * Method connectCutCopyPasteActions.  Connect the cut/copy/paste actions
   * to the workbench part.
   * 
   * @param part
   */
  public void connectCutCopyPasteActions(IWorkbenchPart part)
  {
    IActionBars actionBars = getActionBars(part);
    if (actionBars != null)
    {
      part.getSite().getKeyBindingService().registerAction(cut);
      part.getSite().getKeyBindingService().registerAction(copy);
      part.getSite().getKeyBindingService().registerAction(paste);
      //connectCutCopyPasteActions(actionBars);
    }
  }
  
  
  /**
   * Method connectCutCopyPasteActions.  Only set the actionhandlers if
   * there isn't one active for the appropriate action.
   * 
   * @param actionBars
   */
  public void connectCutCopyPasteActions(IActionBars actionBars)
  {
    if (actionBars.getGlobalActionHandler(ActionFactory.CUT.getId()) == null)
    {
      actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), cut);
    }
    if (actionBars.getGlobalActionHandler(ActionFactory.COPY.getId()) == null)
    {
      actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), copy);
    }
    if (actionBars.getGlobalActionHandler(ActionFactory.PASTE.getId()) == null)
    {
      actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), paste);
    }
    enableActions();
    actionBars.updateActionBars();  
  }

  protected IWorkbenchPart getWorkbenchPart()
  {
    IWorkbenchWindow wbw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (wbw == null)
      return null;
      
    IWorkbenchPage wbp = wbw.getActivePage();
    if (wbp == null)
      return null;
      
    return wbp.getActivePart();
  }
      
  protected void updateActions()
  {
    IWorkbenchPart part = getWorkbenchPart();
    if (part != null &&
        isRegisteredPart(part))
    {
      IActionBars actionBars = getActionBars(part);
      
      Control focusControl = cut.getFocusControl();
      boolean enable = true;
      if (isValidFocusControl() &&
          actionBars != null)
      {
        String clipboardText = paste.getClipboardText();
        String selectionText = copy.getSelectionText();
        boolean isReadOnly = paste.isReadOnlyFocusControl();
        boolean clipboardNotEmpty = clipboardText != null && !clipboardText.equals("");
        boolean textSelected = selectionText != null && !selectionText.equals("");
        
        cut.setEnabled(!isReadOnly && textSelected);
        copy.setEnabled(textSelected && !(focusControl instanceof Combo && isReadOnly));
        paste.setEnabled(!isReadOnly && clipboardNotEmpty);
      }
      else
      {
        cut.setEnabled(false);
        copy.setEnabled(false);
        paste.setEnabled(false);
      }      
      actionBars.updateActionBars();     
    }
  }
  
  protected boolean isValidFocusControl()
  {
    // delgate to one of the edit actions
    return cut.isValidFocusControl();
  }
  
  protected void enableActions()
  {
    cut.setEnabled(true);
    copy.setEnabled(true);
    paste.setEnabled(true);
  }    
 
  /**
   * Connect the cut/copy/paste actions if a registered part is activated.
   * 
   * @see IPartListener#partActivated(IWorkbenchPart)
   */
  public void partActivated(IWorkbenchPart part)
  {
    if (isRegisteredPart(part))
    {
//      System.out.println("registered part activated" + part);
      connectCutCopyPasteActions(part);
    }
  }

  protected EditAction cut,copy,paste;

  /**
   * @see IPartListener#partBroughtToTop(IWorkbenchPart)
   */
  public void partBroughtToTop(IWorkbenchPart part)
  {
  }
  /**
   * @see IPartListener#partClosed(IWorkbenchPart)
   */
  public void partClosed(IWorkbenchPart part)
  {
  }
  /**
   * @see IPartListener#partDeactivated(IWorkbenchPart)
   */
  public void partDeactivated(IWorkbenchPart part)
  {
  }
  /**
   * @see IPartListener#partOpened(IWorkbenchPart)
   */
  public void partOpened(IWorkbenchPart part)
  {
  }


  /**
   * If the menu is hidden on a registered part, then renable all actions.  This
   * takes away the need for us to monitor the control traversal.  If the individual
   * action isn't applicable when it is invoked, the action becomes a no op.
   * @see MenuListener#menuHidden(MenuEvent)
   */
  public void menuHidden(MenuEvent e)
  {
    IWorkbenchPart part = getWorkbenchPart();
    if (part != null &&
        isRegisteredPart(part))
    {
      enableActions();
   
      IActionBars actionbars = getActionBars(part);
      actionbars.updateActionBars();
    }
  }

  /**
   * Update the cut/copy/paste enablement (if a registered part is active) just
   * before showing the edit menu.
   * 
   * @see MenuListener#menuShown(MenuEvent)
   */
  public void menuShown(MenuEvent e)
  {
    updateActions();
  }

}
