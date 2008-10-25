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

package org.eclipse.jst.common.project.facet.ui.libprov.internal;

import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;

import org.eclipse.jst.common.project.facet.ui.libprov.LibraryProviderOperationPanel;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class RuntimeLibraryProviderInstallPanel

    extends LibraryProviderOperationPanel
    
{
    @Override
    public Control createControl( final Composite parent )
    {
        final Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( gl( 1, 0, 0 ) );
        
        final Link link = new Link( composite, SWT.WRAP );
        final GridData data = new GridData( SWT.FILL, SWT.BEGINNING, true, false );
        data.widthHint = 300;
        link.setLayoutData( data );
        link.setText( Resources.message );
        
        return composite;
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String message;

        static
        {
            initializeMessages( RuntimeLibraryProviderInstallPanel.class.getName(), 
                                Resources.class );
        }
    }

}