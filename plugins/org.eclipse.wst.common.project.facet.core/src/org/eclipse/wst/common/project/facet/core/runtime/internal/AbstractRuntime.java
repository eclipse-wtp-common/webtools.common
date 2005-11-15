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

package org.eclipse.wst.common.project.facet.core.runtime.internal;

import java.util.Iterator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public abstract class AbstractRuntime

    implements IRuntime
    
{
    private String name;
    
    /**
     * This class should not be subclassed outside this package.
     */
    
    AbstractRuntime() {}
    
    public final String getName()
    {
        return this.name;
    }
    
    final void setName( final String name )
    {
        this.name = name;
    }

    public final String getProperty( final String key )
    {
        return (String) getProperties().get( key );
    }
    
    public final Object getAdapter( final Class adapter )
    {
        final String t = adapter.getName();
        Object res = Platform.getAdapterManager().loadAdapter( this, t );
        
        if( res == null )
        {
            for( Iterator itr = getRuntimeComponents().iterator(); 
                 itr.hasNext(); )
            {
                res = ( (IRuntimeComponent) itr.next() ).getAdapter( adapter );
                
                if( res != null )
                {
                    return res;
                }
            }
        }
        
        return res;
    }
    
    public final boolean equals( final Object obj )
    {
        if( obj instanceof IRuntime )
        {
            final IRuntime r = (IRuntime) obj;
            
            return getName().equals( r.getName() ) && 
                   getRuntimeComponents().equals( r.getRuntimeComponents() ) &&
                   getProperties().equals( r.getProperties() );
        }
        
        return false;
    }
    
    public final int hashCode()
    {
        return this.name.hashCode();
    }

}
