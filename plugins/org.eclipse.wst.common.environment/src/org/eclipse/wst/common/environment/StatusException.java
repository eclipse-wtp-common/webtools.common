/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
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

/**
 * This is the exception class used by StatusHandlers to tell their
 * callers that processing should stop.
 */
public class StatusException extends EnvironmentException
{
  /**
   * Comment for <code>serialVersionUID</code>
   * */
  private static final long serialVersionUID = 3618141160322119992L;

  /**
   * The Choice that lead to the exception being thrown,
   * if any (ie. may be null).
   */
  protected Choice choice;

  /**
   * Creates a new StatusException with the given Status.
   */
  public StatusException( IStatus status )
  {
    super( status );  
  }
  
  /**
   * Creates a new StatusException with the given Choice.
   * The Choice may be null.
   */
  public StatusException ( Choice choice )
  {
    super();
    this.choice = choice;
  }

  /**
   * Creates a new StatusException with the given Choice
   * and status object. The Choice may be null.
   */
  public StatusException ( IStatus status, Choice choice )
  {
    super(status);
    this.choice = choice;
  }

  /**
   * Returns the Choice object inside this exception.
   */
  public Choice getChoice ()
  {
    return choice;
  }
}
