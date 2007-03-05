/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui;

import java.util.List;

import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.ui.internal.ProjectFacetsUiManagerImpl;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetsUiManager 
{
    private ProjectFacetsUiManager() {}
    
    public static List<IFacetWizardPage> getWizardPages( final String actionId )
    {
        return ProjectFacetsUiManagerImpl.getWizardPages( actionId );
    }

    /**
     * @return (element type: {@see IFacetWizardPage})
     * @deprecated
     */
    
    public static List getWizardPages( final Action.Type actionType,
                                       final IProjectFacetVersion f )
    {
        return ProjectFacetsUiManagerImpl.getWizardPages( actionType, f );
    }

}
