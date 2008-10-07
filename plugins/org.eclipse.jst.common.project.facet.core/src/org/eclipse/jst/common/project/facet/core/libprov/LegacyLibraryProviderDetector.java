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

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;

/**
 * This class should be subclassed to provide detection of legacy library providers in the project.
 * It is used together with the org.eclipse.jst.common.project.facet.core.legacyLibraryProviderDetectors
 * extension point when migrating an existing facet to use the Library Provider Framework.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since WTP 3.1
 */

public abstract class LegacyLibraryProviderDetector
{
    /**
     * Attempts to detect the presence of the legacy library provider in the project.
     * 
     * @param project the project in question
     * @param facet the facet that is making the request for libraries
     * @return the legacy library provider or <code>null</code> if not detected
     */
    
    public abstract ILibraryProvider detect( final IProject project,
                                             final IProjectFacet facet );
}
