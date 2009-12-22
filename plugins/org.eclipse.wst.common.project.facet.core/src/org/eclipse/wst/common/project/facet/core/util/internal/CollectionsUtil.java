/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.util.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class CollectionsUtil
{
    private CollectionsUtil() {}
    
    @SuppressWarnings( "unchecked" )
    private static final Comparator<Comparable<? super Comparable>> INVERTING_COMPARATOR 
        = new Comparator<Comparable<? super Comparable>>()
    {
        public int compare( final Comparable<? super Comparable> obj1,
                            final Comparable<? super Comparable> obj2 )
        {
            return obj2.compareTo( obj1 );
        }
    };

    /**
     * Returns a comparator that simply inverts the results of comparing to 
     * <code>Comparable</code> objects. This is useful for sorting a list in
     * reverse of it's natural order.
     *  
     * @return an inverting comparator
     */
    
    @SuppressWarnings( "unchecked" )
    public static <T extends Comparable> Comparator<T> getInvertingComparator()
    {
        return (Comparator<T>) INVERTING_COMPARATOR;
    }
    
    public static <T> Set<T> set( final T... entries )
    {
        switch( entries.length )
        {
            case 0:
            {
                return Collections.emptySet();
            }
            case 1:
            {
                return Collections.singleton( entries[ 0 ] );
            }
            default:
            {
                final Set<T> result = new HashSet<T>();
                
                for( T entry : entries )
                {
                    result.add( entry );
                }
                
                return result;
            }
        }
    }

    public static <T> List<T> list( final T... entries )
    {
        switch( entries.length )
        {
            case 0:
            {
                return Collections.emptyList();
            }
            case 1:
            {
                return Collections.singletonList( entries[ 0 ] );
            }
            default:
            {
                final List<T> result = new ArrayList<T>();
                
                for( T entry : entries )
                {
                    result.add( entry );
                }
                
                return result;
            }
        }
    }
    
}
