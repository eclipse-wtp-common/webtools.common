/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.ui.libprov;

import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;

/**
 * This {@link LibraryProviderOperationPanel} implementation is useful when the library
 * provider only needs to show a message to the user on the install panel. The panel created 
 * by this implementation contains a simple multi-line label with text from the "message" parameter 
 * in the library provider definition.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public final class SimpleMessageLibraryProviderInstallPanel

    extends LibraryProviderOperationPanel
    
{
    private static final String PARAM_MESSAGE = "message"; //$NON-NLS-1$
    
    @Override
    public Control createControl( final Composite parent )
    {
        final Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( gl( 1, 0, 0 ) );
        
        final String message 
            = getOperationConfig().getLibraryProvider().getParams().get( PARAM_MESSAGE );
        
        final Link link = new Link( composite, SWT.WRAP );
        final GridData data = new GridData( SWT.FILL, SWT.BEGINNING, true, false );
        data.widthHint = 300;
        link.setLayoutData( data );
        link.setText( message );
        
        return composite;
    }

}