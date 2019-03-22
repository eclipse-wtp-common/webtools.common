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

package org.eclipse.wst.common.project.facet.ui;

import org.eclipse.wst.common.project.facet.ui.internal.FacetUiPlugin;

/**
 * Contains the help context ids defined in the facet UI plugin.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class FacetUiHelpContextIds
{
    /**
     * The help context id for the overall facets selection page:  
     * "org.eclipse.wst.common.project.facet.ui.facetsSelectionPage".
     */
    
    public static final String FACETS_SELECTION_PAGE
        = FacetUiPlugin.PLUGIN_ID + ".facetsSelectionPage"; //$NON-NLS-1$
    
    private FacetUiHelpContextIds() {}
    
}
