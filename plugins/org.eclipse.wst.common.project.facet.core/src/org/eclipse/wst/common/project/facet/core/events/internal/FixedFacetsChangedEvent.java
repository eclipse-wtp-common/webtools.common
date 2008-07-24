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

package org.eclipse.wst.common.project.facet.core.events.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.events.IFixedFacetsChangedEvent;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class FixedFacetsChangedEvent

    extends FacetedProjectEvent
    implements IFixedFacetsChangedEvent
    
{
    private final Set<IProjectFacet> oldFixedFacets;
    private final Set<IProjectFacet> newFixedFacets;
    
    public FixedFacetsChangedEvent( final IFacetedProject project,
                                    final Set<IProjectFacet> oldFixedFacets,
                                    final Set<IProjectFacet> newFixedFacets )
    {
        super( project, Type.FIXED_FACETS_CHANGED );
        
        this.oldFixedFacets = Collections.unmodifiableSet( oldFixedFacets );
        this.newFixedFacets = Collections.unmodifiableSet( newFixedFacets );
    }

    public Set<IProjectFacet> getOldFixedFacets()
    {
        return this.oldFixedFacets;
    }

    public Set<IProjectFacet> getNewFixedFacets()
    {
        return this.newFixedFacets;
    }
    
    @Override
    protected void toStringInternal( final StringBuilder buf )
    {
        buf.append( "  <old-fixed-facets>\n" ); //$NON-NLS-1$
        
        for( IProjectFacet f : sort( this.oldFixedFacets ) )
        {
            buf.append( "    <facet id=\"" ); //$NON-NLS-1$
            buf.append( f.getId() );
            buf.append( "\"/>\n" ); //$NON-NLS-1$
        }
        
        buf.append( "  </old-fixed-facets>\n" ); //$NON-NLS-1$

        buf.append( "  <new-fixed-facets>\n" ); //$NON-NLS-1$
        
        for( IProjectFacet f : sort( this.newFixedFacets ) )
        {
            buf.append( "    <facet id=\"" ); //$NON-NLS-1$
            buf.append( f.getId() );
            buf.append( "\"/>\n" ); //$NON-NLS-1$
        }
        
        buf.append( "  </new-fixed-facets>\n" ); //$NON-NLS-1$
    }
    
    private static Collection<IProjectFacet> sort( final Collection<IProjectFacet> input )
    {
        final Set<IProjectFacet> result = new TreeSet<IProjectFacet>( new FacetComparator() );
        result.addAll( input );
        return result;
    }
    
    private static final class FacetComparator 
    
        implements Comparator<IProjectFacet>
    
    {
        public int compare( final IProjectFacet f1, 
                            final IProjectFacet f2 )
        {
            return f1.getId().compareTo( f2.getId() );
        }
    }

}
