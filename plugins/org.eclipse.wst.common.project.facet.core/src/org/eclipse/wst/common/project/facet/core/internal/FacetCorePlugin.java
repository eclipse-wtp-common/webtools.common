/******************************************************************************
 * Copyright (c) 2005, 2006 BEA Systems, Inc.
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
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetCorePlugin 
{
    public static final String PLUGIN_ID = FacetedProjectFramework.PLUGIN_ID;
    
    private static final String TRACING_ACTION_SORTING
        = PLUGIN_ID + "/actionSorting"; //$NON-NLS-1$

    private static final String TRACING_FRAMEWORK_ACTIVATION
        = PLUGIN_ID + "/activation"; //$NON-NLS-1$
    
    private static final Set messagesLogged = new HashSet();
    
    private static final ILog platformLog
        = Platform.getLog( Platform.getBundle( PLUGIN_ID ) );
    
    public static boolean isTracingActionSorting()
    {
        return checkDebugOption( TRACING_ACTION_SORTING );
    }
    
    public static boolean isTracingFrameworkActivation()
    {
        return checkDebugOption( TRACING_FRAMEWORK_ACTIVATION );
    }
    
    private static boolean checkDebugOption( final String debugOption )
    {
        final String optionValue = Platform.getDebugOption( debugOption );
        
        return optionValue == null 
               ? false : Boolean.valueOf( optionValue ).equals( Boolean.TRUE );
    }
    
    public static void log( final Exception e )
    {
        final String msg = e.getMessage() + ""; //$NON-NLS-1$
        log( new Status( IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, e ) );
    }

    public static void log( final IStatus status )
    {
        platformLog.log( status );
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
