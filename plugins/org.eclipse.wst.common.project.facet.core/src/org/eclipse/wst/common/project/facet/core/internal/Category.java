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

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class Category 

    implements ICategory 
    
{
    private String id;
    private String plugin;
    private String label;
    private String description;
    private Set<IProjectFacet> facets;
    private Set<IProjectFacet> facetsReadOnly;
    
    Category() 
    {
        this.facets = new HashSet<IProjectFacet>();
        this.facetsReadOnly = Collections.unmodifiableSet( this.facets );
    }
    
    public String getId() 
    {
        return this.id;
    }
    
    void setId( final String id )
    {
        this.id = id;
    }
    
    public String getPluginId()
    {
        return this.plugin;
    }
    
    void setPluginId( final String plugin )
    {
        this.plugin = plugin;
    }
    
    public String getLabel() 
    {
        return this.label;
    }
    
    void setLabel( final String label )
    {
        this.label = label;
    }

    public String getDescription() 
    {
        return this.description;
    }
    
    void setDescription( final String description )
    {
        this.description = description;
    }
    
    public Set<IProjectFacet> getProjectFacets()
    {
        return this.facetsReadOnly;
    }
    
    void addProjectFacet( final IProjectFacet f )
    {
        this.facets.add( f );
    }
    
    void removeProjectFacet( final IProjectFacet f )
    {
        this.facets.remove( f );
    }
    
    @SuppressWarnings( "rawtypes" )
    
    public Object getAdapter( final Class type )
    {
        return Platform.getAdapterManager().loadAdapter( this, type.getName() );
    }
    
    public String toString()
    {
        return this.label;
    }

}
