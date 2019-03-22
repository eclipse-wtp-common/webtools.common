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

package org.eclipse.wst.common.project.facet.core.util.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProgressMonitorUtil 
{
    private ProgressMonitorUtil() {}
    
    public static void beginTask( final IProgressMonitor monitor,
                                  final String taskName,
                                  final int totalWork )
    {
        if( monitor != null )
        {
            monitor.beginTask( taskName, totalWork );
        }
    }
    
    public static void worked( final IProgressMonitor monitor,
                               final int work )
    {
        if( monitor != null )
        {
            monitor.worked( work );
        }
    }
    
    public static void done( final IProgressMonitor monitor )
    {
        if( monitor != null )
        {
            monitor.done();
        }
    }
    
    public static IProgressMonitor submon( final IProgressMonitor parent,
                                           final int ticks )
    {
        return submon( parent, ticks, SubProgressMonitor.SUPPRESS_SUBTASK_LABEL );
    }

    public static IProgressMonitor submon( final IProgressMonitor parent,
                                           final int ticks,
                                           final int style )
    {
        return ( parent == null ? new NullProgressMonitor() : new SubProgressMonitor( parent, ticks, style ) );
    }
    
    public static void subTask( final IProgressMonitor monitor,
                                final String taskName )
    {
        if( monitor != null )
        {
            monitor.subTask( taskName );
        }
    }
    
    public static void checkIfCanceled( final IProgressMonitor monitor )
    
        throws InterruptedException
        
    {
        if( monitor != null && monitor.isCanceled() )
        {
            throw new InterruptedException();
        }
    }

}
