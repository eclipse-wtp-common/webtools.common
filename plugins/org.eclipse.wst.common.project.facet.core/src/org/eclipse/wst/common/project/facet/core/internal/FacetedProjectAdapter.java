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

package org.eclipse.wst.common.project.facet.core.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * Adapts {@link IProject} to {@link IFacetedProject}.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@SuppressWarnings( "rawtypes" )

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
