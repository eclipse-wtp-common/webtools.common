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
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.common.project.facet.core.libprov.internal.LibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.internal.LibraryProviderFrameworkImpl;
import org.eclipse.jst.common.project.facet.core.libprov.internal.PropertiesHost;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectBase;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * Used for removing a library installed via the Library Provider Framework.
 * Instance of this class would typically be embedded in facet uninstall action config objects 
 * and then executed during the execution of those actions. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public final class LibraryUninstallDelegate

    extends PropertiesHost
    
{
    private final IFacetedProjectBase fproj;
    private IProjectFacetVersion fv;
    private ILibraryProvider oldProvider = null;
    private LibraryProviderOperationConfig oldProviderUninstallOpConfig = null;
    
    /**
     * Constructs a new library uninstall delegate. 
     * 
     * @param fproj the faceted project (or a working copy)
     * @param fv the project facet that installed the libraries
     */
    
    public LibraryUninstallDelegate( final IFacetedProjectBase fproj,
                                     final IProjectFacetVersion fv )
    {
        this.fproj = fproj;
        this.fv = fv;
        
        this.oldProvider = LibraryProviderFramework.getCurrentProvider( fproj.getProject(), fv.getProjectFacet() );
        
        if( this.oldProvider == null )
        {
            this.oldProviderUninstallOpConfig = null;
        }
        else
        {
            final LibraryProvider prov = (LibraryProvider) this.oldProvider;
            
            this.oldProviderUninstallOpConfig 
                = prov.createOperationConfig( fproj, fv, LibraryProviderActionType.UNINSTALL );
        
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
    
    /**
     * Returns the faceted project that this uninstall delegate was configured to use. Can be a working
     * copy.
     * 
     * @return the faceted project that this uninstall delegate was configured to use
     */
    
    public IFacetedProjectBase getFacetedProject()
    {
        return this.fproj;
    }
    
    /**
     * Returns the project facet that installed the libraries.
     * 
     * @return the project facet that installed the libraries
     */
    
    public IProjectFacet getProjectFacet()
    {
        return this.fv.getProjectFacet();
    }
    
    /**
     * Returns the project facet version that installed the libraries.
     * 
     * @return the project facet version that installed the libraries
     */

    public IProjectFacetVersion getProjectFacetVersion()
    {
        return this.fv;
    }
    
    /**
     * Returns the library provider that the system determine is currently installed for
     * the specified facet.
     * 
     * @return the library provider that is currently installed
     */
    
    public ILibraryProvider getLibraryProvider()
    {
        return this.oldProvider;
    }
    
    /**
     * Returns the uninstall operation config for the currently installed library.
     * 
     * @return the uninstall operation config for the currently installed library
     */
    
    public LibraryProviderOperationConfig getUninstallOperationConfig()
    {
        return this.oldProviderUninstallOpConfig;
    }
    
    /**
     * Checks the validity of the library uninstall configuration. 
     * 
     * @return a status object describing configuration problems, if any
     */
    
    public IStatus validate()
    {
        IStatus st = Status.OK_STATUS;
        
        st = this.oldProviderUninstallOpConfig.validate();
        
        return st;
    }
    
    /**
     * Executes the library uninstall operation.
     * 
     * @param monitor the progress monitor for reporting status and handling cancellation requests
     * @throws CoreException if failed for some reason while executing the uninstall operation
     */
    
    public void execute( final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        beginTask( monitor, "", 10 ); //$NON-NLS-1$
        
        try
        {
            final IFacetedProjectBase fproj = getFacetedProject();
            final IProjectFacetVersion fv = getProjectFacetVersion();
            final LibraryProvider provider = (LibraryProvider) getLibraryProvider();
            
            // Uninstall the library.
            
            final LibraryProviderOperation libraryUninstallOp = provider.createOperation( LibraryProviderActionType.UNINSTALL );
            
            final LibraryProviderOperationConfig libraryUninstallOpConfig 
                = getUninstallOperationConfig();
            
            libraryUninstallOp.execute( libraryUninstallOpConfig, submon( monitor, 9 ) );
            
            // Remove information about which library provider was used during the install.
            
            LibraryProviderFrameworkImpl.get().setCurrentProvider( fproj.getProject(), fv.getProjectFacet(), null );
            
            worked( monitor, 1 );
        }
        finally
        {
            done( monitor );
        }
    }

    /**
     * Cleans up allocated resources. Client code that instantiates this class is responsible that the
     * instance is properly disposed by calling the dispose method.
     */
    
    public void dispose()
    {
        try
        {
            this.oldProviderUninstallOpConfig.dispose();
        }
        catch( Exception e )
        {
            log( e );
        }
    }
    
}
