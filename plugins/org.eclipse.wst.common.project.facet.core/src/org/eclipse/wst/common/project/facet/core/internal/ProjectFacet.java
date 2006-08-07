/******************************************************************************
 * Copyright (c) 2005, 2006 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IActionDefinition;
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IDefaultVersionProvider;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.VersionFormatException;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

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
    private ICategory category;
    private final List actionDefinitions = new ArrayList();
    private final List eventHandlers = new ArrayList();
    private IProjectFacetVersion defaultVersion;
    private IDefaultVersionProvider defaultVersionProvider;
    
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
                = NLS.bind( Resources.versionNotFound, this.getId(), version );
            
            throw new IllegalArgumentException( msg );
        }
        
        return fv;
    }
    
    void addVersion( final IProjectFacetVersion version )
    {
        this.versions.add( version.getVersionString(), version );
    }

    public IProjectFacetVersion getLatestVersion()
    
        throws VersionFormatException, CoreException
        
    {
        if( this.versions.size() > 0 )
        {
            final Comparator comp = getVersionComparator( true, VERSION_ADAPTER );
            final Object max = Collections.max( this.versions, comp );
            
            return (IProjectFacetVersion) max;
        }
        else
        {
            return null;
        }
    }
    
    public IProjectFacetVersion getLatestSupportedVersion( final IRuntime r )
    
        throws CoreException
        
    {
        for( Iterator itr = getSortedVersions( false ).iterator(); 
             itr.hasNext(); )
        {
            final IProjectFacetVersion fv = (IProjectFacetVersion) itr.next();
            
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
                try
                {
                    defver = getLatestVersion();
                }
                catch( CoreException e )
                {
                    FacetCorePlugin.log( e );
                }
                catch( VersionFormatException e )
                {
                    FacetCorePlugin.log( e );
                }
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
    
    protected IVersionAdapter getVersionAdapter()
    {
        return VERSION_ADAPTER;
    }
    
    public Object getAdapter( final Class type )
    {
        return Platform.getAdapterManager().loadAdapter( this, type.getName() );
    }
    
    public String createVersionNotFoundErrMsg( final String verstr )
    {
        return NLS.bind( ProjectFacetsManagerImpl.Resources.facetVersionNotDefined,
                         this.id, verstr );
    }
    
    public String toString()
    {
        return this.label;
    }
    
    Set getActionDefinitions( final IProjectFacetVersion fv )
    {
        final Set result = new HashSet();
        
        for( Iterator itr = this.actionDefinitions.iterator(); itr.hasNext(); )
        {
            final IActionDefinition def = (IActionDefinition) itr.next();
            
            if( def.getVersionExpr().evaluate( fv.getVersionString() ) )
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
    
    List getEventHandlers( final IProjectFacetVersion fv,
                           final EventHandler.Type type )
    {
        final List res = new ArrayList();
        
        for( Iterator itr = this.eventHandlers.iterator(); itr.hasNext(); )
        {
            final EventHandler h = (EventHandler) itr.next();
            
            try
            {
                if( h.getType() == type &&
                    h.getVersionExpr().evaluate( (IVersion) fv ) )
                {
                    res.add( h );
                }
            }
            catch( CoreException e )
            {
                FacetCorePlugin.log( e.getStatus() );
            }
        }
        
        return res;
    }
    
    void addEventHandler( final EventHandler h )
    {
        this.eventHandlers.add( h );
    }
    
    public static final class Resources
    
        extends NLS
        
    {
        public static String versionNotFound;
        public static String versionProviderReturnedWrongVersion;
        
        static
        {
            initializeMessages( ProjectFacet.class.getName(), 
                                Resources.class );
        }
    }
    
}
