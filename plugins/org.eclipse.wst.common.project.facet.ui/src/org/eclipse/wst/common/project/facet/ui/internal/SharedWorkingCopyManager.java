/******************************************************************************
 * Copyright (c) 2008 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SharedWorkingCopyManager
{
    private static final Map<IFacetedProject,IFacetedProjectWorkingCopy> sharedWorkingCopies
        = new HashMap<IFacetedProject,IFacetedProjectWorkingCopy>();
    
    private static final Map<IFacetedProject,Integer> sharedWorkingCopyUserCount
        = new HashMap<IFacetedProject,Integer>();
    
    public static synchronized IFacetedProjectWorkingCopy getWorkingCopy( final IFacetedProject project )
    {
        IFacetedProjectWorkingCopy fpjwc = sharedWorkingCopies.get( project );
        
        if( fpjwc != null )
        {
            int userCount = sharedWorkingCopyUserCount.get( project );
            sharedWorkingCopyUserCount.put( project, userCount + 1 );
        }
        else
        {
            fpjwc = project.createWorkingCopy();
            sharedWorkingCopies.put( project, fpjwc );
            sharedWorkingCopyUserCount.put( project, 1 );
        }
        
        return fpjwc;
    }
    
    public static synchronized void releaseWorkingCopy( final IFacetedProject project )
    {
        final IFacetedProjectWorkingCopy fpjwc = sharedWorkingCopies.get( project );
        final int usersCount = sharedWorkingCopyUserCount.get( project );
        
        if( usersCount == 1 )
        {
            sharedWorkingCopies.remove( project );
            sharedWorkingCopyUserCount.remove( project );
            fpjwc.dispose();
        }
        else
        {
            sharedWorkingCopyUserCount.put( project, usersCount - 1 );
        }
    }

}
