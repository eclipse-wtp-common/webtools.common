/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.environment;

import org.eclipse.core.runtime.IStatus;

public class NullStatusHandler implements StatusHandler
{
  
  /**
   * @see org.eclipse.env.common.StatusHandler#report(org.eclipse.env.common.Status, org.eclipse.env.common.Choice[])
   */
  public Choice report(IStatus status, Choice[] choices) 
  {
  	Choice result = null;
  	
  	// Always take the first choice if available.
    if( choices != null && choices.length > 0 )
    {
    	result = choices[0];
    }
    
    return result;
  }

  /**
   * @see org.eclipse.env.common.StatusHandler#report(org.eclipse.env.common.Status)
   */
  public void report(IStatus status) throws StatusException
  {
  }
  
  /*
   * Report and error.
   */
  private boolean reportErrorStatus(IStatus status)
  {
    return false;
  }
  
  /**
   * @see org.eclipse.wst.common.environment.StatusHandler#reportError(org.eclipse.wst.command.internal.provisional.env.core.common.Status)
   */
  public void reportError(IStatus status)
  {
    reportErrorStatus( status );
  }
  
  /**
   * @see org.eclipse.wst.common.environment.StatusHandler#reportInfo(org.eclipse.wst.command.internal.provisional.env.core.common.Status)
   */
  public void reportInfo(IStatus status)
  {
  }
}
