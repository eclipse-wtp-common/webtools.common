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

package org.eclipse.wst.common.project.facet.ui.internal;

import static org.eclipse.jface.resource.JFaceResources.getFontRegistry;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.glmargins;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.ui.internal.util.ImageWithTextComposite;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class CategoryDetailsPanel

    extends Composite

{
    public CategoryDetailsPanel( final Composite parent,
                                 final FacetsSelectionPanel facetsSelectionPanel,
                                 final ICategory category )
    {
        super( parent, SWT.NONE );
        
        setLayout( glmargins( gl( 1 ), 0, 0 ) );

        final ImageWithTextComposite header = new ImageWithTextComposite( this );
        header.setLayoutData( gdhfill() );
        header.setImage( facetsSelectionPanel.getImage( category ) );
        header.setFont( getFontRegistry().get( DetailsPanel.HEADER_FONT ) );
        header.setText( category.toString() );
        
        final Label separator = new Label( this, SWT.SEPARATOR | SWT.HORIZONTAL );
        separator.setLayoutData( gdhfill() );

        final Text descTextField = new Text( this, SWT.WRAP | SWT.READ_ONLY );
        descTextField.setLayoutData( gdhfill() );
        descTextField.setText( category.getDescription() );
        //descTextField.setBackground( getDisplay().getSystemColor( SWT.COLOR_WIDGET_BACKGROUND ) );
    }
    
}
