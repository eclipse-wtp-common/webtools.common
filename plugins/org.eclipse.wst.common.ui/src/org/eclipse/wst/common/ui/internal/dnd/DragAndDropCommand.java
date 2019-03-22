/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *******************************************************************************/
package org.eclipse.wst.common.ui.internal.dnd;

import java.util.Collection;

public interface DragAndDropCommand
{
  //  public DragAndDropCommand(Object target, float location, int operations, int operation, Collection sources);

  public boolean canExecute();

  public void execute();

  public int getFeedback();
  
  public int getOperation();

  public void reinitialize(Object target, float location, int operations, int operation, Collection sources);
}
