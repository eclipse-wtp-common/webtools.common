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

package org.eclipse.wst.common.project.facet.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @since 3.0
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public abstract class ActionConfig
{
    private IFacetedProjectWorkingCopy fpjwc = null;
    private IProjectFacetVersion fv = null;
    
    public IFacetedProjectWorkingCopy getFacetedProjectWorkingCopy()
    {
        return this.fpjwc;
    }
    
    public void setFacetedProjectWorkingCopy( final IFacetedProjectWorkingCopy fpjwc )
    {
        this.fpjwc = fpjwc;
    }
    
    public IProjectFacetVersion getProjectFacetVersion()
    {
        return this.fv;
    }
    
    public void setProjectFacetVersion( final IProjectFacetVersion fv )
    {
        this.fv = fv;
    }
    
    public IStatus validate()
    {
        return Status.OK_STATUS;
    }
    
}
