/******************************************************************************
 * Copyright (c) 2008 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import static org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin.PLUGIN_ID;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findExtensions;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.getTopLevelElements;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.reportMissingAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectBase;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.InvalidExtensionException;

/**
 * Contains the logic for processing the <code>defaultFacets</code> extension point. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class DefaultFacetsExtensionPoint
{
    public static final String EXTENSION_POINT_ID = "defaultFacets"; //$NON-NLS-1$
    
    private static final String ATTR_ID = "id"; //$NON-NLS-1$
    private static final String ATTR_VERSION = "version"; //$NON-NLS-1$
    private static final String EL_CONTEXT = "context"; //$NON-NLS-1$
    private static final String EL_DEFAULT_FACETS = "default-facets"; //$NON-NLS-1$
    private static final String EL_FACET = "facet"; //$NON-NLS-1$
    private static final String EL_FIXED_FACET = "fixed-facet"; //$NON-NLS-1$
    private static final String EL_RUNTIME_COMPONENT = "runtime-component"; //$NON-NLS-1$

    private static List<DefaultFacetsExtension> extensions = null;
    
    public static Set<IProjectFacetVersion> getDefaultFacets( final IFacetedProjectBase fproj )
    
        throws CoreException
        
    {
        readExtensions();

        final Set<IProjectFacet> fixed = fproj.getFixedProjectFacets();
        final IRuntime runtime = fproj.getPrimaryRuntime();
        
        // 1. Get the complete list.
        
        final Map<IProjectFacet,IProjectFacetVersion> facets 
            = new HashMap<IProjectFacet,IProjectFacetVersion>();
        
        for( DefaultFacetsExtension extension : extensions )
        {
            if( extension.match( fproj ) )
            {
                for( IProjectFacetVersion fv : extension.getProjectFacets() )
                {
                    facets.put( fv.getProjectFacet(), fv );
                }
            }
        }
        
        // 2. Remove the facets that conflict with fixed facets.
        
        final Set<IProjectFacet> toRemove = new HashSet<IProjectFacet>();
        
        for( IProjectFacetVersion fv : facets.values() )
        {
            if( ! fv.isValidFor( fixed ) )
            {
                toRemove.add( fv.getProjectFacet() );
            }
        }
        
        for( IProjectFacet f : toRemove )
        {
            facets.remove( f );
        }
        
        // 3. Make sure that the result includes all of the fixed facets.
        
        Map<IProjectFacet,IProjectFacetVersion> toadd = null;
        
        for( IProjectFacet f : fixed )
        {
            if( ! facets.containsKey( f ) )
            {
                if( toadd == null )
                {
                    toadd = new HashMap<IProjectFacet,IProjectFacetVersion>();
                }
                
                final IProjectFacetVersion fv;
                
                if( runtime != null )
                {
                    fv = f.getLatestSupportedVersion( runtime );
                }
                else
                {
                    fv = f.getDefaultVersion();
                }
                
                toadd.put( f, fv );
            }
        }
        
        if( toadd != null )
        {
            facets.putAll( toadd );
        }
        
        // 4. Return the result.
        
        return Collections.unmodifiableSet( new HashSet<IProjectFacetVersion>( facets.values() ) );
    }
    
    private static synchronized void readExtensions()
    {
        if( extensions != null )
        {
            return;
        }
        
        extensions = new ArrayList<DefaultFacetsExtension>();        
        
        for( IConfigurationElement element 
             : getTopLevelElements( findExtensions( PLUGIN_ID, EXTENSION_POINT_ID ) ) )
        {
            if( element.getName().equals( EL_DEFAULT_FACETS ) )
            {
                try
                {
                    readExtension( element );
                }
                catch( InvalidExtensionException e )
                {
                    // Continue. The problem has been reported to the user via the log.
                }
            }
        }
    }

    private static void readExtension( final IConfigurationElement config )
    
        throws InvalidExtensionException
        
    {
        final String pluginId = config.getContributor().getName();
        final DefaultFacetsExtension extension = new DefaultFacetsExtension();
        
        for( IConfigurationElement child : config.getChildren() )
        {
            final String childName = child.getName();
            
            if( childName.equals( EL_RUNTIME_COMPONENT ) )
            {
                final RuntimeComponentTypeRef rctRef = RuntimeComponentTypeRef.read( child );
                
                if( rctRef == null )
                {
                    throw new InvalidExtensionException();
                }
                
                extension.addContext( rctRef );
            }
            else if( childName.equals( EL_CONTEXT ) )
            {
                for( IConfigurationElement contextChild : child.getChildren() )
                {
                    final String contextChildName = contextChild.getName();
                    
                    if( contextChildName.equals( EL_RUNTIME_COMPONENT ) )
                    {
                        final RuntimeComponentTypeRef rctRef = RuntimeComponentTypeRef.read( contextChild );
                        
                        if( rctRef == null )
                        {
                            throw new InvalidExtensionException();
                        }
                        
                        extension.addContext( rctRef );
                    }
                    else if( contextChildName.equals( EL_FIXED_FACET ) )
                    {
                        final String fid = contextChild.getAttribute( ATTR_ID );
                        
                        if( ! ProjectFacetsManager.isProjectFacetDefined( fid ) )
                        {
                            FacetedProjectFrameworkImpl.reportMissingFacet( fid, pluginId );
                            throw new InvalidExtensionException();
                        }
                        
                        final IProjectFacet f = ProjectFacetsManager.getProjectFacet( fid );
                        
                        extension.addContext( f );
                    }
                }
            }
            else if( childName.equals( EL_FACET ) )
            {
                final String fid = child.getAttribute( ATTR_ID );
                
                if( ! ProjectFacetsManager.isProjectFacetDefined( fid ) )
                {
                    reportMissingAttribute( child, ATTR_ID );
                    throw new InvalidExtensionException();
                }

                final IProjectFacet f = ProjectFacetsManager.getProjectFacet( fid );
                
                final String ver = child.getAttribute( ATTR_VERSION );
                
                if( ver == null )
                {
                    reportMissingAttribute( child, ATTR_VERSION );
                    throw new InvalidExtensionException();
                }
                
                if( ! f.hasVersion( ver ) )
                {
                    String msg
                        = NLS.bind( FacetedProjectFrameworkImpl.Resources.facetVersionNotDefined,
                                    f.getId(), ver );
                    
                    msg += NLS.bind( FacetedProjectFrameworkImpl.Resources.usedInPlugin, pluginId ); 
                    
                    FacetCorePlugin.log( msg );
                    
                    throw new InvalidExtensionException();
                }
                
                extension.addProjectFacet( f.getVersion( ver ) );
            }
        }
        
        extensions.add( extension );
    }

    private static final class DefaultFacetsExtension
    {
        private Set<IProjectFacet> contextFixedFacets = new HashSet<IProjectFacet>();
        
        private Set<RuntimeComponentTypeRef> contextRuntimeComponentTypes 
            = new HashSet<RuntimeComponentTypeRef>();
        
        private final Set<IProjectFacetVersion> facets = new HashSet<IProjectFacetVersion>();
        
        public void addContext( final IProjectFacet facet )
        {
            this.contextFixedFacets.add( facet );
        }
        
        public void addContext( final RuntimeComponentTypeRef runtimeComponentTypeRef )
        {
            this.contextRuntimeComponentTypes.add( runtimeComponentTypeRef );
        }
        
        public Set<IProjectFacetVersion> getProjectFacets()
        {
            return this.facets;
        }
        
        public void addProjectFacet( final IProjectFacetVersion fv )
        {
            this.facets.add( fv );
        }
        
        public boolean match( final IFacetedProjectBase fproj )
        {
            if( ! this.contextFixedFacets.isEmpty() )
            {
                final Set<IProjectFacet> fixedFacets = fproj.getFixedProjectFacets();
                
                if( ! fixedFacets.containsAll( this.contextFixedFacets ) )
                {
                    return false;
                }
            }
            
            if( ! this.contextRuntimeComponentTypes.isEmpty() )
            {
                final IRuntime runtime = fproj.getPrimaryRuntime();
                
                if( runtime == null )
                {
                    return false;
                }
                
                for( RuntimeComponentTypeRef rctRef : this.contextRuntimeComponentTypes )
                {
                    boolean found = false;
                    
                    for( IRuntimeComponent rc : runtime.getRuntimeComponents() )
                    {
                        if( rctRef.check( Collections.singleton( rc.getRuntimeComponentVersion() ) ) )
                        {
                            found = true;
                            break;
                        }
                    }
                    
                    if( ! found )
                    {
                        return false;
                    }
                }
            }
            
            return true;
        }
    }
    
}
