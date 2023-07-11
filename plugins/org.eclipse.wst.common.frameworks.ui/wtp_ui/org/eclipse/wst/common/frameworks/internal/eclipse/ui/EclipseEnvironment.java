/*******************************************************************************
 * Copyright (c) 2004, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.eclipse.ui;

import org.eclipse.wst.common.environment.EnvironmentService;
import org.eclipse.wst.common.environment.IEnvironment;
import org.eclipse.wst.common.environment.ILog;
import org.eclipse.wst.common.environment.IStatusHandler;
import org.eclipse.wst.common.environment.uri.IURIFactory;
import org.eclipse.wst.common.environment.uri.IURIScheme;
import org.eclipse.wst.common.environment.uri.SimpleURIFactory;


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
    IURIScheme eclipseScheme = EnvironmentService.getEclipseScheme();
    IURIScheme fileScheme    = EnvironmentService.getFileScheme();
    
    uriFactory_      = new SimpleURIFactory();
    statusHandler_   = statusHandler;
    
    uriFactory_.registerScheme( "platform", eclipseScheme ); //$NON-NLS-1$
    uriFactory_.registerScheme( "file", fileScheme ); //$NON-NLS-1$
  }
  
  /**
   * @see org.eclipse.wst.common.environment.IEnvironment#getLog()
   */
  @Override
public ILog getLog()
  {
	  if( logger_ == null )
    {  
      logger_ = EnvironmentService.getEclipseLog(); 
    }
	
    return logger_;
  }
   
  /**
   * @see org.eclipse.wst.common.environment.IEnvironment#getStatusHandler()
   */
  @Override
public IStatusHandler getStatusHandler()
  {
    return statusHandler_;
  }

  /** (non-Javadoc)
   * @see org.eclipse.wst.common.environment.IEnvironment#getURIFactory()
   */
  @Override
public IURIFactory getURIFactory()
  {
    return uriFactory_;
  }
}
