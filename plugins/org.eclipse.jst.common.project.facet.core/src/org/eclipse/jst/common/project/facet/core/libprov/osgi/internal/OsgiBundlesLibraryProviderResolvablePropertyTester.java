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

package org.eclipse.jst.common.project.facet.core.libprov.osgi.internal;

import static org.eclipse.jst.common.project.facet.core.libprov.osgi.OsgiBundlesLibraryProviderInstallOperationConfig.getBundleReferences;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jst.common.project.facet.core.libprov.EnablementExpressionContext;
import org.eclipse.jst.common.project.facet.core.libprov.ILibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.osgi.BundleReference;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class OsgiBundlesLibraryProviderResolvablePropertyTester

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
            return test( context.getLibraryProvider(), value );
        }
        else if( receiver instanceof ILibraryProvider )
        {
            return test( (ILibraryProvider) receiver, value );
        }
            
        return false;
    }
    
    private boolean test( final ILibraryProvider provider,
                          final Object value )
    {
        boolean isResolvable = true;
        
        for( BundleReference bundleReference : getBundleReferences( provider ) )
        {
            if( ! bundleReference.isResolvable() )
            {
                isResolvable = false;
                break;
            }
        }
        
        if( value instanceof Boolean )
        {
            return value.equals( isResolvable );
        }
        
        return false;
    }

}
