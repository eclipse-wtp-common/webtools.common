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

import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TableWrapLayoutUtil
{
    public static TableWrapLayout twlayout( final int columns )
    {
        return twlayout( columns, 0, 0, 0, 0 );
    }
    
    public static TableWrapLayout twlayout( final int columns,
                                            final int topMargin,
                                            final int bottomMargin,
                                            final int leftMargin,
                                            final int rightMargin )
    {
        final TableWrapLayout layout = new TableWrapLayout();
        layout.numColumns = columns;
        layout.topMargin = topMargin;
        layout.bottomMargin = bottomMargin;
        layout.leftMargin = leftMargin;
        layout.rightMargin = rightMargin;

        return layout;
    }
    
    public static TableWrapData twd()
    {
        return new TableWrapData( TableWrapData.FILL_GRAB, TableWrapData.MIDDLE );        
    }
    
    public static TableWrapData twdcolspan( final TableWrapData twd,
                                            final int colspan )
    {
        twd.colspan = colspan;
        return twd;
    }
    
    public static TableWrapData twdindent( final TableWrapData twd,
                                           final int indent )
    {
        twd.indent = indent;
        return twd;
    }
    
    public static TableWrapData twdhhint( final TableWrapData twd,
                                          final int heightHint )
    {
        twd.heightHint = heightHint;
        return twd;
    }
    
}
