/******************************************************************************
 * Copyright (c) 2006 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
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
import org.eclipse.wst.common.project.facet.core.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.ui.AddRemoveFacetsWizard;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public class RuntimesPropertyPage extends PropertyPage 
{
    private IFacetedProject project;
    private IFacetedProjectListener projectListener;
    private ChangeTargetedRuntimesDataModel model;
    private RuntimesPanel panel;
    
    public void createControl( final Composite parent )
    {
        super.createControl( parent );
        this.getDefaultsButton().setText( Resources.restoreButtonLabel );
    }
    
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
            
            this.model = new ChangeTargetedRuntimesDataModel();
            
            this.model.setTargetedRuntimes( this.project.getTargetedRuntimes() );
            this.model.setPrimaryRuntime( this.project.getPrimaryRuntime() );
            
            this.model.addRuntimeFilter
            (
                new ChangeTargetedRuntimesDataModel.IRuntimeFilter()
                {
                    public boolean check( final IRuntime runtime )
                    {
                        final IFacetedProject fpj 
                            = RuntimesPropertyPage.this.project;
                        
                        for( Iterator itr = fpj.getProjectFacets().iterator();
                             itr.hasNext(); )
                        {
                            final IProjectFacetVersion fv
                                = (IProjectFacetVersion) itr.next();
                            
                            if( ! runtime.supports( fv ) )
                            {
                                return false;
                            }
                        }
                        
                        return true;
                    }
                }
            );
            
            this.projectListener = new IFacetedProjectListener()
            {
                public void projectChanged()
                {
                    handleProjectChangedEvent();
                }
            };
            
            this.project.addListener( this.projectListener );
            
            final Composite composite = new Composite( parent, SWT.NONE );
            composite.setLayoutData( gdfill() );
            
            final GridLayout layout = new GridLayout( 1, false );
            layout.marginHeight = 0;
            layout.marginWidth = 0;
            layout.marginBottom = 5;
            
            composite.setLayout( layout );
            
            this.panel = new RuntimesPanel( composite, SWT.NONE, this.model );
            this.panel.setLayoutData( gdfill() );

            final Label hint = new Label( composite, SWT.WRAP );
            hint.setText( Resources.hint );
            
            final GridData gd = gdhfill();
            gd.grabExcessHorizontalSpace = true;
            gd.widthHint = 300;
            gd.verticalIndent = 5;
            
            hint.setLayoutData( gd );
            
            final Hyperlink addRemoveLink = new Hyperlink( composite, SWT.NONE );
            addRemoveLink.setForeground( parent.getDisplay().getSystemColor( SWT.COLOR_DARK_BLUE ) );
            addRemoveLink.setUnderlined( true );
            addRemoveLink.setText( Resources.addRemoveLinkLabel );
            
            addRemoveLink.addHyperlinkListener
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
            
            return composite;
        }
        return null;
    }
    
    public boolean performOk() 
    {
        final Set targeted = this.model.getTargetedRuntimes();
        final IRuntime primary = this.model.getPrimaryRuntime();
        
        if( ! this.project.getTargetedRuntimes().equals( primary ) ||
            ! equals( this.project.getPrimaryRuntime(), primary ) )
        {
            final Runnable op = new Runnable()
            {
                public void run()
                {
                    try
                    {
                        final IFacetedProject fpj 
                            = RuntimesPropertyPage.this.project;
                        
                        fpj.setTargetedRuntimes( targeted, null );
                        fpj.setPrimaryRuntime( primary, null );
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
        }
        
        return true;
    }
    
    protected void performDefaults() 
    {
        super.performDefaults();
        
        this.model.setTargetedRuntimes( this.project.getTargetedRuntimes() );
        this.model.setPrimaryRuntime( this.project.getPrimaryRuntime() );
    }
    
    private void performAddRemoveFacets()
    {
        final IWizard wizard = new AddRemoveFacetsWizard( this.project );
        final WizardDialog dialog = new WizardDialog( getShell(), wizard );
        
        dialog.open();     
    }
    
    private void handleProjectChangedEvent()
    {
        this.model.refreshTargetableRuntimes();
        
        final Set targetedRuntimes = this.project.getTargetedRuntimes();
        this.model.setTargetedRuntimes( targetedRuntimes );
        
        final IRuntime primaryRuntime = this.project.getPrimaryRuntime();
        this.model.setPrimaryRuntime( primaryRuntime );
    }
    
    private void handleDisposeEvent()
    {
        this.project.removeListener( this.projectListener );
        this.model.dispose();
    }
    
    private static boolean equals( final IRuntime r1,
                                   final IRuntime r2 )
    {
        if( r1 == null && r2 == null )
        {
            return true;
        }
        else if( r1 == null || r2 == null )
        {
            return false;
        }
        else
        {
            return r1.equals( r2 );
        }
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
        public static String addRemoveLinkLabel;
        public static String restoreButtonLabel;
        
        static
        {
            initializeMessages( RuntimesPropertyPage.class.getName(), 
                                Resources.class );
        }
    }

}
