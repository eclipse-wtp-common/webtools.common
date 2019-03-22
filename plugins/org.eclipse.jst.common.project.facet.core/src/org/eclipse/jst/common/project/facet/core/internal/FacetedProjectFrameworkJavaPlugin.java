/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core.internal;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class FacetedProjectFrameworkJavaPlugin 
{
    public static final String PLUGIN_ID = "org.eclipse.jst.common.project.facet.core"; //$NON-NLS-1$
    
    private static final ILog platformLog
        = Platform.getLog( Platform.getBundle( PLUGIN_ID ) );
    
    public static IEclipsePreferences getWorkspacePreferences()
    {
        final InstanceScope prefs = new InstanceScope();
        return prefs.getNode( PLUGIN_ID );
    }
    
    public static void log( final Exception e )
    {
        final String message = e.getMessage() + ""; //$NON-NLS-1$
        log( createErrorStatus( message, e ) );
    }

    public static void log( final IStatus status )
    {
        platformLog.log( status );
    }
    
    public static void logError( final String message )
    {
        log( createErrorStatus( message ) );
    }
    
    public static IStatus createErrorStatus( final String message )
    {
        return createErrorStatus( message, null );
    }

    public static IStatus createErrorStatus( final Exception e )
    {
        return createErrorStatus( e.getMessage(), e );
    }
    
    public static IStatus createErrorStatus( final String message,
                                             final Exception e )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, -1, message, e )  ;      
    }

}
