/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
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
        return ( parent == null ? null : new SubProgressMonitor( parent, ticks ) );
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
