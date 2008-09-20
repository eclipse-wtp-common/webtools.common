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

import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.log;
import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.beginTask;
import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.done;
import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.submon;
import static org.eclipse.wst.common.project.facet.core.util.internal.ProgressMonitorUtil.worked;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.common.project.facet.core.libprov.internal.LibrariesInstallRecord;
import org.eclipse.jst.common.project.facet.core.libprov.internal.LibrariesProvider;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class LibrariesUninstallDelegate

    extends LibrariesDelegate
    
{
    private ILibrariesProvider oldProvider = null;
    private LibrariesProviderOperationConfig oldProviderUninstallOpConfig = null;
    
    @Override
    public synchronized void setFacetedProjectWorkingCopy( final IFacetedProjectWorkingCopy fpjwc )
    {
        super.setFacetedProjectWorkingCopy( fpjwc );
        init();
    }

    @Override
    public synchronized void setProjectFacetVersion( final IProjectFacetVersion fv )
    {
        super.setProjectFacetVersion( fv );
        init();
    }

    @Override
    public synchronized ILibrariesProvider getLibrariesProvider()
    {
        return this.oldProvider;
    }
    
    @Override
    public synchronized LibrariesProviderOperationConfig getLibrariesProviderOperationConfig()
    {
        return this.oldProviderUninstallOpConfig;
    }

    @Override
    public synchronized IStatus validate()
    {
        IStatus st = super.validate();
        
        if( ! st.isOK() )
        {
            return st;
        }
        
        st = this.oldProviderUninstallOpConfig.validate();
        
        return st;
    }
    
    public synchronized void execute( final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        beginTask( monitor, "", 10 ); //$NON-NLS-1$
        
        try
        {
            final IFacetedProjectWorkingCopy fpjwc = getFacetedProjectWorkingCopy();
            final IProjectFacetVersion fv = getProjectFacetVersion();
            final LibrariesProvider provider = (LibrariesProvider) getLibrariesProvider();
            
            // Uninstall the libraries.
            
            final LibrariesProviderOperation librariesUninstallOp = provider.createOperation( LibrariesProviderActionType.UNINSTALL );
            
            final LibrariesProviderOperationConfig librariesUninstallOpConfig 
                = getLibrariesProviderOperationConfig();
            
            librariesUninstallOp.execute( fpjwc.getFacetedProject(), librariesUninstallOpConfig, submon( monitor, 9 ) );
            
            // Remove information about which libraries provider was used during the install.
            
            final LibrariesInstallRecord installRecord = new LibrariesInstallRecord( fpjwc.getProject() );
            
            installRecord.removeEntry( fv.getProjectFacet() );
            installRecord.save();
            
            worked( monitor, 1 );
        }
        finally
        {
            done( monitor );
        }
    }

    @Override
    public synchronized void dispose()
    {
        super.dispose();
        
        try
        {
            this.oldProviderUninstallOpConfig.dispose();
        }
        catch( Exception e )
        {
            log( e );
        }
    }

    private final void init()
    {
        final IFacetedProjectWorkingCopy fpjwc = getFacetedProjectWorkingCopy();
        final IProjectFacetVersion fv = getProjectFacetVersion();
        
        if( fpjwc == null || fv == null )
        {
            return; 
        }
        
        this.oldProvider = readProviderFromInstallRecord();
        
        final LibrariesProvider prov = (LibrariesProvider) this.oldProvider;
        
        this.oldProviderUninstallOpConfig = prov.createOperationConfig( LibrariesProviderActionType.UNINSTALL );
        this.oldProviderUninstallOpConfig.setProjectFacetVersion( fv );
        this.oldProviderUninstallOpConfig.setParent( this );

        this.oldProviderUninstallOpConfig.addListener
        (
            new IPropertyChangeListener()
            {
                public void propertyChanged( final String property,
                                             final Object oldValue,
                                             final Object newValue )
                {
                    notifyListeners( property, oldValue, newValue );
                }
            }
        );
    }
    
}
