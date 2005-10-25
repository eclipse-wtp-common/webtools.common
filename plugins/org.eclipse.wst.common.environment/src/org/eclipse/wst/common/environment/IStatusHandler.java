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
 */
public interface IStatusHandler
{
  /**
   * Reports the given Status and set of possible responses.
   * Returns the choice made by the handler.
   */
  public Choice report ( IStatus status, Choice[] choices );
  
  /**
   * Reports the given Status with implied options to either
   * continue or abort.
   * Throws an exception if the handler decides the caller
   * should stop processing.
   */
  public void report ( IStatus status ) throws StatusException;
  
  /**
   * Report the given Error Status.  No user feedback is provided.
   * @param status
   */
  public void reportError( IStatus status );
  
  /**
   * Report the given Info Status.  No user feedback is provided.
   * @param status
   */
  public void reportInfo( IStatus status );
}
