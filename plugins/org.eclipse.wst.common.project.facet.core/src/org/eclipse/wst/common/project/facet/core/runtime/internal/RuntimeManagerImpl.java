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

package org.eclipse.wst.common.project.facet.core.runtime.internal;

import static org.eclipse.wst.common.project.facet.core.internal.util.PluginUtil.instantiate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IListener;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.VersionFormatException;
import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectFrameworkImpl;
import org.eclipse.wst.common.project.facet.core.internal.ProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.internal.util.IndexedSet;
import org.eclipse.wst.common.project.facet.core.internal.util.VersionExpr;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeBridge;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleEvent;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleListener;
import org.eclipse.wst.common.project.facet.core.runtime.events.internal.RuntimeLifecycleListenerRegistry;
import org.eclipse.wst.common.project.facet.core.runtime.events.internal.ValidationStatusChangedEvent;

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
    
    private static final IndexedSet<String,IRuntimeComponentType> runtimeComponentTypes;
    private static final IndexedSet<String,IRuntime> runtimes;
    private static final List<Mapping> mappings;
    private static final Map<String,IRuntimeBridge> bridges;
    private static final List<DefaultFacetsEntry> defaultFacets;
    private static final Set<IListener> listeners;
    private static final RuntimeLifecycleListenerRegistry runtimeLifecycleListenerRegistry;
    
    static
    {
        runtimeComponentTypes = new IndexedSet<String,IRuntimeComponentType>();
        runtimes = new IndexedSet<String,IRuntime>();
        mappings = new ArrayList<Mapping>();
        bridges = new HashMap<String,IRuntimeBridge>();
        defaultFacets = new ArrayList<DefaultFacetsEntry>();
        listeners = new HashSet<IListener>();
        runtimeLifecycleListenerRegistry = new RuntimeLifecycleListenerRegistry();
        
        readMetadata();
        readBridgesExtensions();
        readDefaultFacetsExtensions();
        
        ( new RuntimeValidationThread() ).start();
    }
    
    private RuntimeManagerImpl() {}
    
    public static Set<IRuntimeComponentType> getRuntimeComponentTypes()
    {
        return runtimeComponentTypes.getUnmodifiable();
    }
    
    public static boolean isRuntimeComponentTypeDefined( final String id )
    {
        return runtimeComponentTypes.containsKey( id );
    }
    
    public static IRuntimeComponentType getRuntimeComponentType( final String id )
    {
        final IRuntimeComponentType rc = runtimeComponentTypes.get( id );
        
        if( rc == null )
        {
            final String msg 
                = NLS.bind( Resources.runtimeComponentTypeNotDefined, id );
            
            throw new IllegalArgumentException( msg );
        }
        
        return rc;
    }
    
    public static IRuntimeComponent createRuntimeComponent( final IRuntimeComponentVersion rcv,
                                                            final Map<String,String> properties )
    {
        final RuntimeComponent rc = new RuntimeComponent();
        
        rc.setRuntimeComponentVersion( rcv );
        
        if( properties != null )
        {
            for( Map.Entry<String,String> entry : properties.entrySet() )
            {
                rc.setProperty( entry.getKey(), entry.getValue() );
            }
        }
        
        return rc;
    }
    
    public static Set<IRuntime> getRuntimes()
    {
        synchronized( runtimes )
        {
            bridge();
            return new HashSet<IRuntime>( runtimes );
        }
    }
    
    public static Set<IRuntime> getRuntimes( final Set<IProjectFacetVersion> facets )
    {
        synchronized( runtimes )
        {
            bridge();
            
            final Set<IRuntime> result = new HashSet<IRuntime>();
            
            for( IRuntime r : runtimes )
            {
                boolean supports = true;
                
                for( IProjectFacetVersion fv : facets )
                {
                    if( ! r.supports( fv ) )
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
            
            final IRuntime runtime = runtimes.get( name );
            
            if( runtime == null )
            {
                final String msg = NLS.bind( Resources.runtimeNotDefined, name );
                throw new IllegalArgumentException( msg );
            }
            
            return runtime;
        }
    }
    
    public static IRuntime defineRuntime( final String name,
                                          final List<IRuntimeComponent> components,
                                          final Map<String,String> properties )
    {
        synchronized( runtimes )
        {
            final Runtime r = new Runtime();
            
            r.setName( name );
            
            for( IRuntimeComponent rc : components )
            {
                r.addRuntimeComponent( rc );
            }
            
            if( properties != null )
            {
                for( Map.Entry<String,String> entry : properties.entrySet() )
                {
                    r.setProperty( entry.getKey(), entry.getValue() );
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
        for( IListener listener : listeners )
        {
            try
            {
                listener.handle();
            }
            catch( Exception e )
            {
                FacetCorePlugin.log( e );
            }
        }
    }
    
    public static void addListener( final IRuntimeLifecycleListener listener,
                                    final IRuntimeLifecycleEvent.Type... types )
    {
        runtimeLifecycleListenerRegistry.addListener( listener, types );
    }
    
    public static void removeListener( final IRuntimeLifecycleListener listener )
    {
        runtimeLifecycleListenerRegistry.removeListener( listener );
    }
    
    static Set<IProjectFacetVersion> getSupportedFacets( final List<IRuntimeComponent> composition )
    {
        final Set<IProjectFacetVersion> result = new HashSet<IProjectFacetVersion>();
        
        for( IRuntimeComponent rc : composition )
        {
            for( Mapping m : mappings )
            {
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
    
    static Set<IProjectFacetVersion> getDefaultFacets( final IRuntimeComponentVersion rcv )
    {
        final Set<IProjectFacetVersion> result = new HashSet<IProjectFacetVersion>();
        
        for( DefaultFacetsEntry dfe : defaultFacets )
        {
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
        
        for( Map.Entry<String,IRuntimeBridge> entry : bridges.entrySet() )
        {
            final String brid = entry.getKey();
            final IRuntimeBridge br = entry.getValue();
            
            // Find the runtimes belonging to this bridge that are currently
            // in the system.
            
            final Map<String,BridgedRuntime> existing = new HashMap<String,BridgedRuntime>();
            
            for( IRuntime r : runtimes )
            {
                if( r instanceof BridgedRuntime )
                {
                    final BridgedRuntime bridged = (BridgedRuntime) r;
                    
                    if( bridged.getBridgeId().equals( brid ) )
                    {
                        existing.put( bridged.getNativeRuntimeId(), bridged );
                    }
                }
            }
            
            // Get the new set of exported runtimes.
            
            final Set<String> exported;
            
            try
            {
                exported = br.getExportedRuntimeNames();
            }
            catch( CoreException e )
            {
                FacetCorePlugin.log( e );
                
                for( BridgedRuntime r : existing.values() )
                {
                    runtimes.remove( r.getName() );
                    modified = true;
                }
                
                continue;
            }
            
            // Remove the absolete entries.
            
            for( BridgedRuntime r : existing.values() )
            {
                if( ! exported.contains( r.getNativeRuntimeId() ) )
                {
                    runtimes.delete( r.getName() );
                    modified = true;
                }
            }
            
            // Create the new entries.
            
            for( String id : exported )
            {
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
        
        final List<IConfigurationElement> cfgels = new ArrayList<IConfigurationElement>();
        
        for( IExtension extension : point.getExtensions() )
        {
            for( IConfigurationElement cfgel : extension.getConfigurationElements() )
            {
                cfgels.add( cfgel );
            }
        }
        
        for( IConfigurationElement config : cfgels )
        {
            if( config.getName().equals( EL_RUNTIME_COMPONENT_TYPE ) )
            {
                readRuntimeComponentType( config );
            }
        }

        for( IConfigurationElement config : cfgels )
        {
            if( config.getName().equals( EL_RUNTIME_COMPONENT_VERSION ) )
            {
                readRuntimeComponentVersion( config );
            }
        }
        
        calculateVersionComparisonTables();

        for( IConfigurationElement config : cfgels )
        {
            if( config.getName().equals( EL_ADAPTER ) )
            {
                readAdapter( config );
            }
        }

        for( IConfigurationElement config : cfgels )
        {
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
    
    /**
     * Pre-computes the tables that describe how versions of runtime components
     * compare to each other. This allows the IRuntimeComponentVersion.compareTo() 
     * operation, which is called rather frequently, to be reduced to a hash 
     * table lookup instead of having to do a parse and comparison of two 
     * version strings.
     */
    
    private static void calculateVersionComparisonTables()
    {
        final List<IRuntimeComponentType> badRuntimeComponentTypes 
            = new ArrayList<IRuntimeComponentType>();
        
        for( IRuntimeComponentType rct : runtimeComponentTypes )
        {
            try
            {
                final Comparator<String> comp = rct.getVersionComparator();
                
                final List<IRuntimeComponentVersion> versions 
                    = new ArrayList<IRuntimeComponentVersion>( rct.getVersions() );
                
                final Map<IRuntimeComponentVersion,Map<IRuntimeComponentVersion,Integer>> compTables
                    = new HashMap<IRuntimeComponentVersion,Map<IRuntimeComponentVersion,Integer>>();
                
                for( IRuntimeComponentVersion rcv : versions )
                {
                    compTables.put( rcv, new HashMap<IRuntimeComponentVersion,Integer>() );
                }
                
                for( int i = 0, n = versions.size(); i < n; i++ )
                {
                    final IRuntimeComponentVersion iVer = versions.get( i );
                    final String iVerStr = iVer.getVersionString();
                    final Map<IRuntimeComponentVersion,Integer> iCompTable = compTables.get( iVer );
                    
                    for( int j = i + 1; j < n; j++ )
                    {
                        final IRuntimeComponentVersion jVer = versions.get( j );
                        final String jVerStr = jVer.getVersionString();
                        final Map<IRuntimeComponentVersion,Integer> jCompTable = compTables.get( jVer );
                        
                        final int result = comp.compare( iVerStr, jVerStr );
                        
                        iCompTable.put( jVer, new Integer( result ) );
                        jCompTable.put( iVer, new Integer( result * -1 ) );
                    }
                }
                
                for( Map.Entry<IRuntimeComponentVersion,Map<IRuntimeComponentVersion,Integer>> entry
                     : compTables.entrySet() )
                {
                    final RuntimeComponentVersion rcv = (RuntimeComponentVersion) entry.getKey();
                    rcv.setComparisonTable( entry.getValue() );
                }
            }
            catch( Exception e )
            {
                // The failure here is due to the problem loading the provided
                // version comparator or due to the problem comparing the
                // version string. In either case, we log the exception and
                // remove all traces of this runtime component type from the 
                // system to keep a faulty runtime component type from dragging
                // down the entire system.
                
                FacetCorePlugin.log( e );
                badRuntimeComponentTypes.add( rct );
            }
        }
        
        for( IRuntimeComponentType rct : badRuntimeComponentTypes )
        {
            runtimeComponentTypes.remove( rct );
        }
    }
    
    private static void readAdapter( final IConfigurationElement config )
    {
        IRuntimeComponentType rctype = null;
        IRuntimeComponentVersion rcversion = null;
        String factory = null;
        final List<String> types = new ArrayList<String>();
        
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
        
        final Set<IRuntimeComponentVersion> versions;
        
        if( rcversion == null )
        {
            versions = rctype.getVersions();
        }
        else
        {
            versions = Collections.singleton( rcversion );
        }
        
        final String plugin = config.getContributor().getName();
        
        for( IRuntimeComponentVersion rcv : versions )
        {
            final RuntimeComponentVersion v = (RuntimeComponentVersion) rcv;
            
            for( String type : types )
            {
                v.addAdapterFactory( type, plugin, factory );
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
                    continue;
                }
                
                final String v = child.getAttribute( ATTR_VERSION );
                VersionExpr<ProjectFacetVersion> expr = null;
                
                if( v != null )
                {
                    try
                    {
                        final String pluginId = config.getContributor().getName();
                        expr = new VersionExpr<ProjectFacetVersion>( f, v, pluginId );
                    }
                    catch( CoreException e )
                    {
                        FacetCorePlugin.log( e.getStatus() );
                        continue;
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
                        continue;
                    }
                    
                    final String v = child.getAttribute( ATTR_VERSION );
                    VersionExpr<RuntimeComponentVersion> expr = null;
                    
                    if( v != null )
                    {
                        try
                        {
                            final String pluginId = config.getContributor().getName();
                            expr = new VersionExpr<RuntimeComponentVersion>( rct, v, pluginId );
                        }
                        catch( CoreException e )
                        {
                            FacetCorePlugin.log( e.getStatus() );
                            continue;
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
        
        final List<IConfigurationElement> cfgels = new ArrayList<IConfigurationElement>();
        
        for( IExtension extension : point.getExtensions() )
        {
            for( IConfigurationElement cfgel : extension.getConfigurationElements() )
            {
                cfgels.add( cfgel );
            }
        }

        for( IConfigurationElement config : cfgels )
        {
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
                
                final IRuntimeBridge br;
                
                try
                {
                    br = instantiate( pluginId, clname, IRuntimeBridge.class );
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
        
        final List<IConfigurationElement> cfgels = new ArrayList<IConfigurationElement>();
        
        for( IExtension extension : point.getExtensions() )
        {
            for( IConfigurationElement cfgel : extension.getConfigurationElements() )
            {
                cfgels.add( cfgel );
            }
        }

        for( IConfigurationElement config : cfgels )
        {
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
                        
                        dfe.rcvexpr = new VersionExpr<RuntimeComponentVersion>( dfe.rct, v, pluginId );
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
                        = NLS.bind( FacetedProjectFrameworkImpl.Resources.facetVersionNotDefined,
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
            FacetedProjectFrameworkImpl.reportMissingFacet( id, config.getContributor().getName() );
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
        public final Map<IProjectFacet,VersionExpr<ProjectFacetVersion>> facets 
            = new HashMap<IProjectFacet,VersionExpr<ProjectFacetVersion>>();
        
        public final Map<IRuntimeComponentType,VersionExpr<RuntimeComponentVersion>> runtimeComponents 
            = new HashMap<IRuntimeComponentType,VersionExpr<RuntimeComponentVersion>>();
        
        private Set<IProjectFacetVersion> getSupportedFacets( final IRuntimeComponent rc )
        
            throws CoreException
            
        {
            final IRuntimeComponentType rct = rc.getRuntimeComponentType();
            final IRuntimeComponentVersion rcv = rc.getRuntimeComponentVersion();
            
            if( this.runtimeComponents.containsKey( rct ) )
            {
                final VersionExpr<RuntimeComponentVersion> expr = this.runtimeComponents.get( rct );
                
                if( expr != null && ! expr.check( rcv ) )
                {
                    return Collections.emptySet();
                }
            }
            else if( ! this.runtimeComponents.isEmpty() )
            {
                return Collections.emptySet();
            }
            
            final Set<IProjectFacetVersion> result = new HashSet<IProjectFacetVersion>();
            
            for( Map.Entry<IProjectFacet,VersionExpr<ProjectFacetVersion>> entry 
                 : this.facets.entrySet() )
            {
                final IProjectFacet f = entry.getKey();
                final VersionExpr<ProjectFacetVersion> expr = entry.getValue();
                
                for( IProjectFacetVersion fv : f.getVersions() )
                {
                    if( expr == null || expr.check( fv ) )
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
        public VersionExpr<RuntimeComponentVersion> rcvexpr;
        public final Set<IProjectFacetVersion> facets = new HashSet<IProjectFacetVersion>();
        
        public boolean match( final IRuntimeComponentVersion rcv )
        
            throws CoreException
            
        {
            if( rcv.getRuntimeComponentType() != this.rct )
            {
                return false;
            }
            else
            {
                return this.rcvexpr.check( rcv );
            }
        }
    }
    
    private static final class RuntimeValidationThread

        extends Thread
        
    {
        private boolean isAborted;
        private Map<String,IStatus> validationResults;
        
        public RuntimeValidationThread()
        {
            this.isAborted = false;
            this.validationResults = new HashMap<String,IStatus>();
        }
        
        private IStatus getValidationResult( final IRuntime runtime )
        {
            synchronized( this.validationResults )
            {
                return this.validationResults.get( runtime.getName() );
            }
        }
        
        private void setValidationResult( final IRuntime runtime,
                                          final IStatus validationResult )
        {
            synchronized( this.validationResults )
            {
                this.validationResults.put( runtime.getName(), validationResult );
            }
        }
        
        @Override
        public void run()
        {
            while( ! this.isAborted )
            {
                for( IRuntime runtime : RuntimeManager.getRuntimes() )
                {
                    final IStatus oldResult = getValidationResult( runtime );
                    final IStatus newResult = runtime.validate( new NullProgressMonitor() );
                    
                    if( oldResult == null || ! oldResult.getMessage().equals( newResult.getMessage() ) )
                    {
                        setValidationResult( runtime, newResult );
                        
                        final IRuntimeLifecycleEvent event
                            = new ValidationStatusChangedEvent( runtime, oldResult, newResult );
                        
                        runtimeLifecycleListenerRegistry.notifyListeners( event );
                    }
                }

                try
                {
                    Thread.sleep( 1000 );
                }
                catch( InterruptedException e ) {}
            }
        }
        
        public void terminate()
        {
            this.isAborted = true;
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