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

import org.eclipse.wst.common.environment.IEnvironment;
import org.eclipse.wst.common.environment.EnvironmentService;
import org.eclipse.wst.common.environment.ILog;
import org.eclipse.wst.common.environment.IStatusHandler;
import org.eclipse.wst.common.environment.uri.SimpleURIFactory;
import org.eclipse.wst.common.environment.uri.IURIFactory;
import org.eclipse.wst.common.environment.uri.IURIScheme;


/**
 * This class implements an IEnvironment class for the Eclipse IEnvironment.
 * This IEnvironment currently supports the "platform" protocol and the "file"
 * protocol.
 *
 */
public class EclipseEnvironment implements IEnvironment
{
  private SimpleURIFactory uriFactory_      = null;
  private IStatusHandler    statusHandler_   = null;
  private ILog              logger_          = null;
  
  public EclipseEnvironment()
  {
    this( new EclipseStatusHandler() );  
  }
  
  public EclipseEnvironment( IStatusHandler   statusHandler )
  {
    IURIScheme eclipseScheme = EnvironmentService.getEclipseScheme( this );
    IURIScheme fileScheme    = EnvironmentService.getFileScheme();
    
    uriFactory_      = new SimpleURIFactory();
    statusHandler_   = statusHandler;
    
    uriFactory_.registerScheme( "platform", eclipseScheme );
    uriFactory_.registerScheme( "file", fileScheme );
  }
  
  /**
   * @see org.eclipse.wst.common.environment.IEnvironment#getLog()
   */
  public ILog getLog()
  {
	  if( logger_ == null )
    {  
      logger_ = EnvironmentService.getEclipseLog(); 
    };
	
    return logger_;
  }
   
  /**
   * @see org.eclipse.wst.common.environment.IEnvironment#getStatusHandler()
   */
  public IStatusHandler getStatusHandler()
  {
    return statusHandler_;
  }

  /** (non-Javadoc)
   * @see org.eclipse.wst.common.environment.IEnvironment#getURIFactory()
   */
  public IURIFactory getURIFactory()
  {
    return uriFactory_;
  }
}
