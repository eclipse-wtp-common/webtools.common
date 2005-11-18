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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IListener;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.VersionFormatException;
import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;
import org.eclipse.wst.common.project.facet.core.internal.IVersion;
import org.eclipse.wst.common.project.facet.core.internal.IndexedSet;
import org.eclipse.wst.common.project.facet.core.internal.ProjectFacetsManagerImpl;
import org.eclipse.wst.common.project.facet.core.internal.VersionMatchExpr;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeBridge;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class RuntimeManagerImpl
{
    private static final String EXTENSION_ID = "runtimes";
    private static final String BRIDGES_EXTENSION_ID = "runtimeBridges";
    
    private static final IndexedSet runtimeComponentTypes;
    private static final IndexedSet runtimes;
    private static final List mappings;
    private static final Map bridges;
    private static final Set listeners;
    
    static
    {
        runtimeComponentTypes = new IndexedSet();
        runtimes = new IndexedSet();
        mappings = new ArrayList();
        bridges = new HashMap();
        listeners = new HashSet();
        
        readMetadata();
        readBridgesExtensions();
    }
    
    private RuntimeManagerImpl() {}
    
    public static Set getRuntimeComponentTypes()
    {
        return runtimeComponentTypes.getUnmodifiable();
    }
    
    public static boolean isRuntimeComponentTypeDefined( final String id )
    {
        return runtimeComponentTypes.containsKey( id );
    }
    
    public static IRuntimeComponentType getRuntimeComponentType( final String id )
    {
        final IRuntimeComponentType rc 
            = (IRuntimeComponentType) runtimeComponentTypes.get( id );
        
        if( rc == null )
        {
            final String msg = "Could not find runtime component type " + id + ".";
            throw new IllegalArgumentException( msg );
        }
        
        return rc;
    }
    
    public static IRuntimeComponent createRuntimeComponent( final IRuntimeComponentVersion rcv,
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
    
    public static Set getRuntimes()
    {
        synchronized( runtimes )
        {
            bridge();
            return (Set) runtimes.clone();
        }
    }
    
    public static Set getRuntimes( final Set facets )
    {
        synchronized( runtimes )
        {
            bridge();
            
            final HashSet result = new HashSet();
            
            for( Iterator itr1 = runtimes.iterator(); itr1.hasNext(); )
            {
                final IRuntime r = (IRuntime) itr1.next();
                boolean supports = true;
                
                for( Iterator itr2 = facets.iterator(); itr2.hasNext(); )
                {
                    if( ! r.supports( (IProjectFacetVersion) itr2.next() ) )
                    {
                        supports = false;
                        break;
                    }
                }
                
                if( supports )
                {
                    result.add( r );
                }
            }
            
            return result;
        }
    }
    
    public static boolean isRuntimeDefined( final String name )
    {
        synchronized( runtimes )
        {
            bridge();
            return runtimes.containsKey( name );
        }
    }
    
    public static IRuntime getRuntime( final String name )
    {
        synchronized( runtimes )
        {
            bridge();
            
            final IRuntime runtime = (IRuntime) runtimes.get( name );
            
            if( runtime == null )
            {
                final String msg = "Could not find runtime " + name + ".";
                throw new IllegalArgumentException( msg );
            }
            
            return runtime;
        }
    }
    
    public static IRuntime defineRuntime( final String name,
                                          final List components,
                                          final Map properties )
    {
        synchronized( runtimes )
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
            
            runtimes.add( r.getName(), r );
            
            notifyRuntimeListeners();
            
            return r;
        }
    }
    
    public static void deleteRuntime( final IRuntime runtime )
    {
        synchronized( runtimes )
        {
            if( runtimes.delete( runtime.getName() ) )
            {
                notifyRuntimeListeners();
            }
        }
    }
    
    public static void addRuntimeListener( final IListener listener )
    {
        synchronized( listeners )
        {
            listeners.add( listener );
        }
    }
    
    public static void removeRuntimeListener( final IListener listener )
    {
        synchronized( listeners )
        {
            listeners.remove( listener );
        }
    }
    
    private static void notifyRuntimeListeners()
    {
        for( Iterator itr = listeners.iterator(); itr.hasNext(); )
        {
            try
            {
                ( (IListener) itr.next() ).handle();
            }
            catch( Exception e )
            {
                FacetCorePlugin.log( e );
            }
        }
    }
    
    static Set getSupportedFacets( final IRuntime runtime )
    {
        final Set result = new HashSet();
        
        for( Iterator itr1 = runtime.getRuntimeComponents().iterator(); 
             itr1.hasNext(); )
        {
            final IRuntimeComponent rc = (IRuntimeComponent) itr1.next();
            
            for( Iterator itr2 = mappings.iterator(); itr2.hasNext(); )
            {
                final Mapping m = (Mapping) itr2.next();
                
                try
                {
                    result.addAll( m.getSupportedFacets( rc ) );                    
                }
                catch( CoreException e )
                {
                    FacetCorePlugin.log( e.getStatus() );
                }
                catch( VersionFormatException e )
                {
                    FacetCorePlugin.log( e );
                }
            }
        }
        
        return result;
    }
    
    private static void bridge()
    {
        boolean modified = false;
        
        for( Iterator itr1 = bridges.entrySet().iterator(); itr1.hasNext(); )
        {
            final Map.Entry entry = (Map.Entry) itr1.next();
            final String brid = (String) entry.getKey();
            final IRuntimeBridge br = (IRuntimeBridge) entry.getValue();
            
            // Find the runtimes belonging to this bridge that are currently
            // in the system.
            
            final HashMap existing = new HashMap();
            
            for( Iterator itr2 = runtimes.iterator(); itr2.hasNext(); )
            {
                final Object obj = itr2.next();
                
                if( obj instanceof BridgedRuntime )
                {
                    final BridgedRuntime bridged = (BridgedRuntime) obj;
                    
                    if( bridged.getBridgeId().equals( brid ) )
                    {
                        existing.put( bridged.getNativeRuntimeId(), bridged );
                    }
                }
            }
            
            // Get the new set of exported runtimes.
            
            final Set exported;
            
            try
            {
                exported = br.getExportedRuntimeNames();
            }
            catch( CoreException e )
            {
                FacetCorePlugin.log( e );
                
                for( Iterator itr2 = existing.values().iterator(); 
                     itr2.hasNext(); )
                {
                    runtimes.remove( ( (IRuntime) itr2 ).getName() );
                    modified = true;
                }
                
                continue;
            }
            
            // Remove the absolete entries.
            
            for( Iterator itr2 = existing.values().iterator(); itr2.hasNext(); )
            {
                final BridgedRuntime r = (BridgedRuntime) itr2.next();
                
                if( ! exported.contains( r.getNativeRuntimeId() ) )
                {
                    runtimes.delete( r.getName() );
                    modified = true;
                }
            }
            
            // Create the new entries.
            
            for( Iterator itr2 = exported.iterator(); itr2.hasNext(); )
            {
                final String id = (String) itr2.next();
                
                if( ! existing.containsKey( id ) )
                {
                    try
                    {
                        final IRuntimeBridge.IStub stub = br.bridge( id );
                        
                        final BridgedRuntime r 
                            = new BridgedRuntime( brid, id, stub );
                        
                        r.setName( createUniqueRuntimeName( id ) );
                        
                        runtimes.add( r.getName(), r );
                        modified = true;
                    }
                    catch( CoreException e )
                    {
                        FacetCorePlugin.log( e );
                    }
                }
            }
            
            if( modified )
            {
                notifyRuntimeListeners();
            }
        }
    }
    
    private static String createUniqueRuntimeName( final String suggestion )
    {
        String name = suggestion;
        
        for( int i = 1; runtimes.contains( name ); i++ )
        {
            name = suggestion + " (" + i + ")";
        }
        
        return name;
    }
    
    private static void readMetadata()
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
    
    private static void readRuntimeComponentType( final IConfigurationElement config )
    {
        final String id = config.getAttribute( "id" );

        if( id == null )
        {
            reportMissingAttribute( config, "id" );
            return;
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
                    reportMissingAttribute( child, "class" );
                    return;
                }
                
                rct.setVersionComparator( clname );
            }
        }
        
        runtimeComponentTypes.add( id, rct );
    }
    
    private static void readRuntimeComponentVersion( final IConfigurationElement config )
    {
        final String type = config.getAttribute( "type" );

        if( type == null )
        {
            reportMissingAttribute( config, "type" );
            return;
        }
        
        final String ver = config.getAttribute( "version" );

        if( ver == null )
        {
            reportMissingAttribute( config, "version" );
            return;
        }
        
        final RuntimeComponentType rct 
            = (RuntimeComponentType) runtimeComponentTypes.get( type );
        
        if( rct == null )
        {
            final String msg
                = NLS.bind( Resources.runtimeComponentTypeNotDefined, 
                            config.getNamespace(), type );
            
            FacetCorePlugin.log( msg );
            
            return;
        }
        
        final RuntimeComponentVersion rcv = new RuntimeComponentVersion();
        
        rcv.setRuntimeComponentType( rct );
        rcv.setVersionString( ver );
        rcv.setPluginId( config.getDeclaringExtension().getNamespace() );
        
        rct.addVersion( rcv );
    }
    
    private static void readAdapter( final IConfigurationElement config )
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
                    reportMissingAttribute( child, "id" );
                    return;
                }
                
                if( ! isRuntimeComponentTypeDefined( id ) )
                {
                    final String msg
                        = NLS.bind( Resources.runtimeComponentTypeNotDefined, 
                                    child.getNamespace(), id );
                    
                    FacetCorePlugin.log( msg );
                    
                    return;
                }
                
                rctype = getRuntimeComponentType( id );
                
                final String version = child.getAttribute( "version" );
                
                if( version != null )
                {
                    if( ! rctype.hasVersion( version ) )
                    {
                        final String[] params
                            = new String[] { config.getNamespace(), id, 
                                             version };
                        
                        final String msg
                            = NLS.bind( Resources.runtimeComponentVersionNotDefined, 
                                        params ); 
                        
                        FacetCorePlugin.log( msg );
                        
                        return;
                    }
                    
                    rcversion = rctype.getVersion( version );
                }
            }
            else if( childName.equals( "factory" ) )
            {
                factory = child.getAttribute( "class" );

                if( factory == null )
                {
                    reportMissingAttribute( child, "class" );
                    return;
                }
            }
            else if( childName.equals( "type" ) )
            {
                final String type = child.getAttribute( "class" );

                if( type == null )
                {
                    reportMissingAttribute( child, "class" );
                    return;
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
        
        final String plugin = config.getDeclaringExtension().getNamespace();
        
        for( Iterator itr1 = versions.iterator(); itr1.hasNext(); )
        {
            final RuntimeComponentVersion rcv
                = (RuntimeComponentVersion) itr1.next();
            
            for( Iterator itr2 = types.iterator(); itr2.hasNext(); )
            {
                rcv.addAdapterFactory( (String) itr2.next(), plugin, factory );
            }
        }
    }
    
    private static void readMapping( final IConfigurationElement config )
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
                    reportMissingAttribute( child, "id" );
                    return;
                }
                
                if( ! ProjectFacetsManager.isProjectFacetDefined( id ) )
                {
                    final String msg
                        = NLS.bind( ProjectFacetsManagerImpl.Resources.facetNotDefined, 
                                    child.getNamespace(), id );
                    
                    FacetCorePlugin.log( msg );
                    
                    return;
                }
                
                final IProjectFacet f
                    = ProjectFacetsManager.getProjectFacet( id );
                
                final String v = child.getAttribute( "version" );
                VersionMatchExpr expr = null;
                
                if( v != null )
                {
                    try
                    {
                        expr = new VersionMatchExpr( f, v );
                    }
                    catch( CoreException e )
                    {
                        FacetCorePlugin.log( e.getStatus() );
                        return;
                    }
                }
                
                m.facets.put( f, expr );
            }
            else if( childName.equals( "runtime-component" ) )
            {
                if( child.getAttribute( "any" ) == null )
                {
                    final String id = child.getAttribute( "id" );
    
                    if( id == null )
                    {
                        reportMissingAttribute( child, "id" );
                        return;
                    }
                    
                    if( ! isRuntimeComponentTypeDefined( id ) )
                    {
                        final String msg
                            = NLS.bind( Resources.runtimeComponentTypeNotDefined, 
                                        config.getNamespace(), id );
                        
                        FacetCorePlugin.log( msg );
                        
                        return;
                    }
                    
                    final IRuntimeComponentType rct 
                        = getRuntimeComponentType( id );
                    
                    final String v = child.getAttribute( "version" );
                    VersionMatchExpr expr = null;
                    
                    if( v != null )
                    {
                        try
                        {
                            expr = new VersionMatchExpr( rct, v );
                        }
                        catch( CoreException e )
                        {
                            FacetCorePlugin.log( e.getStatus() );
                            return;
                        }
                    }
                    
                    m.runtimeComponents.put( rct, expr );
                }
            }
        }
        
        mappings.add( m );
    }
    
    private static void readBridgesExtensions()
    {
        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        
        final IExtensionPoint point 
            = registry.getExtensionPoint( FacetCorePlugin.PLUGIN_ID, 
                                          BRIDGES_EXTENSION_ID );
        
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
            
            if( config.getName().equals( "bridge" ) )
            {
                final String id = config.getAttribute( "id" );

                if( id == null )
                {
                    reportMissingAttribute( config, "id" );
                    return;
                }
                
                final String clname = config.getAttribute( "class" );

                if( clname == null )
                {
                    reportMissingAttribute( config, "class" );
                    return;
                }
                
                final String pluginId = config.getNamespace();
                
                final Object br;
                
                try
                {
                    br = FacetCorePlugin.instantiate( pluginId, clname,
                                                      IRuntimeBridge.class );
                }
                catch( CoreException e )
                {
                    FacetCorePlugin.log( e );
                    continue;
                }
                
                bridges.put( id, br );
            }
        }
    }
    
    private static void reportMissingAttribute( final IConfigurationElement el,
                                                final String attribute )
    {
        final String[] params 
            = new String[] { el.getNamespace(), el.getName(), attribute };
        
        final String msg = NLS.bind( Resources.missingAttribute, params ); 
    
        FacetCorePlugin.log( msg );
    }
    
    private static final class Mapping
    {
        // IProjectFacet -> VersionMatchExpr
        public final Map facets = new HashMap();
        
        // IRuntimeComponentType -> VersionMatchExpr
        public final Map runtimeComponents = new HashMap();
        
        private Set getSupportedFacets( final IRuntimeComponent rc )
        
            throws CoreException
            
        {
            final IRuntimeComponentType rct = rc.getRuntimeComponentType();
            final IRuntimeComponentVersion rcv = rc.getRuntimeComponentVersion();
            
            if( this.runtimeComponents.containsKey( rct ) )
            {
                final VersionMatchExpr expr 
                    = (VersionMatchExpr) this.runtimeComponents.get( rct );
                
                if( expr != null && ! expr.evaluate( (IVersion) rcv ) )
                {
                    return Collections.EMPTY_SET;
                }
            }
            else if( ! this.runtimeComponents.isEmpty() )
            {
                return Collections.EMPTY_SET;
            }
            
            final Set result = new HashSet();
            
            for( Iterator itr1 = this.facets.entrySet().iterator(); 
                 itr1.hasNext(); )
            {
                final Map.Entry entry = (Map.Entry) itr1.next();
                final IProjectFacet f = (IProjectFacet) entry.getKey();
                final VersionMatchExpr expr = (VersionMatchExpr) entry.getValue();
                
                for( Iterator itr2 = f.getVersions().iterator(); 
                     itr2.hasNext(); )
                {
                    final IProjectFacetVersion fv 
                        = (IProjectFacetVersion) itr2.next();
                    
                    if( expr == null || expr.evaluate( (IVersion) fv ) )
                    {
                        result.add( fv );
                    }
                }
            }
            
            return result;
        }
    }

    public static final class Resources
    
        extends NLS
        
    {
        public static String missingAttribute;
        public static String runtimeComponentTypeNotDefined;
        public static String runtimeComponentVersionNotDefined;
        public static String runtimeComponentVersionNotDefinedNoPlugin;
        
        static
        {
            initializeMessages( RuntimeManagerImpl.class.getName(), 
                                Resources.class );
        }
    }
    
}