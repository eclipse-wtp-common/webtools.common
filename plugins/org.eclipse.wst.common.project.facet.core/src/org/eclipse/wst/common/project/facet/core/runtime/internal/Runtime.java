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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class Runtime

    implements IRuntime
    
{
    private String name;
    
    private final List components = new ArrayList();
    
    private final List componentsReadOnly 
        = Collections.unmodifiableList( this.components );
    
    private final Map properties = new HashMap();
    
    private final Map propertiesReadOnly
        = Collections.unmodifiableMap( this.properties );
    
    private Set supported = null;
    
    /**
     * This class should not be instantiated outside this package.
     */
    
    Runtime() {}
    
    public String getName()
    {
        return this.name;
    }
    
    void setName( final String name )
    {
        this.name = name;
    }

    public List getRuntimeComponents()
    {
        return this.componentsReadOnly;
    }
    
    void addRuntimeComponent( final IRuntimeComponent component )
    {
        this.components.add( component );
    }

    public Map getProperties()
    {
        return this.propertiesReadOnly;
    }

    public String getProperty( final String key )
    {
        return (String) this.properties.get( key );
    }
    
    void setProperty( final String key,
                      final String value )
    {
        this.properties.put( key, value );
    }

    public boolean supports( final IProjectFacetVersion fv )
    {
        synchronized( this )
        {
            if( this.supported == null )
            {
                this.supported = RuntimeManagerImpl.getSupportedFacets( this );
            }
            
            return this.supported.contains( fv );
        }
    }

    public Object getAdapter( final Class adapter )
    {
        Object res = Platform.getAdapterManager().loadAdapter( this, adapter.getName() );
        
        if( res == null )
        {
            for( Iterator itr = this.components.iterator(); itr.hasNext(); )
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
    
    public boolean equals( final Object obj )
    {
        if( obj instanceof Runtime )
        {
            final Runtime r = (Runtime) obj;
            
            return this.name.equals( r.name ) && 
                   this.components.equals( r.components ) &&
                   this.properties.equals( r.properties );
        }
        
        return false;
    }
    
    public int hashCode()
    {
        return this.name.hashCode();
    }

}
