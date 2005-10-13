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
package org.eclipse.wst.common.frameworks.internal.eclipse.ui;

import org.eclipse.wst.common.environment.Environment;
import org.eclipse.wst.common.environment.EnvironmentService;
import org.eclipse.wst.common.environment.Log;
import org.eclipse.wst.common.environment.StatusHandler;
import org.eclipse.wst.common.environment.uri.SimpleURIFactory;
import org.eclipse.wst.common.environment.uri.URIFactory;
import org.eclipse.wst.common.environment.uri.URIScheme;


/**
 * This class implements an Environment class for the Eclipse Environment.
 * This Environment currently supports the "platform" protocol and the "file"
 * protocol.
 *
 */
public class EclipseEnvironment implements Environment
{
  private SimpleURIFactory uriFactory_      = null;
  private StatusHandler    statusHandler_   = null;
  private Log              logger_          = null;
  
  public EclipseEnvironment()
  {
    this( new EclipseStatusHandler() );  
  }
  
  public EclipseEnvironment( StatusHandler   statusHandler )
  {
    URIScheme eclipseScheme = EnvironmentService.getEclipseScheme( this );
    URIScheme fileScheme    = EnvironmentService.getFileScheme();
    
    uriFactory_      = new SimpleURIFactory();
    statusHandler_   = statusHandler;
    
    uriFactory_.registerScheme( "platform", eclipseScheme );
    uriFactory_.registerScheme( "file", fileScheme );
  }
  
  /**
   * @see org.eclipse.wst.command.internal.provisional.env.core.common.Environment#getLog()
   */
  public Log getLog()
  {
	  if( logger_ == null )
    {  
      logger_ = EnvironmentService.getEclipseLog(); 
    };
	
    return logger_;
  }
   
  /**
   * @see org.eclipse.wst.command.internal.provisional.env.core.common.Environment#getStatusHandler()
   */
  public StatusHandler getStatusHandler()
  {
    return statusHandler_;
  }

  /** (non-Javadoc)
   * @see org.eclipse.wst.command.internal.provisional.env.core.common.Environment#getURIFactory()
   */
  public URIFactory getURIFactory()
  {
    return uriFactory_;
  }
}
