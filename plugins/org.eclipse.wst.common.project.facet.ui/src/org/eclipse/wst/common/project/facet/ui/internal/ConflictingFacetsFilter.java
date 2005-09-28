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

package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IConstraint;
import org.eclipse.wst.common.project.facet.core.IGroup;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ConflictingFacetsFilter 

    implements FacetsSelectionPanel.IFilter
    
{
    private final Set fixed;
    
    public ConflictingFacetsFilter( final Set fixed )
    {
        this.fixed = fixed;
    }
    
    public boolean check( final IProjectFacetVersion fv )
    {
        for( Iterator itr = this.fixed.iterator(); itr.hasNext(); )
        {
            final IProjectFacet f = (IProjectFacet) itr.next();
            
            if( f.getVersions().contains( fv ) )
            {
                return true;
            }
        }
        
        return check( fv.getConstraint() );
    }
    
    private boolean check( final IConstraint op )
    {
        if( op.getType() == IConstraint.Type.AND )
        {
            for( Iterator itr = op.getOperands().iterator(); itr.hasNext(); )
            {
                if( ! check( (IConstraint) itr.next() ) )
                {
                    return false;
                }
            }
            
            return true;
        }
        else if( op.getType() == IConstraint.Type.OR )
        {
            for( Iterator itr = op.getOperands().iterator(); itr.hasNext(); )
            {
                if( check( (IConstraint) itr.next() ) )
                {
                    return true;
                }
            }
            
            return false;
        }
        else if( op.getType() == IConstraint.Type.CONFLICTS )
        {
            final String gid = (String) op.getOperand( 0 );
            final IGroup group = ProjectFacetsManager.getGroup( gid );
            
            for( Iterator itr = this.fixed.iterator(); itr.hasNext(); )
            {
                final IProjectFacet f = (IProjectFacet) itr.next();
                
                if( group.getMembers().containsAll( f.getVersions() ) )
                {
                    return false;
                }
            }
            
            return true;
        }
        else if( op.getType() == IConstraint.Type.REQUIRES )
        {
            final String name = (String) op.getOperand( 0 );
            final String version = (String) op.getOperand( 1 );
            
            final boolean allowNewer 
                = ( (Boolean) op.getOperand( 2 ) ).booleanValue();
            
            final boolean soft
                = ( (Boolean) op.getOperand( 3 ) ).booleanValue();
        
            if( soft )
            {
                return true;
            }
            else
            {
                final IProjectFacet rf 
                    = ProjectFacetsManager.getProjectFacet( name );
                
                final Set versions = rf.getVersions();
                final Comparator comp = rf.getVersionComparator();
                
                for( Iterator itr = versions.iterator(); itr.hasNext(); )
                {
                    final IProjectFacetVersion fv 
                        = (IProjectFacetVersion) itr.next();
                    
                    final String fvstr = fv.getVersionString();
                    
                    final int compres = comp.compare( fvstr, version );
                    
                    if( ( allowNewer && compres >= 0 ) ||
                        ( ! allowNewer && compres == 0 ) )
                    {
                        if( check( fv ) )
                        {
                            return true;
                        }
                    }
                }
            
                return false;
            }
        }
        else
        {
            throw new IllegalStateException();
        }
    }

}
