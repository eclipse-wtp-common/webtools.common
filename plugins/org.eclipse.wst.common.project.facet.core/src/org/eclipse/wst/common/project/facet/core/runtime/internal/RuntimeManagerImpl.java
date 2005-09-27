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

package org.eclipse.wst.common.project.facet.core.runtime.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;
import org.eclipse.wst.common.project.facet.core.internal.IndexedSet;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;

/**
 * The implementation of the {@see RuntimeManager} abstract class.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class RuntimeManagerImpl

    extends RuntimeManager
    
{
    private static final String EXTENSION_ID = "runtimes";
    
    private final IndexedSet runtimeComponentTypes;
    private final IndexedSet runtimes;
    private final List mappings;
    
    public RuntimeManagerImpl()
    {
        this.runtimeComponentTypes = new IndexedSet();
        this.runtimes = new IndexedSet();
        this.mappings = new ArrayList();
        
        readMetadata();
    }
    
    public Set getRuntimeComponentTypes()
    {
        return this.runtimeComponentTypes.getUnmodifiable();
    }
    
    public boolean isRuntimeComponentTypeDefined( final String id )
    {
        return this.runtimeComponentTypes.containsKey( id );
    }
    
    public IRuntimeComponentType getRuntimeComponentType( final String id )
    {
        final IRuntimeComponentType rc 
            = (IRuntimeComponentType) this.runtimeComponentTypes.get( id );
        
        if( rc == null )
        {
            final String msg = "Could not find runtime component type " + id + ".";
            throw new IllegalArgumentException( msg );
        }
        
        return rc;
    }
    
    public IRuntimeComponent createRuntimeComponent( final IRuntimeComponentVersion rcv,
                                                     final Map properties )
    {
        final RuntimeComponent rc = new RuntimeComponent();
        
        rc.setRuntimeComponentVersion( rcv );
        
        if( properties != null )
        {
            for( Iterator itr = properties.entrySet().iterator(); 
                 itr.hasNext(); )
            {
                final Map.Entry entry = (Map.Entry) itr.next();
                
                rc.setProperty( (String) entry.getKey(), 
                                (String) entry.getValue() );
            }
        }
        
        return rc;
    }
    
    public Set getRuntimes()
    {
        return this.runtimes.getUnmodifiable();
    }
    
    public boolean isRuntimeDefined( final String name )
    {
        return this.runtimes.containsKey( name );
    }
    
    public IRuntime getRuntime( final String name )
    {
        final IRuntime runtime = (IRuntime) this.runtimes.get( name );
        
        if( runtime == null )
        {
            final String msg = "Could not find runtime " + name + ".";
            throw new IllegalArgumentException( msg );
        }
        
        return runtime;
    }
    
    public IRuntime defineRuntime( final String name,
                                   final List components,
                                   final Map properties )
    {
        synchronized( this.runtimes )
        {
            final Runtime r = new Runtime();
            
            r.setName( name );
            
            for( Iterator itr = components.iterator(); itr.hasNext(); )
            {
                r.addRuntimeComponent( (IRuntimeComponent) itr.next() );
            }
            
            if( properties != null )
            {
                for( Iterator itr = properties.entrySet().iterator(); 
                     itr.hasNext(); )
                {
                    final Map.Entry entry = (Map.Entry) itr.next();
                    
                    r.setProperty( (String) entry.getKey(), 
                                   (String) entry.getValue() );
                }
            }
            
            this.runtimes.add( r.getName(), r );
            
            return r;
        }
    }
    
    public void deleteRuntime( final IRuntime runtime )
    {
        synchronized( this.runtimes )
        {
            this.runtimes.delete( runtime.getName() );
        }
    }
    
    Set getSupportedFacets( final IRuntime runtime )
    {
        final HashSet result = new HashSet();
        
        for( Iterator itr1 = runtime.getRuntimeComponents().iterator(); 
             itr1.hasNext(); )
        {
            final IRuntimeComponent comp = (IRuntimeComponent) itr1.next();
            
            for( Iterator itr2 = this.mappings.iterator(); itr2.hasNext(); )
            {
                final Mapping m = (Mapping) itr2.next();
                
                if( m.match( comp.getRuntimeComponentVersion() ) )
                {
                    if( m.facetVersion == null )
                    {
                        result.addAll( m.facet.getVersions() );
                    }
                    else
                    {
                        result.add( m.facetVersion );
                        
                        if( m.facetAllowNewer )
                        {
                            final List sorted = m.facet.getSortedVersions( true );
                            boolean found = false;
                            
                            for( Iterator itr3 = sorted.iterator(); itr3.hasNext(); )
                            {
                                if( found )
                                {
                                    result.add( itr3.next() );
                                }
                                else
                                {
                                    if( itr3.next() == m.facetVersion )
                                    {
                                        found = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
    private void readMetadata()
    {
        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        
        final IExtensionPoint point 
            = registry.getExtensionPoint( FacetCorePlugin.PLUGIN_ID, 
                                          EXTENSION_ID );
        
        if( point == null )
        {
            throw new RuntimeException( "Extension point not found!" );
        }
        
        final ArrayList cfgels = new ArrayList();
        final IExtension[] extensions = point.getExtensions();
        
        for( int i = 0; i < extensions.length; i++ )
        {
            final IConfigurationElement[] elements 
                = extensions[ i ].getConfigurationElements();
            
            for( int j = 0; j < elements.length; j++ )
            {
                cfgels.add( elements[ j ] );
            }
        }
        
        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( "runtime-component-type" ) )
            {
                readRuntimeComponentType( config );
            }
        }

        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( "runtime-component-version" ) )
            {
                readRuntimeComponentVersion( config );
            }
        }

        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( "adapter" ) )
            {
                readAdapter( config );
            }
        }

        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( "supported" ) )
            {
                readMapping( config );
            }
        }
    }
    
    private void readRuntimeComponentType( final IConfigurationElement config )
    {
        final String id = config.getAttribute( "id" );

        if( id == null )
        {
            // TODO: error
        }
        
        final RuntimeComponentType rct = new RuntimeComponentType();
        rct.setId( id );
        rct.setPluginId( config.getDeclaringExtension().getNamespace() );

        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( "version-comparator" ) )
            {
                final String clname = child.getAttribute( "class" );
                
                if( clname == null )
                {
                    // TODO: error
                }
                
                rct.setVersionComparator( clname );
            }
            else if( childName.equals( "icon" ) )
            {
                rct.setIconPath( child.getValue().trim() );
            }
        }
        
        this.runtimeComponentTypes.add( id, rct );
    }
    
    private void readRuntimeComponentVersion( final IConfigurationElement config )
    {
        final String type = config.getAttribute( "type" );

        if( type == null )
        {
            // TODO: error
        }
        
        final String ver = config.getAttribute( "version" );

        if( ver == null )
        {
            // TODO: error
        }
        
        final RuntimeComponentType rct 
            = (RuntimeComponentType) this.runtimeComponentTypes.get( type );
        
        if( rct == null )
        {
            // TODO: error
        }
        
        final RuntimeComponentVersion rcv = new RuntimeComponentVersion();
        
        rcv.setRuntimeComponentType( rct );
        rcv.setVersionString( ver );
        rcv.setPluginId( config.getDeclaringExtension().getNamespace() );
        
        rct.addVersion( rcv );
    }
    
    private void readAdapter( final IConfigurationElement config )
    {
        IRuntimeComponentType rctype = null;
        IRuntimeComponentVersion rcversion = null;
        String factory = null;
        final List types = new ArrayList();
        
        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( "runtime-component" ) )
            {
                final String id = child.getAttribute( "id" );

                if( id == null )
                {
                    // TODO: error
                }
                
                if( ! isRuntimeComponentTypeDefined( id ) )
                {
                    // TODO: error
                }
                
                rctype = getRuntimeComponentType( id );
                
                final String version = child.getAttribute( "version" );
                
                if( version != null )
                {
                    if( ! rctype.hasVersion( version ) )
                    {
                        // TODO: error
                    }
                    
                    rcversion = rctype.getVersion( version );
                }
            }
            else if( childName.equals( "factory" ) )
            {
                factory = child.getAttribute( "class" );

                if( factory == null )
                {
                    // TODO: error
                }
            }
            else if( childName.equals( "type" ) )
            {
                final String type = child.getAttribute( "class" );

                if( type == null )
                {
                    // TODO: error
                }
                else
                {
                    types.add( type );
                }
            }
        }
        
        final Set versions;
        
        if( rcversion == null )
        {
            versions = rctype.getVersions();
        }
        else
        {
            versions = Collections.singleton( rcversion );
        }
        
        for( Iterator itr1 = versions.iterator(); itr1.hasNext(); )
        {
            final RuntimeComponentVersion rcv
                = (RuntimeComponentVersion) itr1.next();
            
            for( Iterator itr2 = types.iterator(); itr2.hasNext(); )
            {
                rcv.addAdapterFactory( (String) itr2.next(), factory );
            }
        }
    }
    
    private void readMapping( final IConfigurationElement config )
    {
        final Mapping m = new Mapping();
        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( "facet" ) )
            {
                final String id = child.getAttribute( "id" );

                if( id == null )
                {
                    // TODO: error
                }
                
                if( ! ProjectFacetsManager.get().isProjectFacetDefined( id ) )
                {
                    // TODO: error
                }
                
                m.facet = ProjectFacetsManager.get().getProjectFacet( id );
                
                final String version = child.getAttribute( "version" );
                
                if( version != null )
                {
                    if( ! m.facet.hasVersion( version ) )
                    {
                        // TODO: error
                    }
                    
                    m.facetVersion = m.facet.getVersion( version );
                    
                    final String newer = child.getAttribute( "allow-newer" );
                    
                    if( newer != null && newer.equalsIgnoreCase( "true" ) )
                    {
                        m.facetAllowNewer = true;
                    }
                }
            }
            else if( childName.equals( "runtime-component" ) )
            {
                if( child.getAttribute( "any" ) == null )
                {
                    final String id = child.getAttribute( "id" );
    
                    if( id == null )
                    {
                        // TODO: error
                    }
                    
                    if( ! isRuntimeComponentTypeDefined( id ) )
                    {
                        // TODO: error
                    }
                    
                    m.runtimeCompType = getRuntimeComponentType( id );
                    
                    final String version = child.getAttribute( "version" );
                    
                    if( version != null )
                    {
                        if( ! m.runtimeCompType.hasVersion( version ) )
                        {
                            // TODO: error
                        }
                        
                        m.runtimeCompVersion 
                            = m.runtimeCompType.getVersion( version );
                        
                        final String newer = child.getAttribute( "allow-newer" );
                        
                        if( newer != null && newer.equalsIgnoreCase( "true" ) )
                        {
                            m.runtimeCompAllowNewer = true;
                        }
                    }
                }
            }
        }
        
        this.mappings.add( m );
    }
    
    private static final class Mapping
    {
        public IProjectFacet facet;
        public IProjectFacetVersion facetVersion;
        public boolean facetAllowNewer;
        public IRuntimeComponentType runtimeCompType;
        public IRuntimeComponentVersion runtimeCompVersion;
        public boolean runtimeCompAllowNewer;
        
        public boolean match( final IRuntimeComponentVersion version )
        {
            if( this.runtimeCompType == null )
            {
                return true;
            }
            else if( this.runtimeCompType == version.getRuntimeComponentType() )
            {
                if( this.runtimeCompVersion == null )
                {
                    return true;
                }
                else if( this.runtimeCompVersion == version )
                {
                    return true;
                }
                else if( this.runtimeCompAllowNewer )
                {
                    final Comparator comparator 
                        = this.runtimeCompType.getVersionComparator();
                    
                    final String v1 = version.getVersionString();
                    final String v2 = this.runtimeCompVersion.getVersionString();
                    
                    if( comparator.compare( v1, v2 ) > 0 )
                    {
                        return true;
                    }
                }
            }
            
            return false;
        }
    }

}