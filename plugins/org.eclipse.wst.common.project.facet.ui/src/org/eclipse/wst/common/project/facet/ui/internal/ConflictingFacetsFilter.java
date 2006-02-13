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

package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.Set;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ConflictingFacetsFilter 

    implements FacetsSelectionPanel.IFilter
    
{
    private final Set fixed;
    
    public ConflictingFacetsFilter( final Set fixed )
    {
        this.fixed = fixed;
    }
    
    public boolean check( final IProjectFacetVersion fv )
    {
        return fv.isValidFor( this.fixed );
    }

}
