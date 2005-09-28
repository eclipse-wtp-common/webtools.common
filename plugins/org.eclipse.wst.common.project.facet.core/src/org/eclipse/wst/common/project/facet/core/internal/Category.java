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

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class Category 

    implements ICategory 
    
{
    private String id = null;
    private String label = null;
    private String description = null;
    private String iconPath = null;
    private String plugin = null;
    private HashSet facets = new HashSet();
    private Set facetsReadOnly = Collections.unmodifiableSet( this.facets );
    
    Category() {}
    
    public String getId() 
    {
        return this.id;
    }
    
    void setId( final String id )
    {
        this.id = id;
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
    
    public String getIconPath()
    {
        return this.iconPath;
    }
    
    void setIconPath( final String iconPath )
    {
        this.iconPath = iconPath;
    }
    
    public String getPlugin()
    {
        return this.plugin;
    }
    
    void setPlugin( final String plugin )
    {
        this.plugin = plugin;
    }
    
    public Set getProjectFacets()
    {
        return this.facetsReadOnly;
    }
    
    void addProjectFacet( final IProjectFacet f )
    {
        this.facets.add( f );
    }
    
    public String toString()
    {
        return this.label;
    }

}
