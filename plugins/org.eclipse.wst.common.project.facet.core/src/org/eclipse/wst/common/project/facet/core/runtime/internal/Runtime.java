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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class Runtime

    extends AbstractRuntime
    
{
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

    void setProperty( final String key,
                      final String value )
    {
        this.properties.put( key, value );
    }

    public synchronized boolean supports( final IProjectFacetVersion fv )
    {
        if( this.supported == null )
        {
            this.supported = RuntimeManagerImpl.getSupportedFacets( this.components );
        }
            
        return this.supported.contains( fv );
    }

}
