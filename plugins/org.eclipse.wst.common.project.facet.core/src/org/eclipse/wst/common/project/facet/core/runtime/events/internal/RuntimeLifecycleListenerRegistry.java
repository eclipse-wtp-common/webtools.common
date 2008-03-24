/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.runtime.events.internal;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleEvent;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleListener;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class RuntimeLifecycleListenerRegistry
{
    private final Map<IRuntimeLifecycleEvent.Type,Set<IRuntimeLifecycleListener>> listeners;
    
    public RuntimeLifecycleListenerRegistry()
    {
        this.listeners
            = new EnumMap<IRuntimeLifecycleEvent.Type,Set<IRuntimeLifecycleListener>>( IRuntimeLifecycleEvent.Type.class );
        
        for( IRuntimeLifecycleEvent.Type t : IRuntimeLifecycleEvent.Type.values() )
        {
            this.listeners.put( t, new CopyOnWriteArraySet<IRuntimeLifecycleListener>() );
        }
    }
    
    public void addListener( final IRuntimeLifecycleListener listener,
                             final IRuntimeLifecycleEvent.Type... types )
    {
        if( listener == null || types == null )
        {
            throw new IllegalArgumentException();
        }
        
        for( IRuntimeLifecycleEvent.Type type 
             : ( types.length > 0 ? types : IRuntimeLifecycleEvent.Type.values() ) )
        {
            this.listeners.get( type ).add( listener );
        }
    }
    
    public void removeListener( final IRuntimeLifecycleListener listener )
    {
        if( listener == null )
        {
            throw new IllegalArgumentException();
        }
        
        for( Set<IRuntimeLifecycleListener> listeners : this.listeners.values() )
        {
            listeners.remove( listener );
        }
    }
    
    public void notifyListeners( final IRuntimeLifecycleEvent event )
    {
        for( IRuntimeLifecycleListener listener : this.listeners.get( event.getType() ) )
        {
            try
            {
                listener.handleEvent( event );
            }
            catch( Exception e )
            {
                FacetCorePlugin.log( e );
            }
        }
    }
    
    public Set<IRuntimeLifecycleListener> getListeners( final IRuntimeLifecycleEvent.Type eventType )
    {
        return this.listeners.get( eventType );
    }

}
