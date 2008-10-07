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
