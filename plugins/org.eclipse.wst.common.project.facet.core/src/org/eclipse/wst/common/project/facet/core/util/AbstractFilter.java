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

import org.eclipse.wst.common.project.facet.core.util.IFilter.IFilterEvent.Type;
import org.eclipse.wst.common.project.facet.core.util.internal.AbstractListenerRegistry;

/**
 * @since 3.0
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public abstract class AbstractFilter<T>

    implements IFilter<T>

{
    private FilterListenerRegistry<T> listeners = new FilterListenerRegistry<T>();
    
    public void addListener( final IFilterListener<T> listener,
                             final IFilterEvent.Type... types )
    {
        this.listeners.addListener( listener, types );
    }
    
    public void removeListener( final IFilterListener<T> listener )
    {
        this.listeners.removeListener( listener );
    }
    
    protected void notifyListeners( final IFilterEvent<T> event )
    {
        this.listeners.notifyListeners( event.getType(), event );
    }
    
    private static final class FilterListenerRegistry<T>
    
        extends AbstractListenerRegistry<Type,IFilterEvent<T>,IFilterListener<T>>
    
    {
        public FilterListenerRegistry()
        {
            super( Type.class );
        }

        @Override
        protected void notifyListener( final IFilterListener<T> listener,
                                       final IFilterEvent<T> event )
        {
            listener.handleEvent( event );
        }
    }
    
}
