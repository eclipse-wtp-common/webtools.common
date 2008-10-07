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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.ActionConfig;
import org.eclipse.wst.common.project.facet.core.DefaultConfigurationPresetFactory;
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
import org.eclipse.wst.common.project.facet.core.MinimalConfigurationPresetFactory;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectFrameworkEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectFrameworkListener;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetsChangedEvent;
import org.eclipse.wst.common.project.facet.core.events.internal.FacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.internal.ProjectFacetsChangedEvent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.common.project.facet.core.util.internal.IndexedSet;
import org.eclipse.wst.common.project.facet.core.util.internal.MiscUtil;
import org.eclipse.wst.common.project.facet.core.util.internal.StatusWrapper;

/**
 * @since 3.0
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class FacetedProjectWorkingCopy

    implements IFacetedProjectWorkingCopy
    
{
    private static final SortedSet<IProjectFacetVersion> EMPTY_SORTED_FV_SET
        = Collections.unmodifiableSortedSet( new TreeSet<IProjectFacetVersion>() );
    
    /**
     * The object that's used internally for synchronizing access to the data structure.
     */
    
    private Object lock;
    
    /**
     * The name of the project in the scenario where the working copy is for a
     * project that doesn't exist yet, <code>null</code> otherwise.
     */
    
    private String projectName;
    
    /**
     * The validation status of the project name. If the project name is acceptable, the status
     * severity is going to be IStatus.OK.
     */
    
    private IStatus projectNameValidation;
    
    /**
     * The location of the project in the scenario where the working copy is for a
     * project that doesn't exist yet, <code>null</code> otherwise.
     */
    
    private IPath projectLocation;
    private IFacetedProject project;
    private Set<IProjectFacet> fixedFacets;
    private IndexedSet<IProjectFacet,IProjectFacetVersion> facets;
    private Map<IProjectFacet,SortedSet<IProjectFacetVersion>> availableFacets;
    private IndexedSet<String,IPreset> availablePresets;
    private String selectedPresetId;
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

    /**
     * Tracks whether or not event notification is currently suspended. This counter is
     * incremented by the suspendEventNotification() method and decremented by the
     * resumeEventNotification() method. Once the counter reaches zero, any queued events
     * can be delivered.
     */
    
    private int suspendEventNoticationCounter;
    
    /**
     * Tracks the events that are queued while event notification is suspended.
     */
    
    private final List<IFacetedProjectEvent> queuedEvents;
    
    public FacetedProjectWorkingCopy( final IFacetedProject project )
    {
        this.lock = new Object();
        this.projectName = null;
        this.projectNameValidation = Status.OK_STATUS;
        this.projectLocation = null;
        this.project = project;
        this.actions = Collections.emptySet();
        this.problems = Collections.emptyList();
        this.fixedFacets = Collections.emptySet();
        this.facets = new IndexedSet<IProjectFacet,IProjectFacetVersion>();
        this.availableFacets = Collections.emptyMap();
        this.availablePresets = new IndexedSet<String,IPreset>();
        this.selectedPresetId = null;
        this.targetableRuntimes = new CopyOnWriteArraySet<IRuntime>();
        this.targetedRuntimes = new CopyOnWriteArraySet<IRuntime>();
        this.primaryRuntime = null;
        this.disposeTasks = new ArrayList<Runnable>();
        
        this.listeners = new HashMap<IFacetedProjectEvent.Type,List<IFacetedProjectListener>>();
        
        for( IFacetedProjectEvent.Type eventType : IFacetedProjectEvent.Type.values() )
        {
            this.listeners.put( eventType, new CopyOnWriteArrayList<IFacetedProjectListener>() );
        }
        
        this.suspendEventNoticationCounter = 0;
        this.queuedEvents = new ArrayList<IFacetedProjectEvent>();
        
        refreshAvailableFacets();
        
        if( this.project != null )
        {
            setFixedProjectFacets( this.project.getFixedProjectFacets() );
        }

        refreshAvailablePresets();
        
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
                final Thread runtimesRefreshThread = new Thread()
                {
                    public void run()
                    {
                        // Suspending event notification to make sure that the internal list of
                        // targetable runtimes is updated before the AVAILABLE_RUNTIMES_CHANGED
                        // event is received by listeners.
                        
                        suspendEventNotification();
                        
                        try
                        {
                            final IFacetedProjectEvent event
                                = new FacetedProjectEvent( FacetedProjectWorkingCopy.this, 
                                                           IFacetedProjectEvent.Type.AVAILABLE_RUNTIMES_CHANGED );
                            
                            notifyListeners( event );
                            
                            refreshTargetableRuntimes();
                        }
                        finally
                        {
                            resumeEventNotification();
                        }
                    }
                };
                
                runtimesRefreshThread.start();
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
    
    public String getProjectName()
    {
        synchronized( this.lock )
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
    }
    
    public void setProjectName( final String name )
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
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
                        
                        final IWorkspace ws = ResourcesPlugin.getWorkspace();
                        
                        final IStatus validateNameResult    
                            = ws.validateName( this.projectName, IResource.PROJECT );
                        
                        if( ! this.projectNameValidation.equals( validateNameResult ) )
                        {
                            this.projectNameValidation = validateNameResult;
                            notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.VALIDATION_PROBLEMS_CHANGED ) );
                        }
                    }
                }
                else
                {
                    throw new IllegalArgumentException(); // TODO: needs message
                }
            }
        }
        finally
        {
            resumeEventNotification();
        }
    }
    
    public IPath getProjectLocation()
    {
        synchronized( this.lock )
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
    }
    
    public void setProjectLocation( final IPath location )
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
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
        }
        finally
        {
            resumeEventNotification();
        }
    }
    
    public IProject getProject()
    {
        final IFacetedProject fproj = getFacetedProject();
        
        if( fproj == null )
        {
            return null;
        }
        else
        {
            return fproj.getProject();
        }
    }
    
    public IFacetedProject getFacetedProject()
    {
        synchronized( this.lock )
        {
            if( this.project == null && this.projectName != null )
            {
                final IProject pj = ResourcesPlugin.getWorkspace().getRoot().getProject( this.projectName );
                
                if( pj != null && pj.exists() )
                {
                    try
                    {
                        this.project = ProjectFacetsManager.create( pj );
                    }
                    catch( CoreException e )
                    {
                        FacetCorePlugin.log( e );
                    }
                }
            }
            
            return this.project;
        }
    }
    
    public Set<IProjectFacet> getFixedProjectFacets()
    {
        synchronized( this.lock )
        {
            return this.fixedFacets;
        }
    }
    
    public boolean isFixedProjectFacet( final IProjectFacet facet )
    {
        synchronized( this.lock )
        {
            return this.fixedFacets.contains( facet );
        }
    }
    
    public void setFixedProjectFacets( final Set<IProjectFacet> fixed )
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
            {
                if( this.fixedFacets.equals( fixed ) )
                {
                    return;
                }
                
                final Set<IProjectFacetVersion> newFacets 
                    = new HashSet<IProjectFacetVersion>( getProjectFacets() );
                
                for( IProjectFacet f : fixed )
                {
                    final IProjectFacetVersion currentVersion = getProjectFacetVersion( f );
                    
                    if( currentVersion == null && f.getVersions().size() > 0 )
                    {
                        IProjectFacetVersion fv = f.getDefaultVersion();
                        
                        if( ! isFacetAvailable( fv ) )
                        {
                            fv = getHighestAvailableVersion( f );
                        }
                        
                        newFacets.add( fv );
                    }
                }
                
                setProjectFacets( newFacets );
                
                this.fixedFacets 
                    = Collections.unmodifiableSet( new HashSet<IProjectFacet>( fixed ) );
                
                notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.FIXED_FACETS_CHANGED ) );
                
                refreshAvailableFacets();
                refreshAvailablePresets();
            }
        }
        finally
        {
            resumeEventNotification();
        }
    }
    
    public Map<IProjectFacet,SortedSet<IProjectFacetVersion>> getAvailableFacets()
    {
        synchronized( this.lock )
        {
            return this.availableFacets;
        }
    }
    
    public boolean isFacetAvailable( final IProjectFacet f )
    {
        synchronized( this.lock )
        {
            return this.availableFacets.containsKey( f );
        }
    }
    
    public boolean isFacetAvailable( final IProjectFacetVersion fv )
    {
        synchronized( this.lock )
        {
            final Set<IProjectFacetVersion> versions 
                = this.availableFacets.get( fv.getProjectFacet() );
            
            return ( versions != null && versions.contains( fv ) );
        }
    }
    
    public SortedSet<IProjectFacetVersion> getAvailableVersions( final IProjectFacet f )
    {
        synchronized( this.lock )
        {
            SortedSet<IProjectFacetVersion> availableVersions = this.availableFacets.get( f );
            
            if( availableVersions == null )
            {
                availableVersions = EMPTY_SORTED_FV_SET;
            }
            
            return availableVersions;
        }
    }
    
    public IProjectFacetVersion getHighestAvailableVersion( final IProjectFacet f )
    {
        synchronized( this.lock )
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
    }
    
    private void refreshAvailableFacets()
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

            refreshAvailablePresets();
        }
    }
    
    public Set<IProjectFacetVersion> getProjectFacets()
    {
        synchronized( this.lock )
        {
            return this.facets.getUnmodifiable();
        }
    }
    
    public IProjectFacetVersion getProjectFacetVersion( final IProjectFacet f )
    {
        synchronized( this.lock )
        {
            return this.facets.get( f );
        }
    }
    
    public boolean hasProjectFacet( final IProjectFacet f )
    {
        synchronized( this.lock )
        {
            return this.facets.containsKey( f );
        }
    }

    public boolean hasProjectFacet( final IProjectFacetVersion fv )
    {
        synchronized( this.lock )
        {
            return this.facets.contains( fv );
        }
    }
    
    public void setProjectFacets( final Set<IProjectFacetVersion> facets )
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
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
                    final IProjectFacetVersion currentFacetVersion 
                        = this.facets.get( fv.getProjectFacet() );
                    
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
                
                notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.PROJECT_MODIFIED ) );
                
                refreshTargetableRuntimes();
                refreshProjectFacetActions();
                refreshAvailablePresets();
                performValidation();
            }
        }
        finally
        {
            resumeEventNotification();
        }
    }
    
    public void addProjectFacet( final IProjectFacetVersion fv )
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
            {
                final IProjectFacetVersion existingVersion 
                    = this.facets.get( fv.getProjectFacet() );
                
                if( existingVersion == null )
                {
                    final Set<IProjectFacetVersion> newProjectFacets 
                        = new HashSet<IProjectFacetVersion>();
        
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
        }
        finally
        {
            resumeEventNotification();
        }
    }
    
    public void removeProjectFacet( final IProjectFacet f )
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
            {
                final IProjectFacetVersion fv = getProjectFacetVersion( f );
                
                if( fv != null )
                {
                    removeProjectFacet( getProjectFacetVersion( f ) );
                }
            }
        }
        finally
        {
            resumeEventNotification();
        }
    }
    
    public void removeProjectFacet( final IProjectFacetVersion fv )
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
            {
                final IProjectFacetVersion existingVersion 
                    = this.facets.get( fv.getProjectFacet() );
                
                if( existingVersion == null )
                {
                    return;
                }
                else if( existingVersion == fv )
                {
                    final Set<IProjectFacetVersion> newProjectFacets 
                        = new HashSet<IProjectFacetVersion>();
        
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
        }
        finally
        {
            resumeEventNotification();
        }
    }

    public void changeProjectFacetVersion( final IProjectFacetVersion fv )
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
            {
                final IProjectFacetVersion existingVersion 
                    = this.facets.get( fv.getProjectFacet() );
                
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
                    final Set<IProjectFacetVersion> newProjectFacets 
                        = new HashSet<IProjectFacetVersion>();
        
                    newProjectFacets.addAll( this.facets );
                    newProjectFacets.remove( existingVersion );
                    newProjectFacets.add( fv );
                    
                    setProjectFacets( newProjectFacets );
                }
            }
        }
        finally
        {
            resumeEventNotification();
        }
    }
    
    private Set<IProjectFacetVersion> getBaseProjectFacets()
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
    
    public Set<IPreset> getAvailablePresets()
    {
        synchronized( this.lock )
        {
            return this.availablePresets.getUnmodifiable();
        }
    }
    
    private void refreshAvailablePresets()
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
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
                            
                            context.put( IDynamicPreset.CONTEXT_KEY_FACETED_PROJECT, this );
                            
                            context.put( IDynamicPreset.CONTEXT_KEY_PRIMARY_RUNTIME, 
                                         this.primaryRuntime );
                            
                            context.put( IDynamicPreset.CONTEXT_KEY_FIXED_FACETS, 
                                         this.fixedFacets );
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
                    
                    if( this.selectedPresetId != null && 
                        ! this.availablePresets.containsKey( this.selectedPresetId ) )
                    {
                        setSelectedPreset( null );
                    }
                }
            }
        }
        finally
        {
            resumeEventNotification();
        }
    }
    
    public IPreset getSelectedPreset()
    {
        synchronized( this.lock )
        {
            if( this.selectedPresetId != null )
            {
                return this.availablePresets.get( this.selectedPresetId );
            }
            else
            {
                return null;
            }
        }
    }
    
    public void setSelectedPreset( final String presetId )
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
            {
                if( presetId != null && ! this.availablePresets.containsKey( presetId ) )
                {
                    final String msg = Resources.bind( Resources.couldNotSelectPreset, presetId ); 
                    throw new IllegalArgumentException( msg );
                }
                
                final IPreset preset = this.availablePresets.get( presetId );
                
                if( ! equals( this.selectedPresetId, presetId ) || 
                    ( preset != null && ! equals( preset.getProjectFacets(), getProjectFacets() ) ) )
                {
                    if( preset != null )
                    {
                        // The following line keeps the setProjectFacets() call that comes next from 
                        // causing a preset change event from being generated. We want to avoid 
                        // firing two preset change events while presenting a consistent data 
                        // structure (the old preset isn't selected) to event handlers listening on 
                        // the facet change event.
                        
                        this.selectedPresetId = null;
                        
                        setProjectFacets( preset.getProjectFacets() );
                    }
                    
                    this.selectedPresetId = presetId;
                    
                    notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.SELECTED_PRESET_CHANGED ) );
                }
            }
        }
        finally
        {
            resumeEventNotification();
        }
    }
    
    public IPreset getDefaultConfiguration()
    {
        synchronized( this.lock )
        {
            return this.availablePresets.get( DefaultConfigurationPresetFactory.PRESET_ID );
        }
    }

    public IPreset getMinimalConfiguration()
    {
        synchronized( this.lock )
        {
            return this.availablePresets.get( MinimalConfigurationPresetFactory.PRESET_ID );
        }
    }

    public Set<IRuntime> getTargetableRuntimes()
    {
        synchronized( this.lock )
        {
            return this.targetableRuntimes;
        }
    }
    
    public boolean isTargetable( final IRuntime runtime )
    {
        synchronized( this.lock )
        {
            return this.targetableRuntimes.contains( runtime );
        }
    }
    
    public void refreshTargetableRuntimes()
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
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
                        refreshAvailableFacets();
                        
                        if( this.primaryRuntime != null && 
                            ! this.targetableRuntimes.contains( this.primaryRuntime ) )
                        {
                            autoAssignPrimaryRuntime();
                        }
                    }
                }
            }
        }
        finally
        {
            resumeEventNotification();
        }
    }
    
    public Set<IRuntime> getTargetedRuntimes()
    {
        synchronized( this.lock )
        {
            return this.targetedRuntimes;
        }
    }
    
    public boolean isTargeted( final IRuntime runtime )
    {
        synchronized( this.lock )
        {
            return this.targetedRuntimes.contains( runtime );
        }
    }
    
    public void setTargetedRuntimes( final Set<IRuntime> runtimes )
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
            {
                if( ! this.targetedRuntimes.equals( runtimes ) )
                {
                    this.targetedRuntimes.clear();
                    this.targetedRuntimes.addAll( runtimes );
                    
                    notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.TARGETED_RUNTIMES_CHANGED ) );
                    notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.PROJECT_MODIFIED ) );
                    refreshAvailableFacets();
                    
                    if( this.primaryRuntime == null ||
                        ! this.targetedRuntimes.contains( this.primaryRuntime ) )
                    {
                        autoAssignPrimaryRuntime();
                    }
                }
            }
        }
        finally
        {
            resumeEventNotification();
        }
    }
    
    public void addTargetedRuntime( final IRuntime runtime )
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
            {
                if( runtime == null )
                {
                    throw new NullPointerException();
                }
                else
                {
                    this.targetedRuntimes.add( runtime );
                    
                    notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.TARGETED_RUNTIMES_CHANGED ) );
                    refreshAvailableFacets();
                    
                    if( this.primaryRuntime == null )
                    {
                        this.primaryRuntime = runtime;

                        notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.PRIMARY_RUNTIME_CHANGED ) );
                        refreshAvailablePresets();
                    }
                    
                    notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.PROJECT_MODIFIED ) );
                }
            }
        }
        finally
        {
            resumeEventNotification();
        }
    }
    
    public void removeTargetedRuntime( final IRuntime runtime )
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
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
                        refreshAvailableFacets();
                        
                        if( runtime.equals( this.primaryRuntime ) )
                        {
                            autoAssignPrimaryRuntime();
                        }
                        
                        notifyListeners( new FacetedProjectEvent( this, IFacetedProjectEvent.Type.PROJECT_MODIFIED ) );
                    }
                }
            }
        }
        finally
        {
            resumeEventNotification();
        }
    }
    
    public IRuntime getPrimaryRuntime()
    {
        synchronized( this.lock )
        {
            return this.primaryRuntime;
        }
    }
    
    public void setPrimaryRuntime( final IRuntime runtime )
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
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
                    refreshAvailablePresets();
                }
            }
        }
        finally
        {
            resumeEventNotification();
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
        refreshAvailablePresets();
    }
    
    public Set<Action> getProjectFacetActions()
    {
        synchronized( this.lock )
        {
            return this.actions;
        }
    }
    
    public Action getProjectFacetAction( final IProjectFacet facet )
    {
        synchronized( this.lock )
        {
            return getProjectFacetAction( this.actions, null, facet );
        }
    }
    
    private static Action getProjectFacetAction( final Set<Action> actions,
                                                 final Action.Type type,
                                                 final IProjectFacetVersion fv )
    {
        for( Action action : actions )
        {
            if( ( type == null || action.getType() == type ) && 
                action.getProjectFacetVersion() == fv )
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
            if( ( type == null || action.getType() == type ) && 
                action.getProjectFacetVersion().getProjectFacet() == f )
            {
                return action;
            }
        }
        
        return null;
    }
    
    private Action createProjectFacetAction( final Set<Action> actions,
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
                    
                    bindProjectFacetActionConfig( config, fv );
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
    
    private void bindProjectFacetActionConfig( final Object actionConfig,
                                               final IProjectFacetVersion fv )
    {
        if( actionConfig != null )
        {
            IActionConfig c1 = null;
            
            if( actionConfig instanceof IActionConfig )
            {
                c1 = (IActionConfig) actionConfig;
            }
            else if( actionConfig != null )
            {
                final IAdapterManager m 
                    = Platform.getAdapterManager();
                
                final String t
                    = IActionConfig.class.getName();
                
                c1 = (IActionConfig) m.loadAdapter( actionConfig, t );
            }
            
            if( c1 != null )
            {
                c1.setProjectName( getProjectName() );
                
                if( fv != null )
                {
                    c1.setVersion( fv );
                }
            }
            
            ActionConfig c2 = null;
            
            if( actionConfig instanceof ActionConfig )
            {
                c2 = (ActionConfig) actionConfig;
            }
            else if( actionConfig != null )
            {
                final IAdapterManager m = Platform.getAdapterManager();
                final String t = ActionConfig.class.getName();
                c2 = (ActionConfig) m.loadAdapter( actionConfig, t );
            }
            
            if( c2 != null )
            {
                c2.setFacetedProjectWorkingCopy( this );
                
                if( fv != null )
                {
                    c2.setProjectFacetVersion( fv );
                }
            }
        }
    }
    
    private void refreshProjectFacetActions()
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
    
    public void setProjectFacetActionConfig( final IProjectFacet facet,
                                             final Object newActionConfig )
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
            {
                final Action oldAction = getProjectFacetAction( facet );
                
                if( oldAction == null )
                {
                    throw new IllegalArgumentException();
                }
                
                final IProjectFacetVersion fv = oldAction.getProjectFacetVersion();
                final Action newAction = new Action( oldAction.getType(), fv, newActionConfig );
                bindProjectFacetActionConfig( newActionConfig, fv );
                
                this.actions.remove( oldAction );
                this.actions.add( newAction );
            }
        }
        finally
        {
            resumeEventNotification();
        }
    }
    
    public void addListener( final IFacetedProjectListener listener,
                             final IFacetedProjectEvent.Type... types )
    {
        synchronized( this.lock )
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
    }

    public void removeListener( final IFacetedProjectListener listener )
    {
        synchronized( this.lock )
        {
            for( List<IFacetedProjectListener> listeners : this.listeners.values() )
            {
                listeners.remove( listener );
            }
        }
    }

    private void notifyListeners( final IFacetedProjectEvent event )
    {
        List<IFacetedProjectListener> listenersToNotify = null;
        
        synchronized( this.lock )
        {
            if( this.suspendEventNoticationCounter == 0 )
            {
                listenersToNotify = this.listeners.get( event.getType() );
            }
            else
            {
                this.queuedEvents.add( event );
            }
        }
        
        if( listenersToNotify != null )
        {
            for( IFacetedProjectListener listener : listenersToNotify )
            {
                try
                {
                    listener.handleEvent( event );
                }
                catch( Exception e )
                {
                    FacetCorePlugin.log( e );
                }
            }
        }
    }
    
    private void suspendEventNotification()
    {
        synchronized( this.lock )
        {
            this.suspendEventNoticationCounter++;
        }
    }
    
    private void resumeEventNotification()
    {
        List<IFacetedProjectEvent> eventsToFire = null;
        
        synchronized( this.lock )
        {
            this.suspendEventNoticationCounter--;
            
            if( this.suspendEventNoticationCounter == 0 )
            {
                if( ! this.queuedEvents.isEmpty() )
                {
                    eventsToFire = new ArrayList<IFacetedProjectEvent>();
                    eventsToFire.addAll( this.queuedEvents );
                    this.queuedEvents.clear();
                }
            }
            else if( this.suspendEventNoticationCounter < 0 )
            {
                throw new IllegalStateException();
            }
        }
        
        if( eventsToFire != null )
        {
            for( IFacetedProjectEvent event : eventsToFire )
            {
                notifyListeners( event );
            }
        }
    }
    
    public IStatus validate( final IProgressMonitor monitor )
    {
        return validate();
    }
    
    public IStatus validate()
    {
        synchronized( this.lock )
        {
            final MultiStatus ms = Constraint.createMultiStatus();
            
            if( ! this.projectNameValidation.isOK() )
            {
                final StatusWrapper wrapper = new StatusWrapper( this.projectNameValidation );
                wrapper.setCode( PROBLEM_PROJECT_NAME );
                
                ms.add( wrapper );
            }
            
            for( IStatus st : this.problems )
            {
                final StatusWrapper wrapper = new StatusWrapper( st );
                wrapper.setCode( PROBLEM_OTHER );
                
                ms.add( wrapper );
            }
            
            for( IRuntime runtime : this.targetedRuntimes )
            {
                final IStatus st = runtime.validate( new NullProgressMonitor() );
                
                if( ! st.isOK() )
                {
                    final String msg 
                        = Resources.bind( Resources.invalidRuntimeMsg, runtime.getName(), 
                                          st.getMessage() );
                    
                    final StatusWrapper wrapper = new StatusWrapper( st );
                    wrapper.setMessage( msg );
                    
                    ms.add( wrapper );
                }
            }

            return ms;
        }
    }
    
    private void performValidation()
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
    
    public boolean isDirty()
    {
        if( this.project == null )
        {
            return true;
        }
        else
        {
            return ! equal( this.project.getFixedProjectFacets(), getFixedProjectFacets() ) ||
                   ! equal( this.project.getProjectFacets(), getProjectFacets() ) ||
                   ! equal( this.project.getTargetedRuntimes(), getTargetedRuntimes() ) ||
                   ! equal( this.project.getPrimaryRuntime(), getPrimaryRuntime() );
        }
    }
    
    private boolean equal( final Object obj1,
                           final Object obj2 )
    {
        return MiscUtil.equal( obj1, obj2 );
    }
    
    private boolean equal( final IRuntime r1,
                           final IRuntime r2 )
    {
        if( r1 == null && r2 == null )
        {
            return true;
        }
        else if( r1 == null || r2 == null )
        {
            return false;
        }
        else
        {
            return r1.getName().equals( r2.getName() );
        }
    }
    
    private boolean equal( final Set<IRuntime> set1,
                           final Set<IRuntime> set2 )
    {
        if( set1.size() != set2.size() )
        {
            return false;
        }
        else
        {
            for( IRuntime r1 : set1 )
            {
                boolean found = false;
                
                for( IRuntime r2 : set2 )
                {
                    if( r1.getName().equals( r2.getName() ) )
                    {
                        found = true;
                        break;
                    }
                }
                
                if( ! found )
                {
                    return false;
                }
            }
            
            return true;
        }
    }
    
    public void commitChanges( final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        final SubMonitor pm = SubMonitor.convert( monitor, 100 );
        
        try
        {
            // In order to avoid deadlocks, we clone the working copy. This allows us to release
            // the lock on this working copy while running mergeChanges(). This is important since
            // mergeChanges() can call out to third-party code.
            
            final FacetedProjectWorkingCopy clone;
            
            synchronized( this.lock )
            {
                clone = (FacetedProjectWorkingCopy) clone();
            }
            
            final IFacetedProject fpj;
                
            if( this.project == null )
            {
                fpj = ProjectFacetsManager.create( this.projectName,
                                                   this.projectLocation, 
                                                   pm.newChild( 10, SubMonitor.SUPPRESS_ALL_LABELS ) );
                
                this.project = fpj;
            }
            else
            {
                fpj = clone.getFacetedProject();
                pm.worked( 10 );
            }
            
            ( (FacetedProject) fpj ).mergeChanges( clone, pm.newChild( 90 ) );
                
            // Reset the working copy so that it can be used again.
            
            synchronized( this.lock )
            {
                this.projectName = null;
                this.projectNameValidation = Status.OK_STATUS;
                this.projectLocation = null;
                
                revertChanges();
            }
        }
        finally
        {
            pm.done();
        }
    }
    
    public void mergeChanges( final IFacetedProjectWorkingCopy fpjwc )
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
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
                
                this.actions.clear();
                this.actions.addAll( ( (FacetedProjectWorkingCopy) fpjwc  ).actions );
                
                for( IFacetedProject.Action action : this.actions )
                {
                    bindProjectFacetActionConfig( action.getConfig(), null );
                }
            }
        }
        finally
        {
            resumeEventNotification();
        }
    }
    
    public void revertChanges()
    {
        suspendEventNotification();
        
        try
        {
            synchronized( this.lock )
            {
            	if( this.project != null )
            	{
                    setFixedProjectFacets( this.project.getFixedProjectFacets() );
                    setTargetedRuntimes( Collections.<IRuntime>emptySet() );
                    setProjectFacets( this.project.getProjectFacets() );
                    setTargetedRuntimes( this.project.getTargetedRuntimes() );
                    setPrimaryRuntime( this.project.getPrimaryRuntime() );
                    this.actions.clear();
            	}
            	else
            	{
            		throw new UnsupportedOperationException();
            	}
            }
        }
        finally
        {
            resumeEventNotification();
        }
    }
    
    public IFacetedProjectWorkingCopy clone()
    {
        synchronized( this.lock )
        {
            final FacetedProjectWorkingCopy clone = new FacetedProjectWorkingCopy( this.project );
            
            if( this.project == null )
            {
	            clone.setProjectName( getProjectName() );
	            clone.setProjectLocation( getProjectLocation() );
            }
            
            clone.setFixedProjectFacets( getFixedProjectFacets() );
            clone.setProjectFacets( getProjectFacets() );
            clone.setTargetedRuntimes( getTargetedRuntimes() );
            clone.setPrimaryRuntime( getPrimaryRuntime() );
            
            final IPreset selectedPreset = getSelectedPreset();
            
            if( selectedPreset != null )
            {
                clone.setSelectedPreset( selectedPreset.getId() );
            }
            
            clone.actions.clear();
            clone.actions.addAll( this.actions );

            return clone;
        }
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
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String couldNotSelectPreset;
        public static String facetNotFound;
        public static String facetVersionNotFound;
        public static String facetNotSupportedByTarget;
        public static String invalidRuntimeMsg;
        
        static
        {
            initializeMessages( FacetedProjectWorkingCopy.class.getName(), 
                                Resources.class );
        }
    }

}
