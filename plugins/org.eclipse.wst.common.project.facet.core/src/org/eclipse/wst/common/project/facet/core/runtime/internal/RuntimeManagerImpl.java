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
import org.eclipse.wst.common.project.facet.core.internal.VersionExpr;
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
    private static final String EXTENSION_ID = "runtimes"; //$NON-NLS-1$
    private static final String BRIDGES_EXTENSION_ID = "runtimeBridges"; //$NON-NLS-1$
    private static final String DEFAULT_FACETS_EXTENSION_ID = "defaultFacets"; //$NON-NLS-1$

    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
    private static final String ATTR_ID = "id"; //$NON-NLS-1$
    private static final String ATTR_TYPE = "type"; //$NON-NLS-1$
    private static final String ATTR_VERSION = "version"; //$NON-NLS-1$
    private static final String EL_ADAPTER = "adapter"; //$NON-NLS-1$
    private static final String EL_BRIDGE = "bridge"; //$NON-NLS-1$
    private static final String EL_DEFAULT_FACETS = "default-facets"; //$NON-NLS-1$
    private static final String EL_FACET = "facet"; //$NON-NLS-1$
    private static final String EL_FACTORY = "factory"; //$NON-NLS-1$
    private static final String EL_RUNTIME_COMPONENT = "runtime-component"; //$NON-NLS-1$
    private static final String EL_RUNTIME_COMPONENT_TYPE = "runtime-component-type"; //$NON-NLS-1$
    private static final String EL_RUNTIME_COMPONENT_VERSION = "runtime-component-version"; //$NON-NLS-1$
    private static final String EL_SUPPORTED = "supported"; //$NON-NLS-1$
    private static final String EL_VERSION_COMPARATOR = "version-comparator"; //$NON-NLS-1$

    private static final String ANY = "any"; //$NON-NLS-1$
    
    private static final IndexedSet runtimeComponentTypes;
    private static final IndexedSet runtimes;
    private static final List mappings;
    private static final Map bridges;
    private static final List defaultFacets;
    private static final Set listeners;
    
    static
    {
        runtimeComponentTypes = new IndexedSet();
        runtimes = new IndexedSet();
        mappings = new ArrayList();
        bridges = new HashMap();
        defaultFacets = new ArrayList();
        listeners = new HashSet();
        
        readMetadata();
        readBridgesExtensions();
        readDefaultFacetsExtensions();
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
            final String msg 
                = NLS.bind( Resources.runtimeComponentTypeNotDefined, id );
            
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
                final String msg = NLS.bind( Resources.runtimeNotDefined, name );
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
    
    static Set getSupportedFacets( final List composition )
    {
        final Set result = new HashSet();
        
        for( Iterator itr1 = composition.iterator(); itr1.hasNext(); )
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
    
    static Set getDefaultFacets( final IRuntimeComponentVersion rcv )
    {
        final Set result = new HashSet();
        
        for( Iterator itr = defaultFacets.iterator(); itr.hasNext(); )
        {
            final DefaultFacetsEntry dfe = (DefaultFacetsEntry) itr.next();
            
            try
            {
                if( dfe.match( rcv ) )
                {
                    result.addAll( dfe.facets );
                }
            }
            catch( CoreException e )
            {
                FacetCorePlugin.log( e );
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
            name = suggestion + " (" + i + ")"; //$NON-NLS-1$ //$NON-NLS-2$
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
            throw new RuntimeException( "Extension point not found!" ); //$NON-NLS-1$
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
            
            if( config.getName().equals( EL_RUNTIME_COMPONENT_TYPE ) )
            {
                readRuntimeComponentType( config );
            }
        }

        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( EL_RUNTIME_COMPONENT_VERSION ) )
            {
                readRuntimeComponentVersion( config );
            }
        }

        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( EL_ADAPTER ) )
            {
                readAdapter( config );
            }
        }

        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( EL_SUPPORTED ) )
            {
                readMapping( config );
            }
        }
    }
    
    private static void readRuntimeComponentType( final IConfigurationElement config )
    {
        final String id = config.getAttribute( ATTR_ID );

        if( id == null )
        {
            reportMissingAttribute( config, ATTR_ID );
            return;
        }
        
        final RuntimeComponentType rct = new RuntimeComponentType();
        rct.setId( id );
        rct.setPluginId( config.getContributor().getName() );

        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( EL_VERSION_COMPARATOR ) )
            {
                final String clname = child.getAttribute( ATTR_CLASS );
                
                if( clname == null )
                {
                    reportMissingAttribute( child, ATTR_CLASS );
                    return;
                }
                
                rct.setVersionComparator( clname );
            }
        }
        
        runtimeComponentTypes.add( id, rct );
    }
    
    private static void readRuntimeComponentVersion( final IConfigurationElement config )
    {
        final String type = config.getAttribute( ATTR_TYPE );

        if( type == null )
        {
            reportMissingAttribute( config, ATTR_TYPE );
            return;
        }
        
        final String ver = config.getAttribute( ATTR_VERSION );

        if( ver == null )
        {
            reportMissingAttribute( config, ATTR_VERSION );
            return;
        }
        
        final RuntimeComponentType rct 
            = (RuntimeComponentType) runtimeComponentTypes.get( type );
        
        if( rct == null )
        {
            final String msg
                = NLS.bind( Resources.runtimeComponentTypeNotDefined, type ) +
                  NLS.bind( Resources.usedInPlugin, config.getContributor().getName() );
            
            FacetCorePlugin.log( msg );
            
            return;
        }
        
        final RuntimeComponentVersion rcv = new RuntimeComponentVersion();
        
        rcv.setRuntimeComponentType( rct );
        rcv.setVersionString( ver );
        rcv.setPluginId( config.getContributor().getName() );
        
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
            
            if( childName.equals( EL_RUNTIME_COMPONENT ) )
            {
                final String id = child.getAttribute( ATTR_ID );

                if( id == null )
                {
                    reportMissingAttribute( child, ATTR_ID );
                    return;
                }
                
                if( ! isRuntimeComponentTypeDefined( id ) )
                {
                    final String msg
                        = NLS.bind( Resources.runtimeComponentTypeNotDefined, id ) +
                          NLS.bind( Resources.usedInPlugin, 
                                    child.getContributor().getName() );
                    
                    FacetCorePlugin.log( msg );
                    
                    return;
                }
                
                rctype = getRuntimeComponentType( id );
                
                final String version = child.getAttribute( ATTR_VERSION );
                
                if( version != null )
                {
                    if( ! rctype.hasVersion( version ) )
                    {
                        String msg
                            = NLS.bind( Resources.runtimeComponentVersionNotDefined,
                                        id, version );
                        
                        msg += NLS.bind( Resources.usedInPlugin, 
                                         config.getContributor().getName() );
                        
                        FacetCorePlugin.log( msg );
                        
                        return;
                    }
                    
                    rcversion = rctype.getVersion( version );
                }
            }
            else if( childName.equals( EL_FACTORY ) )
            {
                factory = child.getAttribute( ATTR_CLASS );

                if( factory == null )
                {
                    reportMissingAttribute( child, ATTR_CLASS );
                    return;
                }
            }
            else if( childName.equals( ATTR_TYPE ) )
            {
                final String type = child.getAttribute( ATTR_CLASS );

                if( type == null )
                {
                    reportMissingAttribute( child, ATTR_CLASS );
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
        
        final String plugin = config.getContributor().getName();
        
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
            
            if( childName.equals( EL_FACET ) )
            {
                final IProjectFacet f = readProjectFacetRef( child );
                
                if( f == null )
                {
                    return;
                }
                
                final String v = child.getAttribute( ATTR_VERSION );
                VersionExpr expr = null;
                
                if( v != null )
                {
                    try
                    {
                        expr = new VersionExpr( f, v, config.getContributor().getName() );
                    }
                    catch( CoreException e )
                    {
                        FacetCorePlugin.log( e.getStatus() );
                        return;
                    }
                }
                
                m.facets.put( f, expr );
            }
            else if( childName.equals( EL_RUNTIME_COMPONENT ) )
            {
                if( child.getAttribute( ANY ) == null )
                {
                    final IRuntimeComponentType rct 
                        = readRuntimeComponentTypeRef( child );
                    
                    if( rct == null )
                    {
                        return;
                    }
                    
                    final String v = child.getAttribute( ATTR_VERSION );
                    VersionExpr expr = null;
                    
                    if( v != null )
                    {
                        try
                        {
                            final String pluginId 
                                = config.getContributor().getName();
                            
                            expr = new VersionExpr( rct, v, pluginId );
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
            throw new RuntimeException( "Extension point not found!" ); //$NON-NLS-1$
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
            
            if( config.getName().equals( EL_BRIDGE ) )
            {
                final String id = config.getAttribute( ATTR_ID );

                if( id == null )
                {
                    reportMissingAttribute( config, ATTR_ID );
                    return;
                }
                
                final String clname = config.getAttribute( ATTR_CLASS );

                if( clname == null )
                {
                    reportMissingAttribute( config, ATTR_CLASS );
                    return;
                }
                
                final String pluginId = config.getContributor().getName();
                
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

    private static void readDefaultFacetsExtensions()
    {
        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        
        final IExtensionPoint point 
            = registry.getExtensionPoint( FacetCorePlugin.PLUGIN_ID, 
                                          DEFAULT_FACETS_EXTENSION_ID );
        
        if( point == null )
        {
            throw new RuntimeException( "Extension point not found!" ); //$NON-NLS-1$
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
            
            if( config.getName().equals( EL_DEFAULT_FACETS ) )
            {
                readDefaultFacets( config );
            }
        }
    }
    
    private static void readDefaultFacets( final IConfigurationElement config )
    {
        final DefaultFacetsEntry dfe = new DefaultFacetsEntry();
        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( EL_RUNTIME_COMPONENT ) )
            {
                dfe.rct = readRuntimeComponentTypeRef( child );
                
                if( dfe.rct == null )
                {
                    return;
                }
                
                final String v = child.getAttribute( ATTR_VERSION );
                
                if( v != null )
                {
                    try
                    {
                        final String pluginId 
                            = config.getContributor().getName();
                        
                        dfe.rcvexpr = new VersionExpr( dfe.rct, v, pluginId );
                    }
                    catch( CoreException e )
                    {
                        FacetCorePlugin.log( e.getStatus() );
                        return;
                    }
                }
            }
            else if( childName.equals( EL_FACET ) )
            {
                final IProjectFacet f = readProjectFacetRef( child );
                
                if( f == null )
                {
                    return;
                }
                
                final String ver = child.getAttribute( ATTR_VERSION );
                
                if( ver == null )
                {
                    reportMissingAttribute( child, ATTR_VERSION );
                    return;
                }
                
                if( ! f.hasVersion( ver ) )
                {
                    String msg
                        = NLS.bind( ProjectFacetsManagerImpl.Resources.facetVersionNotDefined,
                                    f.getId(), ver );
                    
                    msg += NLS.bind( Resources.usedInPlugin, 
                                     config.getContributor().getName() );
                    
                    FacetCorePlugin.log( msg );
                    
                    return;
                }
                
                dfe.facets.add( f.getVersion( ver ) );
            }
        }
        
        if( dfe.rct == null )
        {
            return;
        }
        
        defaultFacets.add( dfe );
    }
    
    private static IRuntimeComponentType readRuntimeComponentTypeRef( final IConfigurationElement config )
    {
        final String id = config.getAttribute( ATTR_ID );
        
        if( id == null )
        {
            reportMissingAttribute( config, ATTR_ID );
            return null;
        }
        
        if( ! isRuntimeComponentTypeDefined( id ) )
        {
            final String msg
                = NLS.bind( Resources.runtimeComponentTypeNotDefined, id ) +
                  NLS.bind( Resources.usedInPlugin, 
                            config.getContributor().getName() );
            
            FacetCorePlugin.log( msg );
            
            return null;
        }
        
        return getRuntimeComponentType( id );
    }
    
    private static IProjectFacet readProjectFacetRef( final IConfigurationElement config )
    {
        final String id = config.getAttribute( ATTR_ID );

        if( id == null )
        {
            reportMissingAttribute( config, ATTR_ID );
            return null;
        }
        
        if( ! ProjectFacetsManager.isProjectFacetDefined( id ) )
        {
            ProjectFacetsManagerImpl.reportMissingFacet( id, config.getContributor().getName() );
            return null;
        }
        
        return ProjectFacetsManager.getProjectFacet( id );
    }
    
    private static void reportMissingAttribute( final IConfigurationElement el,
                                                final String attribute )
    {
        final String[] params 
            = new String[] { el.getContributor().getName(), el.getName(), attribute };
        
        final String msg = NLS.bind( Resources.missingAttribute, params ); 
    
        FacetCorePlugin.log( msg );
    }
    
    private static final class Mapping
    {
        // IProjectFacet -> VersionMatchExpr
        public final Map facets = new HashMap();
        
        // IRuntimeComponentType -> VersionExpr
        public final Map runtimeComponents = new HashMap();
        
        private Set getSupportedFacets( final IRuntimeComponent rc )
        
            throws CoreException
            
        {
            final IRuntimeComponentType rct = rc.getRuntimeComponentType();
            final IRuntimeComponentVersion rcv = rc.getRuntimeComponentVersion();
            
            if( this.runtimeComponents.containsKey( rct ) )
            {
                final VersionExpr expr 
                    = (VersionExpr) this.runtimeComponents.get( rct );
                
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
                final VersionExpr expr = (VersionExpr) entry.getValue();
                
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
    
    private static final class DefaultFacetsEntry
    {
        public IRuntimeComponentType rct;
        public VersionExpr rcvexpr;
        public final Set facets = new HashSet();
        
        public boolean match( final IRuntimeComponentVersion rcv )
        
            throws CoreException
            
        {
            if( rcv.getRuntimeComponentType() != this.rct )
            {
                return false;
            }
            else
            {
                return this.rcvexpr.evaluate( (IVersion) rcv );
            }
        }
    }

    public static final class Resources
    
        extends NLS
        
    {
        public static String missingAttribute;
        public static String runtimeComponentTypeNotDefined;
        public static String runtimeComponentVersionNotDefined;
        public static String runtimeNotDefined;
        public static String usedInPlugin;
        
        static
        {
            initializeMessages( RuntimeManagerImpl.class.getName(), 
                                Resources.class );
        }
    }
    
}