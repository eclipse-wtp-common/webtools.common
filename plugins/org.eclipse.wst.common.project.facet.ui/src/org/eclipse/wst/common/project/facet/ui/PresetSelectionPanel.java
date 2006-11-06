/******************************************************************************
 * Copyright (c) 2005 - 2006 BEA Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Karl Lum - initial API and implementation
 *	  David Schneider, david.schneider@unisys.com - [142500] WTP properties pages fonts don't follow Eclipse preferences
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.ui.internal.AddRemoveFacetsDataModel;
import org.eclipse.wst.common.project.facet.ui.internal.AbstractDataModel.IDataModelListener;

public final class PresetSelectionPanel

    extends Composite
    
{
    private final Group group;
    private final Combo presetsCombo;
    private final Label descLabel;
    private final AddRemoveFacetsDataModel model;
    
    public PresetSelectionPanel( final Composite parent,
                                 final int style,
                                 final AddRemoveFacetsDataModel model )
    {
        super( parent, style );
        
        this.model = model;
        
        GridLayout layout = new GridLayout( 1, false );
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        
        setLayout( layout );
        
        this.group = new Group( this, SWT.NONE );
        this.group.setLayout( new GridLayout( 1, false ) );
        this.group.setLayoutData( gdhfill() );
        this.group.setText( Resources.groupTitle );
        
        this.presetsCombo = new Combo( this.group, SWT.BORDER | SWT.READ_ONLY );
        this.presetsCombo.setLayoutData( gdhfill() );
        
        this.descLabel = new Label( this.group, SWT.WRAP );
   
        final GridData gd = gdhfill();
        gd.widthHint = 400;
        gd.minimumHeight = 30;
        gd.grabExcessVerticalSpace = true;
        
        this.descLabel.setLayoutData( gd );
        
        refreshDescription();
        
        this.model.addListener
        ( 
            AddRemoveFacetsDataModel.EVENT_SELECTED_PRESET_CHANGED,
            new IDataModelListener()
            {
                public void handleEvent()
                {
                    refreshDescription();
                }
            }
        );
        Dialog.applyDialogFont(parent);
    }
    
    public Combo getPresetsCombo()
    {
        return this.presetsCombo;
    }
    
    private void refreshDescription()
    {
        final IPreset preset = this.model.getSelectedPreset();
        
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
    
    private static GridData gdhfill() 
    {
        return new GridData( GridData.FILL_HORIZONTAL );
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
