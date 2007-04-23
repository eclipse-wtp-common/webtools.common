/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.environment;

import org.eclipse.wst.common.environment.uri.IURIFactory;


/**
 * An IEnvironment provides the means for getting a 
 * <ol>
 * <li>A log for writing messages to a logging facility,</li>
 * <li>A progress monitor for receiving progress information,</li>
 * <li>A status handler for receiving and processing status reports,</li>
 * <li>A factory for the handling of URIs (resources).</li>
 * </ol>
 * 
 * @since 1.0
 */
public interface IEnvironment
{
  /**
   * Returns a logging facility.
   * 
   * @return returns a logging facility.
   */
  public ILog getLog ();

  /**
   * Returns a status handler.
   * 
   * @return returns a status handler.
   */
  public IStatusHandler getStatusHandler ();

  /**
   * Returns a IURI factory.
   * Hint: Implementers should insure that the Factory they return
   * has a reference to this IEnvironment so that IURI objects can
   * report progress and announce status.
   * 
   * @return returns a URI factory.
   */
  public IURIFactory getURIFactory ();
}
