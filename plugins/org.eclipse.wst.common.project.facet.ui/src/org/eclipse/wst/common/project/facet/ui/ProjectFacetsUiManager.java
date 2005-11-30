/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui;

import java.util.List;

import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.ui.internal.ProjectFacetsUiManagerImpl;

/**
 * <p><i>This class is part of an interim API that is still under development 
 * and expected to change significantly before reaching stability. It is being 
 * made available at this early stage to solicit feedback from pioneering 
 * adopters on the understanding that any code that uses this API will almost 
 * certainly be broken (repeatedly) as the API evolves.</i></p>
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetsUiManager 
{
    private ProjectFacetsUiManager() {}
    
    /**
     * @return (element type: {@see IFacetWizardPage})
     */
    
    public static List getWizardPages( final Action.Type actionType,
                                       final IProjectFacetVersion f )
    {
        return ProjectFacetsUiManagerImpl.getWizardPages( actionType, f );
    }

}
