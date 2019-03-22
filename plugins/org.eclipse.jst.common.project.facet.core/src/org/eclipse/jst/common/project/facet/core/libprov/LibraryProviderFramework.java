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

package org.eclipse.jst.common.project.facet.core.libprov;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jst.common.project.facet.core.libprov.internal.LibraryProviderFrameworkImpl;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;

/**
 * The root entry point for working with the Library Provider Framework.
 * 
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public final class LibraryProviderFramework
{
    private LibraryProviderFramework() {}
    
    /**
     * Returns all of the library providers declared in the system.
     * 
     * @return all of the library providers declared in the system
     */
    
    public static Set<ILibraryProvider> getProviders()
    {
        return get().getProviders();
    }
    
    /**
     * Determines if a library provider with the specified id exists.
     * 
     * @param id the id of the library provider to use in lookup
     * @return <code>true</code> if and only if a library provider with the specified
     *   id exists
     */
    
    public static boolean isProviderDefined( final String id )
    {
        return get().isProviderDefined( id );
    }
    
    /**
     * Returns the library provider corresponding to the specified id.
     * 
     * @param id the id of the library provider to use in lookup
     * @return the library provider corresponding to the specified id
     * @throws IllegalArgumentException if the specified provider id is not recognized
     */
    
    public static ILibraryProvider getProvider( final String id )
    {
        return get().getProvider( id );
    }
    
    /**
     * Returns the library provider that is currently configured with the specified facet. Will
     * return <code>null</code> if the facet is not installed in the specified project.
     * 
     * @param project the project in question
     * @param facet the facet in question
     * @return the library provider currently used by the facet in the specified project
     */
    
    public static ILibraryProvider getCurrentProvider( final IProject project,
                                                       final IProjectFacet facet )
    {
        return get().getCurrentProvider( project, facet );
    }
    
    private static LibraryProviderFrameworkImpl get()
    {
        return LibraryProviderFrameworkImpl.get();
    }

}
