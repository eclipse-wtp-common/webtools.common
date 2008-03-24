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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IActionFilter;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetedProjectActionFilter

    implements IActionFilter
    
{
    public boolean testAttribute( final Object target, 
                                  final String name, 
                                  final String value )
    {
        if( name.equals( "facet" ) ) //$NON-NLS-1$
        {
            final IFacetedProject fproj = (IFacetedProject) target;
            
            final int colon = value.indexOf( ':' );
            final String fid;
            final String vexprstr;
            
            if( colon == -1 || colon == value.length() - 1 )
            {
                fid = value;
                vexprstr = null;
            }
            else
            {
                fid = value.substring( 0, colon );
                vexprstr = value.substring( colon + 1 );
            }
            
            if( ! ProjectFacetsManager.isProjectFacetDefined( fid ) )
            {
                return false;
            }
            
            final IProjectFacet f = ProjectFacetsManager.getProjectFacet( fid );
            
            if( ! fproj.hasProjectFacet( f ) )
            {
                return false;
            }
            
            if( vexprstr == null )
            {
                return true;
            }
            else
            {
                final IProjectFacetVersion fv = fproj.getInstalledVersion( f );
                
                try
                {
                    if( f.getVersions( vexprstr ).contains( fv ) )
                    {
                        return true;
                    }
                }
                catch( CoreException e )
                {
                    FacetUiPlugin.log( e.getStatus() );
                }
            }
            
            return false;
        }
        else
        {
            return false;
        }
    }
    
    public static final class Factory
    
        implements IAdapterFactory
        
    {
        private static final Class[] ADAPTER_TYPES = { IActionFilter.class };
        
        public Object getAdapter( final Object adaptable, 
                                  final Class adapterType )
        {
            if( adapterType == IActionFilter.class )
            {
                return new FacetedProjectActionFilter();
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
