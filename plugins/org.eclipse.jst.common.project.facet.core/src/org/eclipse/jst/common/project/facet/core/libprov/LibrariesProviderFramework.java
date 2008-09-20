/******************************************************************************
 * Copyright (c) 2008 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core.libprov;

import java.util.Set;

import org.eclipse.jst.common.project.facet.core.libprov.internal.LibrariesProviderFrameworkImpl;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class LibrariesProviderFramework
{
    private static LibrariesProviderFrameworkImpl impl = null;
    
    private LibrariesProviderFramework() {}
    
    public static Set<ILibrariesProvider> getProviders( final IFacetedProjectWorkingCopy fpjwc,
                                                        final IProjectFacetVersion fv )
    {
        initialize();
        return impl.getProviders( fpjwc, fv );
    }
    
    public static boolean isProviderDefined( final String id )
    {
        initialize();
        return impl.isProviderDefined( id );
    }
    
    public static ILibrariesProvider getProvider( final String id )
    {
        initialize();
        return impl.getProvider( id );
    }
    
    public static ILibrariesProvider getLastProviderUsed( final IProjectFacetVersion fv )
    {
        initialize();
        return impl.getLastProviderUsed( fv );
    }
    
    public static void setLastProviderUsed( final IProjectFacetVersion fv,
                                            final ILibrariesProvider provider )
    {
        initialize();
        impl.setLastProviderUsed( fv, provider );
    }
    
    private static synchronized void initialize()
    {
        if( impl == null )
        {
            impl = new LibrariesProviderFrameworkImpl();
        }
    }
}
