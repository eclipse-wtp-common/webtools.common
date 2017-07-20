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

package org.eclipse.wst.common.project.facet.ui.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.ui.IDecorationsProvider;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class RuntimeDecorationsProvider

    implements IDecorationsProvider
    
{
    private static final ImageDescriptor DEFAULT_RUNTIME_IMAGE
        = FacetUiPlugin.getImageDescriptor( "images/default-runtime-image.gif" ); //$NON-NLS-1$
    
    private final IRuntime runtime;
    
    public RuntimeDecorationsProvider( final IRuntime runtime )
    {
        this.runtime = runtime;
    }
    
    public ImageDescriptor getIcon()
    {
        if( this.runtime.getRuntimeComponents().size() > 0 )
        {
            final IRuntimeComponent rc = this.runtime.getRuntimeComponents().get( 0 );
            final IRuntimeComponentVersion rcv = rc.getRuntimeComponentVersion();
            
            final IDecorationsProvider rcvDecorationsProvider 
                =  rcv.getAdapter( IDecorationsProvider.class );
            
            return rcvDecorationsProvider.getIcon();
        }
        else
        {
            return DEFAULT_RUNTIME_IMAGE;
        }
    }
    
    public static final class Factory
    
        implements IAdapterFactory
        
    {
        private static final Class<?>[] ADAPTER_TYPES = { IDecorationsProvider.class };
        
        public Object getAdapter( final Object adaptable, 
                                  final Class adapterType )
        {
            if( adapterType == IDecorationsProvider.class )
            {
                return new RuntimeDecorationsProvider( (IRuntime) adaptable );
            }
            else
            {
                return null;
            }
        }
    
        public Class<?>[] getAdapterList()
        {
            return ADAPTER_TYPES;
        }
    }

}
