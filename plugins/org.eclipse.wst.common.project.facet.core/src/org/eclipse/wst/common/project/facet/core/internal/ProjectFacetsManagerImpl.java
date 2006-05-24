/******************************************************************************
 * Copyright (c) 2005, 2006 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IActionDefinition;
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IConstraint;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IGroup;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * The implementation of the {@see ProjectFacetsManager} abstract class.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetsManagerImpl
{
    private static final String EXTENSION_ID = "facets"; //$NON-NLS-1$

    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
    private static final String ATTR_FACET = "facet"; //$NON-NLS-1$
    private static final String ATTR_GROUP = "group"; //$NON-NLS-1$
    private static final String ATTR_ID = "id"; //$NON-NLS-1$
    private static final String ATTR_NAME = "name"; //$NON-NLS-1$
    private static final String ATTR_SOFT = "soft"; //$NON-NLS-1$
    private static final String ATTR_TYPE = "type"; //$NON-NLS-1$
    private static final String ATTR_VALUE = "value"; //$NON-NLS-1$
    private static final String ATTR_VERSION = "version"; //$NON-NLS-1$
    private static final String EL_ACTION = "action"; //$NON-NLS-1$
    private static final String EL_CATEGORY = "category"; //$NON-NLS-1$
    private static final String EL_CONFIG_FACTORY = "config-factory"; //$NON-NLS-1$
    private static final String EL_CONSTRAINT = "constraint"; //$NON-NLS-1$
    private static final String EL_DELEGATE = "delegate"; //$NON-NLS-1$
    private static final String EL_DESCRIPTION = "description"; //$NON-NLS-1$
    private static final String EL_EVENT_HANDLER = "event-handler"; //$NON-NLS-1$
    private static final String EL_FIXED = "fixed"; //$NON-NLS-1$
    private static final String EL_GROUP_MEMBER = "group-member"; //$NON-NLS-1$
    private static final String EL_LABEL = "label"; //$NON-NLS-1$
    private static final String EL_PRESET = "preset"; //$NON-NLS-1$
    private static final String EL_PROJECT_FACET = "project-facet"; //$NON-NLS-1$
    private static final String EL_PROJECT_FACET_VERSION = "project-facet-version"; //$NON-NLS-1$
    private static final String EL_PROPERTY = "property"; //$NON-NLS-1$
    private static final String EL_TEMPLATE = "template"; //$NON-NLS-1$
    private static final String EL_VERSION_COMPARATOR = "version-comparator"; //$NON-NLS-1$
    
    private static final Set facetsReportedMissing = new HashSet();
    
    private final IndexedSet facets;
    private final IndexedSet actions;
    private final IndexedSet categories;
    private final IndexedSet presets;
    private final IndexedSet templates;
    private final IndexedSet groups;
    private final Map projects;
    
    public ProjectFacetsManagerImpl()
    {
        this.facets = new IndexedSet();
        this.actions = new IndexedSet();
        this.categories = new IndexedSet();
        this.presets = new IndexedSet();
        this.templates = new IndexedSet();
        this.groups = new IndexedSet();
        this.projects = new HashMap();
        
        readMetadata();
        readUserPresets();
        
        final IWorkspace ws = ResourcesPlugin.getWorkspace();
        final IResourceChangeListener ls = new ResourceChangeListener();
        ws.addResourceChangeListener( ls, IResourceChangeEvent.POST_CHANGE );
    }
    
    public Set getProjectFacets()
    {
        return this.facets.getUnmodifiable();
    }
    
    public boolean isProjectFacetDefined( final String id )
    {
        return this.facets.containsKey( id );
    }
    
    public IProjectFacet getProjectFacet( final String id )
    {
        final IProjectFacet f 
            = (IProjectFacet) this.facets.get( id );
        
        if( f == null )
        {
            final String msg = NLS.bind( Resources.facetNotDefined, id );
            throw new IllegalArgumentException( msg );
        }
        
        return f;
    }
    
    public Set getActionDefinitions()
    {
        return this.actions.getUnmodifiable();
    }
    
    public boolean isActionDefined( final String id )
    {
        return this.actions.containsKey( id );
    }
    
    public IActionDefinition getActionDefinition( final String id )
    {
        final IActionDefinition adef
            = (IActionDefinition) this.actions.get( id );
        
        if( adef == null )
        {
            final String msg = NLS.bind( Resources.actionNotDefined, id );
            throw new IllegalArgumentException( msg );
        }
        
        return adef;
    }
    
    public Set getCategories()
    {
        return this.categories.getUnmodifiable();
    }

    public boolean isCategoryDefined( final String id )
    {
        return this.categories.containsKey( id );
    }
    
    public ICategory getCategory( final String id )
    {
        final ICategory category 
            = (ICategory) this.categories.get( id );
        
        if( category == null )
        {
            final String msg = NLS.bind( Resources.categoryNotDefined, id );
            throw new IllegalArgumentException( msg );
        }
        
        return category;
    }
    
    public Set getPresets()
    {
        return this.presets.getUnmodifiable();
    }
    
    public boolean isPresetDefined( final String id )
    {
        return this.presets.containsKey( id );
    }
    
    public IPreset getPreset( final String id )
    {
        final IPreset preset = (IPreset) this.presets.get( id );
        
        if( preset == null )
        {
            final String msg = NLS.bind( Resources.presetNotDefined, id );
            throw new IllegalArgumentException( msg );
        }
        
        return preset;
    }
    
    public IPreset definePreset( final String name,
                                 final Set facets )
    {
        return definePreset( name, "", facets, true ); //$NON-NLS-1$
    }

    public IPreset definePreset( final String name,
                                 final String description,
                                 final Set facets )
    {
        return definePreset( name, description, facets, true );
    }
    
    private IPreset definePreset( final String name,
                                  final String description,
                                  final Set facets,
                                  final boolean save )
    {
        synchronized( this.presets )
        {
            String id;
            int i = 0;
            
            do
            {
                id = ".usr." + i; //$NON-NLS-1$
                i++;
            }
            while( this.presets.containsKey( id ) );
            
            final Preset preset = new Preset();
            
            preset.setId( id );
            preset.setLabel( name );
            preset.setDescription( description == null ? "" : description ); //$NON-NLS-1$
            preset.addProjectFacet( facets );
            preset.setUserDefined( true );
            
            this.presets.add( id, preset );
            
            if( save )
            {
                saveUserPresets();
            }
            
            return preset;
        }
    }
    
    public boolean deletePreset( final IPreset preset )
    {
        synchronized( this.presets )
        {
            if( ! preset.isUserDefined() )
            {
                return false;
            }
            
            final boolean res = this.presets.delete( preset.getId() );
            
            if( res )
            {
                saveUserPresets();
            }
            
            return res;
        }
    }
    
    public Set getTemplates()
    {
        return this.templates.getUnmodifiable();
    }
    
    public boolean isTemplateDefined( final String id )
    {
        return this.templates.containsKey( id );
    }
    
    public IFacetedProjectTemplate getTemplate( final String id )
    {
        final IFacetedProjectTemplate template 
            = (IFacetedProjectTemplate) this.templates.get( id );
        
        if( template == null )
        {
            final String msg = NLS.bind( Resources.templateNotDefined, id );
            throw new IllegalArgumentException( msg );
        }
        
        return template;
    }
    
    public Set getGroups()
    {
        return this.groups.getUnmodifiable();
    }
    
    public boolean isGroupDefined( final String id )
    {
        return this.groups.containsKey( id );
    }
    
    public IGroup getGroup( final String id )
    {
        final IGroup group = (IGroup) this.groups.get( id );
        
        if( group == null )
        {
            final String msg = NLS.bind( Resources.groupNotDefined, id );
            throw new IllegalArgumentException( msg );
        }
        
        return group;
    }

    public Set getFacetedProjects()
    
        throws CoreException
        
    {
        return getFacetedProjects( null, null );
    }

    public Set getFacetedProjects( final IProjectFacet f )
    
        throws CoreException
        
    {
        return getFacetedProjects( f, null );
    }

    public Set getFacetedProjects( final IProjectFacetVersion fv )
    
        throws CoreException
        
    {
        return getFacetedProjects( null, fv );
    }

    private Set getFacetedProjects( final IProjectFacet f,
                                    final IProjectFacetVersion fv )
    
        throws CoreException
        
    {
        final IProject[] all 
            = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        
        final Set result = new HashSet();
        
        for( int i = 0; i < all.length; i++ )
        {
            final IProject proj = all[ i ];
            final IFacetedProject fproj = create( proj );
            
            if( fproj != null )
            {
                if( ( f != null && ! fproj.hasProjectFacet( f ) ) ||
                    ( fv != null && ! fproj.hasProjectFacet( fv ) ) )
                {
                    continue;
                }
                else
                {
                    result.add( fproj );
                }
            }
        }
        
        return result;
    }

    public IFacetedProject create( final IProject project )
    
        throws CoreException
        
    {
        if( project.isAccessible() &&
            project.isNatureEnabled( FacetedProjectNature.NATURE_ID ) )
        {
            synchronized( this.projects )
            {
                FacetedProject fproj 
                    = (FacetedProject) this.projects.get( project.getName() );
                
                if( fproj == null )
                {
                    fproj = new FacetedProject( project );
                    this.projects.put( project.getName(), fproj );
                }
                else
                {
                    fproj.refresh();
                }
                
                return fproj;
            }
        }

        return null;
    }

    public IFacetedProject create( final IProject project,
                                   final boolean convertIfNecessary,
                                   final IProgressMonitor monitor)
    
        throws CoreException
        
    {
        if( monitor != null )
        {
            monitor.beginTask( "", 2 ); //$NON-NLS-1$
        }
        
        try
        {
            if( project.exists() && convertIfNecessary )
            {
                IProjectDescription description = project.getDescription();
                String[] prevNatures = description.getNatureIds();
                String[] newNatures = new String[ prevNatures.length + 1 ];
                System.arraycopy( prevNatures, 0, newNatures, 0, prevNatures.length );
                newNatures[ prevNatures.length ] = FacetedProjectNature.NATURE_ID;
                description.setNatureIds( newNatures );
                
                project.setDescription( description, submon( monitor, 1 ) );
            }
            
            project.open( IResource.BACKGROUND_REFRESH, submon( monitor, 1 ) );
            
            return create( project );
        }
        finally
        {
            if( monitor != null )
            {
                monitor.done();
            }
        }
    }
    
    public IFacetedProject create( final String name,
                                   final IPath location,
                                   final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        if( monitor != null )
        {
            monitor.beginTask( "", 2 ); //$NON-NLS-1$
        }
        
        try
        {
            final IWorkspace ws = ResourcesPlugin.getWorkspace();
            final IProject project = ws.getRoot().getProject( name );
            
            final IProjectDescription desc
                = ws.newProjectDescription( name );
    
            desc.setLocation( location );
                    
            project.create( desc, submon( monitor, 1 ) );
            project.open( IResource.BACKGROUND_REFRESH, submon( monitor, 1 ) );
            
            // This is odd, but apparently nature's configure() method will only
            // be called if the setDescription() method is used. It will not be
            // called if nature is added to the project description prior to
            // calling IProject.create() method.
            
            desc.setNatureIds( new String[] { FacetedProjectNature.NATURE_ID } );
            project.setDescription( desc, null );
            
            return create( project );
        }
        finally
        {
            if( monitor != null )
            {
                monitor.done();
            }
        }
    }
    
    public IStatus check( final Set base,
                          final Set actions )
    {
        MultiStatus result = Constraint.createMultiStatus();
        
        // Verify that all of the actions are supported.
        
        for( Iterator itr = actions.iterator(); itr.hasNext(); )
        {
            final Action action = (Action) itr.next();
            
            if( ! action.getProjectFacetVersion().supports( base, action.getType() ) )
            {
                final ValidationProblem.Type ptype;
                
                if( action.getType() == Action.Type.INSTALL )
                {
                    ptype = ValidationProblem.Type.INSTALL_NOT_SUPPORTED;
                }
                else if( action.getType() == Action.Type.UNINSTALL )
                {
                    ptype = ValidationProblem.Type.UNINSTALL_NOT_SUPPORTED;
                }
                else if( action.getType() == Action.Type.VERSION_CHANGE )
                {
                    ptype = ValidationProblem.Type.VERSION_CHANGE_NOT_SUPPORTED;
                }
                else
                {
                    throw new IllegalStateException();
                }
                
                final IProjectFacetVersion fv = action.getProjectFacetVersion();
                
                final ValidationProblem vp
                    = new ValidationProblem( ptype, 
                                             fv.getProjectFacet().getLabel(),
                                             fv.getVersionString() );
                
                result.add( vp );
            }
        }
        
        // Multiple actions on the same facet are not supported in the same
        // batch. The only exception is an uninstall of a previosly-installed
        // version followed by an install of a new version.
        
        final Map facetToActionsMap = new HashMap();
        
        for( Iterator itr = actions.iterator(); itr.hasNext(); )
        {
            final Action action = (Action) itr.next();
            
            final IProjectFacet f
                = action.getProjectFacetVersion().getProjectFacet();
            
            Set group = (Set) facetToActionsMap.get( f );
            
            if( group == null )
            {
                group = new HashSet();
                facetToActionsMap.put( f, group );
            }
            
            group.add( action );
        }
        
        for( Iterator itr1 = facetToActionsMap.entrySet().iterator(); 
             itr1.hasNext(); )
        {
            final Map.Entry entry = (Map.Entry) itr1.next();
            final Set group = (Set) entry.getValue();
            
            if( group.size() > 1 )
            {
                boolean bad = true;
                
                if( group.size() == 2 )
                {
                    Action install = null;
                    Action uninstall = null;
                    
                    for( Iterator itr2 = group.iterator(); itr2.hasNext(); )
                    {
                        final Action action = (Action) itr2.next();
                        
                        if( action.getType() == Action.Type.INSTALL )
                        {
                            install = action;
                        }
                        else if( action.getType() == Action.Type.UNINSTALL )
                        {
                            uninstall = action;
                        }
                        else
                        {
                            break;
                        }
                    }
                    
                    if( install != null && uninstall != null )
                    {
                        if( base.contains( uninstall.getProjectFacetVersion() ) )
                        {
                            bad = false;
                        }
                    }
                }
                
                if( bad )
                {
                    final ValidationProblem.Type ptype 
                        = ValidationProblem.Type.MULTIPLE_ACTIONS_NOT_SUPPORTED;
                    
                    result.add( new ValidationProblem( ptype ) );
                    
                    break;
                }
            }
        }
        
        // Check for attempts to uninstall or change version of facets that
        // haven't been installed. Also check for attempts to install a facet
        // that's already installed.
        
        for( Iterator itr1 = actions.iterator(); itr1.hasNext(); )
        {
            final Action action = (Action) itr1.next();
            final IProjectFacetVersion fv = action.getProjectFacetVersion();
            final IProjectFacet f = fv.getProjectFacet();

            ValidationProblem.Type ptype = null;
            
            if( action.getType() == Action.Type.UNINSTALL )
            {
                if( ! base.contains( fv ) )
                {
                    ptype = ValidationProblem.Type.CANNOT_UNINSTALL;
                }
            }
            else
            {
                IProjectFacetVersion existing = null;
                
                for( Iterator itr2 = base.iterator(); itr2.hasNext(); )
                {
                    final IProjectFacetVersion temp
                        = (IProjectFacetVersion) itr2.next();
                    
                    if( temp.getProjectFacet() == f )
                    {
                        existing = temp;
                        break;
                    }
                }
                
                if( action.getType() == Action.Type.VERSION_CHANGE && 
                    existing == null )
                {
                    ptype = ValidationProblem.Type.CANNOT_CHANGE_VERSION;
                }
                else if( action.getType() == Action.Type.INSTALL &&
                         existing != null )
                {
                    ptype = ValidationProblem.Type.FACET_ALREADY_INSTALLED;
                }
            }
            
            if( ptype != null )
            {
                result.add( new ValidationProblem( ptype, f.getLabel(),
                                                   fv.getVersionString() ) );
            }
        }
        
        // Abort at this point if there are any validation problems.
        
        if( ! result.isOK() )
        {
            return result;
        }
        
        // Apply all the uninstall actions.
        
        final Set all = new HashSet( base );
        
        for( Iterator itr = actions.iterator(); itr.hasNext(); )
        {
            final Action action = (Action) itr.next();
            
            if( action.getType() == Action.Type.UNINSTALL )
            {
                apply( all, action );
            }
        }
        
        // Apply all the install and version change actions.
        
        for( Iterator itr = actions.iterator(); itr.hasNext(); )
        {
            final Action action = (Action) itr.next();
            
            if( action.getType() != Action.Type.UNINSTALL )
            {
                apply( all, action );
            }
        }
        
        // Check the contrains on all of the facets.
        
        for( Iterator itr = all.iterator(); itr.hasNext(); )
        {
            final IProjectFacetVersion fv
                = (IProjectFacetVersion) itr.next();
            
            final IConstraint constraint = fv.getConstraint();
            
            if( constraint != null )
            {
                final IStatus st = constraint.check( all );
                
                if( ! st.isOK() )
                {
                    result.addAll( st );
                }
            }
        }
        
        // Eliminate symmetric conflicts problem entries.
        
        final Set problems = new HashSet();
        IStatus[] children = result.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            problems.add( children[ i ] );
        }
        
        final Set toremove = new HashSet();
        
        for( Iterator itr = problems.iterator(); itr.hasNext(); )
        {
            final ValidationProblem problem = (ValidationProblem) itr.next();
            
            if( toremove.contains( problem ) )
            {
                itr.remove();
            }
            else
            {
                if( problem.getType() == ValidationProblem.Type.CONFLICTS )
                {
                    final Object[] p = problem.getParameters();
                    
                    final ValidationProblem reverse
                        = new ValidationProblem( ValidationProblem.Type.CONFLICTS,
                                                 new Object[] { p[ 1 ], p[ 0 ] } );
                    
                    toremove.add( reverse );
                }
            }
        }
        
        if( children.length != problems.size() )
        {
            children 
                = (IStatus[]) problems.toArray( new IStatus[ problems.size() ] );
            
            result = Constraint.createMultiStatus( children );
        }
        
        // Return the problems to the caller.
        
        return result;
    }
    
    public void sort( final Set base,
                      final List actions )
    {
        final int count = actions.size();
        
        if( count == 0 )
        {
            return;
        }
        
        // Before sorting, check that the constraints can be met. Otherwise
        // the sort algorithm will not terminate.
        
        final IStatus st = check( base, new HashSet( actions ) );
        
        if( ! st.isOK() )
        {
            FacetCorePlugin.log( st );
            return;
        }
        
        // Initialize tracing.
        
        List unsorted = null;
        int steps = 0;
        
        if( FacetCorePlugin.isTracingActionSorting() )
        {
            unsorted = new ArrayList( actions );
        }
        
        // Step 1 : Pre-sort all uninstall actions to the front of the list.
        
        for( int i = 0, j = 0; j < count; j++ )
        {
            final Action action = (Action) actions.get( j );
            
            if( action.getType() == Action.Type.UNINSTALL && i != j )
            {
                actions.set( j, actions.get( i ) );
                actions.set( i, action );
                i++;
                steps++;
            }
        }
        
        // Step 2 : Sort based on the constraints.
        
        final HashSet fnl = new HashSet( base );
        
        for( Iterator itr = actions.iterator(); itr.hasNext(); )
        {
            apply( fnl, (Action) itr.next() );
        }
        
        boolean makeAnotherPass = true;
        
        while( makeAnotherPass )
        {
            makeAnotherPass = false;
            
            HashSet state = new HashSet( base );
            
            for( int i = 0; i < count; )
            {
                final Action action = (Action) actions.get( i );
                final Action.Type type = action.getType();
                final IProjectFacetVersion fv = action.getProjectFacetVersion();
                final IConstraint constraint = fv.getConstraint();
                
                if( type == Action.Type.UNINSTALL )
                {
                    if( ! constraint.check( state, true ).isOK() &&
                        constraint.check( base, true ).isOK() )
                    {
                        moveToFront( actions, i );
                        makeAnotherPass = true;
                        steps++;
                        break;
                    }
                    else
                    {
                        apply( state, action );
                        i++;
                    }
                }
                else
                {
                    if( constraint.check( state ).isOK() &&
                        ! ( ! constraint.check( state, true ).isOK() &&
                            constraint.check( fnl, true ).isOK() ) )
                    {
                        apply( state, action );
                        i++;
                    }
                    else
                    {
                        moveToEnd( actions, i );
                        steps++;
                    }
                }
            }
        }
        
        // Output tracing information.
        
        if( FacetCorePlugin.isTracingActionSorting() )
        {
            final String text
                = Resources.bind( Resources.tracingActionSorting,
                                  toString( base ), toString( unsorted ),
                                  toString( actions ), String.valueOf( steps ) );
            
            System.out.println( text );
        }
    }
    
    static void apply( final Set facets,
                       final Action action )
    {
        final Action.Type type = action.getType();
        final IProjectFacetVersion fv = action.getProjectFacetVersion();
        
        if( type == Action.Type.INSTALL )
        {
            facets.add( fv );
        }
        else if( type == Action.Type.UNINSTALL )
        {
            facets.remove( fv );
        }
        else if( type == Action.Type.VERSION_CHANGE )
        {
            for( Iterator itr = facets.iterator(); itr.hasNext(); )
            {
                final IProjectFacetVersion x 
                    = (IProjectFacetVersion) itr.next();
                
                if( x.getProjectFacet() == fv.getProjectFacet() )
                {
                    itr.remove();
                    break;
                }
            }
            
            facets.add( fv );
        }
    }
    
    public static void reportMissingFacet( final String fid,
                                           final String plugin )
    {
        synchronized( facetsReportedMissing )
        {
            if( ! facetsReportedMissing.contains( fid ) )
            {
                final String msg
                    = NLS.bind( Resources.facetNotDefined, fid ) +
                      NLS.bind( Resources.usedInPlugin, plugin );
                
                FacetCorePlugin.log( msg );
                
                facetsReportedMissing.add( fid );
            }
        }
    }

    public static void reportMissingFacet( final String fid,
                                           final IProjectFacetVersion fv )
    {
        synchronized( facetsReportedMissing )
        {
            if( ! facetsReportedMissing.contains( fid ) )
            {
                final String msg
                    = NLS.bind( Resources.facetNotDefined, fid ) +
                      NLS.bind( Resources.usedInConstraint, 
                                fv.getProjectFacet().getId(),
                                fv.getVersionString() );
                
                FacetCorePlugin.log( msg );
                
                facetsReportedMissing.add( fid );
            }
        }
    }
    
    private static IProgressMonitor submon( final IProgressMonitor monitor,
                                            final int ticks )
    {
        if( monitor == null )
        {
            return null;
        }
        else
        {
            return new SubProgressMonitor( monitor, ticks );
        }
    }
    
    private static void moveToFront( final List actions,
                                     final int index )
    {
        final Action action = (Action) actions.get( index );
        
        for( int i = index; i > 0; i-- )
        {
            actions.set( i, actions.get( i - 1 ) );
        }
        
        actions.set( 0, action );
    }
    
    private static void moveToEnd( final List actions,
                                   final int index )
    {
        final Action action = (Action) actions.get( index );
        
        for( int i = index + 1, n = actions.size(); i < n; i++ )
        {
            actions.set( i - 1, actions.get( i ) );
        }
        
        actions.set( actions.size() - 1, action );
    }
    
    private void readMetadata()
    {
        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        
        final IExtensionPoint point 
            = registry.getExtensionPoint( FacetCorePlugin.PLUGIN_ID, 
                                          EXTENSION_ID );
        
        if( point == null )
        {
            throw new RuntimeException( "Extension point not found!" ); //$NON-NLS-1$
        }
        
        final ArrayList cfgels = new ArrayList();
        final IExtension[] extensions = point.getExtensions();
        
        for( int i = 0; i < extensions.length; i++ )
        {
            final IConfigurationElement[] elements 
                = extensions[ i ].getConfigurationElements();
            
            for( int j = 0; j < elements.length; j++ )
            {
                cfgels.add( elements[ j ] );
            }
        }
        
        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( EL_CATEGORY ) )
            {
                readCategory( config );
            }
        }
        
        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( EL_PROJECT_FACET ) )
            {
                readProjectFacet( config );
            }
        }
        
        final Map fvToConstraint = new HashMap();
        final Map fvToActions = new HashMap();
        
        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( EL_PROJECT_FACET_VERSION ) )
            {
                readProjectFacetVersion( config, fvToConstraint, fvToActions );
            }
        }
        
        for( Iterator itr = fvToConstraint.entrySet().iterator(); 
             itr.hasNext(); )
        {
            final Map.Entry entry = (Map.Entry) itr.next();
            
            final ProjectFacetVersion fv = (ProjectFacetVersion) entry.getKey();
            
            final IConfigurationElement config 
                = (IConfigurationElement) entry.getValue();
            
            readConstraint( config, fv );
        }
        
        for( Iterator itr1 = fvToActions.entrySet().iterator(); itr1.hasNext(); )
        {
            final Map.Entry entry = (Map.Entry) itr1.next();
            final ProjectFacetVersion fv = (ProjectFacetVersion) entry.getKey();
            final List actions = (List) entry.getValue();
            
            for( Iterator itr2 = actions.iterator(); itr2.hasNext(); )
            {
                final IConfigurationElement config 
                    = (IConfigurationElement) itr2.next();
                
                readAction( config, (ProjectFacet) fv.getProjectFacet(), 
                            fv.getVersionString() );
            }
        }

        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( EL_ACTION ) )
            {
                readAction( config );
            }
            else if( config.getName().equals( EL_EVENT_HANDLER ) )
            {
                readEventHandler( config );
            }
        }
        
        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( EL_PRESET ) )
            {
                readPreset( config );
            }
        }
        
        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( EL_TEMPLATE ) )
            {
                readTemplate( config );
            }
        }
    }
    
    private void readCategory( final IConfigurationElement config )
    {
        final Category category = new Category();
        category.setPluginId( config.getContributor().getName() );
        
        final String id = config.getAttribute( ATTR_ID );

        if( id == null )
        {
            reportMissingAttribute( config, ATTR_ID );
            return;
        }

        category.setId( id );

        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( EL_LABEL ) )
            {
                category.setLabel( child.getValue().trim() );
            }
            else if( childName.equals( EL_DESCRIPTION ) )
            {
                category.setDescription( child.getValue().trim() );
            }
        }
        
        this.categories.add( id, category );
    }
    
    private void readProjectFacet( final IConfigurationElement config )
    {
        final String id = config.getAttribute( ATTR_ID );

        if( id == null )
        {
            reportMissingAttribute( config, ATTR_ID );
            return;
        }
        
        final ProjectFacet descriptor = new ProjectFacet();
        descriptor.setId( id );
        descriptor.setPluginId( config.getContributor().getName() );

        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( EL_LABEL ) )
            {
                descriptor.setLabel( child.getValue().trim() );
            }
            else if( childName.equals( EL_DESCRIPTION ) )
            {
                descriptor.setDescription( child.getValue().trim() );
            }
            else if( childName.equals( EL_VERSION_COMPARATOR ) )
            {
                final String clname = child.getAttribute( ATTR_CLASS );
                
                if( clname == null )
                {
                    reportMissingAttribute( child, ATTR_CLASS );
                    return;
                }
                
                descriptor.setVersionComparator( clname );
            }
            else if( childName.equals( EL_CATEGORY ) )
            {
                final String catname = child.getValue().trim();
                
                final Category category 
                    = (Category) this.categories.get( catname );
                
                if( category == null )
                {
                    final String msg
                        = NLS.bind( Resources.categoryNotDefined, catname ) +
                          NLS.bind( Resources.usedInPlugin, 
                                    child.getContributor().getName() );
                    
                    FacetCorePlugin.log( msg );
                    
                    return;
                }
                
                descriptor.setCategory( category );
                category.addProjectFacet( descriptor );
            }
        }
        
        if( descriptor.getLabel() == null )
        {
            descriptor.setLabel( id );
        }
        
        this.facets.add( id, descriptor );
    }
    
    private void readProjectFacetVersion( final IConfigurationElement config,
                                          final Map fvToConstraint,
                                          final Map fvToActions )
    {
        final String fid = config.getAttribute( ATTR_FACET );

        if( fid == null )
        {
            reportMissingAttribute( config, ATTR_FACET );
            return;
        }
        
        final String ver = config.getAttribute( ATTR_VERSION );

        if( ver == null )
        {
            reportMissingAttribute( config, ATTR_VERSION );
            return;
        }
        
        final ProjectFacet f = (ProjectFacet) this.facets.get( fid );
        
        if( f == null )
        {
            reportMissingFacet( fid, config.getContributor().getName() );
            return;
        }
        
        final ProjectFacetVersion fv
            = new ProjectFacetVersion();
        
        fv.setProjectFacet( f );
        fv.setVersionString( ver );
        fv.setPluginId( config.getContributor().getName() );
        
        final List actions = new ArrayList();
        fvToActions.put( fv, actions );
        
        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( EL_CONSTRAINT ) )
            {
                fvToConstraint.put( fv, child );
            }
            else if( childName.equals( EL_GROUP_MEMBER ) )
            {
                final String id = child.getAttribute( ATTR_ID );
                
                if( id == null )
                {
                    reportMissingAttribute( child, ATTR_ID );
                    return;
                }
                
                Group group = (Group) this.groups.get( id );
                
                if( group == null )
                {
                    group = new Group();
                    group.setId( id );
                    
                    this.groups.add( id, group );
                }
                
                group.addMember( fv );
            }
            else if( childName.equals( EL_ACTION ) )
            {
                actions.add( child );
            }
        }
        
        f.addVersion( fv );

        // This has to happen after facet version is registered.
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( EL_EVENT_HANDLER ) )
            {
                readEventHandler( child, f, ver );
            }
        }
    }
    
    private void readAction( final IConfigurationElement config )
    {
        final String fid = config.getAttribute( ATTR_FACET );

        if( fid == null )
        {
            reportMissingAttribute( config, ATTR_FACET );
            return;
        }
        
        final ProjectFacet f = (ProjectFacet) this.facets.get( fid );
        
        if( f == null )
        {
            reportMissingFacet( fid, config.getContributor().getName() );
            return;
        }
        
        final String ver = config.getAttribute( ATTR_VERSION );

        if( ver == null )
        {
            reportMissingAttribute( config, ATTR_VERSION );
            return;
        }
        
        readAction( config, f, ver );
    }

    private void readAction( final IConfigurationElement config,
                             final ProjectFacet f,
                             final String version )
    {
        final String pluginId = config.getContributor().getName();
        final ActionDefinition def = new ActionDefinition();
        
        def.setPluginId( pluginId );
        
        final String type = config.getAttribute( ATTR_TYPE );
        
        if( type == null )
        {
            reportMissingAttribute( config, ATTR_TYPE );
            return;
        }
        
        // Backwards compatibility of deprecated functionality.
        
        if( type.equals( "runtime-changed" ) ) //$NON-NLS-1$
        {
            final String msg
                = NLS.bind( Resources.deprecatedRuntimeChangedAction, pluginId );
            
            FacetCorePlugin.logWarning( msg, true );
            
            readEventHandler( config, f, version );
            
            return;
        }
        
        // End of backwards compatibility code.
        
        def.setActionType( Action.Type.valueOf( type ) ); 

        if( def.getActionType() == null )
        {
            final String msg
                = NLS.bind( Resources.invalidActionType, type ) +
                  NLS.bind( Resources.usedInPlugin, pluginId );
            
            FacetCorePlugin.log( msg );
            
            return;
        }
        
        try
        {
            def.setVersionExpr( new VersionExpr( f, version, pluginId ) );
        }
        catch( CoreException e )
        {
            FacetCorePlugin.log( e );
            return;
        }

        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( EL_CONFIG_FACTORY ) )
            {
                final String clname = child.getAttribute( ATTR_CLASS );
                
                if( clname == null )
                {
                    reportMissingAttribute( child, ATTR_CLASS );
                    return;
                }
                
                def.setConfigFactoryClassName( clname );
            }
            else if( childName.equals( EL_DELEGATE ) )
            {
                final String clname = child.getAttribute( ATTR_CLASS );
                
                if( clname == null )
                {
                    reportMissingAttribute( config, ATTR_CLASS );
                    return;
                }
                
                def.setDelegateClassName( clname );
            }
            else if( childName.equals( EL_PROPERTY ) )
            {
                final String name = child.getAttribute( ATTR_NAME );

                if( name == null )
                {
                    reportMissingAttribute( config, ATTR_NAME );
                    return;
                }

                final String value = child.getAttribute( ATTR_VALUE );
                
                if( value == null )
                {
                    reportMissingAttribute( config, ATTR_VALUE );
                    return;
                }
                
                if( name.equals( IActionDefinition.PROP_FROM_VERSIONS ) )
                {
                    if( def.getActionType() != Action.Type.VERSION_CHANGE )
                    {
                        final String msg
                            = NLS.bind( Resources.propertyNotApplicable, name,
                                        def.getActionType().name() );
                        
                        FacetCorePlugin.logWarning( msg );
                    }
                    
                    try
                    {
                        final VersionExpr vexpr 
                            = new VersionExpr( f, value, pluginId );
                        
                        def.setProperty( name, vexpr );
                    }
                    catch( CoreException e )
                    {
                        FacetCorePlugin.log( e );
                        return;
                    }
                }
                else
                {
                    final String msg
                        = NLS.bind( Resources.unknownProperty, name ) +
                          NLS.bind( Resources.usedInPlugin, pluginId );
                    
                    FacetCorePlugin.logWarning( msg );
                }
            }
        }
        
        String id = config.getAttribute( ATTR_ID );
        
        if( id == null )
        {
            final StringBuffer buf = new StringBuffer();
            
            buf.append( f.getId() );
            buf.append( '#' );
            buf.append( version );
            buf.append( '#' );
            buf.append( def.getActionType().name() );
            
            for( Iterator itr = def.getProperties().entrySet().iterator(); 
                 itr.hasNext(); )
            {
                final Map.Entry entry = (Map.Entry) itr.next();
                
                buf.append( '#' );
                buf.append( (String) entry.getKey() );
                buf.append( '=' );
                buf.append( entry.getValue().toString() );
                
            }
            
            id = buf.toString();
        }
        
        def.setId( id );
        
        if( isActionDefined( id ) )
        {
            final String msg
                = NLS.bind( Resources.actionAlreadyDefined, id, pluginId );
            
            FacetCorePlugin.logError( msg );
        }
        else
        {
            this.actions.add( def.getId(), def );
            f.addActionDefinition( def );
        }
    }
    
    private void readEventHandler( final IConfigurationElement config )
    {
        final String fid = config.getAttribute( ATTR_FACET );

        if( fid == null )
        {
            reportMissingAttribute( config, ATTR_FACET );
            return;
        }
        
        final ProjectFacet f = (ProjectFacet) this.facets.get( fid );
        
        if( f == null )
        {
            reportMissingFacet( fid, config.getContributor().getName() );
            return;
        }
        
        final String ver = config.getAttribute( ATTR_VERSION );

        if( ver == null )
        {
            reportMissingAttribute( config, ATTR_VERSION );
            return;
        }
        
        readEventHandler( config, f, ver );
    }

    private void readEventHandler( final IConfigurationElement config,
                                   final ProjectFacet f,
                                   final String version )
    {
        final EventHandler h = new EventHandler();
        final String pluginId = config.getContributor().getName();
        h.setPluginId( pluginId );
        
        final String type = config.getAttribute( ATTR_TYPE );
        
        if( type == null )
        {
            reportMissingAttribute( config, ATTR_TYPE );
            return;
        }
        
        if( type.equals( "runtime-changed" ) ) //$NON-NLS-1$
        {
            // Backwards compatibility of deprecated functionality.
            
            h.setType( EventHandler.Type.RUNTIME_CHANGED );
        }
        else
        {
            h.setType( EventHandler.Type.valueOf( type ) );
        }
        
        if( h.getType() == null )
        {
            final String msg
                = NLS.bind( Resources.invalidEventHandlerType, type ) +
                  NLS.bind( Resources.usedInPlugin, pluginId );
            
            FacetCorePlugin.log( msg );
            
            return;
        }
        
        try
        {
            h.setVersionExpr( new VersionExpr( f, version, pluginId ) );
        }
        catch( CoreException e )
        {
            FacetCorePlugin.log( e );
            return;
        }

        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( EL_DELEGATE ) )
            {
                final String clname = child.getAttribute( ATTR_CLASS );
                
                if( clname == null )
                {
                    reportMissingAttribute( config, ATTR_CLASS );
                    return;
                }
                
                h.setDelegate( clname );
            }
        }
        
        if( ! h.hasDelegate() )
        {
            reportMissingElement( config, EL_DELEGATE );
            return;
        }
        
        f.addEventHandler( h );
    }
    
    private void readConstraint( final IConfigurationElement config,
                                 final ProjectFacetVersion fv )
    {
        final IConfigurationElement[] ops = config.getChildren();
        final ArrayList parsed = new ArrayList();
        
        for( int j = 0; j < ops.length; j++ )
        {
            final IConstraint op = readConstraintHelper( ops[ j ], fv );
            
            if( op != null )
            {
                parsed.add( op );
            }
        }
        
        if( parsed.size() == 1 )
        {
            fv.setConstraint( (IConstraint) parsed.get( 0 ) );
        }
        else if( parsed.size() > 1 )
        {
            final IConstraint and 
                = new Constraint( fv, IConstraint.Type.AND, 
                                  parsed.toArray() );
            
            fv.setConstraint( and );
        }
    }
    
    private IConstraint readConstraintHelper( final IConfigurationElement root,
                                              final ProjectFacetVersion fv )
    {
        final String pluginId = root.getContributor().getName();
        
        final IConstraint.Type type
            = IConstraint.Type.valueOf( root.getName() );
        
        final Object[] operands;
     
        if( type == IConstraint.Type.AND ||
            type == IConstraint.Type.OR )
        {
            final IConfigurationElement[] children = root.getChildren();
            operands = new IConstraint[ children.length ];
            
            for( int i = 0; i < children.length; i++ )
            {
                final IConstraint operand 
                    = readConstraintHelper( children[ i ], fv );
                
                if( operand == null )
                {
                    return null;
                }
                
                operands[ i ] = operand;
            }
        }
        else if( type == IConstraint.Type.REQUIRES )
        {
            final String fid = root.getAttribute( ATTR_FACET );
            
            if( fid == null )
            {
                reportMissingAttribute( root, ATTR_FACET );
                return null;
            }

            final String vexprstr = root.getAttribute( ATTR_VERSION );
            
            if( vexprstr == null )
            {
                reportMissingAttribute( root, ATTR_VERSION );
                return null;
            }
            
            final IProjectFacet f;
            final VersionExpr vexpr;
            
            try
            {
                f = getProjectFacet( fid );
                vexpr = new VersionExpr( f, vexprstr, pluginId );
            }
            catch( CoreException e )
            {
                FacetCorePlugin.log( e );
                return null;
            }
            
            final String softStr = root.getAttribute( ATTR_SOFT );
            Boolean soft = Boolean.FALSE;
            
            if( softStr != null && softStr.equals( Boolean.TRUE.toString() ) )
            {
                soft = Boolean.TRUE;
            }
            
            operands = new Object[] { f, vexpr, soft };
        }
        else if( type == IConstraint.Type.CONFLICTS )
        {
            final String gid = root.getAttribute( ATTR_GROUP );
            final String fid = root.getAttribute( ATTR_FACET );
            final String vexprstr = root.getAttribute( ATTR_VERSION );
            
            if( gid != null && ( fid != null || vexprstr != null ) )
            {
                final String msg
                    = NLS.bind( Resources.invalidConflictsConstraint, pluginId );
                
                FacetCorePlugin.logError( msg, true );
                return null;
            }
            else if( gid != null )
            {
                if( ! isGroupDefined( gid ) )
                {
                    final String msg
                        = NLS.bind( Resources.groupNotDefined, gid ) +
                          NLS.bind( Resources.usedInPlugin, pluginId );
                    
                    FacetCorePlugin.logError( msg, true );
                    return null;
                }
                
                operands = new Object[] { getGroup( gid ) };
            }
            else if( fid != null )
            {
                final IProjectFacet f;
                VersionExpr vexpr = null;
                
                try
                {
                    f = getProjectFacet( fid );
                    
                    if( vexprstr != null )
                    {
                        vexpr = new VersionExpr( f, vexprstr, pluginId );
                    }
                }
                catch( CoreException e )
                {
                    FacetCorePlugin.log( e );
                    return null;
                }
                
                if( vexpr == null )
                {
                    operands = new Object[] { f };
                }
                else
                {
                    operands = new Object[] { f, vexpr };
                }
            }
            else
            {
                final String msg
                    = Resources.bind( Resources.missingOneOfTwoAttributes,
                                      pluginId, root.getName(), ATTR_GROUP,
                                      ATTR_FACET );
                
                FacetCorePlugin.logError( msg, true );
                return null;
            }
        }
        else
        {
            throw new IllegalStateException();
        }
        
        return new Constraint( fv, type, operands );
    }
    
    private void readTemplate( final IConfigurationElement config )
    {
        final FacetedProjectTemplate template = new FacetedProjectTemplate();
        
        final String id = config.getAttribute( ATTR_ID );

        if( id == null )
        {
            reportMissingAttribute( config, ATTR_ID );
            return;
        }

        template.setId( id );

        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( EL_LABEL ) )
            {
                template.setLabel( child.getValue().trim() );
            }
            else if( childName.equals( EL_FIXED ) )
            {
                final String fid = child.getAttribute( ATTR_FACET );
                
                if( fid == null )
                {
                    reportMissingAttribute( child, ATTR_FACET );
                    return;
                }
                
                if( ! isProjectFacetDefined( fid ) )
                {
                    reportMissingFacet( fid, child.getContributor().getName() );
                    return;
                }
                
                template.addFixedProjectFacet( getProjectFacet( fid ) );
            }
            else if( childName.equals( EL_PRESET ) )
            {
                final String pid = child.getAttribute( ATTR_ID );
                
                if( pid == null )
                {
                    reportMissingAttribute( child, ATTR_ID );
                    return;
                }
                
                if( ! isPresetDefined( pid ) )
                {
                    final String msg
                        = NLS.bind( Resources.presetNotDefined, pid ) +
                          NLS.bind( Resources.usedInPlugin, 
                                    child.getContributor().getName() );
                    
                    FacetCorePlugin.log( msg );
                    
                    return;
                }
                
                template.setInitialPreset( getPreset( pid ) );
            }
        }
        
        this.templates.add( id, template );
    }
    
    private void readPreset( final IConfigurationElement config )
    {
        final Preset preset = new Preset();
        
        final String id = config.getAttribute( ATTR_ID );

        if( id == null )
        {
            reportMissingAttribute( config, ATTR_ID );
            return;
        }

        preset.setId( id );

        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( EL_LABEL ) )
            {
                preset.setLabel( child.getValue().trim() );
            }
            else if( childName.equals( EL_DESCRIPTION ) )
            {
                preset.setDescription( child.getValue().trim() );
            }
            else if( childName.equals( ATTR_FACET ) )
            {
                final String fid = child.getAttribute( ATTR_ID );
                
                if( fid == null )
                {
                    reportMissingAttribute( child, ATTR_ID );
                    return;
                }
                
                final String fver = child.getAttribute( ATTR_VERSION );
                
                if( fver == null )
                {
                    reportMissingAttribute( child, ATTR_VERSION );
                    return;
                }
                
                final IProjectFacetVersion fv
                    = getProjectFacet( fid ).getVersion( fver );
                
                preset.addProjectFacet( fv );
            }
        }
        
        if( preset.getDescription() == null )
        {
            preset.setDescription( "" ); //$NON-NLS-1$
        }
        
        this.presets.add( id, preset );
    }
    
    static void reportMissingAttribute( final IConfigurationElement el,
                                        final String attribute )
    {
        final String[] params 
            = new String[] { el.getContributor().getName(), el.getName(), 
                             attribute };
        
        final String msg = NLS.bind( Resources.missingAttribute, params ); 
    
        FacetCorePlugin.log( msg );
    }

    static void reportMissingElement( final IConfigurationElement el,
                                      final String element )
    {
        final String[] params 
            = new String[] { el.getContributor().getName(), el.getName(), 
                             element };
        
        final String msg = NLS.bind( Resources.missingElement, params ); 
    
        FacetCorePlugin.log( msg );
    }
    
    private void saveUserPresets()
    {
        try
        {
            final Preferences root = getUserPresetsPreferences();
            
            final String[] children = root.childrenNames();
            
            for( int i = 0; i < children.length; i++ )
            {
                root.node( children[ i ] ).removeNode();
            }
            
            for( Iterator itr = this.presets.iterator(); itr.hasNext(); )
            {
                final IPreset preset = (IPreset) itr.next();
                
                if( preset.isUserDefined() )
                {
                    final Preferences pnode = root.node( preset.getId() );
                    pnode.put( EL_LABEL, preset.getLabel() );
                    pnode.put( EL_DESCRIPTION, preset.getDescription() );
                    
                    int counter = 1;
                    
                    for( Iterator itr2 = preset.getProjectFacets().iterator(); 
                         itr2.hasNext(); )
                    {
                        final IProjectFacetVersion f
                            = (IProjectFacetVersion) itr2.next();
                        
                        final Preferences fnode 
                            = pnode.node( String.valueOf( counter ) );
                        
                        fnode.put( ATTR_ID, f.getProjectFacet().getId() );
                        fnode.put( ATTR_VERSION, f.getVersionString() );
                        
                        counter++;
                    }
                }
            }
        
            root.flush();
        }
        catch( BackingStoreException e )
        {
            FacetCorePlugin.log( e );
        }
    }
    
    private void readUserPresets()
    {
        try
        {
            final Preferences root = getUserPresetsPreferences();
            final String[] pkeys = root.childrenNames();
            
            for( int i = 0; i < pkeys.length; i++ )
            {
                final Preferences pnode = root.node( pkeys[ i ] );
                final String label = pnode.get( EL_LABEL, null );
                
                if( label == null )
                {
                    break;
                }

                String description = pnode.get( EL_DESCRIPTION, null );
                
                if( description == null )
                {
                    description = ""; //$NON-NLS-1$
                }
                
                final String[] fkeys = pnode.childrenNames();
                HashSet facets = new HashSet();
                
                for( int j = 0; j < fkeys.length; j++ )
                {
                    final Preferences fnode = pnode.node( fkeys[ j ] );
                    final String id = fnode.get( ATTR_ID, null );
                    final String version = fnode.get( ATTR_VERSION, null );
                    
                    if( id == null || version == null )
                    {
                        facets = null;
                        break;
                    }
                    
                    if( isProjectFacetDefined( id ) )
                    {
                        final IProjectFacet f = getProjectFacet( id );
                        
                        if( f.hasVersion( version ) )
                        {
                            facets.add( f.getVersion( version ) );
                        }
                        else
                        {
                            facets = null;
                            break;
                        }
                    }
                    else
                    {
                        facets = null;
                        break;
                    }
                }

                if( facets != null )
                {
                    definePreset( label, description, facets, false );
                }
            }
        }
        catch( BackingStoreException e )
        {
            FacetCorePlugin.log( e );
        }
    }
    
    private static Preferences getUserPresetsPreferences()
    {
        final InstanceScope scope = new InstanceScope();
        
        final IEclipsePreferences pluginRoot 
            = scope.getNode( FacetCorePlugin.PLUGIN_ID );
        
        return pluginRoot.node( "user.presets" ); //$NON-NLS-1$
    }
    
    private static String toString( final Collection collection )
    {
        final StringBuffer buf = new StringBuffer();
        
        for( Iterator itr = collection.iterator(); itr.hasNext(); )
        {
            final Object obj = itr.next();
            
            if( buf.length() > 0 )
            {
                buf.append( ", " ); //$NON-NLS-1$
            }
            
            if( obj instanceof IProjectFacetVersion )
            {
                final IProjectFacetVersion fv = (IProjectFacetVersion) obj;
                
                buf.append( fv.getProjectFacet().getId() );
                buf.append( ' ' );
                buf.append( fv.getVersionString() );
            }
            else
            {
                buf.append( obj.toString() );
            }
        }
        
        return buf.toString();
    }
    
    private final class ResourceChangeListener
    
        implements IResourceChangeListener
        
    {
        public void resourceChanged( final IResourceChangeEvent event )
        {
            final IResourceDelta delta = event.getDelta();
            
            synchronized( ProjectFacetsManagerImpl.this.projects )
            {
                for( Iterator itr = ProjectFacetsManagerImpl.this.projects.values().iterator(); 
                     itr.hasNext(); )
                {
                    final FacetedProject fproj = (FacetedProject) itr.next();
                    
                    final IResourceDelta subdelta 
                        = delta.findMember( fproj.f.getFullPath() );
                    
                    if( subdelta != null )
                    {
                        try
                        {
                            fproj.refresh();
                        }
                        catch( CoreException e )
                        {
                            FacetCorePlugin.log( e );
                        }
                    }
                }
            }
        }
        
    }

    public static final class Resources
    
        extends NLS
        
    {
        public static String missingAttribute;
        public static String missingOneOfTwoAttributes;
        public static String missingElement;
        public static String categoryNotDefined;
        public static String facetNotDefined;
        public static String facetVersionNotDefined;
        public static String actionNotDefined;
        public static String actionAlreadyDefined;
        public static String groupNotDefined;
        public static String presetNotDefined;
        public static String templateNotDefined;
        public static String usedInPlugin;
        public static String usedInConstraint;
        public static String invalidActionType;
        public static String invalidEventHandlerType;
        public static String invalidConflictsConstraint;
        public static String deprecatedRuntimeChangedAction;
        public static String tracingActionSorting;
        public static String unknownProperty;
        public static String propertyNotApplicable;
        
        static
        {
            initializeMessages( ProjectFacetsManagerImpl.class.getName(), 
                                Resources.class );
        }
        
        public static String bind( final String template,
                                   final Object arg1,
                                   final Object arg2,
                                   final Object arg3 )
        {
            return NLS.bind( template, new Object[] { arg1, arg2, arg3 } );
        }

        public static String bind( final String template,
                                   final Object arg1,
                                   final Object arg2,
                                   final Object arg3,
                                   final Object arg4 )
        {
            return NLS.bind( template, new Object[] { arg1, arg2, arg3, arg4 } );
        }
    }
    
}
