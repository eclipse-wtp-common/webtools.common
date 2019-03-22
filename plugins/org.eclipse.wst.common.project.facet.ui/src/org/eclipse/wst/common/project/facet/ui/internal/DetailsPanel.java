/******************************************************************************
 * Copyright (c) 2010, 2011 Oracle and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Roberto Sanchez Herrera - [348784] Remove IFacetedProjectListener on dispose
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import static org.eclipse.jface.resource.JFaceResources.getDefaultFont;
import static org.eclipse.jface.resource.JFaceResources.getFontRegistry;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.glmargins;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class DetailsPanel

    extends Composite

{
    public static final String HEADER_FONT 
        = DetailsPanel.class.getName() + ".HEADER_FONT"; //$NON-NLS-1$
    
    static
    {
        final String defaultFontName = getDefaultFont().getFontData()[ 0 ].getName();
        final FontData[] fontData = getFontRegistry().getBold( defaultFontName ).getFontData();
        
        getFontRegistry().put( HEADER_FONT, fontData );
    }

    private final FacetsSelectionPanel facetsSelectionPanel;
    private Composite content = null;
    
    public DetailsPanel( final Composite parent,
                         final FacetsSelectionPanel facetsSelectionPanel )
    {
        super( parent, SWT.NONE );
        
        setLayout( glmargins( gl( 1 ), 5, 8 ) );
        
        this.facetsSelectionPanel = facetsSelectionPanel;
        
        this.facetsSelectionPanel.addSelectionChangedListener
        (
            new ISelectionChangedListener()
            {
                public void selectionChanged( final SelectionChangedEvent event )
                {
                    refresh();
                }
            }
        );
        
        final IFacetedProjectListener facetedProjectListener = new IFacetedProjectListener()
        {
            public void handleEvent( final IFacetedProjectEvent event ) 
            {
                refresh();
            }
        };
        
        this.facetsSelectionPanel.getFacetedProjectWorkingCopy().addListener( facetedProjectListener, IFacetedProjectEvent.Type.FIXED_FACETS_CHANGED );
        
        addDisposeListener
        (
            new DisposeListener()
            {
                public void widgetDisposed( final DisposeEvent e )
                {
                	facetsSelectionPanel.getFacetedProjectWorkingCopy().removeListener( facetedProjectListener );
                }				
            }
        );
        
        refresh();
    }
    
    private void refresh()
    {
        if( this.content != null )
        {
            this.content.dispose();
        }
        
        final IStructuredSelection sel = (IStructuredSelection) this.facetsSelectionPanel.getSelection();
        
        if( sel == null || sel.isEmpty() )
        {
            this.content = new Composite( this, SWT.NONE );
            this.content.setLayout( glmargins( gl( 1 ), 0, 0 ) );
            
            final Text noSelectionTextField = new Text( this.content, SWT.WRAP | SWT.READ_ONLY );
            noSelectionTextField.setLayoutData( gdhfill() );
            noSelectionTextField.setText( Resources.noSelectionLabel );
        }
        else
        {
            final Object selection = sel.getFirstElement();

            if( selection instanceof IProjectFacetVersion )
            {
                final IProjectFacetVersion fv = (IProjectFacetVersion) selection;
                this.content = new FacetDetailsPanel( this, this.facetsSelectionPanel, fv );
            }
            else if( selection instanceof ICategory )
            {
                final ICategory cat = (ICategory) selection;
                this.content = new CategoryDetailsPanel( this, this.facetsSelectionPanel, cat );
            }
        }

        this.content.setLayoutData( gdfill() );
        
        layout();
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String noSelectionLabel;
        
        static
        {
            initializeMessages( DetailsPanel.class.getName(), 
                                Resources.class );
        }
    }
    
}
