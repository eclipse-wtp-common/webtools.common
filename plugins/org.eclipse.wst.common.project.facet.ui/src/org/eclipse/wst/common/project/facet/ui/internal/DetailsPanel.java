/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
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
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
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
                    handleSelectionChangedEvent( event );
                }
            }
        );
        
        handleSelectionChangedEvent( null );
    }
    
    private void handleSelectionChangedEvent( final SelectionChangedEvent event )
    {
        if( this.content != null )
        {
            this.content.dispose();
        }
        
        Object selection = null;
        
        if( event != null )
        {
            final IStructuredSelection sel = (IStructuredSelection) event.getSelection();
            selection = sel.getFirstElement();
        }
        
        if( selection == null )
        {
            this.content = new Composite( this, SWT.NONE );
            this.content.setLayout( glmargins( gl( 1 ), 0, 0 ) );
            
            final Label noSelectionLabel = new Label( this.content, SWT.WRAP );
            noSelectionLabel.setLayoutData( gdhfill() );
            noSelectionLabel.setText( Resources.noSelectionLabel );
        }
        else if( selection instanceof IProjectFacetVersion )
        {
            final IProjectFacetVersion fv = (IProjectFacetVersion) selection;
            this.content = new FacetDetailsPanel( this, this.facetsSelectionPanel, fv );
        }
        else if( selection instanceof ICategory )
        {
            final ICategory cat = (ICategory) selection;
            this.content = new CategoryDetailsPanel( this, this.facetsSelectionPanel, cat );
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
