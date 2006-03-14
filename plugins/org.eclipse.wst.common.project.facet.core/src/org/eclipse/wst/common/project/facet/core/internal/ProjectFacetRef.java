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

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetRef
{
    private static final String ATTR_ID = "id"; //$NON-NLS-1$
    private static final String ATTR_VERSION = "version"; //$NON-NLS-1$
    
    private final IProjectFacet f;
    private final VersionExpr vexpr;
    
    public ProjectFacetRef( final IProjectFacet f,
                            final VersionExpr vexpr )
    {
        this.f = f;
        this.vexpr = vexpr;
    }
    
    public boolean check( final Set facets )
    
        throws CoreException
        
    {
        for( Iterator itr = facets.iterator(); itr.hasNext(); )
        {
            final IProjectFacetVersion fv = (IProjectFacetVersion) itr.next();
            
            if( this.f == fv.getProjectFacet() )
            {
                if( this.vexpr != null )
                {
                    return this.vexpr.evaluate( (IVersion) fv );
                }
                else
                {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static ProjectFacetRef read( final IConfigurationElement config )
    {
        final String id = config.getAttribute( ATTR_ID );

        if( id == null )
        {
            ProjectFacetsManagerImpl.reportMissingAttribute( config, ATTR_ID );
            return null;
        }
        
        if( ! ProjectFacetsManager.isProjectFacetDefined( id ) )
        {
            ProjectFacetsManagerImpl.reportMissingFacet( id, config.getContributor().getName() );
            return null;
        }
        
        final IProjectFacet f = ProjectFacetsManager.getProjectFacet( id );
        
        final String v = config.getAttribute( ATTR_VERSION );
        VersionExpr vexpr = null;
        
        if( v != null )
        {
            try
            {
                vexpr = new VersionExpr( f, v, config.getContributor().getName() );
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
        if( this.vexpr == null )
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
        public static String versionExpr;
        
        static
        {
            initializeMessages( ProjectFacetRef.class.getName(), 
                                Resources.class );
        }
    }
    
}
