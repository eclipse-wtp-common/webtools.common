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

package org.eclipse.jst.common.project.facet.core.libprov.internal;

import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.log;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jst.common.project.facet.core.internal.ClasspathUtil;
import org.eclipse.jst.common.project.facet.core.libprov.ILibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.LegacyLibraryProviderDetector;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderFramework;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class LegacyRuntimeLibraryProviderDetector

    extends LegacyLibraryProviderDetector
    
{
    private static final String LEGACY_RUNTIME_LIBRARY_PROVIDER_ID 
        = "legacy-runtime-library-provider"; //$NON-NLS-1$
    
    @Override
    public ILibraryProvider detect( final IProject project,
                                    final IProjectFacet facet )
    {
        try
        {
            if( ClasspathUtil.getClasspathEntries( project, facet ).size() > 0 )
            {
                return LibraryProviderFramework.getProvider( LEGACY_RUNTIME_LIBRARY_PROVIDER_ID );
            }
        }
        catch( CoreException e )
        {
            log( e );
        }
        
        return null;
    }
    
}
