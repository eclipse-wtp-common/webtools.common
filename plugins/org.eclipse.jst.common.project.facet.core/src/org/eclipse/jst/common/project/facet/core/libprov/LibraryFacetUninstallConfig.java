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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.project.facet.core.ActionConfig;
import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author <a href="konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public class LibraryFacetUninstallConfig

    extends ActionConfig
    
{
    private LibraryUninstallDelegate libraryUninstallDelegate = null;

    public final LibraryUninstallDelegate getLibraryUninstallDelegate()
    {
        return this.libraryUninstallDelegate;
    }
    
    @Override
    public void setFacetedProjectWorkingCopy( final IFacetedProjectWorkingCopy fpjwc )
    {
        super.setFacetedProjectWorkingCopy( fpjwc );
        init();
    }

    @Override
    public void setProjectFacetVersion( final IProjectFacetVersion fv )
    {
        super.setProjectFacetVersion( fv );
        init();
    }
    
    @Override
    public IStatus validate() 
    {
        if( this.libraryUninstallDelegate != null )
        {
            return this.libraryUninstallDelegate.validate();
        }
        else
        {
            return Status.OK_STATUS;
        }
    }

    private void init()
    {
        final IFacetedProjectWorkingCopy fpjwc = getFacetedProjectWorkingCopy();
        final IProjectFacetVersion fv = getProjectFacetVersion();
        
        if( fpjwc != null && fv != null )
        {
            this.libraryUninstallDelegate = new LibraryUninstallDelegate( fpjwc, fv );
        }
    }
    
    public static final class Factory
    
        implements IActionConfigFactory
        
    {
        public Object create()
        {
            return new LibraryFacetUninstallConfig();
        }
    }

}
