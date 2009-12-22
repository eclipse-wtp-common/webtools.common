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

package org.eclipse.jst.common.project.facet.core.libprov.internal;

import java.util.List;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jst.common.project.facet.core.IClasspathProvider;
import org.eclipse.jst.common.project.facet.core.libprov.EnablementExpressionContext;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class RuntimeLibraryProviderPropertyTester

    extends PropertyTester
    
{
    public boolean test( final Object receiver, 
                         final String property, 
                         final Object[] args, 
                         final Object value )
    {
        if( receiver instanceof EnablementExpressionContext )
        {
            final EnablementExpressionContext context = (EnablementExpressionContext) receiver;
            final IProjectFacetVersion fv = context.getRequestingProjectFacetVersion();
            final IRuntime runtime = context.getFacetedProject().getPrimaryRuntime();
            
            if( runtime != null )
            {
                final IClasspathProvider cpprov 
                    = (IClasspathProvider) runtime.getAdapter( IClasspathProvider.class );
                
                final List<IClasspathEntry> cpentries = cpprov.getClasspathEntries( fv );
                
                if( cpentries != null && ! cpentries.isEmpty() )
                {
                    return true;
                }
            }
        }
            
        return false;
    }

}
