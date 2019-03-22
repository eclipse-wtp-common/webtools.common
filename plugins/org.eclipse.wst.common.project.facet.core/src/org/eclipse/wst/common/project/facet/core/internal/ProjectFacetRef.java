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

import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.reportMissingAttribute;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IVersionExpr;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.util.internal.VersionExpr;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetRef
{
    private static final String ATTR_ID = "id"; //$NON-NLS-1$
    private static final String ATTR_VERSION = "version"; //$NON-NLS-1$
    
    private final IProjectFacet f;
    private final VersionExpr<IProjectFacetVersion> vexpr;
    
    @SuppressWarnings( "unchecked" )
    public ProjectFacetRef( final IProjectFacet f,
                            final IVersionExpr vexpr )
    {
        this( f, (VersionExpr<IProjectFacetVersion>) vexpr );
    }

    public ProjectFacetRef( final IProjectFacet f,
                            final VersionExpr<IProjectFacetVersion> vexpr )
    {
        this.f = f;
        this.vexpr = vexpr;
    }
    
    public IProjectFacet getProjectFacet()
    {
        return this.f;
    }
    
    public IVersionExpr getVersionExpr()
    {
        return this.vexpr;
    }
    
    public boolean hasVersionExpr()
    {
        return ( this.vexpr != null );
    }
    
    public boolean check( final IProjectFacetVersion fv )
    {
        if( this.f == fv.getProjectFacet() )
        {
            if( this.vexpr != null )
            {
                return this.vexpr.check( fv );
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    public boolean check( final Collection<IProjectFacetVersion> facets )
    {
        for( IProjectFacetVersion fv : facets )
        {
            if( check( fv ) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    public static ProjectFacetRef read( final IConfigurationElement config )
    {
        final String id = config.getAttribute( ATTR_ID );

        if( id == null )
        {
            reportMissingAttribute( config, ATTR_ID );
            return null;
        }
        
        if( ! ProjectFacetsManager.isProjectFacetDefined( id ) )
        {
            ProblemLog.reportMissingFacet( id, config.getContributor().getName() );
            return null;
        }
        
        final IProjectFacet f = ProjectFacetsManager.getProjectFacet( id );
        
        final String v = config.getAttribute( ATTR_VERSION );
        VersionExpr<ProjectFacetVersion> vexpr = null;
        
        if( v != null )
        {
            try
            {
                final String pluginId = config.getContributor().getName();
                vexpr = new VersionExpr<ProjectFacetVersion>( f, v, pluginId );
            }
            catch( CoreException e )
            {
                FacetCorePlugin.log( e.getStatus() );
                return null;
            }
        }
        
        return new ProjectFacetRef( f, vexpr );
    }
    
    public String toString()
    {
        if( this.vexpr == null || ( (ProjectFacet) this.f ).isVersionHidden() )
        {
            return this.f.getLabel();
        }
        else if( this.vexpr.isSingleVersionMatch() )
        {
            return NLS.bind( Resources.exactVersion, this.f.getLabel(),
                             this.vexpr.toString() );
        }
        else if( this.vexpr.isSimpleAllowNewer() )
        {
            return NLS.bind( Resources.allowNewer, this.f.getLabel(),
                             this.vexpr.getFirstVersion() );
        }
        else if( this.vexpr.isWildcard() )
        {
            return NLS.bind( Resources.wildcard, this.f.getLabel() );
        }
        else
        {
            return NLS.bind( Resources.versionExpr, this.f.getLabel(),
                             this.vexpr.toString() );
        }
    }

    private static final class Resources
    
        extends NLS
        
    {
        public static String exactVersion;
        public static String allowNewer;
        public static String wildcard;
        public static String versionExpr;
        
        static
        {
            initializeMessages( ProjectFacetRef.class.getName(), 
                                Resources.class );
        }
    }
    
}
