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

package org.eclipse.wst.common.project.facet.ui.internal;

import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.glmargins;

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
    private final IFacetedProjectWorkingCopy fpjwcOriginal;
    private final IFacetedProjectWorkingCopy fpjwcLocal;
    private FacetsSelectionPanel panel;

    public FacetsSelectionDialog( final Shell parentShell,
                                  final IFacetedProjectWorkingCopy fpjwc )
    {
        super( parentShell );
        
        setShellStyle( getShellStyle() | SWT.RESIZE );

        this.fpjwcOriginal = fpjwc;
        this.fpjwcLocal = fpjwc.clone();
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
        
        this.panel = new FacetsSelectionPanel( composite, this.fpjwcLocal );
        this.panel.setLayoutData( gdfill() );
        this.panel.setFocus();

        return composite;
    }
    
    @Override
    protected void okPressed()
    {
        this.fpjwcOriginal.mergeChanges( this.fpjwcLocal );
        super.okPressed();
    }

    
    public static final void openDialog( final Shell parentShell,
                                         final IFacetedProjectWorkingCopy fpjwc )
    {
         ( new FacetsSelectionDialog( parentShell, fpjwc ) ).open();
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String dialogTitle;
        public static String dialogDescription;
        
        static
        {
            initializeMessages( FacetsSelectionDialog.class.getName(), 
                                Resources.class );
        }
    }

}
