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

package org.eclipse.wst.common.project.facet.core.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetedProjectPropertyTester

    extends PropertyTester
    
{
    public boolean test( final Object receiver, 
                         final String property, 
                         final Object[] args, 
                         final Object value )
    {
        try
        {
            if( ! ( receiver instanceof IResource ) )
            {
                return false;
            }
            
            final IProject pj = ( (IResource) receiver ).getProject();
            
            if( pj == null )
            {
                return false;
            }
            
            final IFacetedProject fpj = ProjectFacetsManager.create( pj );
            
            if( fpj == null || value == null || ! ( value instanceof String ) )
            {
                return false;
            }
            
            final String val = (String) value;
            final int colon = val.indexOf( ':' );
            
            final String fid;
            final String vexprstr;
            
            if( colon == -1 || colon == val.length() - 1 )
            {
                fid = val;
                vexprstr = null;
            }
            else
            {
                fid = val.substring( 0, colon );
                vexprstr = val.substring( colon + 1 );
            }
            
            if( ProjectFacetsManager.isProjectFacetDefined( fid ) )
            {
                final IProjectFacet f 
                    = ProjectFacetsManager.getProjectFacet( fid );
                
                final IProjectFacetVersion fv = fpj.getInstalledVersion( f );
                
                if( fv != null )
                {
                    if( vexprstr == null )
                    {
                        return true;
                    }
                    else
                    {
                        return f.getVersions( vexprstr ).contains( fv );
                    }
                }
            }
        }
        catch( CoreException e )
        {
            FacetCorePlugin.log( e.getStatus() );
        }
            
        return false;
    }

}
