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

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ObjectReference<T>
{
    private T object;
    
    public ObjectReference()
    {
        this.object = null;
    }
    
    public ObjectReference( final T object )
    {
        this.object = object;
    }
    
    public T get()
    {
        return this.object;
    }
    
    public void set( final T object )
    {
        this.object = object;
    }
    
}
