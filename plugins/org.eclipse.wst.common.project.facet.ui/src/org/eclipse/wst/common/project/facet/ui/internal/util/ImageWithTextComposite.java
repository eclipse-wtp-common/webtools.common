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

package org.eclipse.wst.common.project.facet.ui.internal.util;

import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gd;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdvalign;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.glmargins;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.glspacing;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ImageWithTextComposite

    extends Composite
    
{
    private final Label imageLabel;
    private final Label textLabel;
    
    public ImageWithTextComposite( final Composite parent )
    {
        super( parent, SWT.NONE );
        
        setLayout( glmargins( gl( 1 ), 0, 0 ) );
        
        final Composite internalComposite = new Composite( this, SWT.NONE );
        internalComposite.setLayoutData( gdfill() );
        internalComposite.setLayout( glspacing( glmargins( gl( 2 ), 0, 0 ), 5, 0 ) );
        
        this.imageLabel = new Label( internalComposite, SWT.NONE );
        this.imageLabel.setLayoutData( gdvalign( gd(), SWT.TOP ) );
        
        this.textLabel = new Label( internalComposite, SWT.WRAP );
        this.textLabel.setLayoutData( gdhfill() );
    }
    
    public void setImage( final Image image )
    {
        this.imageLabel.setImage( image );
    }
    
    public void setText( final String textLabel )
    {
        this.textLabel.setText( textLabel );
    }
    
    public void setFont( final Font font )
    {
        this.textLabel.setFont( font );
    }
}
