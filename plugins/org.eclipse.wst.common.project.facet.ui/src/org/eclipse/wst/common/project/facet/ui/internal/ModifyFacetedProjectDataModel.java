/******************************************************************************
 * Copyright (c) 2006 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IPreset;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ModifyFacetedProjectDataModel

    extends AbstractDataModel
    
{
    public static String EVENT_SELECTABLE_PRESETS_CHANGED 
        = "selectablePresetsChanged"; //$NON-NLS-1$
    
    public static String EVENT_SELECTED_PRESET_CHANGED 
        = "selectedPresetChanged"; //$NON-NLS-1$
    
    private final Set presets;
    private final Set presetsReadOnly; 
    private IPreset selectedPreset;
    private final ChangeTargetedRuntimesDataModel runtimesDataModel;
    
    public ModifyFacetedProjectDataModel()
    {
        this.presets = new HashSet();
        this.presetsReadOnly = Collections.unmodifiableSet( this.presets );
        this.selectedPreset = null;
        this.runtimesDataModel = new ChangeTargetedRuntimesDataModel();
    }
    
    public synchronized Set getPresets()
    {
        return this.presetsReadOnly;
    }
    
    // TODO: Remove this. Presets should be auto-computed by the data-model.
    // The user should not be allowed to set the list of presets.
    
    public synchronized void setPresets( final Set presets )
    {
        if( this.selectedPreset != null && 
            ! presets.contains( this.selectedPreset ) )
        {
            setSelectedPreset( null );
        }

        this.presets.clear();
        this.presets.addAll( presets );
        
        notifyListeners( EVENT_SELECTABLE_PRESETS_CHANGED );
    }
    
    public synchronized IPreset getSelectedPreset()
    {
        return this.selectedPreset;
    }
    
    public synchronized void setSelectedPreset( final IPreset preset )
    {
        if( preset != null && ! this.presets.contains( preset ) )
        {
            throw new IllegalArgumentException();
        }
        
        this.selectedPreset = preset;
        
        notifyListeners( EVENT_SELECTED_PRESET_CHANGED );
    }
    
    public ChangeTargetedRuntimesDataModel getTargetedRuntimesDataModel()
    {
        return this.runtimesDataModel;
    }
    
    public void dispose()
    {
        this.runtimesDataModel.dispose();
    }
    
}
