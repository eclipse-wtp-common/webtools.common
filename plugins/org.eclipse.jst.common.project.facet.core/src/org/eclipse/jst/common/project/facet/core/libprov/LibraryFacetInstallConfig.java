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

public class LibraryFacetInstallConfig

    extends ActionConfig
    
{
    private LibraryInstallDelegate libraryInstallDelegate = null;

    public final LibraryInstallDelegate getLibraryInstallDelegate()
    {
        return this.libraryInstallDelegate;
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
        
        final boolean initialized = init();
        
        if( this.libraryInstallDelegate != null && ! initialized )
        {
            this.libraryInstallDelegate.setProjectFacetVersion( fv );
        }
    }
    
    @Override
    public IStatus validate() 
    {
        if( this.libraryInstallDelegate != null )
        {
            return this.libraryInstallDelegate.validate();
        }
        else
        {
            return Status.OK_STATUS;
        }
    }

    private boolean init()
    {
        final IFacetedProjectWorkingCopy fpjwc = getFacetedProjectWorkingCopy();
        final IProjectFacetVersion fv = getProjectFacetVersion();
        
        if( this.libraryInstallDelegate == null && fpjwc != null && fv != null )
        {
            this.libraryInstallDelegate = new LibraryInstallDelegate( fpjwc, fv );
            return true;
        }
        
        return false;
    }
    
    @Override
	public void dispose() 
    {
    	if( this.libraryInstallDelegate != null )
    	{
    		this.libraryInstallDelegate.dispose();
    	}
	}

	public static final class Factory
        
        implements IActionConfigFactory
        
    {
        public Object create()
        {
            return new LibraryFacetInstallConfig();
        }
    }

}
