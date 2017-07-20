/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gd;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdwhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.glmargins;
import static org.eclipse.wst.common.project.facet.ui.internal.util.SwtUtil.runOnDisplayThread;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.wst.common.project.facet.core.ActionConfig;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFrameworkException;
import org.eclipse.wst.common.project.facet.core.IActionConfig;
import org.eclipse.wst.common.project.facet.core.IActionDefinition;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.ui.IFacetWizardPage;
import org.eclipse.wst.common.project.facet.ui.ModifyFacetedProjectWizard;
import org.eclipse.wst.common.project.facet.ui.ProjectFacetsUiManager;
import org.eclipse.wst.common.project.facet.ui.internal.util.EnhancedHyperlink;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class FacetsPropertyPage 

	extends PropertyPage
	
{
    public static final String ID = "org.eclipse.wst.common.project.facet.ui.FacetsPropertyPage"; //$NON-NLS-1$
    
    private IProject project;
	private IFacetedProject fpj;
    private IFacetedProjectWorkingCopy fpjwc;
    private Composite topComposite;
    private Composite furtherConfigComposite;
    
    @Override
    public void createControl( final Composite parent )
    {
        super.createControl( parent );
        getDefaultsButton().setText( Resources.revertButtonLabel );
    }

    protected Control createContents( final Composite parent ) 
    {
        this.topComposite = new Composite( parent, SWT.NONE );

        this.topComposite.setLayoutData( gdfill() );
        this.topComposite.setLayout( glmargins( gl( 1 ), 0, 0, 0, 5 ) );
        
        this.topComposite.addDisposeListener
        (
            new DisposeListener()
            {
                public void widgetDisposed( final DisposeEvent e )
                {
                    handleDisposeEvent();
                }
            }
        );
        
        Dialog.applyDialogFont( parent );
        
        resetContents();
        
        return this.topComposite;
    }
    
    private void resetContents()
    {
        for( Control control : this.topComposite.getChildren() )
        {
            control.dispose();
        }
        
        this.project = getElement().getAdapter( IProject.class );
        
        try 
        {
            this.fpj = ProjectFacetsManager.create( this.project );
        }
        catch( CoreException e )
        {
            throw new RuntimeException( e );
        }
        
        if( this.fpj == null )
        {
            final StringBuilder buf = new StringBuilder();
            buf.append( "<form><p>" ); //$NON-NLS-1$
            buf.append( Resources.projectNotFacetedMessage );
            buf.append( "</p></form>" ); //$NON-NLS-1$

            final FormText messageControl = new FormText( this.topComposite, SWT.NONE );
            messageControl.setLayoutData( gdwhint( gdhfill(), 200 ) );
            messageControl.setText( buf.toString(), true, false );
            
            final Link convertLink = new Link( this.topComposite, SWT.NONE );
            convertLink.setLayoutData( gdhfill() );
            convertLink.setText( "<a>" + Resources.convertLink + "</a>" ); //$NON-NLS-1$ //$NON-NLS-2$
            
            convertLink.addSelectionListener
            (
                new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected( final SelectionEvent event )
                    {
                        handleConvertProjectAction();
                    }
                }
            );
        }
        else
        {
            this.fpjwc = SharedWorkingCopyManager.getWorkingCopy( this.fpj );
            
            final FacetsSelectionPanel facetsSelectionPanel 
                = new FacetsSelectionPanel( this.topComposite, this.fpjwc );
            
            facetsSelectionPanel.setLayoutData( gdfill() );
            
            this.fpjwc.addListener
            (
                new IFacetedProjectListener()
                {
                    public void handleEvent( final IFacetedProjectEvent event ) 
                    {
                        handleProjectModifiedEvent();
                    }
                },
                IFacetedProjectEvent.Type.PROJECT_MODIFIED
            );
            
            this.furtherConfigComposite = new Composite( this.topComposite, SWT.NONE );
            
            updateFurtherConfigHyperlink();
        }
    }
    
	@Override
	public boolean performOk() 
	{
	    if( this.fpjwc == null )
	    {
	        return true;
	    }
	    
	    for( IProjectFacetVersion fv : this.fpjwc.getFacetedProject().getProjectFacets() )
	    {
	        if( fv.getPluginId() == null || fv.getProjectFacet().getPluginId() == null )
	        {
	            final MessageDialog dialog 
	                = new MessageDialog( getShell(), Resources.warningDialogTitle, null,
	                                     Resources.modifyWithUnknownWarningMessage, MessageDialog.WARNING, 
	                                     new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 1 );
	            
	            if( dialog.open() != Window.OK )
	            {
	                return false;
	            }
	        }
	    }
	    
        final IWorkspaceRunnable wr = new IWorkspaceRunnable()
        {
            public void run( final IProgressMonitor monitor ) 
            
                throws CoreException
                
            {
            	FacetsPropertyPage.this.fpjwc.commitChanges( monitor );
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
            
            if( te instanceof CoreException )
            {
                IStatus st = ( (CoreException) te ).getStatus();
                final String msg = st.getMessage();

                if( ! ( te instanceof FacetedProjectFrameworkException ) ||
                    ! ( (FacetedProjectFrameworkException) te ).isExpected() )
                {
                    FacetUiPlugin.log( st );
                }
                
                final Throwable cause = st.getException();
                
                if( cause instanceof CoreException )
                {
                    st = ( (CoreException) cause ).getStatus();
                }

                ErrorDialog.openError( getShell(), Resources.errDlgTitle, msg, st );
            }
            else
            {
                throw new RuntimeException( te );
            }
        }
        finally
        {
            // Take care of the case where all changes could not be applied, such as if the user
            // rejects a validateEdit request. 
            
            try
            {
                this.fpjwc.revertChanges();
            }
            catch( Exception e )
            {
                FacetUiPlugin.log( e );
            }
        }
        
		return true;
	}
	
	@Override
	protected void performApply() 
	{
		performOk();
		updateFurtherConfigHyperlink();
	}

	@Override
	protected void performDefaults() 
	{
		this.fpjwc.revertChanges();
		super.performDefaults();
	}

	private void handleProjectModifiedEvent()
	{
        final Runnable runnable = new Runnable()
        {
            public void run()
            {
                updateFurtherConfigHyperlink();
            }
        };
        
        runOnDisplayThread( this.topComposite.getDisplay(), runnable );
	}
	
	private void updateFurtherConfigHyperlink()
	{
		for( Control child : this.furtherConfigComposite.getChildren() )
		{
			child.dispose();
		}

		final Set<IProjectFacetVersion> base 
			= this.fpjwc.getFacetedProject().getProjectFacets();
		
		final boolean basicValidationErrorsPresent 
		    = ( this.fpjwc.validate().getSeverity() == IStatus.ERROR );
		
		boolean actionConfigErrorsPresent = false;
		boolean configPagesAvailable = false;
		
		if( ! basicValidationErrorsPresent )
		{
			for( IFacetedProject.Action action : this.fpjwc.getProjectFacetActions() )
			{
			    final IProjectFacetVersion fv = action.getProjectFacetVersion();
                
				if( ! configPagesAvailable )
				{
		            try
		            {
		            	final IFacetedProject.Action.Type actionType = action.getType();
		    			final IActionDefinition actiondef = fv.getActionDefinition( base, actionType );
		                
		                if( ! ProjectFacetsUiManager.getWizardPages( actiondef.getId() ).isEmpty() )
		                {
		                	configPagesAvailable = true;
		                }
		            }
		            catch( CoreException e )
		            {
		                FacetUiPlugin.log( e );
		            }
				}
				
				if( ! actionConfigErrorsPresent )
				{
	                final Object config = action.getConfig();
	                
	                if( config != null )
	                {
	                    IActionConfig c = null;
	                    
	                    if( config instanceof IActionConfig )
	                    {
	                        c = (IActionConfig) config;
	                    }
	                    else
	                    {
	                        final IAdapterManager m = Platform.getAdapterManager();
	                        final String t = IActionConfig.class.getName();
	                        c = (IActionConfig) m.loadAdapter( config, t );
	                    }
	                    
	                    if( c != null )
	                    {
	                        final IStatus result = c.validate();
	                        
	                        if( result.getSeverity() == IStatus.ERROR )
	                        {
	                            traceActionConfigValidation( fv, result );
	                        	actionConfigErrorsPresent = true;
	                        }
	                    }
	                    
	                    ActionConfig c2 = null;
	                    
	                    if( config instanceof ActionConfig )
	                    {
	                        c2 = (ActionConfig) config;
	                    }
	                    else
	                    {
	                        final IAdapterManager m = Platform.getAdapterManager();
	                        final String t = ActionConfig.class.getName();
	                        c2 = (ActionConfig) m.loadAdapter( config, t );
	                    }
	                    
	                    if( c2 != null )
	                    {
	                        final IStatus result = c2.validate();
	                        
	                        if( result.getSeverity() == IStatus.ERROR )
	                        {
                                traceActionConfigValidation( fv, result );
	                        	actionConfigErrorsPresent = true;
	                        }
	                    }
	                }
				}
			}
		}

		if( configPagesAvailable )
		{
	        final ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();

	        final Color infoBackgroundColor
            	= this.topComposite.getDisplay().getSystemColor( SWT.COLOR_INFO_BACKGROUND );
	        
	        this.furtherConfigComposite.setLayoutData( gdhfill() );
	        this.furtherConfigComposite.setLayout( glmargins( gl( 1 ), 0, 2, 0, 0 ) );
	        
	        final Composite subComposite = new Composite( this.furtherConfigComposite, SWT.BORDER );
	        subComposite.setLayoutData( gdfill() );
	        subComposite.setLayout( glmargins( gl( 2 ), 5, 5 ) );
	        subComposite.setBackground( infoBackgroundColor );
	        
	        final Label image = new Label( subComposite, SWT.NONE );
	        image.setBackground( infoBackgroundColor );
	        
	        final String imageType 
	        	= ( actionConfigErrorsPresent ? ISharedImages.IMG_OBJS_ERROR_TSK : ISharedImages.IMG_OBJS_INFO_TSK );
	        
	        image.setImage( sharedImages.getImage( imageType ) );

	        final EnhancedHyperlink link = new EnhancedHyperlink( subComposite, SWT.NONE );
	        link.setBackground( infoBackgroundColor );
	
			if( actionConfigErrorsPresent )
			{
				link.setText( Resources.furtherConfigRequired );
			}
			else
			{
				link.setText( Resources.furtherConfigAvailable );
			}
			
			link.addHyperlinkListener
			(
				new HyperlinkAdapter()
				{
					public void linkActivated( final HyperlinkEvent event ) 
					{
						handleFurtherConfigHyperlinkEvent();
					}
				}
			);
		}
		else
		{
			this.furtherConfigComposite.setLayoutData( gdhhint( gd(), 0 ) );
		}
		
		this.topComposite.layout( true, true );
		
		setValid( ! basicValidationErrorsPresent && ! actionConfigErrorsPresent );
	}
	
	private void handleFurtherConfigHyperlinkEvent()
	{
		final IWizard wizard = new ModifyFacetedProjectWizard( this.fpjwc )
		{
			@Override
			public boolean getShowFacetsSelectionPage() 
			{
				return false;
			}

			@Override
			public boolean canFinish() 
			{
				return true;
			}

			@Override
			public boolean performFinish() 
			{
				for( IWizardPage page : getPages() )
				{
					final IFacetWizardPage facetPage = (IFacetWizardPage) page;
					facetPage.transferStateToConfig();
				}
				
				return true;
			}
		};
		
		final WizardDialog dialog = new WizardDialog( getShell(), wizard )
		{
			@Override
			protected void createButtonsForButtonBar( final Composite parent ) 
			{
				super.createButtonsForButtonBar( parent );
				
				getButton( IDialogConstants.FINISH_ID ).setText( IDialogConstants.OK_LABEL );
				getButton( IDialogConstants.CANCEL_ID ).dispose();
				( (GridLayout) parent.getLayout() ).numColumns--;
			}
		};
		
		dialog.open();
		updateFurtherConfigHyperlink();
	}
	
	private void handleConvertProjectAction()
	{
	    ConvertProjectToFacetedFormRunnable.runInProgressDialog( getShell(), this.project );
	    resetContents();
	}

    private void handleDisposeEvent()
    {
        if( this.fpj != null )
        {
            SharedWorkingCopyManager.releaseWorkingCopy( this.fpj );
        }
    }
    
    private void traceActionConfigValidation( final IProjectFacetVersion fv,
                                              final IStatus result )
    {
        if( FacetUiPlugin.isTracingPropPageActionConfigValidation() )
        {
            System.out.println( fv.getProjectFacet().getId() + " : " + fv.getVersionString() ); //$NON-NLS-1$
            System.out.println( result );
            
            final Throwable e = result.getException();
            
            if( e != null )
            {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter( sw );
                e.printStackTrace( pw );
                
                System.out.println( sw.getBuffer().toString() );
            }
        }
    }
    
    private static final class Resources 
    
    	extends NLS
    	
    {
        public static String revertButtonLabel;
        public static String furtherConfigAvailable;
        public static String furtherConfigRequired;
        public static String errDlgTitle;
        public static String warningDialogTitle;
        public static String modifyWithUnknownWarningMessage;
        public static String projectNotFacetedMessage;
        public static String convertLink;
        
        static
        {
            initializeMessages( FacetsPropertyPage.class.getName(), Resources.class );
        }
    }

}
