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

import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdwhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;

import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class BasicToolTip
    
    extends ToolTip
    
{
    private String message = ""; //$NON-NLS-1$
    
    public BasicToolTip( final Control control )
    {
        super( control );
        
        setPopupDelay( 1000 );
        setShift( new Point( 10, 3 ) );
    }

    @Override
    protected Composite createToolTipContentArea( final Event event,
                                                  final Composite parent )
    {
        final Display display = parent.getDisplay();
        
        final Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( gl( 1 ) );
        composite.setBackground( display.getSystemColor( SWT.COLOR_INFO_BACKGROUND ) );
        
        final Label label = new Label( composite, SWT.WRAP );
        label.setLayoutData( gdwhint( gdfill(), 300 ) );
        label.setBackground( display.getSystemColor( SWT.COLOR_INFO_BACKGROUND ) );
        label.setText( this.message );
        
        return composite;
    }
    
    public String getMessage()
    {
        return this.message;
    }
    
    public void setMessage( final String message )
    {
        this.message = message;
    }
    
}
