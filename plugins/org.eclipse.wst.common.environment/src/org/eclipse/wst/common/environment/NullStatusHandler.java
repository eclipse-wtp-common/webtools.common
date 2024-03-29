/*******************************************************************************
 * Copyright (c) 2001, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.environment;

import org.eclipse.core.runtime.IStatus;

/**
 * 
 * This class provides a default implementation of the IStatusHandler interface.
 * 
 * @since 1.0
 *
 */
public class NullStatusHandler implements IStatusHandler
{
  
  /**
   * @see org.eclipse.env.common.IStatusHandler#report(org.eclipse.core.runtime.IStatus, org.eclipse.env.common.Choice[])
   */
  @Override
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
   * @see org.eclipse.env.common.IStatusHandler#report(org.eclipse.core.runtime.IStatus)
   */
  @Override
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
   * @see org.eclipse.wst.common.environment.IStatusHandler#reportError(org.eclipse.core.runtime.IStatus)
   */
  @Override
public void reportError(IStatus status)
  {
    reportErrorStatus( status );
  }
  
  /**
   * @see org.eclipse.wst.common.environment.IStatusHandler#reportInfo(org.eclipse.core.runtime.IStatus)
   */
  @Override
public void reportInfo(IStatus status)
  {
  }
}
