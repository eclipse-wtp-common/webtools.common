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

package org.eclipse.wst.common.project.facet.core.runtime.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
    private static final String PROP_LOCALIZED_NAME = "localized-name"; //$NON-NLS-1$
    
    /**
     * The value of the property should be a comma-separate list of names that this runtime
     * can be alternatively known by. Any commas in the names have to be escaped using a leading
     * slash character ('\'). 
     */
    
    private static final String PROP_ALTERNATE_NAMES = "alternate-names"; //$NON-NLS-1$
    
    private String name;
    
    /**
     * This class should not be subclassed outside this package.
     */
    
    AbstractRuntime() {}
    
    public final String getName()
    {
        return this.name;
    }
    
    public final String getLocalizedName()
    {
        String localizedName = getProperty( PROP_LOCALIZED_NAME );
        
        if( localizedName == null )
        {
            localizedName = getName();
        }
        
        return localizedName;
    }
    
    public final Set<String> getAlternateNames()
    {
        final String alternateNamesProp = getProperty( PROP_ALTERNATE_NAMES );
        final Set<String> alternateNames = new HashSet<String>();
        
        final String localizedName = getLocalizedName();
        
        if( ! localizedName.equals( getName() ) )
        {
            alternateNames.add( localizedName );
        }
        
        if( alternateNamesProp != null )
        {
            final StringBuilder buf = new StringBuilder();
            boolean seenEscapeChar = false;
            
            for( int i = 0, n = alternateNamesProp.length(); i < n; i++ )
            {
                final char ch = alternateNamesProp.charAt( i );
                
                if( seenEscapeChar )
                {
                    if( ch != ',' )
                    {
                        buf.append( '\\' );
                    }
                    
                    buf.append( ch );
                    seenEscapeChar = false;
                }
                else
                {
                    if( ch == '\\' && i != n - 1 )
                    {
                        seenEscapeChar = true;
                    }
                    else if( ch == ',' )
                    {
                        final String name = buf.toString().trim();
                        
                        if( name.length() > 0 )
                        {
                            alternateNames.add( name );
                        }
                        
                        buf.setLength( 0 );
                    }
                    else
                    {
                        buf.append( ch );
                    }
                }
            }
            
            if( seenEscapeChar )
            {
                buf.append( '\\' );
            }
            
            final String name = buf.toString().trim();
            
            if( name.length() > 0 )
            {
                alternateNames.add( name );
            }
        }
        
        return Collections.unmodifiableSet( alternateNames );
    }
    
    final void setName( final String name )
    {
        this.name = name;
    }

    public final String getProperty( final String key )
    {
        return getProperties().get( key );
    }
    
    public final Object getAdapter( final Class adapter )
    {
        final String t = adapter.getName();
        Object res = Platform.getAdapterManager().loadAdapter( this, t );
        
        if( res == null )
        {
            for( IRuntimeComponent rc : getRuntimeComponents() )
            {
                res = rc.getAdapter( adapter );
                
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
        for( IProjectFacetVersion fv : f.getVersions() )
        {
            if( supports( fv ) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    public Set<IProjectFacetVersion> getDefaultFacets( final Set<IProjectFacet> fixed )
    
        throws CoreException
        
    {
        // 1. Get the complete list.
        
        final Map<IProjectFacet,IProjectFacetVersion> facets 
            = new HashMap<IProjectFacet,IProjectFacetVersion>();
        
        for( IRuntimeComponent rc : getRuntimeComponents() )
        {
            final IRuntimeComponentVersion rcv = rc.getRuntimeComponentVersion();

            for( IProjectFacetVersion fv : RuntimeManagerImpl.getDefaultFacets( rcv ) )
            {
                if( ! facets.containsKey( fv.getProjectFacet() ) )
                {
                    facets.put( fv.getProjectFacet(), fv );
                }
            }
        }
        
        // 2. Remove the facets that conflict with fixed facets.
        
        final Set<IProjectFacet> toRemove = new HashSet<IProjectFacet>();
        
        for( IProjectFacetVersion fv : facets.values() )
        {
            if( ! fv.isValidFor( fixed ) )
            {
                toRemove.add( fv.getProjectFacet() );
            }
        }
        
        for( IProjectFacet f : toRemove )
        {
            facets.remove( f );
        }
        
        // 3. Make sure that the result includes all of the fixed facets.
        
        Map<IProjectFacet,IProjectFacetVersion> toadd = null;
        
        for( IProjectFacet f : fixed )
        {
            if( ! facets.containsKey( f ) )
            {
                if( toadd == null )
                {
                    toadd = new HashMap<IProjectFacet,IProjectFacetVersion>();
                }
                
                toadd.put( f, f.getLatestSupportedVersion( this ) );
            }
        }
        
        if( toadd != null )
        {
            facets.putAll( toadd );
        }
        
        // 4. Return the result.
        
        return new HashSet<IProjectFacetVersion>( facets.values() );
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
