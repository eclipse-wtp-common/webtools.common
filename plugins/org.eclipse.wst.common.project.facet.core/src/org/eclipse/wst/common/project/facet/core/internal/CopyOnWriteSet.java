/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class CopyOnWriteSet

    extends AbstractSet
    
{
    private Set base;
    private Set baseReadOnly;
    
    public CopyOnWriteSet()
    {
        this.base = new HashSet();
        this.baseReadOnly = null;
    }
    
    public boolean add( final Object obj )
    {
        copy();
        return this.base.add( obj );
    }

    public boolean addAll( final Collection collection )
    {
        copy();
        return this.base.addAll( collection );
    }
    
    public boolean remove( final Object obj )
    {
        copy();
        return this.base.remove( obj );
    }
    
    public boolean removeAll( final Collection collection )
    {
        copy();
        return this.base.removeAll( collection );
    }

    public boolean retainAll( final Collection collection )
    {
        copy();
        return this.base.retainAll( collection );
    }
    
    public void clear()
    {
        if( this.baseReadOnly != null )
        {
            this.base = new HashSet();
            this.baseReadOnly = null;
        }
        else
        {
            this.base.clear();
        }
    }

    public int size()
    {
        return this.base.size();
    }

    public Iterator iterator()
    {
        return new CopyOnWriteIterator( this.base.iterator() );
    }
    
    public Set getReadOnlySet()
    {
        if( this.baseReadOnly == null )
        {
            this.baseReadOnly = Collections.unmodifiableSet( this.base );
        }
        
        return this.baseReadOnly;
    }
    
    private void copy()
    {
        if( this.baseReadOnly != null )
        {
            this.base = new HashSet( this.base );
            this.baseReadOnly = null;
        }
    }
    
    private final class CopyOnWriteIterator
    
        implements Iterator
        
    {
        private final Iterator itr;
        private Object current;
        
        public CopyOnWriteIterator( final Iterator itr )
        {
            this.itr = itr;
            this.current = null;
        }
        
        public void remove()
        {
            copy();
            this.itr.remove();
        }

        public boolean hasNext()
        {
            return this.itr.hasNext();
        }

        public Object next()
        {
            this.current = this.itr.next();
            return this.current;
        }
    }

}
