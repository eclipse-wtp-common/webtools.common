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
package org.eclipse.wst.common.internal.environment.eclipse;

import org.eclipse.wst.common.environment.Environment;
import org.eclipse.wst.common.environment.Log;
import org.eclipse.wst.common.environment.NullStatusHandler;
import org.eclipse.wst.common.environment.StatusHandler;
import org.eclipse.wst.common.environment.uri.SimpleURIFactory;
import org.eclipse.wst.common.environment.uri.URIFactory;
import org.eclipse.wst.common.internal.environment.uri.file.FileScheme;


/**
 *  This class is intended for use in a headless Eclipse environment.  
 */
public class ConsoleEclipseEnvironment implements Environment
{
	private SimpleURIFactory  uriFactory_      = null;
	private StatusHandler     statusHandler_   = null;
	  
	public ConsoleEclipseEnvironment()
	{
	  this( new NullStatusHandler() );	
	}
	
	public ConsoleEclipseEnvironment( StatusHandler  statusHandler )
	{
	  uriFactory_      = new SimpleURIFactory();
	  statusHandler_   = statusHandler;
	    
	  uriFactory_.registerScheme( "platform", new EclipseScheme( this ) );
	  uriFactory_.registerScheme( "file", new FileScheme() );
	}
			
	/* (non-Javadoc)
	 * @see org.eclipse.wst.command.internal.provisional.env.core.common.Environment#getLog()
	 */
	public Log getLog() 
	{
		return new EclipseLog();
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.wst.command.internal.provisional.env.core.common.Environment#getStatusHandler()
	 */
	public StatusHandler getStatusHandler() 
	{
		return statusHandler_;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.command.internal.provisional.env.core.common.Environment#getURIFactory()
	 */
	public URIFactory getURIFactory() 
	{
		return uriFactory_;
	}
}
