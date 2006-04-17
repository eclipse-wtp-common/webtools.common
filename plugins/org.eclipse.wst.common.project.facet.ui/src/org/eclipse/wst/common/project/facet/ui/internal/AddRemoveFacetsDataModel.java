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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IPreset;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class AddRemoveFacetsDataModel
{
    public static String PROP_PRESETS = "presets"; //$NON-NLS-1$
    public static String PROP_SELECTED_PRESET = "selectedPreset"; //$NON-NLS-1$
    
    private Map listeners = new HashMap();
    private Set presets = new HashSet();
    private Set presetsReadOnly = null; 
    private IPreset selectedPreset = null;
    
    public synchronized Set getPresets()
    {
        if( this.presetsReadOnly == null )
        {
            this.presetsReadOnly = Collections.unmodifiableSet( this.presets );
        }
        
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

        if( this.presetsReadOnly != null )
        {
            this.presets = new HashSet( presets );
            this.presetsReadOnly = null;
        }
        else
        {
            this.presets.clear();
            this.presets.addAll( presets );
        }
        
        notifyListeners( PROP_PRESETS );
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
        
        notifyListeners( PROP_SELECTED_PRESET );
    }
    
    public synchronized void addListener( final String property,
                                          final IListener listener )
    {
        List list = (List) this.listeners.get( property );
        
        if( list == null )
        {
            list = new ArrayList();
            this.listeners.put( property, list );
        }
        
        list.add( listener );
    }
    
    private void notifyListeners( final String property )
    {
        final List listeners = (List) this.listeners.get( property );
        
        for( Iterator itr = listeners.iterator(); itr.hasNext(); )
        {
            ( (IListener) itr.next() ).handleEvent();
        }
    }
    
    public static interface IListener
    {
        void handleEvent();
    }

}
