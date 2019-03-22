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

package org.eclipse.wst.common.project.facet.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface IFacetedProjectValidator
{
    /**
     * The marker type that should be used as the base for all markers reported
     * by the faceted project validator.
     */
    
    static final String BASE_MARKER_ID
        = FacetCorePlugin.PLUGIN_ID + ".validation.marker"; //$NON-NLS-1$
    
    void validate( IFacetedProject fproj )
    
        throws CoreException;
    
}
