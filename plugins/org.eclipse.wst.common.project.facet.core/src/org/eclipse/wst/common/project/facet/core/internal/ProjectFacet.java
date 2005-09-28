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
import java.util.Comparator;

import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.VersionFormatException;

/**
 * The implementation of the <code>IProjectFacet</code> interface.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacet 

    extends Versionable
    implements IProjectFacet 
    
{
    private static final IVersionAdapter VERSION_ADAPTER = new IVersionAdapter()
    {
        public String adapt( final Object obj )
        {
            return ( (IProjectFacetVersion) obj ).getVersionString();
        }
    };
    
    private String id;
    private String plugin;
    private String label;
    private String description;
    private String iconPath;
    private ICategory category;
    
    ProjectFacet() {}
    
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
    
    public String getIconPath()
    {
        return this.iconPath;
    }
    
    void setIconPath( final String iconPath )
    {
        this.iconPath = iconPath;
    }
    
    public ICategory getCategory()
    {
        return this.category;
    }
    
    void setCategory( final ICategory category )
    {
        this.category = category;
    }
    
    public IProjectFacetVersion getVersion( final String version )
    {
        final IProjectFacetVersion fv
            = (IProjectFacetVersion) this.versions.get( version );
        
        if( fv == null )
        {
            final String msg 
                = "Could not find version " + version + " of project facet " 
                  + this.id + ".";
            
            throw new IllegalArgumentException( msg );
        }
        
        return fv;
    }
    
    void addVersion( final IProjectFacetVersion version )
    {
        this.versions.add( version.getVersionString(), version );
    }

    public IProjectFacetVersion getLatestVersion()
    
        throws VersionFormatException
        
    {
        final Comparator comp = getVersionComparator( true, VERSION_ADAPTER );
        final Object max = Collections.max( this.versions, comp );
        
        return (IProjectFacetVersion) max;
    }
    
    protected IVersionAdapter getVersionAdapter()
    {
        return VERSION_ADAPTER;
    }
    
    public String toString()
    {
        return this.label;
    }

}
