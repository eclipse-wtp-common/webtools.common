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

package org.eclipse.jst.common.project.facet.core.internal;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jst.common.project.facet.core.IClasspathProvider;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class RuntimeClasspathProvider

    implements IClasspathProvider
    
{
    private final IRuntime r;
    
    public RuntimeClasspathProvider( final IRuntime r )
    {
        this.r = r;
    }

    public List getClasspathEntries( final IProjectFacetVersion fv )
    {
        for( Iterator itr = this.r.getRuntimeComponents().iterator(); 
             itr.hasNext(); )
        {
            final IRuntimeComponent rc = (IRuntimeComponent) itr.next();
            
            final IClasspathProvider cpprov 
                = (IClasspathProvider) rc.getAdapter( IClasspathProvider.class );
            
            if( cpprov != null )
            {
                final List cp = cpprov.getClasspathEntries( fv );
                
                if( cp != null )
                {
                    return cp;
                }
            }
        }
        
        return null;
    }
    
    public static final class Factory
    
        implements IAdapterFactory
        
    {
        private static final Class[] ADAPTER_TYPES = { IClasspathProvider.class };
        
        public Object getAdapter( final Object adaptable, 
                                  final Class adapterType )
        {
            if( adapterType == IClasspathProvider.class )
            {
                return new RuntimeClasspathProvider( (IRuntime) adaptable );
            }
            else
            {
                return null;
            }
        }

        public Class[] getAdapterList()
        {
            return ADAPTER_TYPES;
        }
    }

}
