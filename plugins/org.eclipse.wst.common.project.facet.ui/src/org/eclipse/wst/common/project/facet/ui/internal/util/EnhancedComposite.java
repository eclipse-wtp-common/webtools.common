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
import org.eclipse.swt.widgets.Control;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class EnhancedComposite

    extends Composite
    
{
    public EnhancedComposite( final Composite parent )
    {
        this( parent, SWT.NONE );
    }
    
    public EnhancedComposite( final Composite parent,
                              final int style )
    {
        super( parent, style );
    }
    
    @Override
    public void setEnabled( boolean enabled )
    {
        super.setEnabled( enabled );
        
        for( Control child : getChildren() )
        {
            child.setEnabled( enabled );
        }
    }
    
}
