/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import static org.eclipse.wst.common.project.facet.core.util.internal.MiscUtil.equal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IActionDefinition;
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IDefaultVersionProvider;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.util.internal.Versionable;

/**
 * The implementation of the <code>IProjectFacet</code> interface.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacet 

    extends Versionable<IProjectFacetVersion>
    implements IProjectFacet 
    
{
    private String id;
    private final Set<String> aliases;
    private final Set<String> aliasesReadOnly;
    private String plugin;
    private String label;
    private String description;
    private ICategory category;
    private final List<IActionDefinition> actionDefinitions;
    private IProjectFacetVersion defaultVersion;
    private IDefaultVersionProvider defaultVersionProvider;
    private final Map<String,Object> properties;
    private final Map<String,Object> propertiesReadOnly;
    
    ProjectFacet() 
    {
        this.aliases = new HashSet<String>();
        this.aliasesReadOnly = Collections.unmodifiableSet( this.aliases );
        this.actionDefinitions = new ArrayList<IActionDefinition>();
        this.properties = new HashMap<String,Object>();
        this.propertiesReadOnly = Collections.unmodifiableMap( this.properties );
    }
    
    public String getId() 
    {
        return this.id;
    }
    
    void setId( final String id )
    {
        this.id = id;
    }
    
    public Set<String> getAliases()
    {
        return this.aliasesReadOnly;
    }
    
    void addAlias( final String alias )
    {
        this.aliases.add( alias );
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
    
    void addVersion( final IProjectFacetVersion fv )
    {
        this.versions.addItem( fv );
        this.versions.addKey( fv.getVersionString(), fv );
        
        for( String alias : ProjectFacetAliasesExtensionPoint.getAliases( fv ) )
        {
            this.versions.addKey( alias, fv );
            ( (ProjectFacetVersion) fv ).addAlias( alias );
        }
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
    
    @SuppressWarnings( "rawtypes" )
    
    public Object getAdapter( final Class type )
    {
        return Platform.getAdapterManager().loadAdapter( this, type.getName() );
    }
    
    public String createVersionNotFoundErrMsg( final String verstr )
    {
        return NLS.bind( FacetedProjectFrameworkImpl.Resources.facetVersionNotDefined,
                         this.id, verstr );
    }
    
    public Map<String,Object> getProperties()
    {
        return this.propertiesReadOnly;
    }
    
    public Object getProperty( final String name )
    {
        return this.properties.get( name );
    }

    void setProperty( final String name,
                      final Object value )
    {
        this.properties.put( name, value );
    }
    
    public boolean isVersionHidden()
    {
        return ( this.versions.getItemSet().size() == 1 &&
                 equal( getProperty( PROP_HIDE_VERSION ), true ) );
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
