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

package org.eclipse.wst.common.project.facet.ui.internal.util;

import static java.lang.Math.max;
import static org.eclipse.jface.resource.JFaceResources.getColorRegistry;
import static org.eclipse.jface.resource.JFaceResources.getDefaultFont;
import static org.eclipse.jface.resource.JFaceResources.getFontRegistry;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdwhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.glmargins;
import static org.eclipse.wst.common.project.facet.ui.internal.util.SwtUtil.getPreferredWidth;

import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class HeaderToolTip
    
    extends ToolTip
    
{
    private static final String HEADER_BG_COLOR 
        = HeaderToolTip.class.getName() + ".TOOLTIP_HEAD_BG_COLOR"; //$NON-NLS-1$
    
    private static final String HEADER_FG_COLOR 
        = HeaderToolTip.class.getName() + ".TOOLTIP_HEAD_FG_COLOR"; //$NON-NLS-1$
    
    private static final String HEADER_FONT 
        = HeaderToolTip.class.getName() + ".TOOLTIP_HEAD_FONT"; //$NON-NLS-1$
    
    static
    {
        getColorRegistry().put( HEADER_BG_COLOR, new RGB( 255, 255, 255 ) );
        getColorRegistry().put( HEADER_FG_COLOR, new RGB( 0, 0, 0 ) );
        
        final String defaultFontName = getDefaultFont().getFontData()[ 0 ].getName();
        final FontData[] fontData = getFontRegistry().getBold( defaultFontName ).getFontData();
        
        getFontRegistry().put( HEADER_FONT, fontData );
    }
    
    public HeaderToolTip( final Control control )
    {
        super( control );
        
        setPopupDelay( 1000 );
        setShift( new Point( 10, 3 ) );
    }

    @Override
    protected final Composite createToolTipContentArea( final Event event,
                                                        final Composite parent )
    {
        final Composite composite = new Composite( parent, SWT.NONE );
        final GridLayout layout = glmargins( gl( 1 ), 0, 0 );
        layout.verticalSpacing = 1;
        composite.setLayout( layout );
        
        final Composite topArea = new Composite( composite, SWT.NONE );
        topArea.setBackground( getColorRegistry().get( HEADER_BG_COLOR ) );
        topArea.setLayout( glmargins( gl( 3 ), 5, 5, 2, 2 ) );
        
        final Label titleLabel = new Label( topArea, SWT.NONE  );
        titleLabel.setLayoutData( gdfill() );
        titleLabel.setBackground( getColorRegistry().get( HEADER_BG_COLOR ) );
        titleLabel.setFont( getFontRegistry().get( HEADER_FONT ) );
        titleLabel.setForeground( getColorRegistry().get( HEADER_FG_COLOR ) );
        titleLabel.setText( getToolTipTitle( event ) );
        
        final Control content = createContentArea( event, composite );

        final int width = max( getPreferredWidth( titleLabel ) + 50, 300 );
        topArea.setLayoutData( gdwhint( gdhfill(), width ) );
        content.setLayoutData( gdwhint( gdfill(), width ) );
        
        return composite;
    }
    
    protected abstract String getToolTipTitle( Event event );
    
    protected abstract Composite createContentArea( Event event,
                                                    Composite parent );
}
