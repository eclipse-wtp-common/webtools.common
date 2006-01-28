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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class IndexedSet

    extends HashSet
    
{
    private static final long serialVersionUID = 1L;
    private final Set unmodifiable = Collections.unmodifiableSet( this );
    private final HashMap index = new HashMap();
    
    public void add( final Object key,
                     final Object value )
    {
        add( value );
        this.index.put( key, value );
    }
    
    public boolean delete( final Object key )
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
    
    public Object get( final Object key )
    {
        return this.index.get( key );
    }
    
    public boolean containsKey( final Object key )
    {
        return this.index.containsKey( key );
    }
    
    public Set getUnmodifiable()
    {
        return this.unmodifiable;
    }
}