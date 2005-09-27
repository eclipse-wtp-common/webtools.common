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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.internal.ProjectFacetsManagerImpl;

/**
 * This is the entry point to the project facet framework API. From here, you 
 * can (among other things) list available project facets and create instances 
 * of {@see IFacetedProject}.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public abstract class ProjectFacetsManager 
{
    private static ProjectFacetsManager instance = new ProjectFacetsManagerImpl();
    
    /**
     * Returns the singleton instance of the <code>ProjectFacetsManager</code>
     * 
     * @return the singleton instance of the <code>ProjectFacetsManager</code>
     */
    
    public static ProjectFacetsManager get()
    {
        return instance;
    }
    
    /**
     * Returns all of the available project facets.
     * 
     * @return a set containing all of the available project facets (element 
     *   type: {@link IProjectFacet})
     */
    
    public abstract Set getProjectFacets();

    /**
     * Determines whether a given project facet id is recognized.
     * 
     * @param id the id of the project facet
     * @return <code>true</code> if the project facet id is recognized, 
     *   <code>false</code> otherwise 
     */
    
    public abstract boolean isProjectFacetDefined( String id );
    
    /**
     * Returns the project facet descriptor corresponding to the specified id.
     * 
     * @param id the id of the project facet
     * @return the project facet descriptor
     * @throws IllegalArgumentException if id is not found
     */
    
    public abstract IProjectFacet getProjectFacet( String id );
    
    /**
     * Returns all of the categories.
     * 
     * @return a set containing all of the categories (element type: {@link 
     *   ICategory})
     */
    
    public abstract Set getCategories();

    /**
     * Determines whether a given category id is recognized.
     * 
     * @param id the id of the category
     * @return <code>true</code> if the category id is recognized, 
     *   <code>false</code> otherwise
     */
    
    public abstract boolean isCategoryDefined( String id );
    
    /**
     * Returns the category corresponding to the specified id.
     * 
     * @param id the id of the category
     * @return the category
     * @throws IllegalArgumentException if id is not found
     */
    
    public abstract ICategory getCategory( String id );
    
    /**
     * Returns all of the presets.
     * 
     * @return a set conaining all of the presets (element type: {@link IPreset})
     */
    
    public abstract Set getPresets();
    
    /**
     * Determines whether a given preset id is recognized.
     * 
     * @param id the preset id
     * @return <code>true</code> if the preset id is recognized,
     *   <code>false</code> otherwise
     */
    
    public abstract boolean isPresetDefined( String id );
    
    /**
     * Returns the preset corresponding to the specified id.
     * 
     * @param id the preset id
     * @return the preset
     * @throws IllegalArgumentException if the preset is not found
     */
    
    public abstract IPreset getPreset( final String id );
    
    /**
     * Defines a new preset. User-defined presets are stored in the workspace. 
     * 
     * @param name the name of the preset
     * @param facets the set of project facets that the preset should contain
     *   (element type: {@see IProjectFacetVersion})
     * @return the preset
     */
    
    public abstract IPreset definePreset( final String name,
                                          final Set facets );
    
    /**
     * Deletes a preset. Note that only user-defined presets can be deleted.
     * 
     * @param preset the preset
     * @return <code>true</code> if the preset was deleted, or 
     *   <code>false</code> if the preset was not found or was not user-defined 
     */
    
    public abstract boolean deletePreset( final IPreset preset );
    
    /**
     * Returns all of the groups.
     * 
     * @return a set containing all of the groups (element type: {@link IGroup})
     */
    
    public abstract Set getGroups();

    /**
     * Determines whether a given group id is recognized.
     * 
     * @param id the group id
     * @return <code>true</code> if the group id is recognized, 
     *   <code>false</code> otherwise
     */
    
    public abstract boolean isGroupDefined( String id );
    
    /**
     * Returns the group corresponding to the specified id.
     * 
     * @param id the group id
     * @return the group descriptor
     * @throws IllegalArgumentException if the group id is not found 
     */
    
    public abstract IGroup getGroup( String id );
    
    /**
     * Creates a wrapper around an <code>IProject</code> that exposes API for
     * manipulating the set of project facets installed on a project.
     *  
     * @param project an Eclipse project
     * @return an instance of {@link IFacetedProject}
     */

    public abstract IFacetedProject create( IProject project );
    
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
    
    public abstract IStatus check( Set base,
                                   Set actions);
    
    /**
     * Sorts actions in the order that they should be applied to a project such
     * that project facet constraints are not violated.
     * 
     * @param base the set of project facets that the actions will be applied to
     *   (element type: {@link IProjectFacetVersion})
     * @param actions the list of actions to sort (element type: {@link 
     *   Action}); this list will be modified
     */
    
    public abstract void sort( Set base,
                               List actions );
    
}
