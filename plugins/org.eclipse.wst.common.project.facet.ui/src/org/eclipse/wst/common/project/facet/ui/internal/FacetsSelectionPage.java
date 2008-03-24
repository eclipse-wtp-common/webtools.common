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

import java.util.Set;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleEvent;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleListener;
import org.eclipse.wst.common.project.facet.ui.FacetUiHelpContextIds;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetsSelectionPage

    extends WizardPage

{
    public FacetsSelectionPanel panel;
    private IFacetedProjectWorkingCopy fpjwc;

    public FacetsSelectionPage( final Set<IProjectFacetVersion> base,
                                final IFacetedProjectWorkingCopy fpjwc )
    {
        super( "facets.selection.page" ); //$NON-NLS-1$

        setTitle( Resources.pageTitle );
        setDescription( Resources.pageDescription );
        setImageDescriptor( FacetedProjectFrameworkImages.BANNER_IMAGE.getImageDescriptor() );

        this.fpjwc = fpjwc;
    }
    
    public void createControl( final Composite parent )
    {
        this.panel = new FacetsSelectionPanel( parent, this.fpjwc );
        
        updatePageState();

        this.fpjwc.addListener
        (
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    updatePageState();
                }
            },
            IFacetedProjectEvent.Type.PROJECT_MODIFIED
        );
        
        final IRuntimeLifecycleListener runtimeLifecycleListener = new IRuntimeLifecycleListener()
        {
            public void handleEvent( final IRuntimeLifecycleEvent event )
            {
                updatePageState();
            }
        };
        
        RuntimeManager.addListener( runtimeLifecycleListener, 
                                    IRuntimeLifecycleEvent.Type.VALIDATION_STATUS_CHANGED );
        
        this.panel.addDisposeListener
        (
            new DisposeListener()
            {
                public void widgetDisposed( final DisposeEvent e )
                {
                    RuntimeManager.removeListener( runtimeLifecycleListener );
                }
            }
        );

        final IWorkbenchHelpSystem h = PlatformUI.getWorkbench().getHelpSystem();
        h.setHelp( this.panel, FacetUiHelpContextIds.FACETS_SELECTION_PAGE );
        
        setControl( this.panel );
    }

    private void updatePageState()
    {
        if( ! Thread.currentThread().equals( this.panel.getDisplay().getThread() ) )
        {
            final Runnable uiRunnable = new Runnable()
            {
                public void run()
                {
                    updatePageState();
                }
            };
            
            this.panel.getDisplay().syncExec( uiRunnable );
            return;
        }

        setPageComplete( this.panel.isSelectionValid() );

        if( getContainer().getCurrentPage() != null )
        {
            getContainer().updateButtons();
        }
    }

    public void setVisible( final boolean visible )
    {
        super.setVisible( visible );
        
        if( visible )
        {
            this.panel.setFocus();
        }
    }

    private static final class Resources
    
        extends NLS
        
    {
        public static String pageTitle;
        public static String pageDescription;
        
        static
        {
            initializeMessages( FacetsSelectionPage.class.getName(), 
                                Resources.class );
        }
    }

}

