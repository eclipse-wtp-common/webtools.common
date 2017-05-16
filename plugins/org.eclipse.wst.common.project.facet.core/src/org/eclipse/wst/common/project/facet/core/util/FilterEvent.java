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

package org.eclipse.wst.common.project.facet.core.util;

/**
 * @since 3.0
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class FilterEvent<T>

    implements IFilter.IFilterEvent<T>
{
    private final IFilter<T> filter;
    private final Type eventType;
    
    public FilterEvent( final IFilter<T> filter,
                        final Type eventType )
    {
        this.filter = filter;
        this.eventType = eventType;
    }
    
    public IFilter<T> getFilter()
    {
        return this.filter;
    }

    public Type getType()
    {
        return this.eventType;
    }
    
}
