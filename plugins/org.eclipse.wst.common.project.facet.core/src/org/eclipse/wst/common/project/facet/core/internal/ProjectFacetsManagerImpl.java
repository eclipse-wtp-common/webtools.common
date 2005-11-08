/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
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
import org.eclipse.wst.common.project.facet.core.IActionConfig;
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IConstraint;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IGroup;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.internal.ProjectFacet.ActionDefinition;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * The implementation of the {@see ProjectFacetsManager} abstract class.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetsManagerImpl
{
    private static final String EXTENSION_ID = "facets";
    
    private final IndexedSet facets;
    private final IndexedSet categories;
    private final IndexedSet presets;
    private final IndexedSet templates;
    private final IndexedSet groups;
    
    public ProjectFacetsManagerImpl()
    {
        this.facets = new IndexedSet();
        this.categories = new IndexedSet();
        this.presets = new IndexedSet();
        this.templates = new IndexedSet();
        this.groups = new IndexedSet();
        
        readMetadata();
        readUserPresets();
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
            final String msg = "Could not find project facet " + id + ".";
            throw new IllegalArgumentException( msg );
        }
        
        return f;
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
            final String msg = "Could not find category " + id + ".";
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
            final String msg = "Could not find preset " + id + ".";
            throw new IllegalArgumentException( msg );
        }
        
        return preset;
    }
    
    public IPreset definePreset( final String name,
                                 final Set facets )
    {
        return definePreset( name, facets, true );
    }

    private IPreset definePreset( final String name,
                                  final Set facets,
                                  final boolean save )
    {
        synchronized( this.presets )
        {
            String id;
            int i = 0;
            
            do
            {
                id = ".usr." + i;
                i++;
            }
            while( this.presets.containsKey( id ) );
            
            final Preset preset = new Preset();
            
            preset.setId( id );
            preset.setLabel( name );
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
            final String msg = "Could not find template " + id + ".";
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
            final String msg = "Could not find group " + id + ".";
            throw new IllegalArgumentException( msg );
        }
        
        return group;
    }

    public IFacetedProject create( final IProject project )
    
        throws CoreException
        
    {
        if( project.isNatureEnabled( FacetedProjectNature.NATURE_ID ) )
        {
            return new FacetedProject( project );
        }

        return null;
    }
    
    public IFacetedProject create( final String name,
                                   final IPath location,
                                   final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        final IWorkspace ws = ResourcesPlugin.getWorkspace();
        final IProject project = ws.getRoot().getProject( name );
        
        final IProjectDescription desc
            = ws.newProjectDescription( name );

        desc.setLocation( location );
        desc.setNatureIds( new String[] { FacetedProjectNature.NATURE_ID } );
                
        project.create( desc, new SubProgressMonitor( monitor, 1 ) );
                    
        project.open( IResource.BACKGROUND_REFRESH,
                      new SubProgressMonitor( monitor, 1 ) );

        return create( project );
    }
    
    public IFacetedProject create( final IProject project,
    								final boolean convertIfNecessary,
    								final IProgressMonitor monitor)
    
    throws CoreException
    {
    	if( project.exists() && convertIfNecessary ){
	  		IProjectDescription description = project.getDescription();
			String[] prevNatures = description.getNatureIds();
			String[] newNatures = new String[ prevNatures.length + 1 ];
			System.arraycopy( prevNatures, 0, newNatures, 0, prevNatures.length );
			newNatures[ prevNatures.length ] = FacetedProjectNature.NATURE_ID;
			description.setNatureIds( newNatures );
			project.setDescription( description, monitor ); 
    	}
        project.open( IResource.BACKGROUND_REFRESH,
                new SubProgressMonitor( monitor, 1 ) );  
        return create( project );
    }
    
    public IStatus check( final Set base,
                          final Set actions )
    {
        MultiStatus result = Constraint.createMultiStatus();
        
        // Verify that all of the actions are supported.
        
        for( Iterator itr = actions.iterator(); itr.hasNext(); )
        {
            final Action action = (Action) itr.next();
            
            if( ! action.getProjectFacetVersion().supports( action.getType() ) )
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
        
        // Step 1 : Pre-sort all uninstall actions to the front of the list.
        
        for( int i = 0, j = 0; j < count; j++ )
        {
            final Action action = (Action) actions.get( j );
            
            if( action.getType() == Action.Type.UNINSTALL && i != j )
            {
                actions.set( j, actions.get( i ) );
                actions.set( i, action );
                i++;
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
                        break;
                    }
                    else
                    {
                        apply( state, action );
                        i++;
                    }
                }
                else if( type == Action.Type.VERSION_CHANGE )
                {
                    final HashSet copy = new HashSet( state );
                    apply( state, action );
                    
                    if( ! constraint.check( copy, true ).isOK() &&
                        constraint.check( fnl, true ).isOK() )
                    {
                        moveToEnd( actions, i );
                    }
                    else
                    {
                        state = copy;
                        i++;
                    }
                }
                else
                {
                    if( ! constraint.check( state, true ).isOK() &&
                        constraint.check( fnl, true ).isOK() )
                    {
                        moveToEnd( actions, i );
                    }
                    else
                    {
                        apply( state, action );
                        i++;
                    }
                }
            }
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
            throw new RuntimeException( "Extension point not found!" );
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
            
            if( config.getName().equals( "category" ) )
            {
                readCategory( config );
            }
        }
        
        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( "project-facet" ) )
            {
                readProjectFacet( config );
            }
        }
        
        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( "project-facet-version" ) )
            {
                readProjectFacetVersion( config );
            }
        }

        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( "action" ) )
            {
                readAction( config );
            }
        }
        
        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( "preset" ) )
            {
                readPreset( config );
            }
        }
        
        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( "template" ) )
            {
                readTemplate( config );
            }
        }
    }
    
    private void readCategory( final IConfigurationElement config )
    {
        final Category category = new Category();
        category.setPlugin( config.getDeclaringExtension().getNamespace() );
        
        final String id = config.getAttribute( "id" );

        if( id == null )
        {
            reportMissingAttribute( config, "id" );
            return;
        }

        category.setId( id );

        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( "label" ) )
            {
                category.setLabel( child.getValue().trim() );
            }
            else if( childName.equals( "description" ) )
            {
                category.setDescription( child.getValue().trim() );
            }
            else if( childName.equals( "icon" ) )
            {
                category.setIconPath( child.getValue().trim() );
            }
        }
        
        this.categories.add( id, category );
    }
    
    private void readProjectFacet( final IConfigurationElement config )
    {
        final String id = config.getAttribute( "id" );

        if( id == null )
        {
            reportMissingAttribute( config, "id" );
            return;
        }
        
        final ProjectFacet descriptor = new ProjectFacet();
        descriptor.setId( id );
        descriptor.setPluginId( config.getDeclaringExtension().getNamespace() );

        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( "label" ) )
            {
                descriptor.setLabel( child.getValue().trim() );
            }
            else if( childName.equals( "description" ) )
            {
                descriptor.setDescription( child.getValue().trim() );
            }
            else if( childName.equals( "icon" ) )
            {
                descriptor.setIconPath( child.getValue().trim() );
            }
            else if( childName.equals( "version-comparator" ) )
            {
                final String clname = child.getAttribute( "class" );
                
                if( clname == null )
                {
                    reportMissingAttribute( child, "class" );
                    return;
                }
                
                descriptor.setVersionComparator( clname );
            }
            else if( childName.equals( "category" ) )
            {
                final String catname = child.getValue().trim();
                
                final Category category 
                    = (Category) this.categories.get( catname );
                
                if( category == null )
                {
                    final String msg
                        = NLS.bind( Resources.categoryNotDefined, 
                                    child.getNamespace(), catname );
                    
                    FacetCorePlugin.log( msg );
                    
                    return;
                }
                
                descriptor.setCategory( category );
                category.addProjectFacet( descriptor );
            }
        }
        
        this.facets.add( id, descriptor );
    }
    
    private void readProjectFacetVersion( final IConfigurationElement config )
    {
        final String fid = config.getAttribute( "facet" );

        if( fid == null )
        {
            reportMissingAttribute( config, "facet" );
            return;
        }
        
        final String ver = config.getAttribute( "version" );

        if( ver == null )
        {
            reportMissingAttribute( config, "version" );
            return;
        }
        
        final ProjectFacet f = (ProjectFacet) this.facets.get( fid );
        
        if( f == null )
        {
            final String msg
                = NLS.bind( Resources.facetNotDefined, 
                            config.getNamespace(), fid );
            
            FacetCorePlugin.log( msg );
            
            return;
        }
        
        final ProjectFacetVersion fv
            = new ProjectFacetVersion();
        
        fv.setProjectFacet( f );
        fv.setVersionString( ver );
        fv.setPlugin( config.getDeclaringExtension().getNamespace() );
        
        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( "constraint" ) )
            {
                final IConfigurationElement[] ops = child.getChildren();
                final ArrayList parsed = new ArrayList();
                
                for( int j = 0; j < ops.length; j++ )
                {
                    final IConstraint op = readConstraint( ops[ j ], fv );
                    
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
            else if( childName.equals( "group-member" ) )
            {
                final String id = child.getAttribute( "id" );
                
                if( id == null )
                {
                    reportMissingAttribute( child, "id" );
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
        }
        
        f.addVersion( fv );

        // This has to happen after facet version is registered.
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( "action" ) )
            {
                readAction( child, f, ver );
            }
        }
    }
    
    private void readAction( final IConfigurationElement config )
    {
        final String fid = config.getAttribute( "facet" );

        if( fid == null )
        {
            reportMissingAttribute( config, "facet" );
            return;
        }
        
        final ProjectFacet f = (ProjectFacet) this.facets.get( fid );
        
        if( f == null )
        {
            final String msg
                = NLS.bind( Resources.facetNotDefined, 
                            config.getNamespace(), fid );
            
            FacetCorePlugin.log( msg );
            
            return;
        }
        
        final String ver = config.getAttribute( "version" );

        if( ver == null )
        {
            reportMissingAttribute( config, "version" );
            return;
        }
        
        readAction( config, f, ver );
    }

    private void readAction( final IConfigurationElement config,
                             final ProjectFacet f,
                             final String version )
    {
        final ActionDefinition def = new ActionDefinition();
        
        final String type = config.getAttribute( "type" );
        
        if( type == null )
        {
            reportMissingAttribute( config, "type" );
            return;
        }
        else if( type.equals( "install" ) )
        {
            def.type = IDelegate.Type.INSTALL;
        }
        else if( type.equals( "uninstall" ) )
        {
            def.type = IDelegate.Type.UNINSTALL;
        }
        else if( type.equals( "version-change" ) )
        {
            def.type = IDelegate.Type.VERSION_CHANGE; 
        }
        else if( type.equals( "runtime-changed" ) )
        {
            def.type = IDelegate.Type.RUNTIME_CHANGED;
        }
        else
        {
            final String msg
                = NLS.bind( Resources.invalidActionType, config.getNamespace(),
                            type );
            
            FacetCorePlugin.log( msg );
            
            return;
        }
        
        try
        {
            def.versionMatchExpr = new VersionMatchExpr( f, version );
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
            
            if( childName.equals( "config-factory" ) )
            {
                final String clname = child.getAttribute( "class" );
                
                if( clname == null )
                {
                    reportMissingAttribute( child, "class" );
                    return;
                }
                
                def.configFactoryClassName = clname;
            }
            else if( childName.equals( "delegate" ) )
            {
                final String clname = child.getAttribute( "class" );
                
                if( clname == null )
                {
                    reportMissingAttribute( config, "class" );
                    return;
                }
                
                def.delegateClassName = clname;
            }
        }
        
        f.addActionDefinition( def );
    }
    
    private IConstraint readConstraint( final IConfigurationElement root,
                                        final ProjectFacetVersion fv )
    {
        final IConstraint.Type type
            = IConstraint.Type.get( root.getName() );
        
        final Object[] operands;
     
        if( type == IConstraint.Type.AND ||
            type == IConstraint.Type.OR )
        {
            final IConfigurationElement[] children = root.getChildren();
            operands = new IConstraint[ children.length ];
            
            for( int i = 0; i < children.length; i++ )
            {
                operands[ i ] = readConstraint( children[ i ], fv );
            }
        }
        else if( type == IConstraint.Type.REQUIRES )
        {
            final String fid = root.getAttribute( "facet" );
            
            if( fid == null )
            {
                reportMissingAttribute( root, "facet" );
                return null;
            }

            final String version = root.getAttribute( "version" );
            
            if( version == null )
            {
                reportMissingAttribute( root, "version" );
                return null;
            }
            
            final String allowNewerStr = root.getAttribute( "allow-newer" );
            Boolean allowNewer = Boolean.FALSE;
            
            if( allowNewerStr != null && allowNewerStr.equals( "true" ) )
            {
                allowNewer = Boolean.TRUE;
            }
            
            final String softStr = root.getAttribute( "soft" );
            Boolean soft = Boolean.FALSE;
            
            if( softStr != null && softStr.equals( "true" ) )
            {
                soft = Boolean.TRUE;
            }
            
            operands = new Object[] { fid, version, allowNewer, soft };
        }
        else if( type == IConstraint.Type.CONFLICTS )
        {
            final String group = root.getAttribute( "group" );
            
            if( group == null )
            {
                reportMissingAttribute( root, "group" );
                return null;
            }

            operands = new Object[] { group };
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
        
        final String id = config.getAttribute( "id" );

        if( id == null )
        {
            reportMissingAttribute( config, "id" );
            return;
        }

        template.setId( id );

        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( "label" ) )
            {
                template.setLabel( child.getValue().trim() );
            }
            else if( childName.equals( "fixed" ) )
            {
                final String fid = child.getAttribute( "facet" );
                
                if( fid == null )
                {
                    reportMissingAttribute( child, "facet" );
                    return;
                }
                
                if( ! isProjectFacetDefined( fid ) )
                {
                    final String msg
                        = NLS.bind( Resources.facetNotDefined, 
                                    child.getNamespace(), fid );
                    
                    FacetCorePlugin.log( msg );
                    
                    return;
                }
                
                template.addFixedProjectFacet( getProjectFacet( fid ) );
            }
            else if( childName.equals( "preset" ) )
            {
                final String pid = child.getAttribute( "id" );
                
                if( pid == null )
                {
                    reportMissingAttribute( child, "id" );
                    return;
                }
                
                if( ! isPresetDefined( pid ) )
                {
                    final String msg
                        = NLS.bind( Resources.presetNotDefined, 
                                    child.getNamespace(), pid );
                    
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
        
        final String id = config.getAttribute( "id" );

        if( id == null )
        {
            reportMissingAttribute( config, "id" );
            return;
        }

        preset.setId( id );

        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( "label" ) )
            {
                preset.setLabel( child.getValue().trim() );
            }
            else if( childName.equals( "facet" ) )
            {
                final String fid = child.getAttribute( "id" );
                
                if( fid == null )
                {
                    reportMissingAttribute( child, "id" );
                    return;
                }
                
                final String fver = child.getAttribute( "version" );
                
                if( fver == null )
                {
                    reportMissingAttribute( child, "version" );
                    return;
                }
                
                final IProjectFacetVersion fv
                    = getProjectFacet( fid ).getVersion( fver );
                
                preset.addProjectFacet( fv );
            }
        }
        
        this.presets.add( id, preset );
    }
    
    private static void reportMissingAttribute( final IConfigurationElement el,
                                                final String attribute )
    {
        final String[] params 
            = new String[] { el.getNamespace(), el.getName(), attribute };
        
        final String msg = NLS.bind( Resources.missingAttribute, params ); 
    
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
                    pnode.put( "label", preset.getLabel() );
                    
                    int counter = 1;
                    
                    for( Iterator itr2 = preset.getProjectFacets().iterator(); 
                         itr2.hasNext(); )
                    {
                        final IProjectFacetVersion f
                            = (IProjectFacetVersion) itr2.next();
                        
                        final Preferences fnode 
                            = pnode.node( String.valueOf( counter ) );
                        
                        fnode.put( "id", f.getProjectFacet().getId() );
                        fnode.put( "version", f.getVersionString() );
                        
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
                final String label = pnode.get( "label", null );
                
                if( label == null )
                {
                    break;
                }
                
                final String[] fkeys = pnode.childrenNames();
                HashSet facets = new HashSet();
                
                for( int j = 0; j < fkeys.length; j++ )
                {
                    final Preferences fnode = pnode.node( fkeys[ j ] );
                    final String id = fnode.get( "id", null );
                    final String version = fnode.get( "version", null );
                    
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
                    definePreset( label, facets, false );
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
        
        return pluginRoot.node( "user.presets" );
    }

    public static final class Resources
    
        extends NLS
        
    {
        public static String missingAttribute;
        public static String categoryNotDefined;
        public static String facetNotDefined;
        public static String facetVersionNotDefined;
        public static String facetVersionNotDefinedNoPlugin;
        public static String presetNotDefined;
        public static String invalidActionType;
        
        static
        {
            initializeMessages( ProjectFacetsManagerImpl.class.getName(), 
                                Resources.class );
        }
    }
    
}
