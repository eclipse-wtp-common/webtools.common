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

package org.eclipse.wst.common.project.facet.core.util.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class IndexedSet<K,V>

    extends HashSet<V>
    
{
    private static final long serialVersionUID = 1L;
    private final Set<V> unmodifiable;
    private final Map<K,V> index;
    
    public IndexedSet()
    {
        this.unmodifiable = Collections.unmodifiableSet( this );
        this.index = new HashMap<K,V>();
    }
    
    public void add( final K key,
                     final V value )
    {
        remove( this.index.get( key ) );
        add( value );
        this.index.put( key, value );
    }
    
    public boolean delete( final K key )
    {
        final Object value = this.index.get( key );
        
        if( value == null )
        {
            return false;
        }
        else
        {
            remove( value );
            this.index.remove( key );
            return true;
        }
    }
    
    public V get( final K key )
    {
        return this.index.get( key );
    }
    
    public boolean containsKey( final K key )
    {
        return this.index.containsKey( key );
    }
    
    public Set<V> getUnmodifiable()
    {
        return this.unmodifiable;
    }
}