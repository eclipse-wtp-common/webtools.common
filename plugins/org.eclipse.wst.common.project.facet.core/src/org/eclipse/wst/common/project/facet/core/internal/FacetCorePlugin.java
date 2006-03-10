/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetCorePlugin 

    extends Plugin 
    
{
    public static final String PLUGIN_ID 
        = "org.eclipse.wst.common.project.facet.core"; //$NON-NLS-1$
    
    private static FacetCorePlugin plugin;
    private static final Set messagesLogged = new HashSet();
    
    public FacetCorePlugin() 
    {
        super();
        plugin = this;
    }
    
    public static FacetCorePlugin getInstance()
    {
        return plugin;
    }
    
    public static void log( final Exception e )
    {
        final String msg = e.getMessage() + ""; //$NON-NLS-1$
        log( new Status( IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, e ) );
    }

    public static void log( final IStatus status )
    {
        getInstance().getLog().log( status );
    }
    
    public static void log( final String msg )
    {
        log( new Status( IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, null ) );
    }
    
    public static void logError( final String msg )
    {
        logError( msg, false );
    }
    
    public static void logError( final String msg,
                                 final boolean suppressDuplicates )
    {
        if( suppressDuplicates && messagesLogged.contains( msg ) )
        {
            return;
        }
        
        messagesLogged.add( msg );
        
        log( new Status( IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, null ) );
    }

    public static void logWarning( final String msg )
    {
        logWarning( msg, false );
    }
    
    public static void logWarning( final String msg,
                                   final boolean suppressDuplicates )
    {
        if( suppressDuplicates && messagesLogged.contains( msg ) )
        {
            return;
        }
        
        messagesLogged.add( msg );
        
        log( new Status( IStatus.WARNING, PLUGIN_ID, IStatus.OK, msg, null ) );
    }
    
    public static IStatus createErrorStatus( final String msg )
    {
        return createErrorStatus( msg, null );
    }

    public static IStatus createErrorStatus( final String msg,
                                             final Exception e )
    {
        return new Status( IStatus.ERROR, FacetCorePlugin.PLUGIN_ID, 0, msg, e );
    }
    
    public static Object instantiate( final String pluginId,
                                      final String clname,
                                      final Class interfc )
    
        throws CoreException
        
    {
        final Bundle bundle = Platform.getBundle( pluginId );
        
        final Object obj;
        
        try
        {
            final Class cl = bundle.loadClass( clname );
            obj = cl.newInstance();
        }
        catch( Exception e )
        {
            final String msg
                = NLS.bind( Resources.failedToCreate, clname );
            
            throw new CoreException( createErrorStatus( msg, e ) );
        }
        
        if( ! interfc.isAssignableFrom( obj.getClass() ) )
        {
            final String msg
                = NLS.bind( Resources.doesNotImplement, clname, 
                            interfc.getClass().getName() );
            
            throw new CoreException( createErrorStatus( msg ) );
        }
        
        return obj;
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String failedToCreate;
        public static String doesNotImplement;
        
        static
        {
            initializeMessages( FacetCorePlugin.class.getName(), 
                                Resources.class );
        }
    }
    
}
