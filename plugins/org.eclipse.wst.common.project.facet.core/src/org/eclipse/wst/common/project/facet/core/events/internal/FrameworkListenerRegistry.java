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

package org.eclipse.wst.common.project.facet.core.events.internal;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectFrameworkEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectFrameworkListener;
import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class FrameworkListenerRegistry
{
    private final Map<IFacetedProjectFrameworkEvent.Type,Set<IFacetedProjectFrameworkListener>> listeners;
    
    public FrameworkListenerRegistry()
    {
        this.listeners
            = new EnumMap<IFacetedProjectFrameworkEvent.Type,Set<IFacetedProjectFrameworkListener>>( IFacetedProjectFrameworkEvent.Type.class );
        
        for( IFacetedProjectFrameworkEvent.Type t : IFacetedProjectFrameworkEvent.Type.values() )
        {
            this.listeners.put( t, new CopyOnWriteArraySet<IFacetedProjectFrameworkListener>() );
        }
    }
    
    public void addListener( final IFacetedProjectFrameworkListener listener,
                             final IFacetedProjectFrameworkEvent.Type... types )
    {
        if( listener == null || types == null )
        {
            throw new IllegalArgumentException();
        }
        
        for( IFacetedProjectFrameworkEvent.Type type 
             : ( types.length > 0 ? types : IFacetedProjectFrameworkEvent.Type.values() ) )
        {
            this.listeners.get( type ).add( listener );
        }
    }
    
    public void removeListener( final IFacetedProjectFrameworkListener listener )
    {
        if( listener == null )
        {
            throw new IllegalArgumentException();
        }
        
        for( Set<IFacetedProjectFrameworkListener> listeners : this.listeners.values() )
        {
            listeners.remove( listener );
        }
    }
    
    public void notifyListeners( final IFacetedProjectFrameworkEvent event )
    {
        for( IFacetedProjectFrameworkListener listener : this.listeners.get( event.getType() ) )
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
    
    public Set<IFacetedProjectFrameworkListener> getListeners( final IFacetedProjectFrameworkEvent.Type eventType )
    {
        return this.listeners.get( eventType );
    }

}
