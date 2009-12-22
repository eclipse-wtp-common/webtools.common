/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.runtime.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class Runtime

    extends AbstractRuntime
    
{
    private final List<IRuntimeComponent> components = new ArrayList<IRuntimeComponent>();
    
    private final List<IRuntimeComponent> componentsReadOnly 
        = Collections.unmodifiableList( this.components );
    
    private final Map<String,String> properties = new HashMap<String,String>();
    
    private final Map<String,String> propertiesReadOnly
        = Collections.unmodifiableMap( this.properties );
    
    private Set<IProjectFacetVersion> supported = null;
    
    /**
     * This class should not be instantiated outside this package.
     */
    
    Runtime() {}
    
    public List<IRuntimeComponent> getRuntimeComponents()
    {
        return this.componentsReadOnly;
    }
    
    void addRuntimeComponent( final IRuntimeComponent component )
    {
        ( (RuntimeComponent) component ).setRuntime( this );
        this.components.add( component );
    }

    public Map<String,String> getProperties()
    {
        return this.propertiesReadOnly;
    }

    void setProperty( final String key,
                      final String value )
    {
        this.properties.put( key, value );
    }

    public synchronized boolean supports( final IProjectFacetVersion fv )
    {
        if( fv.getPluginId() == null )
        {
            return true;
        }
        
        if( this.supported == null )
        {
            this.supported = RuntimeManagerImpl.getSupportedFacets( this.components );
        }
            
        return this.supported.contains( fv );
    }

}
