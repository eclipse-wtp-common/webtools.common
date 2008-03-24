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

package org.eclipse.wst.common.project.facet.core.events.internal;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ProjectListenerRegistry
{
    private final Map<IFacetedProjectEvent.Type,Set<IFacetedProjectListener>> listeners;
    
    public ProjectListenerRegistry()
    {
        this.listeners
            = new EnumMap<IFacetedProjectEvent.Type,Set<IFacetedProjectListener>>( IFacetedProjectEvent.Type.class );
        
        for( IFacetedProjectEvent.Type t : IFacetedProjectEvent.Type.values() )
        {
            this.listeners.put( t, new CopyOnWriteArraySet<IFacetedProjectListener>() );
        }
    }
    
    public void addListener( final IFacetedProjectListener listener,
                             final IFacetedProjectEvent.Type... types )
    {
        if( listener == null || types == null )
        {
            throw new IllegalArgumentException();
        }
        
        for( IFacetedProjectEvent.Type type 
             : ( types.length > 0 ? types : IFacetedProjectEvent.Type.values() ) )
        {
            this.listeners.get( type ).add( listener );
        }
    }
    
    public void removeListener( final IFacetedProjectListener listener )
    {
        if( listener == null )
        {
            throw new IllegalArgumentException();
        }
        
        for( Set<IFacetedProjectListener> listeners : this.listeners.values() )
        {
            listeners.remove( listener );
        }
    }
    
    public void notifyListeners( final IFacetedProjectEvent event )
    {
        for( IFacetedProjectListener listener : this.listeners.get( event.getType() ) )
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
    
    public Set<IFacetedProjectListener> getListeners( final IFacetedProjectEvent.Type eventType )
    {
        return this.listeners.get( eventType );
    }

}
