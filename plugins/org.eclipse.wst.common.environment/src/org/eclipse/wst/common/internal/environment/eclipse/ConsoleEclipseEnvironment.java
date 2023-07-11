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
package org.eclipse.wst.common.internal.environment.eclipse;

import org.eclipse.wst.common.environment.IEnvironment;
import org.eclipse.wst.common.environment.ILog;
import org.eclipse.wst.common.environment.NullStatusHandler;
import org.eclipse.wst.common.environment.IStatusHandler;
import org.eclipse.wst.common.environment.uri.SimpleURIFactory;
import org.eclipse.wst.common.environment.uri.IURIFactory;
import org.eclipse.wst.common.internal.environment.uri.file.FileScheme;


/**
 *  This class is intended for use in a headless Eclipse environment.  
 */
public class ConsoleEclipseEnvironment implements IEnvironment
{
	private SimpleURIFactory  uriFactory_      = null;
	private IStatusHandler     statusHandler_   = null;
	  
	public ConsoleEclipseEnvironment()
	{
	  this( new NullStatusHandler() );	
	}
	
	public ConsoleEclipseEnvironment( IStatusHandler  statusHandler )
	{
	  uriFactory_      = new SimpleURIFactory();
	  statusHandler_   = statusHandler;
	    
	  uriFactory_.registerScheme( "platform", new EclipseScheme( this ) ); //$NON-NLS-1$
	  uriFactory_.registerScheme( "file", new FileScheme() ); //$NON-NLS-1$
	}
			
	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.environment.IEnvironment#getLog()
	 */
	@Override
	public ILog getLog() 
	{
		return new EclipseLog();
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.environment.IEnvironment#getStatusHandler()
	 */
	@Override
	public IStatusHandler getStatusHandler() 
	{
		return statusHandler_;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.environment.IEnvironment#getURIFactory()
	 */
	@Override
	public IURIFactory getURIFactory() 
	{
		return uriFactory_;
	}
}
