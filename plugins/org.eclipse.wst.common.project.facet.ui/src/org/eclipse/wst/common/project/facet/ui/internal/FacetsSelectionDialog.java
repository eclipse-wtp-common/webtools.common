/******************************************************************************
 * Copyright (c) 2011 Oracle and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Roberto Sanchez Herrera - [334438] Disable the Cancel button in Project Facets dialog
 *    Nitin Dahyabbhai        - [334844] Remove the disabled Cancel button from Project Facets dialog
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.glmargins;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class FacetsSelectionDialog

    extends TitleAreaDialog
    
{
    private final IFacetedProjectWorkingCopy fpjwc;
    private FacetsSelectionPanel panel;

    public FacetsSelectionDialog( final Shell parentShell,
                                  final IFacetedProjectWorkingCopy fpjwc )
    {
        super( parentShell );
        
        setShellStyle( getShellStyle() | SWT.RESIZE );

        this.fpjwc = fpjwc;
        this.panel = null;
    }
    
    @Override
    protected Control createDialogArea( final Composite parent ) 
    {
        parent.getShell().setText( Resources.dialogTitle );
        setTitle( Resources.dialogTitle );
        setMessage( Resources.dialogDescription );
        setTitleImage( FacetedProjectFrameworkImages.BANNER_IMAGE.getImage() );

        final Composite dialogComposite = (Composite) super.createDialogArea( parent );
        
        final Composite composite = new Composite( dialogComposite, SWT.NONE );
        composite.setLayoutData( gdfill() );
        composite.setLayout( glmargins( gl( 1 ), 5, 5 ) );
        
        this.panel = new FacetsSelectionPanel( composite, this.fpjwc );
        this.panel.setLayoutData( gdfill() );
        this.panel.setFocus();

        return composite;
    }
    
    @Override
    protected void createButtonsForButtonBar( final Composite parent ) 
    {
        // Create only the OK button. There is no handling for cancel.
        
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
    }

    public static final void openDialog( final Shell parentShell,
                                         final IFacetedProjectWorkingCopy fpjwc )
    {
         ( new FacetsSelectionDialog( parentShell, fpjwc ) ).open();
    }
    
    private static final class Resources extends NLS
    {
        public static String dialogTitle;
        public static String dialogDescription;
        
        static
        {
            initializeMessages( FacetsSelectionDialog.class.getName(), Resources.class );
        }
    }

}
