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

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IActionDefinition;
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IDefaultVersionProvider;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.internal.util.Versionable;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * The implementation of the <code>IProjectFacet</code> interface.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacet 

    extends Versionable<IProjectFacetVersion>
    implements IProjectFacet 
    
{
    private String id;
    private String plugin;
    private String label;
    private String description;
    private ICategory category;
    private final List<IActionDefinition> actionDefinitions;
    private IProjectFacetVersion defaultVersion;
    private IDefaultVersionProvider defaultVersionProvider;
    
    ProjectFacet() 
    {
        this.actionDefinitions = new ArrayList<IActionDefinition>();
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
    
    public ICategory getCategory()
    {
        return this.category;
    }
    
    void setCategory( final ICategory category )
    {
        this.category = category;
    }
    
    void addVersion( final IProjectFacetVersion ver )
    {
        this.versions.add( ver.getVersionString(), ver );
    }
    
    public IProjectFacetVersion getLatestSupportedVersion( final IRuntime r )
    {
        for( IProjectFacetVersion fv : getSortedVersions( false ) )
        {
            if( r.supports( fv ) )
            {
                return fv;
            }
        }
        
        return null;
    }
    
    public IProjectFacetVersion getDefaultVersion()
    {
        IProjectFacetVersion defver = null;
        
        if( this.defaultVersionProvider != null )
        {
            try
            {
                defver = this.defaultVersionProvider.getDefaultVersion();
            }
            catch( Exception e )
            {
                FacetCorePlugin.log( e );
            }
            
            if( defver != null )
            {
                if( defver.getProjectFacet() != this )
                {
                    final String msg
                        = NLS.bind( Resources.versionProviderReturnedWrongVersion,
                                    this.id );
                    
                    FacetCorePlugin.log( msg );

                    defver = null;
                }
            }
            
            if( defver == null )
            {
                defver = getLatestVersion();
            }
            
            return defver;
        }
        else
        {
            return this.defaultVersion;
        }
    }
    
    void setDefaultVersion( IProjectFacetVersion fv )
    {
        this.defaultVersion = fv;
        this.defaultVersionProvider = null;
    }
    
    void setDefaultVersionProvider( IDefaultVersionProvider provider )
    {
        this.defaultVersion = null;
        this.defaultVersionProvider = provider;
    }
    
    public Object getAdapter( final Class type )
    {
        return Platform.getAdapterManager().loadAdapter( this, type.getName() );
    }
    
    public String createVersionNotFoundErrMsg( final String verstr )
    {
        return NLS.bind( FacetedProjectFrameworkImpl.Resources.facetVersionNotDefined,
                         this.id, verstr );
    }
    
    public String toString()
    {
        return this.label;
    }
    
    Set<IActionDefinition> getActionDefinitions( final IProjectFacetVersion fv )
    {
        final Set<IActionDefinition> result = new HashSet<IActionDefinition>();
        
        for( IActionDefinition def : this.actionDefinitions )
        {
            if( def.getVersionExpr().check( fv ) )
            {
                result.add( def );
            }
        }
        
        return result;
    }
    
    void addActionDefinition( final ActionDefinition actionDefinition )
    {
        this.actionDefinitions.add( actionDefinition );
    }
    
    public static final class Resources
    
        extends NLS
        
    {
        public static String versionProviderReturnedWrongVersion;
        
        static
        {
            initializeMessages( ProjectFacet.class.getName(), 
                                Resources.class );
        }
    }
    
}
