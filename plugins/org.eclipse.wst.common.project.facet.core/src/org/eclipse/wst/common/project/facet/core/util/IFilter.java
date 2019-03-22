/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.util;

/**
 * Interface for implementing filters for various object types.
 * 
 * @since 3.0
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface IFilter<T>
{
    interface IFilterEvent<T>
    {
        enum Type
        {
            FILTER_CHANGED
        }
        
        Type getType();
        IFilter<T> getFilter();
    }
    
    interface IFilterListener<T>
    {
        void handleEvent( IFilterEvent<T> event );
    }
    
    /**
     * The method that is called to let the filter make the determination whether an object
     * passes through the filter.
     * 
     * @param object the object that the filter should check
     * @return <code>true</code> if the object passes through the filter and 
     *   <code>false</code> otherwise
     */
    
    boolean check( T object );
    
    void addListener( IFilterListener<T> listener,
                      IFilterEvent.Type... types );
    
    void removeListener( IFilterListener<T> listener );
    
}
