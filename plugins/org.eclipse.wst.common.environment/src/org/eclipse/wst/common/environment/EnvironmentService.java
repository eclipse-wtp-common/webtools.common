/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.environment;

import org.eclipse.wst.common.environment.uri.IURIScheme;
import org.eclipse.wst.common.environment.uri.URIException;
import org.eclipse.wst.common.internal.environment.eclipse.ConsoleEclipseEnvironment;

/**
 * 
 * This class creates a console environment.  It also contains static
 * conviences methods for creating an ILog object as well as an Eclipse and
 * File Scheme.
 * 
 * @since 1.0
 *
 */
public class EnvironmentService
{
  static private IEnvironment environment;
  
  /**
   * 
   * @return returns an Eclipse console environment.
   */
  public static IEnvironment getEclipseConsoleEnvironment()
  {
    if( environment == null ) environment = new ConsoleEclipseEnvironment();
    
    return environment; 
  }  
  
  /**
   * 
   * @return returns an Eclipse logger.
   */
  public static ILog getEclipseLog()
  {
    IEnvironment environment = getEclipseConsoleEnvironment();
    
    return environment.getLog();
  }
  
  /**
   * 
   * @return returns an Eclipse scheme.
   */
  public static IURIScheme getEclipseScheme()
  {
    IEnvironment environment = getEclipseConsoleEnvironment();
    IURIScheme   scheme      = null;
    
    try
    {
      scheme = environment.getURIFactory().newURIScheme( "platform" ); //$NON-NLS-1$
    }
    catch( URIException exc )
    {    
    }
    
    return scheme;
  }  
  
  /**
   * 
   * @return returns a File scheme.
   */
  public static IURIScheme getFileScheme()
  {
    IEnvironment environment = getEclipseConsoleEnvironment();
    IURIScheme   scheme      = null;
    
    try
    {
      scheme = environment.getURIFactory().newURIScheme( "file" ); //$NON-NLS-1$
    }
    catch( URIException exc )
    {    
    }
    
    return scheme;
  }
}
