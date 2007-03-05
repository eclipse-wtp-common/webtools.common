/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.common.project.facet.core.IActionConfig;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.ui.FacetUiHelpContextIds;
import org.eclipse.wst.common.project.facet.ui.IWizardContext;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetsSelectionPage

    extends WizardPage

{
    private static final String IMG_PATH_WIZBAN
        = "images/facets-page-wizban.png"; //$NON-NLS-1$
    
    private IWizardContext context;
    private Set<IProjectFacetVersion> base;
    private IPreset initialPreset;
    private Set<IProjectFacetVersion> initialSelection;
    private final Set<IProjectFacet> fixed;
    public FacetsSelectionPanel panel;
    private List<Listener> listeners;
    private List<Combo> initialSyncWithPresetsCombos;
    private IRuntime initialSetDefaultFacetsForRuntime;
    private ModifyFacetedProjectDataModel model;

    public FacetsSelectionPage( final IWizardContext context,
                                final Set<IProjectFacetVersion> base,
                                final ModifyFacetedProjectDataModel model )
    {
        super( "facets.selection.page" ); //$NON-NLS-1$

        setTitle( Resources.pageTitle );
        setDescription( Resources.pageDescription );
        setImageDescriptor( FacetUiPlugin.getImageDescriptor( IMG_PATH_WIZBAN ) );

        this.context = context;
        this.base = base;
        this.model = model;
        this.initialPreset = null;
        this.initialSelection = null;
        this.fixed = new HashSet<IProjectFacet>();
        this.listeners = new ArrayList<Listener>();
        this.initialSyncWithPresetsCombos = new ArrayList<Combo>();
        this.initialSetDefaultFacetsForRuntime = null;
    }
    
    public void setInitialPreset( final IPreset preset )
    {
        this.initialPreset = preset;
    }
    
    public void setInitialSelection( final Set<IProjectFacetVersion> sel )
    {
        this.initialSelection = sel;
    }

    public void setFixedProjectFacets( final Set<IProjectFacet> fixed )
    {
        this.fixed.clear();
        this.fixed.addAll( fixed );
    }

    public Set<Action> getActions()
    {
        return this.panel.getActions();
    }

    public Set<IProjectFacetVersion> getSelectedProjectFacets()
    {
        return this.panel.getSelectedProjectFacets();
    }

    public void addSelectedFacetsChangedListener( final Listener listener )
    {
        synchronized( this.listeners )
        {
            this.listeners.add( listener );
        }
    }

    public void removeSelectedFacetsChangedListener( final Listener listener )
    {
        synchronized( this.listeners )
        {
            this.listeners.remove( listener );
        }
    }
    
    public void setDefaultFacetsForRuntime( final IRuntime runtime )
    {
        if( this.panel == null )
        {
            this.initialSetDefaultFacetsForRuntime = runtime;
        }
        else
        {
            this.panel.setDefaultFacetsForRuntime( runtime );
        }
    }
    
    public void syncWithPresetsModel( final Combo combo )
    {
        if( this.initialSyncWithPresetsCombos == null )
        {
            this.panel.syncWithPresetsModel( combo );
        }
        else
        {
            this.initialSyncWithPresetsCombos.add( combo );
        }
    }

    public void createControl( final Composite parent )
    {
        this.panel 
            = new FacetsSelectionPanel( parent, SWT.NONE, this.context, 
                                        this.base, this.model );
        
        this.panel.setFixedProjectFacets( this.fixed );

        if( this.initialPreset != null )
        {
            this.panel.selectPreset( this.initialPreset );
        }
        
        if( this.initialSelection != null )
        {
            this.panel.setSelectedProjectFacets( this.initialSelection );
        }
        
        if( this.initialSetDefaultFacetsForRuntime != null )
        {
            this.panel.setDefaultFacetsForRuntime( this.initialSetDefaultFacetsForRuntime );
        }
        
        setPageComplete( this.panel.isSelectionValid() );

        this.panel.addProjectFacetsListener
        (
            new Listener()
            {
                public void handleEvent( final Event event )
                {
                    handleSelectedFacetsChangedEvent( event );
                }
            }
        );

        for( Combo combo : this.initialSyncWithPresetsCombos )
        {
            this.panel.syncWithPresetsModel( combo );
        }
        
        this.initialSyncWithPresetsCombos = null;
        
        final IWorkbenchHelpSystem h = PlatformUI.getWorkbench().getHelpSystem();
        h.setHelp( this.panel, FacetUiHelpContextIds.FACETS_SELECTION_PAGE );
        
        setControl( this.panel );
    }

    private void handleSelectedFacetsChangedEvent( final Event event )
    {
        setPageComplete( this.panel.isSelectionValid() );

        final List<Listener> copyOfListenersList = new ArrayList<Listener>();
        
        synchronized( this.listeners )
        {
            copyOfListenersList.addAll( this.listeners );
        }

        for( Listener listener : copyOfListenersList )
        {
            try
            {
                listener.handleEvent( event );
            }
            catch( Exception e )
            {
                FacetUiPlugin.log( e );
            }
        }
        
        if( getContainer().getCurrentPage() != null )
        {
            getContainer().updateButtons();
        }
    }

    public void setVisible( final boolean visible )
    {
        if( visible )
        {
            for( Action action : this.panel.getActions() )
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
                        final IAdapterManager m 
                            = Platform.getAdapterManager();
                        
                        final String t
                            = IActionConfig.class.getName();
                        
                        c = (IActionConfig) m.loadAdapter( config, t );
                    }
                    
                    if( c != null )
                    {
                        c.setProjectName( this.context.getProjectName() );
                    }
                }
            }
        }
        
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

