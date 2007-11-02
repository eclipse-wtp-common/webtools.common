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

package org.eclipse.wst.common.project.facet.ui.internal.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class GridLayoutUtil
{
    public static final GridLayout gl( final int columns )
    {
        return new GridLayout( columns, false );
    }

    public static final GridLayout glmargins( final GridLayout layout,
                                              final int marginWidth,
                                              final int marginHeight )
    {
        layout.marginWidth = marginWidth;
        layout.marginHeight = marginHeight;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        
        return layout;
    }

    public static final GridLayout glmargins( final GridLayout layout,
                                              final int leftMargin,
                                              final int rightMargin,
                                              final int topMargin,
                                              final int bottomMargin )
    {
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginLeft = leftMargin;
        layout.marginRight = rightMargin;
        layout.marginTop = topMargin;
        layout.marginBottom = bottomMargin;
        
        return layout;
    }
    
    public static final GridLayout glspacing( final GridLayout layout,
                                              final int horizontalSpacing,
                                              final int verticalSpacing )
    {
        layout.horizontalSpacing = horizontalSpacing;
        layout.verticalSpacing = verticalSpacing;
        
        return layout;
    }

    public static final GridData gd()
    {
        return new GridData();
    }
    
    public static final GridData gdfill()
    {
        return new GridData( SWT.FILL, SWT.FILL, true, true );
    }
    
    public static final GridData gdhfill()
    {
        return new GridData( GridData.FILL_HORIZONTAL );
    }

    public static final GridData gdvfill()
    {
        return new GridData( GridData.FILL_VERTICAL );
    }
    
    public static final GridData gdhhint( final GridData gd,
                                          final int heightHint )
    {
        gd.heightHint = heightHint;
        return gd;
    }
    
    public static final GridData gdwhint( final GridData gd,
                                          final int widthHint )
    {
        gd.widthHint = widthHint;
        return gd;
    }
    
    public static final GridData gdhindent( final GridData gd,
                                            final int horizontalIndent )
    {
        gd.horizontalIndent = horizontalIndent;
        return gd;
    }

    public static final GridData gdvindent( final GridData gd,
                                            final int verticalIndent )
    {
        gd.verticalIndent = verticalIndent;
        return gd;
    }
    
    public static final GridData gdhspan( final GridData gd,
                                          final int span )
    {
        gd.horizontalSpan = span;
        return gd;
    }

    public static final GridData gdvspan( final GridData gd,
                                          final int span )
    {
        gd.verticalSpan = span;
        return gd;
    }
    
    public static final GridData gdhalign( final GridData gd,
                                           final int alignment )
    {
        gd.horizontalAlignment = alignment;
        return gd;
    }
    
    public static final GridData gdvalign( final GridData gd,
                                           final int alignment )
    {
        gd.verticalAlignment = alignment;
        return gd;
    }
    
}
