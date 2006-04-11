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

package org.eclipse.wst.common.project.facet.core;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.internal.ProjectFacetsManagerImpl;

/**
 * This is the entry point to the project facet framework API. From here, you 
 * can (among other things) list available project facets and create instances 
 * of {@see IFacetedProject}.
 * 
 * <p><i>This class is part of an interim API that is still under development 
 * and expected to change significantly before reaching stability. It is being 
 * made available at this early stage to solicit feedback from pioneering 
 * adopters on the understanding that any code that uses this API will almost 
 * certainly be broken (repeatedly) as the API evolves.</i></p>
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetsManager 
{
    private static ProjectFacetsManagerImpl impl 
        = new ProjectFacetsManagerImpl();
    
    private ProjectFacetsManager() {}
    
    /**
     * Returns all of the available project facets.
     * 
     * @return a set containing all of the available project facets (element 
     *   type: {@link IProjectFacet})
     */
    
    public static Set getProjectFacets()
    {
        return impl.getProjectFacets();
    }

    /**
     * Determines whether a given project facet id is recognized.
     * 
     * @param id the id of the project facet
     * @return <code>true</code> if the project facet id is recognized, 
     *   <code>false</code> otherwise 
     */
    
    public static boolean isProjectFacetDefined( final String id )
    {
        return impl.isProjectFacetDefined( id );
    }
    
    /**
     * Returns the project facet descriptor corresponding to the specified id.
     * 
     * @param id the id of the project facet
     * @return the project facet descriptor
     * @throws IllegalArgumentException if id is not found
     */
    
    public static IProjectFacet getProjectFacet( final String id )
    {
        return impl.getProjectFacet( id );
    }
    
    /**
     * Returns all of the available project facet action definitions.
     * 
     * @return a set containing all of the available project facets action
     *   definitions (element type: {@link IActionDefinition})
     */
    
    public static Set getActionDefinitions()
    {
        return impl.getActionDefinitions();
    }
    
    /**
     * Determines whether a given project facet action id is recognized.
     * 
     * @param id the id of the project facet action
     * @return <code>true</code> if the project facet action id is recognized, 
     *   <code>false</code> otherwise 
     */
    
    public static boolean isActionDefined( final String id )
    {
        return impl.isActionDefined( id );
    }
    
    /**
     * Returns the project facet action definition corresponding to the 
     * specified action id.
     * 
     * @param id the id of the project facet action
     * @return the project facet action definition
     * @throws IllegalArgumentException if id is not found
     */
    
    public static IActionDefinition getActionDefinition( final String id )
    {
        return impl.getActionDefinition( id );
    }

    /**
     * Returns all of the categories.
     * 
     * @return a set containing all of the categories (element type: {@link 
     *   ICategory})
     */
    
    public static Set getCategories()
    {
        return impl.getCategories();
    }

    /**
     * Determines whether a given category id is recognized.
     * 
     * @param id the id of the category
     * @return <code>true</code> if the category id is recognized, 
     *   <code>false</code> otherwise
     */
    
    public static boolean isCategoryDefined( final String id )
    {
        return impl.isCategoryDefined( id );
    }
    
    /**
     * Returns the category corresponding to the specified id.
     * 
     * @param id the id of the category
     * @return the category
     * @throws IllegalArgumentException if id is not found
     */
    
    public static ICategory getCategory( final String id )
    {
        return impl.getCategory( id );
    }
    
    /**
     * Returns all of the presets.
     * 
     * @return a set conaining all of the presets (element type: 
     *   {@link IPreset})
     */
    
    public static Set getPresets()
    {
        return impl.getPresets();
    }
    
    /**
     * Determines whether a given preset id is recognized.
     * 
     * @param id the preset id
     * @return <code>true</code> if the preset id is recognized,
     *   <code>false</code> otherwise
     */
    
    public static boolean isPresetDefined( final String id )
    {
        return impl.isPresetDefined( id );
    }
    
    /**
     * Returns the preset corresponding to the specified id.
     * 
     * @param id the preset id
     * @return the preset
     * @throws IllegalArgumentException if the preset is not found
     */
    
    public static IPreset getPreset( final String id )
    {
        return impl.getPreset( id );
    }
    
    /**
     * Defines a new preset. User-defined presets are stored in the workspace. 
     * 
     * @param name the name of the preset
     * @param facets the set of project facets that the preset should contain
     *   (element type: {@see IProjectFacetVersion})
     * @return the preset
     */
    
    public static IPreset definePreset( final String name,
                                        final Set facets )
    {
        return impl.definePreset( name, facets );
    }
    
    /**
     * Deletes a preset. Note that only user-defined presets can be deleted.
     * 
     * @param preset the preset
     * @return <code>true</code> if the preset was deleted, or 
     *   <code>false</code> if the preset was not found or was not user-defined 
     */
    
    public static boolean deletePreset( final IPreset preset )
    {
        return impl.deletePreset( preset );
    }
    
    /**
     * Returns all of the faceted project templates.
     * 
     * @return a set conaining all of the faceted project templates (element 
     *   type: {@link IFacetedProjectTemplate})
     */
    
    public static Set getTemplates()
    {
        return impl.getTemplates();
    }
    
    /**
     * Determines whether a given template id is recognized.
     * 
     * @param id the template id
     * @return <code>true</code> if the template id is recognized,
     *   <code>false</code> otherwise
     */
    
    public static boolean isTemplateDefined( final String id )
    {
        return impl.isTemplateDefined( id );
    }
    
    /**
     * Returns the faceted project template corresponding to the specified id.
     * 
     * @param id the template id
     * @return the faceted project templte
     * @throws IllegalArgumentException if the template is not found
     */
    
    public static IFacetedProjectTemplate getTemplate( final String id )
    {
        return impl.getTemplate( id );
    }
    
    /**
     * Returns all of the groups.
     * 
     * @return a set containing all of the groups (element type: {@link IGroup})
     */
    
    public static Set getGroups()
    {
        return impl.getGroups();
    }

    /**
     * Determines whether a given group id is recognized.
     * 
     * @param id the group id
     * @return <code>true</code> if the group id is recognized, 
     *   <code>false</code> otherwise
     */
    
    public static boolean isGroupDefined( final String id )
    {
        return impl.isGroupDefined( id );
    }
    
    /**
     * Returns the group corresponding to the specified id.
     * 
     * @param id the group id
     * @return the group descriptor
     * @throws IllegalArgumentException if the group id is not found 
     */
    
    public static IGroup getGroup( final String id )
    {
        return impl.getGroup( id );
    }
    
    public static Set getFacetedProjects()
    
        throws CoreException
        
    {
        return impl.getFacetedProjects();
    }

    public static Set getFacetedProjects( final IProjectFacet f )
    
        throws CoreException
        
    {
        return impl.getFacetedProjects( f );
    }

    public static Set getFacetedProjects( final IProjectFacetVersion fv )
    
        throws CoreException
        
    {
        return impl.getFacetedProjects( fv );
    }
    
    /**
     * Creates a wrapper around an <code>IProject</code> that exposes API for
     * manipulating the set of project facets installed on a project.
     *  
     * @param project an Eclipse project
     * @return an instance of {@link IFacetedProject}, or <code>null</code>
     * @throws CoreException
     */

    public static IFacetedProject create( final IProject project )
    
        throws CoreException
        
    {
        return impl.create( project );
    }
    
    /**
     * <p>Creates a wrapper around an <code>IProject</code> that exposes API for
     * manipulating the set of project facets installed on a project. The
     * project will be made into a faceted project if necessary.</p>
     * 
     * <p>This method should not be called from the UI thread as it is long-
     * running and may trigger resource change events. Although this framework
     * is safe, there is no guarantee that other bundles are UI-safe and the
     * risk of UI deadlock is high.</p>
     *  
     * @param project an Eclipse project
     * @param convertIfNecessary whether the project should be converted into a
     *   faceted project
     * @param monitor a progress monitor, or null if progress reporting and 
     *   cancellation are not desired
     * @return an instance of {@link IFacetedProject}, or <code>null</code>
     * @throws CoreException
     */

    public static IFacetedProject create( final IProject project,
                                          final boolean convertIfNecessary,
                                          final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        return impl.create( project, convertIfNecessary, monitor );
    }

    /**
     * <p>Creates a new faceted project.</p>
     * 
     * <p>This method should not be called from the UI thread as it is long-
     * running and may trigger resource change events. Although this framework
     * is safe, there is no guarantee that other bundles are UI-safe and the
     * risk of UI deadlock is high.</p>

     * @param name project name
     * @param location 
     * @param monitor a progress monitor, or null if progress reporting and 
     *   cancellation are not desired
     * @return an instance of {@link IFacetedProject}
     * @throws CoreException
     */
    
    public static IFacetedProject create( final String name,
                                          final IPath location,
                                          final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        return impl.create( name, location, monitor );
    }
    
    /**
     * Checks the validity of applying the specified set of actions to the
     * specified set of base project facets. Returns the union of all validation
     * problems that are found.
     *
     * @param base the set of project facets that the actions will be applied to
     *   (element type: {@link IProjectFacetVersion})
     * @param actions the set of actions to evaluate (element type: {@link 
     *   Action})
     * @return a status object with severity of {@see IStatus#OK} if all of the
     *   constraints were satisfied or otherwise a {@see MultiStatus} composed
     *   of {@see ValidationProblem} status objects
     */
    
    public static IStatus check( final Set base,
                                 final Set actions )
    {
        return impl.check( base, actions );
    }
    
    /**
     * Sorts actions in the order that they should be applied to a project such
     * that project facet constraints are not violated.
     * 
     * @param base the set of project facets that the actions will be applied to
     *   (element type: {@link IProjectFacetVersion})
     * @param actions the list of actions to sort (element type: {@link 
     *   Action}); this list will be modified
     */
    
    public static void sort( final Set base,
                             final List actions )
    {
        impl.sort( base, actions );
    }
    
}
