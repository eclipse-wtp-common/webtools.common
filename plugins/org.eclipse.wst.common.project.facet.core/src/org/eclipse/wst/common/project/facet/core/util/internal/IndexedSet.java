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

package org.eclipse.wst.common.project.facet.core.util.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class IndexedSet<K,V>
{
    private final Set<V> set;
    private final Set<V> unmodifiable;
    private final Map<K,V> index;
    
    public IndexedSet()
    {
        this.set = new HashSet<V>();
        this.unmodifiable = Collections.unmodifiableSet( this.set );
        this.index = new HashMap<K,V>();
    }
    
    public Set<V> getItemSet()
    {
        return this.unmodifiable;
    }

    public V getItemByKey( final K key )
    {
        return this.index.get( key );
    }
    
    public boolean containsKey( final K key )
    {
        return this.index.containsKey( key );
    }

    public boolean containsItem( final V item )
    {
        return this.set.contains( item );
    }

    public void addItem( final V item )
    {
        if( item == null )
        {
            throw new IllegalArgumentException();
        }
        
        this.set.add( item );
    }
    
    public void addItemWithKey( final K key,
                                final V item )
    {
        addItem( item );
        addKey( key, item );
    }
    
    public void addKey( final K key,
                        final V item )
    {
        if( key == null || item == null )
        {
            throw new IllegalArgumentException();
        }
        
        if( ! this.set.contains( item ) )
        {
            throw new IllegalArgumentException();
        }
        
        this.index.put( key, item );
    }
    
    public boolean removeItem( final V item )
    {
        if( this.set.remove( item ) )
        {
            for( Iterator<Map.Entry<K,V>> itr = this.index.entrySet().iterator(); itr.hasNext(); )
            {
                final Map.Entry<K,V> entry = itr.next();
                
                if( entry.getValue() == item )
                {
                    itr.remove();
                }
            }
        
            return true;
        }
        
        return false;
    }

    public boolean removeItemByKey( final K key )
    {
        final V item = this.index.get( key );
        
        if( item != null )
        {
            return removeItem( item );
        }
        
        return false;
    }
    
}