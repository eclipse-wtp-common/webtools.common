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

package org.eclipse.wst.common.project.facet.core.util.internal;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class AbstractListenerRegistry<T extends Enum<T>,E,L>
{
    private final Class<T> eventTypeClass;
    private final Map<T,Set<L>> listeners;
    
    public AbstractListenerRegistry( final Class<T> eventTypeClass )
    {
        this.eventTypeClass = eventTypeClass;
        this.listeners = new EnumMap<T,Set<L>>( this.eventTypeClass );
        
        for( T t : this.eventTypeClass.getEnumConstants() )
        {
            this.listeners.put( t, new CopyOnWriteArraySet<L>() );
        }
    }
    
    public void addListener( final L listener,
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
    
    public void removeListener( final L listener )
    {
        if( listener == null )
        {
            throw new IllegalArgumentException();
        }
        
        for( Set<L> listeners : this.listeners.values() )
        {
            listeners.remove( listener );
        }
    }
    
    public void notifyListeners( final T eventType,
                                 final E event )
    {
        for( L listener : this.listeners.get( eventType ) )
        {
            try
            {
                notifyListener( listener, event );
            }
            catch( Exception e )
            {
                FacetCorePlugin.log( e );
            }
        }
    }
    
    protected abstract void notifyListener( final L listener,
                                            final E event );
    
}
