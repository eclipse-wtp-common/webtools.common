/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.ui.libprov;

import static org.eclipse.jst.common.project.facet.ui.libprov.LibraryProviderFrameworkUi.createInstallLibraryPanel;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jst.common.project.facet.core.libprov.IPropertyChangeListener;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryInstallDelegate;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.ui.internal.SharedWorkingCopyManager;

/**
 * Base implementation that can be used by those wishing to create a property page for
 * a facet where the associated libraries can be changed after the facet has been installed.
 * Other content can be added to the page by overriding the createPageContents
 * method.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public abstract class LibraryFacetPropertyPage 

    extends PropertyPage
    
{
    private IProject project;
    private IFacetedProject facetedProject;
    private IFacetedProjectWorkingCopy facetedProjectWorkingCopy;
    private IFacetedProjectListener facetedProjectListener;
    private Composite rootComposite;
    private LibraryInstallDelegate libraryInstallDelegate;
    
    public abstract IProjectFacetVersion getProjectFacetVersion();
    
    /**
     * Returns the project that this property page belongs to.
     * 
     * @return the project that this property page belongs to
     */
    
    protected final IProject getProject()
    {
        return this.project;
    }
    
    /**
     * Returns the faceted project that this property page belongs to.
     * 
     * @return the faceted project that this property page belongs to
     */
    
    protected final IFacetedProject getFacetedProject()
    {
        return this.facetedProject;
    }
    
    /**
     * Returns the library install delegate that is controlling library configuration
     * for this facet.
     * 
     * @return the library install delegate that is controlling library configuration
     *   for this facet
     */
    
    protected final LibraryInstallDelegate getLibraryInstallDelegate()
    {
        return this.libraryInstallDelegate;
    }

    /**
     * Constructs a new library install delegate. This method can be overridden to adjust
     * the construction of the library install delegate as necessary. This is necessary, for
     * instance, in order to declare custom enablement expression evaluation context variables.
     *
     * @param project the faceted project
     * @param fv the project facet version
     * @return the constructed library install delegate
     */
    
    protected LibraryInstallDelegate createLibraryInstallDelegate( final IFacetedProject project,
                                                                   final IProjectFacetVersion fv )
    {
        return new LibraryInstallDelegate( project, fv );
    }
    
    @Override
    public final void createControl( final Composite parent )
    {
        super.createControl( parent );
        
        final Button revertButton = getDefaultsButton();
        
        revertButton.setText( Resources.revertButton );

        final GridData gd = new GridData( GridData.HORIZONTAL_ALIGN_FILL );
        final Point minButtonSize = revertButton.computeSize( SWT.DEFAULT, SWT.DEFAULT, true );
        gd.widthHint = Math.max( convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH), minButtonSize.x );
        revertButton.setLayoutData( gd );
        
        updateButtons();
        
        getShell().layout( true, true );
    }
    
    protected final Control createContents( final Composite parent ) 
    {
        final IAdaptable element = getElement();
        
        if( element instanceof IProject )
        {
            this.project = (IProject) element;
        }
        else
        {
            this.project = (IProject) Platform.getAdapterManager().loadAdapter( element, IProject.class.getName() );
        }
        
        if( this.project != null )
        {
            try
            {
                this.facetedProject = ProjectFacetsManager.create( this.project );
            }
            catch( CoreException e ) 
            {
                e.printStackTrace();
            } 
        }
        
        Dialog.applyDialogFont( parent );

        this.rootComposite = new Composite( parent, SWT.NONE );
        
        if( this.facetedProject != null )
        {
            this.facetedProjectWorkingCopy = SharedWorkingCopyManager.getWorkingCopy( this.facetedProject );

            this.facetedProjectListener = new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    updateContents();
                }
            };
            
            this.facetedProject.addListener( this.facetedProjectListener, IFacetedProjectEvent.Type.PROJECT_MODIFIED );
            this.facetedProjectWorkingCopy.addListener( this.facetedProjectListener, IFacetedProjectEvent.Type.PROJECT_MODIFIED );
        }
        
        updateContents();
        
        return this.rootComposite;
    }
    
    /**
     * The method that creates the actual interesting page content. It can be overridden
     * as necessary to expand the scope of information managed by the page. 
     * 
     * @param parent the parent composite
     * @return the create control with all the page contents
     */
    
    protected Control createPageContents( final Composite parent )
    {
        return createInstallLibraryPanel( parent, this.libraryInstallDelegate );
    }
    
    private void updateContents()
    {
        if( this.rootComposite.getDisplay().getThread() != Thread.currentThread() )
        {
            this.rootComposite.getDisplay().asyncExec
            (
                new Runnable()
                {
                    public void run()
                    {
                        updateContents();
                    }
                }
            );
            
            return;
        }
        
        final IProjectFacetVersion fv = getProjectFacetVersion();
        
        String errorMessage = null;
        
        if( this.facetedProject == null )
        {
            errorMessage = Resources.invalidContextMessage;
        }
        else if( fv == null || ! this.facetedProject.hasProjectFacet( fv ) )
        {
            errorMessage = Resources.facetNotPresetInProjectMessage;
        }
        else if( this.facetedProjectWorkingCopy.isDirty() )
        {
            errorMessage = Resources.dirtyWorkingCopyMessage;
        }
        else if( this.libraryInstallDelegate == null )
        {
            for( Control control : this.rootComposite.getChildren() )
            {
                control.dispose();
            }
        
            final IPropertyChangeListener delegateListener = new IPropertyChangeListener()
            {
                public void propertyChanged( final String property,
                                             final Object oldValue,
                                             final Object newValue )
                {
                    updateValidation();
                }
            };

            this.libraryInstallDelegate = createLibraryInstallDelegate( this.facetedProject, fv );
            this.libraryInstallDelegate.addListener( delegateListener );
            
            this.rootComposite.setLayout( gl( 1, 0, 0 ) );
            
            final Control contents = createPageContents( this.rootComposite );
            contents.setLayoutData( gdhfill() );
        }
        
        if( errorMessage != null )
        {
            for( Control control : this.rootComposite.getChildren() )
            {
                control.dispose();
            }
            
            if( this.libraryInstallDelegate != null )
            {
            	this.libraryInstallDelegate.dispose();
            }
            
            this.libraryInstallDelegate = null;
            
            this.rootComposite.setLayout( gl( 1, 0, 0 ) );
            
            final Text label = new Text( this.rootComposite, SWT.WRAP | SWT.READ_ONLY );
            label.setText( errorMessage );
            label.setLayoutData( gdhfill() );
        }
        
        updateValidation();
        updateButtons();
        
        getShell().layout( true, true );
    }
    
    private void updateButtons()
    {
        boolean enableApply = isValid();
        boolean enableRevert = true;
        
        if( this.libraryInstallDelegate == null )
        {
            enableApply = false;
            enableRevert = false;
        }
        
        final Button applyButton = getApplyButton();
        
        if( applyButton != null )
        {
            applyButton.setEnabled( enableApply );
        }
        
        final Button revertButton = getDefaultsButton();
        
        if( revertButton != null )
        {
            revertButton.setEnabled( enableRevert );
        }
    }
    
    protected final void updateValidation()
    {
        if( this.rootComposite.isDisposed() )
        {
            return;
        }
        
        if( this.rootComposite.getDisplay().getThread() != Thread.currentThread() )
        {
            this.rootComposite.getDisplay().asyncExec
            (
                new Runnable()
                {
                    public void run()
                    {
                        updateValidation();
                    }
                }
            );
            
            return;
        }
        
        final IStatus st = performValidation();
        final int severity = st.getSeverity();
        
        if( severity == IStatus.ERROR )
        {
            setMessage( st.getMessage(), ERROR );
            setValid( false );
        }
        else
        {
            if( severity == IStatus.WARNING )
            {
                setMessage( st.getMessage(), WARNING );
            }
            else if( severity == IStatus.INFO )
            {
                setMessage( st.getMessage(), INFORMATION );
            }
            else
            {
                setMessage( null );
            }
            
            setValid( true );
        }
    }
    
    /**
     * The method that performs validation of controls displayed on the page. It can be
     * overridden as necessary to incorporate validation of additional controls. The
     * subclass can call updateValidation() method to refresh page validation.
     * 
     * @return the validation result
     */
    
    protected IStatus performValidation()
    {
        if( this.libraryInstallDelegate != null )
        {
            return this.libraryInstallDelegate.validate();
        }
        
        return Status.OK_STATUS;
    }

    @Override
    public boolean performOk() 
    {
        final IWorkspaceRunnable wr = new IWorkspaceRunnable()
        {
            public void run( final IProgressMonitor monitor ) 
            
                throws CoreException
                
            {
                performOkInternal();
            }
        };
        
        final IRunnableWithProgress op = new IRunnableWithProgress()
        {
            public void run( final IProgressMonitor monitor ) 
            
                throws InvocationTargetException, InterruptedException
                
            {
                try
                {
                    final IWorkspace ws = ResourcesPlugin.getWorkspace();
                    ws.run( wr, ws.getRoot(), IWorkspace.AVOID_UPDATE, monitor );
                }
                catch( CoreException e )
                {
                    throw new InvocationTargetException( e );
                }
            }
        };

        try 
        {
            new ProgressMonitorDialog( getShell() ).run( true, false, op );
        }
        catch( InterruptedException e ) 
        {
            return false;
        } 
        catch( InvocationTargetException e ) 
        {
            final Throwable te = e.getTargetException();
            throw new RuntimeException( te );
        }
        
        return true;
    }
    
    private void performOkInternal()
    
        throws CoreException
        
    {
        if( this.libraryInstallDelegate != null )
        {
            this.libraryInstallDelegate.execute( null );
            this.libraryInstallDelegate.reset();
        }
    }

    @Override
    protected void performApply() 
    {
        performOk();
    }

    @Override
    protected void performDefaults()
    {
        if( this.libraryInstallDelegate != null )
        {
            this.libraryInstallDelegate.reset();
        }
    }

    @Override
    public void dispose()
    {
        super.dispose();
        
        if( this.libraryInstallDelegate != null )
        {
        	this.libraryInstallDelegate.dispose();
        }
        
        if( this.facetedProject != null )
        {
            this.facetedProject.removeListener( this.facetedProjectListener );
            SharedWorkingCopyManager.releaseWorkingCopy( this.facetedProject );
        }
    }

    private static final class Resources
    
        extends NLS
        
    {
        public static String revertButton;
        public static String invalidContextMessage;
        public static String facetNotPresetInProjectMessage;
        public static String dirtyWorkingCopyMessage;
        
        static
        {
            initializeMessages( LibraryFacetPropertyPage.class.getName(), 
                                Resources.class );
        }
    }
}
