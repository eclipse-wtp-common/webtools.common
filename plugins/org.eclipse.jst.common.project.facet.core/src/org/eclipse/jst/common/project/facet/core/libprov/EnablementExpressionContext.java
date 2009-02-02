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

import org.eclipse.wst.common.project.facet.core.IFacetedProjectBase;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * Contains all of the information available in the context of library provider enablement
 * expression. Useful for writing custom property testers.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public final class EnablementExpressionContext
{
    private final IFacetedProjectBase fproj;
    private final IProjectFacetVersion fv;
    private final ILibraryProvider provider;
    
    public EnablementExpressionContext( final IFacetedProjectBase fproj,
                                        final IProjectFacetVersion fv,
                                        final ILibraryProvider provider )
    {
        this.fproj = fproj;
        this.fv = fv;
        this.provider = provider;
    }
    
    public IFacetedProjectBase getFacetedProject()
    {
        return this.fproj;
    }
    
    public IProjectFacet getRequestingProjectFacet()
    {
        return this.fv.getProjectFacet();
    }
    
    public IProjectFacetVersion getRequestingProjectFacetVersion()
    {
        return this.fv;
    }
    
    public ILibraryProvider getLibraryProvider()
    {
        return this.provider;
    }

}
