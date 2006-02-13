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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IDelegate;
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
    
        throws VersionFormatException, CoreException
        
    {
        final Comparator comp = getVersionComparator( true, VERSION_ADAPTER );
        final Object max = Collections.max( this.versions, comp );
        
        return (IProjectFacetVersion) max;
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
    
    protected IVersionAdapter getVersionAdapter()
    {
        return VERSION_ADAPTER;
    }
    
    ActionDefinition getActionDefinition( final IProjectFacetVersion fv,
                                          final IDelegate.Type type )
    
        throws CoreException
        
    {
        ActionDefinition result = null;
        
        for( Iterator itr = this.actionDefinitions.iterator(); itr.hasNext(); )
        {
            final ActionDefinition def = (ActionDefinition) itr.next();
            
            if( def.type == type && 
                def.versionMatchExpr.evaluate( (IVersion) fv ) )
            {
                if( result == null )
                {
                    result = def;
                }
                else
                {
                    final String msg
                        = Resources.bind( Resources.multipleActionDefinitions,
                                          fv.getProjectFacet().getId(),
                                          fv.getVersionString(),
                                          type.toString() );
                    
                    FacetCorePlugin.log( msg );

                    break;
                }
            }
        }
        
        return result;
    }
    
    void addActionDefinition( final ActionDefinition actionDefinition )
    {
        this.actionDefinitions.add( actionDefinition );
    }
    
    public Object getAdapter( final Class type )
    {
        return Platform.getAdapterManager().loadAdapter( this, type.getName() );
    }
    
    public String createVersionNotFoundErrMsg( final String verstr )
    {
        return NLS.bind( ProjectFacetsManagerImpl.Resources.facetVersionNotDefinedNoPlugin,
                         this.id, verstr );
    }
    
    public String toString()
    {
        return this.label;
    }
    
    static final class ActionDefinition
    {
        public IDelegate.Type type;
        public VersionMatchExpr versionMatchExpr;
        public String delegateClassName;
        public String configFactoryClassName;
    }

    public static final class Resources
    
        extends NLS
        
    {
        public static String multipleActionDefinitions;
        
        static
        {
            initializeMessages( ProjectFacet.class.getName(), 
                                Resources.class );
        }
        
        public static String bind( final String template,
                                   final Object arg1,
                                   final Object arg2,
                                   final Object arg3 )
        {
            return NLS.bind( template, new Object[] { arg1, arg2, arg3 } );
        }
    }
    
}
