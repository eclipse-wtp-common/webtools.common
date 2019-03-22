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

import java.util.List;

import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.ui.internal.ProjectFacetsUiManagerImpl;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetsUiManager 
{
    private ProjectFacetsUiManager() {}
    
    public static List<IFacetWizardPage> getWizardPages( final String actionId )
    {
        return ProjectFacetsUiManagerImpl.getWizardPages( actionId );
    }

    /**
     * @return (element type: {@link IFacetWizardPage})
     * @deprecated
     */
    
    public static List getWizardPages( final Action.Type actionType,
                                       final IProjectFacetVersion f )
    {
        return ProjectFacetsUiManagerImpl.getWizardPages( actionType, f );
    }

}
