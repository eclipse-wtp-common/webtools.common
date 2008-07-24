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

package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.ui.ModifyFacetedProjectWizard;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class RuntimesPropertyPage extends PropertyPage 
{
    private IFacetedProject project;
    private IFacetedProjectListener projectListener;
    private IFacetedProjectWorkingCopy fpjwc;
    private RuntimesPanel panel;
    
    protected Control createContents( final Composite parent ) 
    {
        final IAdaptable element = getElement();

        if( element instanceof IProject )
        {
            final IProject project = (IProject) element;
            
            try 
            {
                this.project = ProjectFacetsManager.create( project );
            }
            catch( CoreException e ) {}
            
            if( this.project == null )
            {
                return null;
            }
            
            this.fpjwc = SharedWorkingCopyManager.getWorkingCopy( this.project );
            
            this.projectListener = new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    handleProjectChangedEvent();
                }
            };
            
            this.project.addListener( this.projectListener, 
                                      IFacetedProjectEvent.Type.PROJECT_MODIFIED );
            
            final Composite composite = new Composite( parent, SWT.NONE );
            composite.setLayoutData( gdfill() );
            
            final GridLayout layout = new GridLayout( 1, false );
            layout.marginHeight = 0;
            layout.marginWidth = 0;
            layout.marginBottom = 5;
            
            composite.setLayout( layout );
            
            this.panel = new RuntimesPanel( composite, this.fpjwc );
            this.panel.setLayoutData( gdfill() );

            final Label hint = new Label( composite, SWT.WRAP );
            hint.setText( Resources.hint );
            
            final GridData gd = gdhfill();
            gd.grabExcessHorizontalSpace = true;
            gd.widthHint = 300;
            gd.verticalIndent = 5;
            
            hint.setLayoutData( gd );
            
            final Hyperlink uninstallFacetsLink = new Hyperlink( composite, SWT.NONE );
            uninstallFacetsLink.setForeground( parent.getDisplay().getSystemColor( SWT.COLOR_DARK_BLUE ) );
            uninstallFacetsLink.setUnderlined( true );
            uninstallFacetsLink.setText( Resources.uninstallFacetsLinkLabel );
            
            uninstallFacetsLink.addHyperlinkListener
            (
                new HyperlinkAdapter() 
                {
                    public void linkActivated( final HyperlinkEvent evt ) 
                    {
                        performAddRemoveFacets();
                    }
                }
            );
            
            composite.addDisposeListener
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
            
            return composite;
        }
        return null;
    }
    
    public boolean performOk() 
    {
        final Runnable op = new Runnable()
        {
            public void run()
            {
                try
                {
                    RuntimesPropertyPage.this.fpjwc.commitChanges( null );
                }
                catch( CoreException e )
                {
                    final IStatus st = e.getStatus();
                    
                    ErrorDialog.openError( getShell(), Resources.errDlgTitle,
                                           st.getMessage(), st );
                    
                    FacetUiPlugin.log( st );
                }
            }
        };
        
        BusyIndicator.showWhile( null, op );
        
        return true;
    }
    
    protected void performDefaults() 
    {
        super.performDefaults();
        
        this.fpjwc.setTargetedRuntimes( this.project.getTargetedRuntimes() );
        this.fpjwc.setPrimaryRuntime( this.project.getPrimaryRuntime() );
    }
    
    private void performAddRemoveFacets()
    {
        final IWizard wizard = new ModifyFacetedProjectWizard( this.project );
        final WizardDialog dialog = new WizardDialog( getShell(), wizard );
        
        dialog.open();     
    }
    
    private void handleProjectChangedEvent()
    {
        this.fpjwc.refreshTargetableRuntimes();
        
        final Set<IRuntime> targetedRuntimes = this.project.getTargetedRuntimes();
        this.fpjwc.setTargetedRuntimes( targetedRuntimes );
        
        final IRuntime primaryRuntime = this.project.getPrimaryRuntime();
        this.fpjwc.setPrimaryRuntime( primaryRuntime );
    }
    
    private void handleDisposeEvent()
    {
        this.project.removeListener( this.projectListener );
        SharedWorkingCopyManager.releaseWorkingCopy( this.project );
    }
    
    private static GridData gdfill()
    {
        return new GridData( SWT.FILL, SWT.FILL, true, true );
    }

    private static GridData gdhfill()
    {
        return new GridData( GridData.FILL_HORIZONTAL );
    }
    
    
    private static final class Resources extends NLS
    {
        public static String errDlgTitle;
        public static String hint;
        public static String uninstallFacetsLinkLabel;
        
        static
        {
            initializeMessages( RuntimesPropertyPage.class.getName(), 
                                Resources.class );
        }
    }

}
