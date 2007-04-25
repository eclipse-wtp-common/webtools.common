/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import static org.eclipse.wst.common.project.facet.ui.internal.ChangeTargetedRuntimesDataModel.EVENT_PRIMARY_RUNTIME_CHANGED;
import static org.eclipse.wst.common.project.facet.ui.internal.ChangeTargetedRuntimesDataModel.EVENT_TARGETED_RUNTIMES_CHANGED;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IDynamicPreset;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.internal.util.IndexedSet;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ModifyFacetedProjectDataModel

    extends AbstractDataModel
    
{
    public static String EVENT_FIXED_FACETS_CHANGED = "fixedFacetsChanged"; //$NON-NLS-1$
    public static String EVENT_AVAILABLE_FACETS_CHANGED = "availableFacetsChanged"; //$NON-NLS-1$
    public static String EVENT_AVAILABLE_PRESETS_CHANGED = "availablePresetsChanged"; //$NON-NLS-1$
    public static String EVENT_SELECTED_PRESET_CHANGED = "selectedPresetChanged"; //$NON-NLS-1$
    
    private Set<IProjectFacet> fixedFacets;
    private Map<IProjectFacet,Set<IProjectFacetVersion>> availableFacets;
    private IndexedSet<String,IPreset> availablePresets;
    private IPreset selectedPreset;
    private final ChangeTargetedRuntimesDataModel runtimesDataModel;
    
    public ModifyFacetedProjectDataModel()
    {
        this.fixedFacets = Collections.emptySet();
        this.availableFacets = Collections.emptyMap();
        this.availablePresets = new IndexedSet<String,IPreset>();
        this.selectedPreset = null;
        this.runtimesDataModel = new ChangeTargetedRuntimesDataModel();

        refreshAvailableFacets();
        
        final IDataModelListener avFacetsListener = new IDataModelListener()
        {
            public void handleEvent()
            {
                refreshAvailableFacets();
            }
        };
        
        addListener( EVENT_FIXED_FACETS_CHANGED, avFacetsListener );
        this.runtimesDataModel.addListener( EVENT_TARGETED_RUNTIMES_CHANGED, avFacetsListener );

        refreshAvailablePresets();
        
        final IDataModelListener avPresetsListener = new IDataModelListener()
        {
            public void handleEvent()
            {
                refreshAvailablePresets();
            }
        };
        
        addListener( EVENT_FIXED_FACETS_CHANGED, avPresetsListener );
        addListener( EVENT_AVAILABLE_FACETS_CHANGED, avPresetsListener );
        this.runtimesDataModel.addListener( EVENT_PRIMARY_RUNTIME_CHANGED, avPresetsListener );
    }
    
    public synchronized Set<IProjectFacet> getFixedFacets()
    {
        return this.fixedFacets;
    }
    
    public synchronized void setFixedFacets( final Set<IProjectFacet> fixed )
    {
        if( this.fixedFacets.equals( fixed ) )
        {
            return;
        }
        
        this.fixedFacets = Collections.unmodifiableSet( new HashSet<IProjectFacet>( fixed ) );
        
        notifyListeners( EVENT_FIXED_FACETS_CHANGED );
    }
    
    public synchronized Map<IProjectFacet,Set<IProjectFacetVersion>> getAvailableFacets()
    {
        return this.availableFacets;
    }
    
    public synchronized boolean isFacetAvailable( final IProjectFacet f )
    {
        return this.availableFacets.containsKey( f );
    }
    
    public synchronized boolean isFacetAvailable( final IProjectFacetVersion fv )
    {
        final Set<IProjectFacetVersion> versions = this.availableFacets.get( fv.getProjectFacet() );
        return ( versions != null && versions.contains( fv ) );
    }
    
    private synchronized void refreshAvailableFacets()
    {
        final Map<IProjectFacet,Set<IProjectFacetVersion>> newAvailableFacets
            = new HashMap<IProjectFacet,Set<IProjectFacetVersion>>();
        
        final Set<IRuntime> targetedRuntimes = this.runtimesDataModel.getTargetedRuntimes();
        
        for( IProjectFacet f : ProjectFacetsManager.getProjectFacets() )
        {
            Set<IProjectFacetVersion> versions = null;
            
            for( IProjectFacetVersion fv : f.getVersions() )
            {
                boolean available = true;
                
                for( IRuntime r : targetedRuntimes )
                {
                    if( ! r.supports( fv ) )
                    {
                        available = false;
                        break;
                    }
                }
                
                if( available && ! fv.isValidFor( this.fixedFacets ) )
                {
                    available = false;
                }
                
                if( available )
                {
                    if( versions == null )
                    {
                        versions = new HashSet<IProjectFacetVersion>();
                    }
                    
                    versions.add( fv );
                }
            }
            
            if( versions != null )
            {
                newAvailableFacets.put( f, Collections.unmodifiableSet( versions ) );
            }
        }
        
        if( ! this.availableFacets.equals( newAvailableFacets ) )
        {
            this.availableFacets = Collections.unmodifiableMap( newAvailableFacets );
            notifyListeners( EVENT_AVAILABLE_FACETS_CHANGED );
        }
    }
    
    public synchronized Set<IPreset> getAvailablePresets()
    {
        return this.availablePresets.getUnmodifiable();
    }
    
    public synchronized void refreshAvailablePresets()
    {
        final IndexedSet<String,IPreset> newAvailablePresets = new IndexedSet<String,IPreset>();
        Map<String,Object> context = null;
        
        for( IPreset preset : ProjectFacetsManager.getPresets() )
        {
            if( preset.getType() == IPreset.Type.DYNAMIC )
            {
                if( context == null )
                {
                    context = new HashMap<String,Object>();
                    
                    context.put( IDynamicPreset.CONTEXT_KEY_FIXED_FACETS, this.fixedFacets );
                    
                    context.put( IDynamicPreset.CONTEXT_KEY_PRIMARY_RUNTIME, 
                                 this.runtimesDataModel.getPrimaryRuntime() );
                }
                
                preset = ( (IDynamicPreset) preset ).resolve( context );
                
                if( preset == null )
                {
                    continue;
                }
            }
            
            final Set<IProjectFacetVersion> facets = preset.getProjectFacets();
            boolean applicable = true;
            
            // All of the facets listed in the preset and their versions must be available.
            
            for( IProjectFacetVersion fv : facets )
            {
                if( ! isFacetAvailable( fv ) )
                {
                    applicable = false;
                    break;
                }
            }
            
            // The preset must span across all of the fixed facets.
            
            for( IProjectFacet f : this.fixedFacets )
            {
                boolean found = false;

                for( IProjectFacetVersion fv : f.getVersions() )
                {
                    if( facets.contains( fv ) )
                    {
                        found = true;
                        break;
                    }
                }
                
                if( ! found )
                {
                    applicable = false;
                    break;
                }
            }
            
            if( applicable )
            {
                newAvailablePresets.add( preset.getId(), preset );
            }
        }
        
        if( ! this.availablePresets.equals( newAvailablePresets ) )
        {
            this.availablePresets = newAvailablePresets;
            notifyListeners( EVENT_AVAILABLE_PRESETS_CHANGED );
            
            if( this.selectedPreset != null && 
                ! this.availablePresets.contains( this.selectedPreset ) )
            {
                setSelectedPreset( null );
            }
        }
    }
    
    public synchronized IPreset getSelectedPreset()
    {
        return this.selectedPreset;
    }
    
    public synchronized void setSelectedPreset( final String presetId )
    {
        if( presetId != null && ! this.availablePresets.containsKey( presetId ) )
        {
            final String msg = Resources.bind( Resources.couldNotSelectPreset, presetId ); 
            throw new IllegalArgumentException( msg );
        }
        
        final IPreset preset = this.availablePresets.get( presetId );

        if( ! equals( this.selectedPreset, preset ) )
        {
            this.selectedPreset = preset;
            
            notifyListeners( EVENT_SELECTED_PRESET_CHANGED );
        }
    }
    
    public ChangeTargetedRuntimesDataModel getTargetedRuntimesDataModel()
    {
        return this.runtimesDataModel;
    }
    
    public void dispose()
    {
        this.runtimesDataModel.dispose();
    }
    
    private static boolean equals( final Object obj1,
                                   final Object obj2 )
    {
        if( obj1 == obj2 )
        {
            return true;
        }
        else if( obj1 == null || obj2 == null )
        {
            return false;
        }
        else
        {
            return obj1.equals( obj2 );
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String couldNotSelectPreset;
        
        static
        {
            initializeMessages( ModifyFacetedProjectDataModel.class.getName(), 
                                Resources.class );
        }
    }
    
}
