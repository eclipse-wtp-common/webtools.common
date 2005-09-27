/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetsSelectionPage

    extends WizardPage

{
    private IPreset initialPreset;
    private Set initialSelection;
    private final Set fixed;
    private FacetsSelectionPanel.IFilter[] filters;
    private IRuntime runtime;
    private FacetsSelectionPanel panel;
    private ArrayList listeners;

    public FacetsSelectionPage()
    {
        super( "facets.selection.page" );

        setTitle( "Select Project Facets" );
        setDescription( "Select facets for this project." );

        this.initialPreset = null;
        this.initialSelection = null;
        this.fixed = new HashSet();
        this.filters = new FacetsSelectionPanel.IFilter[ 0 ];
        this.runtime = null;
        this.listeners = new ArrayList();
    }

    public void setInitialPreset( final IPreset preset )
    {
        this.initialPreset = preset;
    }
    
    public void setInitialSelection( final Set sel )
    {
        this.initialSelection = sel;
    }

    public void setFixedProjectFacets( final Set fixed )
    {
        this.fixed.clear();
        this.fixed.addAll( fixed );
    }

    public void setFilters( final FacetsSelectionPanel.IFilter[] filters )
    {
        this.filters = filters;
    }
    
    public void setRuntime( final IRuntime runtime )
    {
        this.runtime = runtime;
    }
    
    public Set getActions()
    {
        return this.panel.getActions();
    }

    public Set getSelectedProjectFacets()
    {
        return this.panel.getSelectedProjectFacets();
    }

    public void addSelectedFacetsChangedListener( final Listener listener )
    {
        this.listeners.add( listener );
    }

    public void removeSelectedFacetsChangedListener( final Listener listener )
    {
        this.listeners.remove( listener );
    }
    
    public IRuntime getSelectedRuntime()
    {
        return this.panel.getSelectedRuntime();
    }

    public void createControl( final Composite parent )
    {
        this.panel 
            = new FacetsSelectionPanel( parent, SWT.NONE, this.runtime );

        this.panel.setFixedProjectFacets( this.fixed );
        
        if( this.initialPreset != null )
        {
            this.panel.selectPreset( this.initialPreset );
        }
        
        if( this.initialSelection != null )
        {
            this.panel.setSelectedProjectFacets( this.initialSelection );
        }

        for( int i = 0; i < this.filters.length; i++ )
        {
            this.panel.addFilter( this.filters[ i ] );
        }
        
        this.panel.addSelectionChangedListener
        (
            new ISelectionChangedListener()
            {
                public void selectionChanged( final SelectionChangedEvent e )
                {
                    handleSelectionChangedEvent( e );
                }
            }
        );

        this.panel.addListener
        (
            new Listener()
            {
                public void handleEvent( final Event event )
                {
                    handleSelectedFacetsChangedEvent( event );
                }
            }
        );

        setControl( this.panel );
    }

    private void handleSelectionChangedEvent( final SelectionChangedEvent e )
    {
        final IStructuredSelection ss
            = (IStructuredSelection) e.getSelection();

        final Object sel = ss.getFirstElement();

        if( sel != null )
        {
            final String desc;

            if( sel instanceof IProjectFacet )
            {
                desc = ( (IProjectFacet) sel ).getDescription();
            }
            else
            {
                desc = ( (ICategory) sel ).getDescription();
            }

            setDescription( desc );
        }
    }

    private void handleSelectedFacetsChangedEvent( final Event event )
    {
        for( int i = 0, n = this.listeners.size(); i < n; i++ )
        {
            ( (Listener) this.listeners.get( i ) ).handleEvent( event );
        }
        
        final boolean valid
            = FacetsSelectionPage.this.panel.isSelectionValid();

        setPageComplete( valid );
    }

}

