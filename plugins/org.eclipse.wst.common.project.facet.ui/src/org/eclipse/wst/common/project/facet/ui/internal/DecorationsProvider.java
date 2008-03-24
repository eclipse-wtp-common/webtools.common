/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
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
import org.eclipse.wst.common.project.facet.ui.IDecorationsProvider;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class DecorationsProvider

    implements IDecorationsProvider
    
{
    private final Object adaptable;
    
    public DecorationsProvider( final Object adaptable )
    {
        this.adaptable = adaptable;
    }
    
    public ImageDescriptor getIcon()
    {
        return ProjectFacetsUiManagerImpl.getIcon( this.adaptable ); 
    }
    
    public static final class Factory
    
        implements IAdapterFactory
        
    {
        private static final Class[] ADAPTER_TYPES = { IDecorationsProvider.class };
        
        public Object getAdapter( final Object adaptable, 
                                  final Class adapterType )
        {
            if( adapterType == IDecorationsProvider.class )
            {
                return new DecorationsProvider( adaptable );
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
