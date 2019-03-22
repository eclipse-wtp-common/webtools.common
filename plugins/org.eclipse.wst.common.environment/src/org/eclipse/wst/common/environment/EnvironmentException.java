/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
 * This is the exception class for conditions raised by the IEnvironment.
 * 
 * @since 1.0
 */
public class EnvironmentException extends Exception
{
  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3978983275899402036L;
  
  /**
   * The status for this exception.
   */
  protected IStatus status = null;
  
  /**
   * Creates a new EnvironmentException.
   */
  public EnvironmentException ()
  {
    super();
  }

  /**
   * Creates a new EnvironmentException.
   * 
   * @param status the status for this exception.
   */
  public EnvironmentException ( IStatus status )
  {
    super(status == null ? null : status.getMessage());
    this.status = status;
  }

  /**
   * Returns the Status object.
   * 
   * @return the status for this exception.
   */
  public IStatus getStatus()
  {
    return status;
  }
}
