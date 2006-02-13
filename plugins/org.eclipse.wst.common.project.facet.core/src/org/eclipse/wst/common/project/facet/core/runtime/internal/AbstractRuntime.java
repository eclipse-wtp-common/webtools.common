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

package org.eclipse.wst.common.project.facet.core.runtime.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public abstract class AbstractRuntime

    implements IRuntime
    
{
    private String name;
    
    /**
     * This class should not be subclassed outside this package.
     */
    
    AbstractRuntime() {}
    
    public final String getName()
    {
        return this.name;
    }
    
    final void setName( final String name )
    {
        this.name = name;
    }

    public final String getProperty( final String key )
    {
        return (String) getProperties().get( key );
    }
    
    public final Object getAdapter( final Class adapter )
    {
        final String t = adapter.getName();
        Object res = Platform.getAdapterManager().loadAdapter( this, t );
        
        if( res == null )
        {
            for( Iterator itr = getRuntimeComponents().iterator(); 
                 itr.hasNext(); )
            {
                res = ( (IRuntimeComponent) itr.next() ).getAdapter( adapter );
                
                if( res != null )
                {
                    return res;
                }
            }
        }
        
        return res;
    }
    
    
    public boolean supports( final IProjectFacet f )
    {
        for( Iterator itr = f.getVersions().iterator(); itr.hasNext(); )
        {
            if( supports( (IProjectFacetVersion) itr.next() ) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    public Set getDefaultFacets( final Set fixed )
    
        throws CoreException
        
    {
        // 1. Get the complete list.
        
        final Map facets = new HashMap();
        
        for( Iterator itr1 = getRuntimeComponents().iterator(); itr1.hasNext(); )
        {
            final IRuntimeComponent rc = (IRuntimeComponent) itr1.next();
            final IRuntimeComponentVersion rcv = rc.getRuntimeComponentVersion();
            
            for( Iterator itr2 = RuntimeManagerImpl.getDefaultFacets( rcv ).iterator();
                 itr2.hasNext(); )
            {
                final IProjectFacetVersion fv = (IProjectFacetVersion) itr2.next();
                
                if( ! facets.containsKey( fv.getProjectFacet() ) )
                {
                    facets.put( fv.getProjectFacet(), fv );
                }
            }
        }
        
        // 2. Remove the facets that conflict with fixed facets.
        
        for( Iterator itr = facets.values().iterator(); itr.hasNext(); )
        {
            if( ! ( (IProjectFacetVersion) itr.next() ).isValidFor( fixed ) )
            {
                itr.remove();
            }
        }
        
        // 3. Make sure that the result includes all of the fixed facets.
        
        Map toadd = null;
        
        for( Iterator itr = fixed.iterator(); itr.hasNext(); )
        {
            final IProjectFacet f = (IProjectFacet) itr.next();
            
            if( ! facets.containsKey( f ) )
            {
                if( toadd == null )
                {
                    toadd = new HashMap();
                }
                
                toadd.put( f, f.getLatestSupportedVersion( this ) );
            }
        }
        
        if( toadd != null )
        {
            facets.putAll( toadd );
        }
        
        // 4. Return the result.
        
        return new HashSet( facets.values() );
    }

    public final boolean equals( final Object obj )
    {
        if( obj instanceof IRuntime )
        {
            final IRuntime r = (IRuntime) obj;
            
            return getName().equals( r.getName() ) && 
                   getRuntimeComponents().equals( r.getRuntimeComponents() ) &&
                   getProperties().equals( r.getProperties() );
        }
        
        return false;
    }
    
    public final int hashCode()
    {
        return this.name.hashCode();
    }
    
    public String toString()
    {
        return this.name;
    }

}
