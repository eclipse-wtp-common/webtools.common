/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.frameworks.internal.datamodel.ui;

/**
 * 
 * The wizard framework allows page groups to be inserted after other page group.
 * If more than one page group inserts itself after a particular page group it may
 * not be deterministic which page group should follow each other.  This class
 * allows a particular page group to determine the order in which subsequent page
 * groups should be ordered.
 * 
 * For example: A page group T has three page groups X, Y, and Z that have registered
 *              via the wizardPageGroup extension point to follow it.
 *              The wizard framework will call getNextGroup with:
 *              
 *                 T.getNextGroup( null, { "X", "Y", "Z" } )
 *                 
 *              This method should return either "X", "Y", "Z", or null if no page group
 *              should follow.  If "Y" was returned then a subequent call
 *              will be made by the framework with:
 *              
 *                 T.getNextGroup( "Y", { "X", "Y", "Z" } )
 *                 
 *              Again this method should return either "X", "Y", "Z", or null if no page
 *              group should follow this page group "Y".
 *              
 * Note: any page group can have a page group handler associated with it so this method
 *       call is recursive in nature.  For example: the page group Y might have page
 *       groups Y1, Y2, and Y3 following it.  This would result in the following calls:
 *       
 *                 T.getNextGroup( null, { "X", "Y", "Z" } )  // "Y" is selected using Ts handler.
 *                 Y.getNextGroup( null, { "Y1", "Y2", "Y3" } ) // "Y1" is selected using Ys handler.
 *                 
 *                 For this example Y1 has no page groups following it.
 *                 
 *                 T.getNextGroup( "Y", { "X", "Y", "Z" } )  // null is selected using Ts handler.
 *                 
 *                 For this example, the T handler decided that no page group followed Y not even X or Z.
 *
 */
public interface IDMPageGroupHandler 
{
  /**
   * 
   * @param currentPageGroupID the current page group ID.  This value will be null the first time 
   *                           this method is called.
   * @param pageGroupIDs a list of page group IDs that follow the page group for this handler.
   * @return returns the page group id that should follow currentPageGroupID, or it
   * should return null if no page group follows currentPageGroupID.
   * 
   */
  public String getNextPageGroup( String currentPageGroupID, String[] pageGroupIDs );
}
