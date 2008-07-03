/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
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
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.wst.common.project.facet.core.ActionConfig;
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
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetsPropertyPage 

	extends PropertyPage
	
{
	private IFacetedProject project;
    private IFacetedProjectWorkingCopy fpjwc;
    private Composite topComposite;
    private Composite furtherConfigComposite;
    
    public void createControl( final Composite parent )
    {
        super.createControl( parent );
        getDefaultsButton().setText( Resources.revertButtonLabel );
    }
    
    protected Control createContents(Composite parent) 
    {
        final IAdaptable element = getElement();

        if( ! ( element instanceof IProject ) )
        {
        	return null;
        }
        
        final IProject project = (IProject) element;
        
        try 
        {
            this.project = ProjectFacetsManager.create( project );
        }
        catch( CoreException e )
        {
            return null;
        }
        
        this.topComposite = new Composite( parent, SWT.NONE );

        this.topComposite.setLayoutData( gdfill() );
        this.topComposite.setLayout( glmargins( gl( 1 ), 0, 0, 0, 5 ) );

        this.fpjwc = SharedWorkingCopyManager.getWorkingCopy( this.project );
        
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
        
        return this.topComposite;
    }
    
	@Override
	public boolean performOk() 
	{
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

        boolean failed = false;
        
        try 
        {
        	new ProgressMonitorDialog( getShell() ).run( true, false, op );
        }
        catch( InterruptedException e ) 
        {
            failed = true;
            return false;
        } 
        catch( InvocationTargetException e ) 
        {
            failed = true;
            
            final Throwable te = e.getTargetException();
            
            if( te instanceof CoreException )
            {
                final IStatus st = ( (CoreException) te ).getStatus();
                
                ErrorDialog.openError( getShell(), Resources.errDlgTitle,
                                       st.getMessage(), st );
                
                FacetUiPlugin.log( st );
            }
            else
            {
                throw new RuntimeException( te );
            }
        }
        finally
        {
            if( failed )
            {
                try
                {
                    this.fpjwc.revertChanges();
                }
                catch( Exception e )
                {
                    FacetUiPlugin.log( e );
                }
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

	@Override
	public boolean isValid() 
	{
		return this.fpjwc.validate().getSeverity() != IStatus.ERROR;
	}
	
	private void handleProjectModifiedEvent()
	{
        final Runnable runnable = new Runnable()
        {
            public void run()
            {
                updateApplyButton();
                getContainer().updateButtons();
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
		
		boolean errors = false;
		boolean configPagesAvailable = false;
		
		if( this.fpjwc.validate().getSeverity() != IStatus.ERROR )
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
				
				if( ! errors )
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
	                        	errors = true;
	                        }
	                    }
	                    
	                    ActionConfig c2 = null;
	                    
	                    if( config instanceof ActionConfig )
	                    {
	                        c2 = (ActionConfig) config;
	                    }
	                    else if( config != null )
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
	                        	errors = true;
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
	        	= ( errors ? ISharedImages.IMG_OBJS_ERROR_TSK : ISharedImages.IMG_OBJS_INFO_TSK );
	        
	        image.setImage( sharedImages.getImage( imageType ) );

	        final EnhancedHyperlink link = new EnhancedHyperlink( subComposite, SWT.NONE );
	        link.setBackground( infoBackgroundColor );
	
			if( errors )
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

    private void handleDisposeEvent()
    {
        SharedWorkingCopyManager.releaseWorkingCopy( this.project );
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
        
        static
        {
            initializeMessages( FacetsPropertyPage.class.getName(), 
                                Resources.class );
        }
    }

}
