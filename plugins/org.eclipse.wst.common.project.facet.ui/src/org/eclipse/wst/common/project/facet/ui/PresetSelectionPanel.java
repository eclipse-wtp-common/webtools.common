/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui;

import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.*;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.util.IFilter;
import org.eclipse.wst.common.project.facet.ui.internal.FacetsSelectionDialog;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class PresetSelectionPanel

    extends Composite
    
{
    private final Group group;
    private final Combo presetsCombo;
    private final Button modifyButton;
    private final Label descLabel;
    private final IFacetedProjectWorkingCopy fpjwc;
    
    public PresetSelectionPanel( final Composite parent,
                                 final IFacetedProjectWorkingCopy fpjwc )
    {
        this( parent, fpjwc, null );
    }

    public PresetSelectionPanel( final Composite parent,
                                 final IFacetedProjectWorkingCopy fpjwc,
                                 final IFilter<IPreset> filter )
    {
        super( parent, SWT.NONE );
        
        Dialog.applyDialogFont( parent );
        
        this.fpjwc = fpjwc;
        
        GridLayout layout = new GridLayout( 1, false );
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        
        setLayout( layout );
        
        this.group = new Group( this, SWT.NONE );
        this.group.setLayout( new GridLayout( 2, false ) );
        this.group.setLayoutData( gdhfill() );
        this.group.setText( Resources.groupTitle );
        
        this.presetsCombo = new Combo( this.group, SWT.BORDER | SWT.READ_ONLY );
        this.presetsCombo.setLayoutData( gdhfill() );
        
        this.modifyButton = new Button( this.group, SWT.PUSH );
        this.modifyButton.setText( "Modify..." ); //$NON-NLS-1$
        
        this.modifyButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                @Override
                public void widgetSelected( final SelectionEvent event )
                {
                    handleModifyButtonPressed();
                }
            }
        );
        
        this.descLabel = new Label( this.group, SWT.WRAP );
   
        final GridData gd = gdhspan( gdhfill(), 2 );
        gd.widthHint = 400;
        gd.minimumHeight = 30;
        gd.grabExcessVerticalSpace = true;
        
        this.descLabel.setLayoutData( gd );
        
        refreshDescription();
        
        this.fpjwc.addListener
        ( 
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    final Runnable runnable = new Runnable()
                    {
                        public void run()
                        {
                            refreshDescription();
                        }
                    };
                    
                    Display.getDefault().syncExec( runnable );
                }
            },
            IFacetedProjectEvent.Type.SELECTED_PRESET_CHANGED
        );
        
        ModifyFacetedProjectWizard.syncWithPresetsModel( this.fpjwc, this.presetsCombo, filter );
    }
    
    private void refreshDescription()
    {
        final IPreset preset = this.fpjwc.getSelectedPreset();
        
        final String desc;
        
        if( preset == null )
        {
            desc = Resources.hint;
        }
        else
        {
            desc = preset.getDescription();
        }
        
        this.descLabel.setText( desc );
    }
    
    private void handleModifyButtonPressed()
    {
        FacetsSelectionDialog.openDialog( getShell(), this.fpjwc );
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String groupTitle;
        public static String hint;
        
        static
        {
            initializeMessages( PresetSelectionPanel.class.getName(), 
                                Resources.class );
        }
    }
    
}
