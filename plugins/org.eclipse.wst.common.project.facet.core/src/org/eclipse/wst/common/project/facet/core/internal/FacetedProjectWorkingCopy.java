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

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.ActionConfig;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IActionConfig;
import org.eclipse.wst.common.project.facet.core.IActionDefinition;
import org.eclipse.wst.common.project.facet.core.IDynamicPreset;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IListener;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectFrameworkEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectFrameworkListener;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetsChangedEvent;
import org.eclipse.wst.common.project.facet.core.events.internal.FacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.internal.ProjectFacetsChangedEvent;
import org.eclipse.wst.common.project.facet.core.internal.util.IndexedSet;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;

/**
 * @since 3.0
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetedProjectWorkingCopy

    implements IFacetedProjectWorkingCopy
    
{
    private static final SortedSet<IProjectFacetVersion> EMPTY_SORTED_FV_SET
        = Collections.unmodifiableSortedSet( new TreeSet<IProjectFacetVersion>() );
    
    /**
     * The name of the project in the scenario where the working copy is for a
     * project that doesn't exist yet, <code>null</code> otherwise.
     */
    
    private String projectName;
    
    /**
     * The location of the project in the scenario where the working copy is for a
     * project that doesn't exist yet, <code>null</code> otherwise.
     */
    
    private IPath projectLocation;
    private final IFacetedProject project;
    private Set<IProjectFacet> fixedFacets;
    private IndexedSet<IProjectFacet,IProjectFacetVersion> facets;
    private Map<IProjectFacet,SortedSet<IProjectFacetVersion>> availableFacets;
    private IndexedSet<String,IPreset> availablePresets;
    private IPreset selectedPreset;
    private final Set<IRuntime> targetableRuntimes;
    private final Set<IRuntime> targetedRuntimes;
    private IRuntime primaryRuntime;
    private Set<Action> actions;
    private List<IStatus> problems;
    private final List<Runnable> disposeTasks;    
    
    /**
     * Maps event types to the list of listeners registered for those events. The map is
     * populated with empty listeners lists for all event types at initialization and is 
     * not modified after that. The contained lists use copy-on-write behavior to guarantee 
     * for safe iteration when notifying listeners.
     */
    
    private final Map<IFacetedProjectEvent.Type,List<IFacetedProjectListener>> listeners; 
    
    public FacetedProjectWorkingCopy( final IFacetedProject project )
    {
        this.projectName = null;
        this.projectLocation = null;
        this.project = project;
        this.actions = Collections.emptySet();
        this.problems = Collections.emptyList();
        this.fixedFacets = Collections.emptySet();
        this.facets = new IndexedSet<IProjectFacet,IProjectFacetVersion>();
        this.availableFacets = Collections.emptyMap();
        this.availablePresets = new IndexedSet<String,IPreset>();
        this.selectedPreset = null;
        this.targetableRuntimes = new CopyOnWriteArraySet<IRuntime>();
        this.targetedRuntimes = new CopyOnWriteArraySet<IRuntime>();
        this.primaryRuntime = null;
        this.disposeTasks = new ArrayList<Runnable>();
        
        this.listeners = new HashMap<IFacetedProjectEvent.Type,List<IFacetedProjectListener>>();
        
        for( IFacetedProjectEvent.Type eventType : IFacetedProjectEvent.Type.values() )
        {
            this.listeners.put( eventType, new CopyOnWriteArrayList<IFacetedProjectListener>() );
        }
        
        refreshAvailableFacets();
        
        final IFacetedProjectListener avFacetsListener = new IFacetedProjectListener()
        {
            public void handleEvent( final IFacetedProjectEvent event )
            {
                refreshAvailableFacets();
            }
        };
        
        addListener( avFacetsListener, 
                     IFacetedProjectEvent.Type.FIXED_FACETS_CHANGED, 
                     IFacetedProjectEvent.Type.TARGETED_RUNTIMES_CHANGED );

        if( this.project != null )
        {
            setFixedProjectFacets( this.project.getFixedProjectFacets() );
        }

        refreshAvailablePresets();
        
        final IFacetedProjectListener avPresetsListener = new IFacetedProjectListener()
        {
            public void handleEvent( final IFacetedProjectEvent event )
            {
                refreshAvailablePresets();
            }
        };
        
        addListener( avPresetsListener,
                     IFacetedProjectEvent.Type.FIXED_FACETS_CHANGED,
                     IFacetedProjectEvent.Type.AVAILABLE_FACETS_CHANGED,
                     IFacetedProjectEvent.Type.PRIMARY_RUNTIME_CHANGED );
        
        if( this.project != null )
        {
            setProjectFacets( this.project.getProjectFacets() );
        }
        
        refreshTargetableRuntimes();
        
        if( this.project != null )
        {
            setTargetedRuntimes( this.project.getTargetedRuntimes() );
            setPrimaryRuntime( this.project.getPrimaryRuntime() );
        }
        
        // Listen for changes to registered runtimes.
        
        final IListener runtimeManagerListener = new IListener()
        {
            public void handle()
            {
                final IFacetedProjectEvent event
                    = new FacetedProjectEvent( FacetedProjectWorkingCopy.this, 
                                               IFacetedProjectEvent.Type.AVAILABLE_RUNTIMES_CHANGED );
                
                notifyListeners( event );
                
                refreshTargetableRuntimes();
            }
        };
        
        RuntimeManager.addRuntimeListener( runtimeManagerListener );
        
        addDisposeTask
        (
            new Runnable()
            {
                public void run()
                {
                    RuntimeManager.removeRuntimeListener( runtimeManagerListener );
                }
            }
        );
        
        // Listen for changes to registered presets.
        
        final IFacetedProjectFrameworkListener presetsListener = new IFacetedProjectFrameworkListener()
        {
            public void handleEvent( final IFacetedProjectFrameworkEvent event )
            {
                refreshAvailablePresets();
            }
        };
        
        FacetedProjectFramework.addListener( presetsListener, 
                                             IFacetedProjectFrameworkEvent.Type.PRESET_ADDED, 
                                             IFacetedProjectFrameworkEvent.Type.PRESET_REMOVED );
        
        addDisposeTask
        (
            new Runnable()
            {
                public void run()
                {
                    FacetedProjectFramework.removeListener( presetsListener );
                }
            }
        );
        
        // Listen for changes to the project.
        
        final IFacetedProjectListener projectFacetsChangedListener = new IFacetedProjectListener()
        {
            public void handleEvent( final IFacetedProjectEvent event )
            {
                refreshTargetableRuntimes();
                refreshProjectFacetActions();
                performValidation();
            }
        };
        
        addListener( projectFacetsChangedListener,
                     IFacetedProjectEvent.Type.PROJECT_FACETS_CHANGED );
        
        final IFacetedProjectListener targetedRuntimesChangedListener = new IFacetedProjectListener()
        {
            public void handleEvent( final IFacetedProjectEvent event )
            {
                performValidation();
            }
        };
        
        addListener( targetedRuntimesChangedListener,
                     IFacetedProjectEvent.Type.TARGETED_RUNTIMES_CHANGED );
    }
    
    public synchronized String getProjectName()
    {
        if( this.project != null )
        {
            return this.project.getProject().getName();
        }
        else
        {
            return this.projectName;
        }
    }
    
    public synchronized void setProjectName( final String name )
    {
        if( this.project == null )
        {
            if( ! equals( this.projectName, name ) )
            {
                this.projectName = name;
                
                for( Action action : getProjectFacetActions() )
                {
                    final Object config = action.getConfig();
                    
                    if( config != null )
                    {
                        IActionConfig c = null;
                        
                        if( config instanceof IActionConfig )
                        {
                            c = (IActionConfig) config;
                        }
                        else
                        {
                            final IAdapterManager m = Platform.getAdapterManager();
                            final String t = IActionConfig.class.getName();
                            c = (IActionConfig) m.loadAdapter( config, t );
                        }
                        
                        if( c != null )
                        {
                            c.setProjectName( this.projectName );
                        }
                    }
                }
                
                notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.PROJECT_NAME_CHANGED ) );
            }
        }
        else
        {
            throw new IllegalArgumentException(); // TODO: needs message
        }
    }
    
    public synchronized IPath getProjectLocation()
    {
        if( this.project != null )
        {
            return this.project.getProject().getLocation();
        }
        else
        {
            return this.projectLocation;
        }
    }
    
    public synchronized void setProjectLocation( final IPath location )
    {
        if( this.project == null )
        {
            this.projectLocation = location;
        }
        else
        {
            throw new IllegalArgumentException(); // TODO: needs message
        }
    }
    
    public IProject getProject()
    {
        if( this.project == null )
        {
            return null;
        }
        else
        {
            return this.project.getProject();
        }
    }
    
    public IFacetedProject getFacetedProject()
    {
        return this.project;
    }
    
    public synchronized Set<IProjectFacet> getFixedProjectFacets()
    {
        return this.fixedFacets;
    }
    
    public synchronized boolean isFixedProjectFacet( final IProjectFacet facet )
    {
        return this.fixedFacets.contains( facet );
    }
    
    public synchronized void setFixedProjectFacets( final Set<IProjectFacet> fixed )
    {
        if( this.fixedFacets.equals( fixed ) )
        {
            return;
        }
        
        for( IProjectFacet f : fixed )
        {
            final IProjectFacetVersion currentVersion = getProjectFacetVersion( f );
            
            if( currentVersion == null )
            {
                IProjectFacetVersion fv = f.getDefaultVersion();
                
                if( ! isFacetAvailable( fv ) )
                {
                    fv = getHighestAvailableVersion( f );
                }
                
                this.facets.add( f, fv );
            }
        }
        
        notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.PROJECT_FACETS_CHANGED ) );
        
        this.fixedFacets = Collections.unmodifiableSet( new HashSet<IProjectFacet>( fixed ) );
        
        notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.FIXED_FACETS_CHANGED ) );
    }
    
    public synchronized Map<IProjectFacet,SortedSet<IProjectFacetVersion>> getAvailableFacets()
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
    
    public synchronized SortedSet<IProjectFacetVersion> getAvailableVersions( final IProjectFacet f )
    {
        SortedSet<IProjectFacetVersion> availableVersions = this.availableFacets.get( f );
        
        if( availableVersions == null )
        {
            availableVersions = EMPTY_SORTED_FV_SET;
        }
        
        return availableVersions;
    }
    
    public synchronized IProjectFacetVersion getHighestAvailableVersion( final IProjectFacet f )
    {
        IProjectFacetVersion version = null;
        
        for( IProjectFacetVersion fv : this.availableFacets.get( f ) )
        {
            if( version == null )
            {
                version = fv;
            }
            else
            {
                if( fv.compareTo( version ) > 0 )
                {
                    version = fv;
                }
            }
        }
        
        return version;
    }
    
    private synchronized void refreshAvailableFacets()
    {
        final Map<IProjectFacet,SortedSet<IProjectFacetVersion>> newAvailableFacets
            = new HashMap<IProjectFacet,SortedSet<IProjectFacetVersion>>();
        
        final Set<IRuntime> targetedRuntimes = getTargetedRuntimes();
        
        for( IProjectFacet f : ProjectFacetsManager.getProjectFacets() )
        {
            SortedSet<IProjectFacetVersion> versions = null;
            
            for( IProjectFacetVersion fv : f.getVersions() )
            {
                boolean available = true;
                
                if( this.project == null || ! this.project.hasProjectFacet( fv ) )
                {
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
                }
                
                if( available )
                {
                    if( versions == null )
                    {
                        versions = new TreeSet<IProjectFacetVersion>();
                        newAvailableFacets.put( f, versions );
                    }
                    
                    versions.add( fv );
                }
            }
        }
        
        // Add any unknown facets that are referenced by the project.
        
        if( this.project != null )
        {
            for( IProjectFacetVersion fv : this.project.getProjectFacets() )
            {
                if( fv.getPluginId() == null )
                {
                    final IProjectFacet f = fv.getProjectFacet();
                    SortedSet<IProjectFacetVersion> versions = newAvailableFacets.get( f );
                    
                    if( versions == null )
                    {
                        versions = new TreeSet<IProjectFacetVersion>();
                        newAvailableFacets.put( f, versions );
                    }
                    
                    versions.add( fv );
                }
            }
        }
        
        // If there is a change to the available facets, apply the change and notify the listeners.
        
        if( ! this.availableFacets.equals( newAvailableFacets ) )
        {
            for( Map.Entry<IProjectFacet,SortedSet<IProjectFacetVersion>> entry 
                 : newAvailableFacets.entrySet() )
            {
                entry.setValue( Collections.unmodifiableSortedSet( entry.getValue() ) );
            }
            
            this.availableFacets = Collections.unmodifiableMap( newAvailableFacets );
            
            notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.AVAILABLE_FACETS_CHANGED ) );
        }
    }
    
    public synchronized Set<IProjectFacetVersion> getProjectFacets()
    {
        return this.facets.getUnmodifiable();
    }
    
    public synchronized IProjectFacetVersion getProjectFacetVersion( final IProjectFacet f )
    {
        return this.facets.get( f );
    }
    
    public synchronized boolean hasProjectFacet( final IProjectFacet f )
    {
        return this.facets.containsKey( f );
    }

    public synchronized boolean hasProjectFacet( final IProjectFacetVersion fv )
    {
        return this.facets.contains( fv );
    }
    
    public synchronized void setProjectFacets( final Set<IProjectFacetVersion> facets )
    {
        final IndexedSet<IProjectFacet,IProjectFacetVersion> newProjectFacets
            = new IndexedSet<IProjectFacet,IProjectFacetVersion>();
        
        for( IProjectFacetVersion fv : facets )
        {
            newProjectFacets.add( fv.getProjectFacet(), fv );
        }
        
        final Set<IProjectFacetVersion> addedFacets = new HashSet<IProjectFacetVersion>();
        final Set<IProjectFacetVersion> removedFacets = new HashSet<IProjectFacetVersion>();
        final Set<IProjectFacetVersion> changedVersions = new HashSet<IProjectFacetVersion>();
        
        for( IProjectFacetVersion fv : newProjectFacets )
        {
            final IProjectFacetVersion currentFacetVersion = this.facets.get( fv.getProjectFacet() );
            
            if( currentFacetVersion == null )
            {
                addedFacets.add( fv );
            }
            else
            {
                if( ! fv.equals( currentFacetVersion ) )
                {
                    changedVersions.add( fv );
                }
            }
        }
        
        for( IProjectFacetVersion fv : this.facets )
        {
            if( ! newProjectFacets.containsKey( fv.getProjectFacet() ) )
            {
                removedFacets.add( fv );
            }
        }
        
        if( addedFacets.isEmpty() && removedFacets.isEmpty() && changedVersions.isEmpty() )
        {
            return;
        }
        
        setSelectedPreset( null );
        
        this.facets = newProjectFacets;
        
        final IProjectFacetsChangedEvent event
            = new ProjectFacetsChangedEvent( this, addedFacets, removedFacets,
                                             changedVersions );
        
        notifyListeners( event );
    }
    
    public synchronized void setDefaultFacetsForRuntime( final IRuntime runtime )
    {
        final Set<IProjectFacetVersion> defaultFacets;
        
        if( runtime != null )
        {
            try
            {
                defaultFacets = runtime.getDefaultFacets( getFixedProjectFacets() );
            }
            catch( CoreException e )
            {
                FacetCorePlugin.log( e );
                return;
            }
        }
        else
        {
            defaultFacets = new HashSet<IProjectFacetVersion>();
            
            for( IProjectFacet f : getFixedProjectFacets() )
            {
                defaultFacets.add( f.getDefaultVersion() );
            }
        }
        
        setProjectFacets( defaultFacets );
    }
    
    public synchronized void addProjectFacet( final IProjectFacetVersion fv )
    {
        final IProjectFacetVersion existingVersion = this.facets.get( fv.getProjectFacet() );
        
        if( existingVersion == null )
        {
            final Set<IProjectFacetVersion> newProjectFacets = new HashSet<IProjectFacetVersion>();

            newProjectFacets.addAll( this.facets );
            newProjectFacets.add( fv );
            
            setProjectFacets( newProjectFacets );
        }
        else if( existingVersion == fv )
        {
            return;
        }
        else
        {
            // TODO: needs exception msg
            throw new IllegalArgumentException();
        }
    }
    
    public synchronized void removeProjectFacet( final IProjectFacet f )
    {
        final IProjectFacetVersion fv = getProjectFacetVersion( f );
        
        if( fv != null )
        {
            removeProjectFacet( getProjectFacetVersion( f ) );
        }
    }
    
    public synchronized void removeProjectFacet( final IProjectFacetVersion fv )
    {
        final IProjectFacetVersion existingVersion = this.facets.get( fv.getProjectFacet() );
        
        if( existingVersion == null )
        {
            return;
        }
        else if( existingVersion == fv )
        {
            final Set<IProjectFacetVersion> newProjectFacets = new HashSet<IProjectFacetVersion>();

            newProjectFacets.addAll( this.facets );
            newProjectFacets.remove( fv );
            
            setProjectFacets( newProjectFacets );
        }
        else
        {
            // TODO: needs exception msg
            throw new IllegalArgumentException();
        }
    }

    public synchronized void changeProjectFacetVersion( final IProjectFacetVersion fv )
    {
        final IProjectFacetVersion existingVersion = this.facets.get( fv.getProjectFacet() );
        
        if( existingVersion == null )
        {
            // TODO: needs exception msg
            throw new IllegalArgumentException();
        }
        else if( existingVersion == fv )
        {
            return;
        }
        else
        {
            final Set<IProjectFacetVersion> newProjectFacets = new HashSet<IProjectFacetVersion>();

            newProjectFacets.addAll( this.facets );
            newProjectFacets.remove( existingVersion );
            newProjectFacets.add( fv );
            
            setProjectFacets( newProjectFacets );
        }
    }
    
    private synchronized Set<IProjectFacetVersion> getBaseProjectFacets()
    {
        if( this.project == null )
        {
            return Collections.emptySet();
        }
        else
        {
            return this.project.getProjectFacets();
        }
    }
    
    public synchronized Set<IPreset> getAvailablePresets()
    {
        return this.availablePresets.getUnmodifiable();
    }
    
    private synchronized void refreshAvailablePresets()
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
                    context.put( IDynamicPreset.CONTEXT_KEY_PRIMARY_RUNTIME, this.primaryRuntime );
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
            notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.AVAILABLE_PRESETS_CHANGED ) );
            
            if( this.selectedPreset != null && 
                ! this.availablePresets.containsKey( this.selectedPreset.getId() ) )
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
            if( preset != null )
            {
                // The following line keeps the setProjectFacets() call that comes next from 
                // causing a preset change event from being generated. We want to avoid firing 
                // two preset change events while presenting a consistent data structure (the 
                // old preset isn't selected) to event handlers listening on the facet change 
                // event.
                
                this.selectedPreset = null;
                
                setProjectFacets( preset.getProjectFacets() );
            }
            
            this.selectedPreset = preset;
            
            notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.SELECTED_PRESET_CHANGED ) );
        }
    }
    
    public synchronized Set<IRuntime> getTargetableRuntimes()
    {
        return this.targetableRuntimes;
    }
    
    public synchronized boolean isTargetable( final IRuntime runtime )
    {
        return this.targetableRuntimes.contains( runtime );
    }
    
    public synchronized void refreshTargetableRuntimes()
    {
        final Set<IRuntime> result = new HashSet<IRuntime>();
        
        for( IRuntime r : RuntimeManager.getRuntimes() )
        {
            boolean ok;
            
            if( this.project != null && 
                this.project.getTargetedRuntimes().contains( r ) )
            {
                ok = true;
            }
            else
            {
                ok = true;
                
                for( IProjectFacetVersion fv : this.facets )
                {
                    if( ! r.supports( fv ) )
                    {
                        ok = false;
                        break;
                    }
                }
            }

            if( ok )
            {
                result.add( r );
            }
        }
        
        if( ! this.targetableRuntimes.equals( result ) )
        {
            this.targetableRuntimes.clear();
            this.targetableRuntimes.addAll( result );
            notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.TARGETABLE_RUNTIMES_CHANGED ) );
            
            final List<IRuntime> toRemove = new ArrayList<IRuntime>();
            
            for( IRuntime r : this.targetedRuntimes )
            {
                if( ! this.targetableRuntimes.contains( r ) )
                {
                    toRemove.add( r );
                }
            }
            
            this.targetedRuntimes.removeAll( toRemove );
            
            if( ! toRemove.isEmpty() )
            {
                notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.TARGETED_RUNTIMES_CHANGED ) );
                
                if( this.primaryRuntime != null && 
                    ! this.targetableRuntimes.contains( this.primaryRuntime ) )
                {
                    autoAssignPrimaryRuntime();
                }
            }
        }
    }
    
    public synchronized Set<IRuntime> getTargetedRuntimes()
    {
        return this.targetedRuntimes;
    }
    
    public synchronized boolean isTargeted( final IRuntime runtime )
    {
        return this.targetedRuntimes.contains( runtime );
    }
    
    public synchronized void setTargetedRuntimes( final Set<IRuntime> runtimes )
    {
        if( ! this.targetedRuntimes.equals( runtimes ) )
        {
            this.targetedRuntimes.clear();
            
            for( IRuntime r : runtimes )
            {
                if( this.targetableRuntimes.contains( r ) )
                {
                    this.targetedRuntimes.add( r );
                }
            }
            
            notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.TARGETED_RUNTIMES_CHANGED ) );
            
            if( this.primaryRuntime == null ||
                ! this.targetedRuntimes.contains( this.primaryRuntime ) )
            {
                autoAssignPrimaryRuntime();
            }
        }
    }
    
    public synchronized void addTargetedRuntime( final IRuntime runtime )
    {
        if( runtime == null )
        {
            throw new NullPointerException();
        }
        else
        {
            this.targetedRuntimes.add( runtime );
            notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.TARGETED_RUNTIMES_CHANGED ) );
            
            if( this.primaryRuntime == null )
            {
                this.primaryRuntime = runtime;
                notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.PRIMARY_RUNTIME_CHANGED ) );
            }
        }
    }
    
    public synchronized void removeTargetedRuntime( final IRuntime runtime )
    {
        if( runtime == null )
        {
            throw new NullPointerException();
        }
        else
        {
            if( this.targetedRuntimes.remove( runtime ) )
            {
                notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.TARGETED_RUNTIMES_CHANGED ) );
                
                if( runtime.equals( this.primaryRuntime ) )
                {
                    autoAssignPrimaryRuntime();
                }
            }
        }
    }
    
    public synchronized IRuntime getPrimaryRuntime()
    {
        return this.primaryRuntime;
    }
    
    public synchronized void setPrimaryRuntime( final IRuntime runtime )
    {
        if( ! equals( this.primaryRuntime, runtime ) )
        {
            if( runtime == null && this.targetedRuntimes.size() > 0 )
            {
                throw new IllegalArgumentException();
            }
            
            if( this.targetedRuntimes.contains( runtime ) )
            {
                this.primaryRuntime = runtime;
            }
            
            notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.PRIMARY_RUNTIME_CHANGED ) );
        }
    }
    
    private void autoAssignPrimaryRuntime()
    {
        if( this.targetedRuntimes.isEmpty() )
        {
            this.primaryRuntime = null;
        }
        else
        {
            // Pick one to be the primary. No special semantics as to which 
            // one.
            
            this.primaryRuntime = this.targetedRuntimes.iterator().next();
        }

        notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.PRIMARY_RUNTIME_CHANGED ) );
    }
    
    public synchronized Set<Action> getProjectFacetActions()
    {
        return this.actions;
    }
    
    public synchronized Action getProjectFacetAction( final Action.Type type,
                                                      final IProjectFacetVersion fv )
    {
        return getProjectFacetAction( this.actions, type, fv );
    }

    public synchronized Action getProjectFacetAction( final Action.Type type,
                                                      final IProjectFacet f )
    {
        return getProjectFacetAction( this.actions, type, f );
    }

    private static Action getProjectFacetAction( final Set<Action> actions,
                                                 final Action.Type type,
                                                 final IProjectFacetVersion fv )
    {
        for( Action action : actions )
        {
            if( action.getType() == type && action.getProjectFacetVersion() == fv )
            {
                return action;
            }
        }
        
        return null;
    }
    
    private static Action getProjectFacetAction( final Set<Action> actions,
                                                 final Action.Type type,
                                                 final IProjectFacet f )
    {
        for( Action action : actions )
        {
            if( action.getType() == type && 
                action.getProjectFacetVersion().getProjectFacet() == f )
            {
                return action;
            }
        }
        
        return null;
    }
    
    private synchronized Action createProjectFacetAction( final Set<Action> actions,
                                                          final Action.Type type,
                                                          final IProjectFacetVersion fv )
    {
        Action action = getProjectFacetAction( actions, type, fv );
        
        if( action == null )
        {
            final Set<IProjectFacetVersion> base = getBaseProjectFacets();
            
            Object config = null;
            
            if( fv.supports( base, type ) )
            {
                try
                {
                    final IProjectFacet f = fv.getProjectFacet();
                    
                    action = getProjectFacetAction( actions, type, f );
                    
                    if( action != null )
                    {
                        final IProjectFacetVersion current
                            = action.getProjectFacetVersion();
                        
                        if( fv.supports( base, type ) &&
                            current.supports( base, type ) &&
                            fv.getActionDefinition( base, type )
                              == current.getActionDefinition( base, type ) )
                        {
                            config = action.getConfig();
                        }
                    }
                    
                    if( config == null )
                    {
                        final IActionDefinition def = fv.getActionDefinition( base, type );
                        config = def.createConfigObject();
                    }

                    if( config != null )
                    {
                        IActionConfig c1 = null;
                        
                        if( config instanceof IActionConfig )
                        {
                            c1 = (IActionConfig) config;
                        }
                        else if( config != null )
                        {
                            final IAdapterManager m 
                                = Platform.getAdapterManager();
                            
                            final String t
                                = IActionConfig.class.getName();
                            
                            c1 = (IActionConfig) m.loadAdapter( config, t );
                        }
                        
                        if( c1 != null )
                        {
                            c1.setProjectName( getProjectName() );
                            c1.setVersion( fv );
                        }
                        
                        ActionConfig c2 = null;
                        
                        if( config instanceof ActionConfig )
                        {
                            c2 = (ActionConfig) config;
                        }
                        else if( config != null )
                        {
                            final IAdapterManager m = Platform.getAdapterManager();
                            final String t = ActionConfig.class.getName();
                            c2 = (ActionConfig) m.loadAdapter( config, t );
                        }
                        
                        if( c2 != null )
                        {
                            c2.setFacetedProjectWorkingCopy( this );
                            c2.setProjectFacetVersion( fv );
                        }
                    }
                }
                catch( CoreException e )
                {
                    FacetCorePlugin.log( e );
                }
            }

            action = new Action( type, fv, config );
        }
        
        return action;
    }
    
    private synchronized void refreshProjectFacetActions()
    {
        final Set<IProjectFacetVersion> base = getBaseProjectFacets();
        final Set<IProjectFacetVersion> sel = getProjectFacets();
        final Set<Action> old = new HashSet<Action>( this.actions );
        final Set<Action> newActions = new HashSet<Action>();
        
        // What has been removed?
        
        for( IProjectFacetVersion fv : base )
        {
            if( ! sel.contains( fv ) )
            {
                newActions.add( createProjectFacetAction( old, Action.Type.UNINSTALL, fv ) );
            }
        }

        // What has been added?

        for( IProjectFacetVersion fv : sel )
        {
            if( ! base.contains( fv ) )
            {
                newActions.add( createProjectFacetAction( old, Action.Type.INSTALL, fv ) );
            }
        }
        
        // Coalesce uninstall/install pairs into version change actions, if
        // possible.
        
        final Set<Action> toadd = new HashSet<Action>();
        final Set<Action> toremove = new HashSet<Action>();
        
        for( Action action1 : newActions )
        {
            for( Action action2 : newActions )
            {
                if( action1.getType() == Action.Type.UNINSTALL &&
                    action2.getType() == Action.Type.INSTALL )
                {
                    final IProjectFacetVersion f1 = action1.getProjectFacetVersion();
                    final IProjectFacetVersion f2 = action2.getProjectFacetVersion();
                    
                    if( f1.getProjectFacet() == f2.getProjectFacet() )
                    {
                        toremove.add( action1 );
                        toremove.add( action2 );
                        toadd.add( createProjectFacetAction( old, Action.Type.VERSION_CHANGE, f2 ) );
                    }
                }
            }
        }
        
        newActions.removeAll( toremove );
        newActions.addAll( toadd );
        
        this.actions = newActions;
    }
    
    public synchronized void addListener( final IFacetedProjectListener listener,
                                          final IFacetedProjectEvent.Type... types )
    {
        if( types.length == 0 )
        {
            throw new IllegalArgumentException();
        }
        
        for( IFacetedProjectEvent.Type type : types )
        {
            this.listeners.get( type ).add( listener );
        }
    }

    public synchronized void removeListener( final IFacetedProjectListener listener )
    {
        for( List<IFacetedProjectListener> listeners : this.listeners.values() )
        {
            listeners.remove( listener );
        }
    }

    private synchronized void notifyListeners( final IFacetedProjectEvent event )
    {
        final List<IFacetedProjectListener> listeners = this.listeners.get( event.getType() );
        
        for( IFacetedProjectListener listener : listeners )
        {
            listener.handleEvent( event );
        }
    }
    
    public synchronized IStatus validate( final IProgressMonitor monitor )
    {
        return validate();
    }
    
    public synchronized IStatus validate()
    {
        if( this.problems.isEmpty() )
        {
            return Status.OK_STATUS;
        }
        else
        {
            final IStatus[] probs = this.problems.toArray( new IStatus[ this.problems.size() ] );
            return Constraint.createMultiStatus( probs );
        }
    }
    
    private synchronized void performValidation()
    {
        final Set<IProjectFacetVersion> base = getBaseProjectFacets();

        final List<IStatus> probs = new ArrayList<IStatus>();
        final MultiStatus ms = (MultiStatus) ProjectFacetsManager.check( base, this.actions );
        
        for( IStatus st : ms.getChildren() )
        {
            probs.add( st );
        }
        
        for( IProjectFacetVersion fv : base )
        {
            final IProjectFacet f = fv.getProjectFacet();
            
            String msg = null; 
            
            if( f.getPluginId() == null )
            {
                msg = NLS.bind( Resources.facetNotFound, f.getId() );
            }
            else if( fv.getPluginId() == null )
            {
                msg = NLS.bind( Resources.facetVersionNotFound, f.getId(), 
                                fv.getVersionString() );
            }
            
            if( msg != null )
            {
                final IStatus sub
                    = new Status( IStatus.WARNING, FacetCorePlugin.PLUGIN_ID, 0, msg, null );
                
                probs.add( sub );
            }
        }
        
        for( IRuntime r : getTargetedRuntimes() )
        {
            for( IProjectFacetVersion fv : getProjectFacets() )
            {
                if( ! r.supports( fv ) )
                {
                    final String msg
                        = NLS.bind( Resources.facetNotSupportedByTarget, fv.toString(), 
                                    r.getLocalizedName() );
                    
                    final IStatus sub
                        = new Status( IStatus.ERROR, FacetCorePlugin.PLUGIN_ID, 0, msg, null );
                    
                    probs.add( sub );
                }
            }
        }
        
        if( ! probs.equals( this.problems ) )
        {
            this.problems = probs;
            notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.VALIDATION_PROBLEMS_CHANGED ) );
        }
    }
    
    public synchronized void commitChanges( final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        final SubMonitor pm = SubMonitor.convert( monitor, 24 );
        
        try
        {
            // Create the project, if it doesn't exist already.
            
            final IFacetedProject fpj;
            
            if( this.project == null )
            {
                fpj = ProjectFacetsManager.create( this.projectName,
                                                   this.projectLocation, submon( pm, 2 ) );
            }
            else
            {
                fpj = this.project;
            }
            
            // Figure out whether we can set runtimes before applying facet actions. This is better
            // for performance reasons, but may not work if the project contains facets that are
            // not supported by the new runtime. You can get into this situation if the user tries
            // to simultaneously uninstall a facet and select a different runtime. The fallback
            // solution for this situation is to set the targeted runtimes to an empty list first,
            // then apply the facet actions, and then set the targeted runtimes to the new list.
            // This is more drastic than necessary in all situations, but it is not clear that
            // implementing additional optimizations is necessary either.
            
            boolean canSetRuntimesFirst = true;
            
            for( IProjectFacetVersion fv : getProjectFacets() )
            {
                for( IRuntime r : getTargetedRuntimes() )
                {
                    if( ! r.supports( fv ) )
                    {
                        canSetRuntimesFirst = false;
                        break;
                    }
                }
                
                if( ! canSetRuntimesFirst )
                {
                    break;
                }
            }
            
            pm.subTask( Resources.taskConfiguringRuntimes );
            
            if( canSetRuntimesFirst )
            {
                fpj.setTargetedRuntimes( getTargetedRuntimes(), submon( pm, 2 ) );
                
                if( fpj.getPrimaryRuntime() != null )
                {
                    fpj.setPrimaryRuntime( getPrimaryRuntime(), submon( pm, 2 ) );
                }
                else
                {
                    pm.worked( 2 );
                }
            }
            else
            {
                final Set<IRuntime> emptySet = Collections.emptySet();
                fpj.setTargetedRuntimes( emptySet, submon( pm, 2 ) );
            }
            
            fpj.modify( getProjectFacetActions(), 
                        pm.newChild( 16, SubMonitor.SUPPRESS_SETTASKNAME ) );
            
            if( ! canSetRuntimesFirst )
            {
                pm.subTask( Resources.taskConfiguringRuntimes );
                
                fpj.setTargetedRuntimes( getTargetedRuntimes(), submon( pm, 2 ) );
                
                if( fpj.getPrimaryRuntime() != null )
                {
                    fpj.setPrimaryRuntime( getPrimaryRuntime(), submon( pm, 2 ) );
                }
                else
                {
                    pm.worked( 2 );
                }
            }
            
            fpj.setFixedProjectFacets( getFixedProjectFacets() );
            pm.worked( 2 );
        }
        finally
        {
            pm.done();
        }
    }
    
    public synchronized void mergeChanges( final IFacetedProjectWorkingCopy fpjwc )
    {
        // TODO: Both this method and the clone method ignore the action config objects.
        
        synchronized( fpjwc )
        {
            setProjectName( fpjwc.getProjectName() );
            setProjectLocation( fpjwc.getProjectLocation() );
            setFixedProjectFacets( fpjwc.getFixedProjectFacets() );
            setProjectFacets( fpjwc.getProjectFacets() );
            setTargetedRuntimes( fpjwc.getTargetedRuntimes() );
            setPrimaryRuntime( fpjwc.getPrimaryRuntime() );
            
            final IPreset selectedPreset = fpjwc.getSelectedPreset();
            
            if( selectedPreset != null )
            {
                setSelectedPreset( selectedPreset.getId() );
            }
        }
    }
    
    public synchronized IFacetedProjectWorkingCopy clone()
    {
        final FacetedProjectWorkingCopy clone = new FacetedProjectWorkingCopy( this.project );
        
        clone.setProjectName( getProjectName() );
        clone.setProjectLocation( getProjectLocation() );
        clone.setFixedProjectFacets( getFixedProjectFacets() );
        clone.setProjectFacets( getProjectFacets() );
        clone.setTargetedRuntimes( getTargetedRuntimes() );
        clone.setPrimaryRuntime( getPrimaryRuntime() );
        
        final IPreset selectedPreset = getSelectedPreset();
        
        if( selectedPreset != null )
        {
            clone.setSelectedPreset( selectedPreset.getId() );
        }
        
        return clone;
    }
    
    public void dispose()
    {
        synchronized( this.disposeTasks )
        {
            for( Runnable task : this.disposeTasks )
            {
                try
                {
                    task.run();
                }
                catch( Exception e )
                {
                    FacetCorePlugin.log( e );
                }
            }
        }
    }
    
    public void addDisposeTask( final Runnable task )
    {
        synchronized( this.disposeTasks )
        {
            this.disposeTasks.add( task );
        }
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
    
    private static SubMonitor submon( final SubMonitor parent,
                                      final int ticks )
    {
        return parent.newChild( ticks, SubMonitor.SUPPRESS_ALL_LABELS );
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String couldNotSelectPreset;
        public static String facetNotFound;
        public static String facetVersionNotFound;
        public static String facetNotSupportedByTarget;
        public static String taskConfiguringRuntimes;
        
        static
        {
            initializeMessages( FacetedProjectWorkingCopy.class.getName(), 
                                Resources.class );
        }
    }

}
