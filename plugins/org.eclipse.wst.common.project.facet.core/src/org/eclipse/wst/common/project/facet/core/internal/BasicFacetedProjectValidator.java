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

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectValidator;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class BasicFacetedProjectValidator

    implements IFacetedProjectValidator
    
{
    public void validate( final IFacetedProject fproj ) 
    
        throws CoreException
        
    {
        // Is the runtime that the project is associated with defined?
        
        final String runtimeName = ( (FacetedProject) fproj ).getRuntimeName();
        
        if( runtimeName != null )
        {
            if( ! RuntimeManager.isRuntimeDefined( runtimeName ) )
            {
                final String msg
                    = NLS.bind( Resources.runtimeNotDefined, runtimeName );
                
                fproj.createErrorMarker( msg );
            }
        }
        
        // Is an installed facet not supported by the runtime?
        
        final IRuntime r = fproj.getRuntime();
        
        if( r != null )
        {
            for( Iterator itr = fproj.getProjectFacets().iterator(); 
                 itr.hasNext(); )
            {
                final IProjectFacetVersion fv 
                    = (IProjectFacetVersion) itr.next();
                
                if( ! r.supports( fv ) )
                {
                    final String msg
                        = NLS.bind( Resources.facetNotSupported, fv.toString(), 
                                    r.getName() );
                    
                    fproj.createErrorMarker( msg );
                }
            }
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String runtimeNotDefined;
        public static String facetNotSupported;
        
        static
        {
            initializeMessages( BasicFacetedProjectValidator.class.getName(), 
                                Resources.class );
        }
    }

}
