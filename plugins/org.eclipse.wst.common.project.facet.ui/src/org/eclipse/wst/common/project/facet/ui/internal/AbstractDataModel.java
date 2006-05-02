package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractDataModel
{
    private final Map listeners = new HashMap();
    
    public synchronized void addListener( final String event,
                                          final IDataModelListener listener )
    {
        List list = (List) this.listeners.get( event );
        
        if( list == null )
        {
            list = new ArrayList();
            this.listeners.put( event, list );
        }
        
        list.add( listener );
    }
    
    public synchronized void removeListener( final String event,
                                             final IDataModelListener listener )
    {
        final List list = (List) this.listeners.get( event );
        
        if( list != null )
        {
            list.remove( listener );
        }
    }
    
    public synchronized void removeListener( final IDataModelListener listener )
    {
        for( Iterator itr = this.listeners.keySet().iterator(); itr.hasNext(); )
        {
            removeListener( (String) itr.next(), listener );
        }
    }
    
    protected void notifyListeners( final String event )
    {
        final List listeners = (List) this.listeners.get( event );
        
        if( listeners != null )
        {
            for( Iterator itr = listeners.iterator(); itr.hasNext(); )
            {
                ( (IDataModelListener) itr.next() ).handleEvent();
            }
        }
    }
    
    public static interface IDataModelListener
    {
        void handleEvent();
    }

}
