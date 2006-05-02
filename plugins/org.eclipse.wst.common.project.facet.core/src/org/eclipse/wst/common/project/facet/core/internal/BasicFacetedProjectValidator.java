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
import org.eclipse.wst.common.project.facet.core.runtime.internal.UnknownRuntime;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class BasicFacetedProjectValidator

    implements IFacetedProjectValidator
    
{
    public void validate( final IFacetedProject fproj ) 
    
        throws CoreException
        
    {
        // Are any of the target runtimes not defined?
        
        for( Iterator itr1 = fproj.getTargetedRuntimes().iterator();
             itr1.hasNext(); )
        {
            final IRuntime r = (IRuntime) itr1.next();
            
            if( r instanceof UnknownRuntime )
            {
                final String msg
                    = NLS.bind( Resources.runtimeNotDefined, r.getName() );
                
                fproj.createErrorMarker( msg );
            }
        }
        
        // Is an installed facet not supported by the runtime?
        
        for( Iterator itr1 = fproj.getTargetedRuntimes().iterator();
             itr1.hasNext(); )
        {
            final IRuntime r = (IRuntime) itr1.next();
            
            for( Iterator itr2 = fproj.getProjectFacets().iterator(); 
                 itr2.hasNext(); )
            {
                final IProjectFacetVersion fv 
                    = (IProjectFacetVersion) itr2.next();
                
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
