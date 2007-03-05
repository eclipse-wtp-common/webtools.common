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

package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public abstract class AbstractDataModel
{
    private final Map<String,List<IDataModelListener>> listeners 
        = new HashMap<String,List<IDataModelListener>>();
    
    public synchronized void addListener( final String event,
                                          final IDataModelListener listener )
    {
        List<IDataModelListener> list = this.listeners.get( event );
        
        if( list == null )
        {
            list = new ArrayList<IDataModelListener>();
            this.listeners.put( event, list );
        }
        
        list.add( listener );
    }
    
    public synchronized void removeListener( final String event,
                                             final IDataModelListener listener )
    {
        final List<IDataModelListener> list = this.listeners.get( event );
        
        if( list != null )
        {
            list.remove( listener );
        }
    }
    
    public synchronized void removeListener( final IDataModelListener listener )
    {
        for( String property : this.listeners.keySet() )
        {
            removeListener( property, listener );
        }
    }
    
    protected void notifyListeners( final String event )
    {
        final List<IDataModelListener> listeners = this.listeners.get( event );
        
        if( listeners != null )
        {
            for( IDataModelListener listener : listeners )
            {
                listener.handleEvent();
            }
        }
    }
    
    public static interface IDataModelListener
    {
        void handleEvent();
    }

}
