/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * Adapts {@see IProject} to {@see IFacetedProject}.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetedProjectAdapter

    implements IAdapterFactory
    
{
    private static final Class[] ADAPTER_TYPES = { IFacetedProject.class };
    
    public Object getAdapter( final Object adaptable, 
                              final Class adapterType )
    {
        if( adapterType == IFacetedProject.class )
        {
            try
            {
                return ProjectFacetsManager.create( (IProject) adaptable );
            }
            catch( CoreException e )
            {
                FacetCorePlugin.log( e.getStatus() );
            }
        }

        return null;
    }

    public Class[] getAdapterList()
    {
        return ADAPTER_TYPES;
    }

}
