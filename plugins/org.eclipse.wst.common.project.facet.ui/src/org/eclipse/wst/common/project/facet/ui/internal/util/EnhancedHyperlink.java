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

package org.eclipse.wst.common.project.facet.ui.internal.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class EnhancedHyperlink

    extends Hyperlink
    
{
    public EnhancedHyperlink( final Composite parent,
                              final int style )
    {
        super( parent, style );
        
        setUnderlined( true );
        setForeground( getDisplay().getSystemColor( SWT.COLOR_DARK_BLUE ) );
    }
    
    @Override
    public void setEnabled( final boolean enabled )
    {
        super.setEnabled( enabled );
        
        final int color = ( enabled ? SWT.COLOR_DARK_BLUE : SWT.COLOR_DARK_GRAY );
        setForeground( getDisplay().getSystemColor( color ) );
    }
    
}
