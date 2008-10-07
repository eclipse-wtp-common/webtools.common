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

package org.eclipse.jst.common.project.facet.core.libprov.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jst.common.project.facet.core.libprov.IPropertyChangeListener;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class PropertiesHost
{
    private final Set<IPropertyChangeListener> globalListeners
        = new CopyOnWriteArraySet<IPropertyChangeListener>();
    
    private final Map<String,Set<IPropertyChangeListener>> propertySpecificListeners 
        = new HashMap<String,Set<IPropertyChangeListener>>();
    
    public void addListener( final IPropertyChangeListener listener,
                             final String... properties )
    {
        if( properties.length == 0 )
        {
            synchronized( this.globalListeners )
            {
                this.globalListeners.add( listener );
            }
        }
        else
        {
            synchronized( this.propertySpecificListeners )
            {
                for( String property : properties )
                {
                    Set<IPropertyChangeListener> list = this.propertySpecificListeners.get( property );
                    
                    if( list == null )
                    {
                        list = new CopyOnWriteArraySet<IPropertyChangeListener>();
                        this.propertySpecificListeners.put( property, list );
                    }
                    
                    list.add( listener );
                }
            }
        }
    }
    
    public void removeListener( final IPropertyChangeListener listener )
    {
        boolean globalListenerRemoveResult;
        
        synchronized( this.globalListeners )
        {
            globalListenerRemoveResult = this.globalListeners.remove( listener );
        }
        
        if( globalListenerRemoveResult == false )
        {
            synchronized( this.propertySpecificListeners )
            {
                for( Set<IPropertyChangeListener> listeners : this.propertySpecificListeners.values() )
                {
                    listeners.remove( listener );
                }
            }
        }
    }
    
    protected void notifyListeners( final String property,
                                    final Object oldValue,
                                    final Object newValue )
    {
        for( IPropertyChangeListener listener : this.globalListeners )
        {
            listener.propertyChanged( property, oldValue, newValue );
        }
        
        final Set<IPropertyChangeListener> listeners;
        
        synchronized( this.propertySpecificListeners )
        {
            listeners = this.propertySpecificListeners.get( property );
        }
        
        if( listeners != null )
        {
            for( IPropertyChangeListener listener : listeners )
            {
                listener.propertyChanged( property, oldValue, newValue );
            }
        }
    }

}
