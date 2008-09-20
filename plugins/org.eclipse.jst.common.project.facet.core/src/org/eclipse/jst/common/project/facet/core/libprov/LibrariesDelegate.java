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

import static org.eclipse.jst.common.project.facet.core.libprov.internal.UnknownProviderUninstallOperationConfig.UNKNOWN_PROVIDER_ID;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.common.project.facet.core.libprov.internal.LibrariesInstallRecord;
import org.eclipse.jst.common.project.facet.core.libprov.internal.PropertiesHost;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class LibrariesDelegate

    extends PropertiesHost
    
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
    
    public abstract ILibrariesProvider getLibrariesProvider();
    
    public abstract LibrariesProviderOperationConfig getLibrariesProviderOperationConfig();
    
    public abstract void execute( final IProgressMonitor monitor ) throws CoreException;
    
    public IStatus validate()
    {
        synchronized( this )
        {
            if( this.fv == null )
            {
                // TODO: Return the proper error status.
                throw new RuntimeException();
            }
            else if( this.fpjwc == null )
            {
                // TODO: Return the proper error status.
                throw new RuntimeException();
            }
            else
            {
                return Status.OK_STATUS;
            }
        }
    }
    
    public void dispose()
    {
        // The default implementation does not do anything.
    }
    
    /**
     * Reads (from the project metadata) the libraries provider that was used when 
     * the facet was installed.
     * 
     * @return the libraries provider
     */
    
    protected final ILibrariesProvider readProviderFromInstallRecord()
    {
        final IProject project = this.fpjwc.getProject();
        final LibrariesInstallRecord installRecord;
        
        try
        {
            installRecord = new LibrariesInstallRecord( project );
        }
        catch( CoreException e )
        {
            // TODO: Figure out what to do in the case where we fail to parse
            // the libraries install record file. Probably need some sort of
            // a fallback behavior.
            
            throw new RuntimeException( e );
        }
        
        final LibrariesInstallRecord.Entry entry 
            = installRecord.getEntry( this.fv.getProjectFacet() );
        
        final String providerId;
        
        if( entry != null )
        {
            providerId = entry.getLibrariesProviderId();
        }
        else
        {
            providerId = UNKNOWN_PROVIDER_ID;
        }
        
        if( ! LibrariesProviderFramework.isProviderDefined( providerId ) )
        {
            // TODO: Handle this.
            throw new RuntimeException();
        }
        
        return LibrariesProviderFramework.getProvider( providerId );
    }

}
