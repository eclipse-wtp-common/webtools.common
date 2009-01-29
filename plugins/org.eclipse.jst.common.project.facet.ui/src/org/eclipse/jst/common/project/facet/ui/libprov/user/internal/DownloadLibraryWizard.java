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

package org.eclipse.jst.common.project.facet.ui.libprov.user.internal;

import static org.eclipse.jst.common.project.facet.ui.internal.FacetedProjectFrameworkJavaExtUiPlugin.log;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jst.common.project.facet.core.libprov.user.UserLibraryProviderInstallOperationConfig;
import org.eclipse.jst.common.project.facet.core.libprov.user.internal.DownloadableLibrariesExtensionPoint;
import org.eclipse.jst.common.project.facet.core.libprov.user.internal.DownloadableLibrary;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class DownloadLibraryWizard 

    extends Wizard 
    
{
    private List<DownloadableLibrary> libraries;
    private DownloadLibraryWizardMainPage mainPage;
    private DownloadLibraryWizardLicensePage licensePage;
    private String downloadedLibraryName;
    
    private DownloadLibraryWizard( final List<DownloadableLibrary> libraries ) 
    {
        this.libraries = libraries;
        
        setWindowTitle( "Download Library" ); //$NON-NLS-1$
        setNeedsProgressMonitor( true );
    }
    
    public static String open( final UserLibraryProviderInstallOperationConfig cfg )
    {
        final Shell shell = Display.getDefault().getActiveShell();
        final List<DownloadableLibrary> libraries = new ArrayList<DownloadableLibrary>();
        
        try
        {
            final IRunnableWithProgress operation = new IRunnableWithProgress()
            {
                public void run( IProgressMonitor monitor ) 
                
                    throws InvocationTargetException, InterruptedException
                    
                {
                    final List<DownloadableLibrary> libs 
                        = DownloadableLibrariesExtensionPoint.list( cfg, monitor );
                    
                    if( libs == null )
                    {
                        throw new InterruptedException();
                    }
                    else
                    {
                        libraries.addAll( libs );
                    }
                }
            };
            
            ( new ProgressMonitorDialog( shell ) ).run( true, true, operation );
        }
        catch( InvocationTargetException e )
        {
            log( e );
            return null;
        }
        catch( InterruptedException e )
        {
            return null;
        }
        
        final DownloadLibraryWizard wizard = new DownloadLibraryWizard( libraries );
        final WizardDialog dlg = new WizardDialog( Display.getDefault().getActiveShell(), wizard );
        
        if( dlg.open() == Window.OK )
        {
            return wizard.downloadedLibraryName;
        }
        else
        {
            return null;
        }
    }
    
    public DownloadLibraryWizardMainPage getMainPage()
    {
        return this.mainPage;
    }
    
    public DownloadLibraryWizardLicensePage getLicensePage()
    {
        return this.licensePage;
    }
    
    public DownloadableLibrary getSelectedLibrary()
    {
        return this.mainPage.getSelectedLibrary();
    }
    
    public String getLibraryName()
    {
        return this.mainPage.getLibraryName();
    }
    
    public String getDownloadDestination()
    {
        return this.mainPage.getDownloadDestination();
    }
    
    public void addPages() 
    {
        this.mainPage = new DownloadLibraryWizardMainPage( this, this.libraries );
        addPage( this.mainPage );
        
        this.licensePage = new DownloadLibraryWizardLicensePage();
        addPage( this.licensePage );
    }
    
    @Override
    public IWizardPage getNextPage( final IWizardPage page )
    {
        if( page == this.mainPage )
        {
            final DownloadableLibrary library = getSelectedLibrary();
            
            if( library != null && library.getLicenseUrl() != null )
            {
                return this.licensePage;
            }
        }

        return null;
    }

    public boolean performFinish() 
    {
        final DownloadableLibrary library = getSelectedLibrary();
        final String localLibraryName = getLibraryName();
        final String dest = getDownloadDestination();
        final File destFolder = new File( dest );
        
        try 
        {
            getContainer().run
            (
                true, true, 
                new IRunnableWithProgress() 
                {
                    public void run( final IProgressMonitor monitor ) 
                    
                        throws InvocationTargetException, InterruptedException
                        
                    {
                        try 
                        {
                            library.download( destFolder, localLibraryName, monitor );
                        } 
                        catch( CoreException e ) 
                        {
                            throw new InvocationTargetException( e );
                        } 
                        
                        DownloadLibraryWizard.this.downloadedLibraryName = localLibraryName;
                    }
                }
            );
        }
        catch( InvocationTargetException e ) 
        {
            final Throwable cause = getRootCause( e );
            MessageDialog.openError( getShell(), Resources.transferErrorDialogTitle, cause.getMessage() );
        }
        catch( InterruptedException e ) {} 
        
        return true;
    }
    
    private static Throwable getRootCause( final Throwable e )
    {
        Throwable cause = e.getCause();
        
        if( cause == null && e instanceof CoreException )
        {
            cause = ( (CoreException) e ).getStatus().getException();
        }
        
        if( cause != null )
        {
            return getRootCause( cause );
        }
        else
        {
            return e;
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String transferErrorDialogTitle;
    
        static
        {
            initializeMessages( DownloadLibraryWizard.class.getName(), 
                                Resources.class );
        }
    }
    
}
