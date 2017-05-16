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
