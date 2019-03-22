/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
 * ILog objects provide a means for logging information for FFDC etc.
 * 
 * @since 1.0
 */
public interface ILog
{
  /**
   * This constant indicates that a logging message is Ok.
   */
  public static final int OK = 0;
  
  /**
   * This constant indicates that a logging message is informational.
   */
  public static final int INFO = 1;
  
  /**
   * This constant indicates that a logging message is warning.
   */
  public static final int WARNING = 2;
  
  /**
   * This constant indicates that a logging message is an error.
   */
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
   * Returns true if this debug option is set to true.
   * 
   * @param option this debug option string.
   * @return returns true if this debug option is set to true.
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
   * @param option this debug option string.
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
   * @param option this debug option string.
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
   * @param option this debug option string.
   * @param messageNum  The message number.
   * @param caller The object (for non-static methods) or class (for
   * static methods) doing the logging.
   * @param method The simple name of the method doing the loging.
   * @param object The Object to log.
   */
  public void log ( int severity, String option, int messageNum, Object caller, String method, Object object );
}
