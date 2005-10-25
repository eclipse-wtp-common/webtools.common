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
package org.eclipse.wst.common.environment.uri;

import org.eclipse.core.runtime.IStatus;

/**
 * This is the base class for most exceptions thrown by IURI classes.
 * Every URIException carries a IURI and a Status, each of which may
 * be null. The getMessage() method as inherited from Exception will
 * return the message from the URIExceptin's Status object, if any.
 */
public class URIException extends Exception
{
  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3258130271424756018L;

  /**
   * A Status object containing details about the exception,
   * if applicable.
   */
  protected IStatus status;

  /**
   * The IURI for which the exception occured,
   * if applicable.
   */
  protected IURI uri;

  /**
   * Creates a new URIException with the given Status.
   * The status may be null.
   */
  public URIException ( IStatus status )
  {
    super();
    this.status = status;
  }

  /**
   * Creates a new URIException for the given Status and IURI,
   * each of which may be null.
   */
  public URIException ( IStatus status, IURI uri )
  {
    super(status != null ? status.getMessage() : null);
    this.status = status;
    this.uri = uri;
  }

  /**
   * Returns the Status object inside this exception.
   */
  public IStatus getStatus ()
  {
    return status;
  }

  /**
   * Returns the IURI inside this exception.
   */
  public IURI getURI ()
  {
    return uri;
  }
}
