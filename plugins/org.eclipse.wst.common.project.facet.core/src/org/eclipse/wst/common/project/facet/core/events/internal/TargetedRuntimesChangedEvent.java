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

package org.eclipse.wst.common.project.facet.core.events.internal;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.events.ITargetedRuntimesChangedEvent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TargetedRuntimesChangedEvent

    extends FacetedProjectEvent
    implements ITargetedRuntimesChangedEvent
    
{
    private final Set<IRuntime> oldTargetedRuntimes;
    private final Set<IRuntime> newTargetedRuntimes;
    
    public TargetedRuntimesChangedEvent( final IFacetedProject project,
                                         final Set<IRuntime> oldTargetedRuntimes,
                                         final Set<IRuntime> newTargetedRuntimes )
    {
        super( project, Type.TARGETED_RUNTIMES_CHANGED );
        
        this.oldTargetedRuntimes = oldTargetedRuntimes;
        this.newTargetedRuntimes = newTargetedRuntimes;
    }

    public Set<IRuntime> getOldTargetedRuntimes()
    {
        return this.oldTargetedRuntimes;
    }

    public Set<IRuntime> getNewTargetedRuntimes()
    {
        return this.newTargetedRuntimes;
    }

    @Override
    protected void toStringInternal( final StringBuilder buf )
    {
        buf.append( "  <old-targeted-runtimes>\n" ); //$NON-NLS-1$
        
        for( IRuntime r : sort( this.oldTargetedRuntimes ) )
        {
            buf.append( "    <runtime name=\"" ); //$NON-NLS-1$
            buf.append( r.getName() );
            buf.append( "\"/>\n" ); //$NON-NLS-1$
        }
        
        buf.append( "  </old-targeted-runtimes>\n" ); //$NON-NLS-1$

        buf.append( "  <new-targeted-runtimes>\n" ); //$NON-NLS-1$
        
        for( IRuntime r : sort( this.newTargetedRuntimes ) )
        {
            buf.append( "    <runtime id=\"" ); //$NON-NLS-1$
            buf.append( r.getName() );
            buf.append( "\"/>\n" ); //$NON-NLS-1$
        }
        
        buf.append( "  </new-targeted-runtimes>\n" ); //$NON-NLS-1$
    }
    
    private static Collection<IRuntime> sort( final Collection<IRuntime> input )
    {
        final Set<IRuntime> result = new TreeSet<IRuntime>( new RuntimeComparator() );
        result.addAll( input );
        return result;
    }
    
    private static final class RuntimeComparator 
    
        implements Comparator<IRuntime>
    
    {
        public int compare( final IRuntime r1, 
                            final IRuntime r2 )
        {
            return r1.getName().compareTo( r2.getName() );
        }
    }
    
}
