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

/**
 * Processes status events raised by the caller and returns an
 * indication of choices made by the recipient of the status and
 * may raise an exception against the caller to have them abort
 * procesing.
 * 
 * @since 1.0
 */
public interface IStatusHandler
{
  /**
   * Reports the given Status and set of possible responses.
   * 
   * @param status the status to report.
   * @param choices the choices that will be displayed to the user.
   * @return returns the choice made by the user/handler.
   */
  public Choice report ( IStatus status, Choice[] choices );
  
  /**
   * Reports the given Status with implied options to either
   * continue or abort.
   * @param status the status to report.
   * @throws StatusException Throws an exception if the handler decides the caller
   * should stop processing.
   */
  public void report ( IStatus status ) throws StatusException;
  
  /**
   * Report the given Error Status.  No user feedback is provided.
   * @param status the error status to report.
   */
  public void reportError( IStatus status );
  
  /**
   * Report the given Info Status.  No user feedback is provided.
   * @param status the info status to report.
   */
  public void reportInfo( IStatus status );
}
