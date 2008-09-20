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

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.common.project.facet.core.libprov.internal.PropertiesHost;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class LibrariesProviderOperationConfig

    extends PropertiesHost
    
{
    private ILibrariesProvider provider;
    private IProjectFacetVersion fv;
    private LibrariesDelegate parent; 
    
    public synchronized ILibrariesProvider getLibrariesProvider()
    {
        return this.provider;
    }
    
    public synchronized void setLibrariesProvider( final ILibrariesProvider provider )
    {
        this.provider = provider;
    }
    
    public void setParams( final Map<String,String> params )
    {
        // The default implementation does not do anything.
    }
    
    public synchronized IProjectFacetVersion getProjectFacetVersion()
    {
        return this.fv;
    }
    
    public synchronized void setProjectFacetVersion( final IProjectFacetVersion fv )
    {
        this.fv = fv;
    }
    
    public synchronized LibrariesDelegate getParent()
    {
        return this.parent;
    }
    
    public synchronized void setParent( final LibrariesDelegate parent )
    {
        this.parent = parent;
    }
    
    public void dispose()
    {
        // The default implementation does not do anything.
    }
    
    public IStatus validate()
    {
        return Status.OK_STATUS;
    }
    
}
