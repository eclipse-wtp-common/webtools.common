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

package org.eclipse.wst.common.project.facet.core.util;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;

/**
 * @since 3.0
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class EventListenerRegistry<T extends Enum<T>,E>
{
    private final Class<T> eventTypeClass;
    private final Map<T,Set<IEventListener<E>>> listeners;
    
    public EventListenerRegistry( final Class<T> eventTypeClass )
    {
        this.eventTypeClass = eventTypeClass;
        this.listeners = new EnumMap<T,Set<IEventListener<E>>>( this.eventTypeClass );
        
        for( T t : this.eventTypeClass.getEnumConstants() )
        {
            this.listeners.put( t, new CopyOnWriteArraySet<IEventListener<E>>() );
        }
    }
    
    public void addListener( final IEventListener<E> listener,
                             final T... types )
    {
        if( listener == null || types == null )
        {
            throw new IllegalArgumentException();
        }
        
        for( T type : ( types.length > 0 ? types : this.eventTypeClass.getEnumConstants() ) )
        {
            this.listeners.get( type ).add( listener );
        }
    }
    
    public void removeListener( final IEventListener<E> listener )
    {
        if( listener == null )
        {
            throw new IllegalArgumentException();
        }
        
        for( Set<IEventListener<E>> listeners : this.listeners.values() )
        {
            listeners.remove( listener );
        }
    }
    
    public void notifyListeners( final T eventType,
                                 final E event )
    {
        for( IEventListener<E> listener : this.listeners.get( eventType ) )
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
    
}
