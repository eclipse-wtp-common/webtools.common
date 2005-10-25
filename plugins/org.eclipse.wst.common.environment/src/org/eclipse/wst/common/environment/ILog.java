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
 * ILog objects provide a means for logging information for FFDC etc.
 */
public interface ILog
{
  public static final int OK = 0;
  public static final int INFO = 1;
  public static final int WARNING = 2;
  public static final int ERROR = 4;

  /**
   * Returns true if logging is enabled.
   * There is no requirement to call this method before attempting
   * a call to one of the <code>log</code> methods, as they will
   * quietly do nothing if logging is disabled. This method can be
   * useful, however, anywhere the cost of building up the parameters
   * to a log method might be high and best avoided except when logging
   * is enabled.
   * @return True if logging is enabled, and false otherwise.
   */
  public boolean isEnabled ();
  
  /**
   * Returns true if the the debug option is set to true.
   */
  public boolean isEnabled (String option);

  /**
   * Logs a <code>Throwable</code>.
   * @param severity The severity of the logging entry.
   * @param messageNum  The message number.
   * @param caller The object (for non-static methods) or class (for
   * static methods) doing the logging.
   * @param method The simple name of the method doing the loging.
   * @param throwable The Throwable to log.
   */
  public void log ( int severity, int messageNum, Object caller, String method, Throwable throwable );
  
  /**
   * Logs a <code>Throwable</code>.
   * @param severity The severity of the logging entry.
   * @param debug option
   * @param messageNum The message number.
   * @param caller The object (for non-static methods) or class (for
   * static methods) doing the logging.
   * @param method The simple name of the method doing the loging.
   * @param throwable The Throwable to log.
   */
  public void log ( int severity, String option, int messageNum, Object caller, String method, Throwable throwable );

  /**
   * Logs a <code>Status</code>.
   * @param severity The severity of the logging entry.
   * @param messageNum  The message number.
   * @param caller The object (for non-static methods) or class (for
   * static methods) doing the logging.
   * @param method The simple name of the method doing the loging.
   * @param status The Status to log.
   */
  public void log ( int severity, int messageNum, Object caller, String method, IStatus status );
  
  /**
   * Logs a <code>Status</code>.
   * @param severity The severity of the logging entry.
   * @param debug option
   * @param messageNum  The message number.
   * @param caller The object (for non-static methods) or class (for
   * static methods) doing the logging.
   * @param method The simple name of the method doing the loging.
   * @param status The Status to log.
   */
  public void log ( int severity, String option, int messageNum, Object caller, String method, IStatus status );

  /**
   * Logs an <code>Object</code>.
   * @param severity The severity of the logging entry.
   * @param messageNum  The message number.
   * @param caller The object (for non-static methods) or class (for
   * static methods) doing the logging.
   * @param method The simple name of the method doing the loging.
   * @param object The Object to log.
   */
  public void log ( int severity, int messageNum, Object caller, String method, Object object );
  
  /**
   * Logs an <code>Object</code>.
   * @param severity The severity of the logging entry.
   * @param debug option
   * @param messageNum  The message number.
   * @param caller The object (for non-static methods) or class (for
   * static methods) doing the logging.
   * @param method The simple name of the method doing the loging.
   * @param object The Object to log.
   */
  public void log ( int severity, String option, int messageNum, Object caller, String method, Object object );
}
