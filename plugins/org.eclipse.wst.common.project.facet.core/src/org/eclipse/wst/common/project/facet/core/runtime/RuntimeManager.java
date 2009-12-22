/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.runtime;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IListener;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleEvent;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleListener;
import org.eclipse.wst.common.project.facet.core.runtime.internal.RuntimeManagerImpl;

/**
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class RuntimeManager 
{
    private RuntimeManager() {}
    
    /**
     * Returns all of the available runtime component types.
     * 
     * @return all of the available runtime component types
     */
    
    public static Set<IRuntimeComponentType> getRuntimeComponentTypes()
    {
        return RuntimeManagerImpl.getRuntimeComponentTypes();
    }
    
    /**
     * Determines whether the specified runtime component type exists.
     * 
     * @param id the runtime component type id
     * @return <code>true</code> if the specified runtime component type exists,
     *   <code>false</code> otherwise
     */
    
    public static boolean isRuntimeComponentTypeDefined( final String id )
    {
        return RuntimeManagerImpl.isRuntimeComponentTypeDefined( id );
    }
    
    /**
     * Returns the {@link IRuntimeComponentType} object corresponding to the
     * specified runtime component type id.
     * 
     * @param id the runtime componenet type id
     * @return the {@link IRuntimeComponentType} object corresponding to the
     *   specified runtime componenet type id
     * @throws IllegalArgumentException if the runtime component type id is not
     *   recognized
     */
    
    public static IRuntimeComponentType getRuntimeComponentType( final String id )
    {
        return RuntimeManagerImpl.getRuntimeComponentType( id );
    }
    
    /**
     * Returns all of the defined runtimes.
     * 
     * @return all of the defined runtimes
     */
    
    public static Set<IRuntime> getRuntimes()
    {
        return RuntimeManagerImpl.getRuntimes();
    }
    
    /**
     * Returns the runtimes that support all of the specified facets.
     * 
     * @param facets the facets that need to be supported
     * @return the runtimes that support all of the specified facets
     */
    
    public static Set<IRuntime> getRuntimes( final Set<IProjectFacetVersion> facets )
    {
        return RuntimeManagerImpl.getRuntimes( facets );
    }
    
    
    /**
     * Determines whether the specified runtime has been defined.
     * 
     * @param name the runtime name
     * @return <code>true</code> if the specified runtime is defined, 
     *   <code>false</code> otherwise
     */
    
    public static boolean isRuntimeDefined( final String name )
    {
        return RuntimeManagerImpl.isRuntimeDefined( name );
    }
    
    /**
     * Returns the runtime corresponding to the specified name.
     * 
     * @param name the runtime name
     * @return the runtime corresponding to the specified name
     * @throws IllegalArgumentException if the runtime name is not recognized
     */
    
    public static IRuntime getRuntime( final String name )
    {
        return RuntimeManagerImpl.getRuntime( name );
    }
    
    /**
     * Defines a new runtime.
     * 
     * @param name the runtime name
     * @param components the list of runtime componenets
     * @param properties the runtime properties
     * @return the new runtime
     */
    
    public static IRuntime defineRuntime( final String name,
                                          final List<IRuntimeComponent> components,
                                          final Map<String,String> properties )
    {
        return RuntimeManagerImpl.defineRuntime( name, components, properties );
    }
    
    /**
     * Deletes the runtime from the registry.
     * 
     * @param runtime the runtime to delete
     */
    
    public static void deleteRuntime( final IRuntime runtime )
    {
        RuntimeManagerImpl.deleteRuntime( runtime );
    }
    
    /**
     * Creates a new runtime componenet. This method is intended to be used in
     * conjunction with the {@link #defineRuntime(String,List,Map)} method.
     * 
     * @param rcv the runtime component version
     * @param properties the runtime component properties
     * @return the new runtime component
     */
    
    public static IRuntimeComponent createRuntimeComponent( final IRuntimeComponentVersion rcv,
                                                            final Map<String,String> properties )
    {
        return RuntimeManagerImpl.createRuntimeComponent( rcv, properties );
    }
    
    public static void addRuntimeListener( final IListener listener )
    {
        RuntimeManagerImpl.addRuntimeListener( listener );
    }

    public static void removeRuntimeListener( final IListener listener )
    {
        RuntimeManagerImpl.removeRuntimeListener( listener );
    }
    
    public static void addListener( final IRuntimeLifecycleListener listener,
                                    final IRuntimeLifecycleEvent.Type... types )
    {
        RuntimeManagerImpl.addListener( listener, types );
    }
    
    public static void removeListener( final IRuntimeLifecycleListener listener )
    {
        RuntimeManagerImpl.removeListener( listener );
    }
    
}
