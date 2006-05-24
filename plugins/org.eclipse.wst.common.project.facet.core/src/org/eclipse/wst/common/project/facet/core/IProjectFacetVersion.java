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

package org.eclipse.wst.common.project.facet.core;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;

/**
 * Contains metadata that describes a specific version of a project facet. This 
 * interface is not intended to be implemented by by clients.
 * 
 * <p><i>This class is part of an interim API that is still under development 
 * and expected to change significantly before reaching stability. It is being 
 * made available at this early stage to solicit feedback from pioneering 
 * adopters on the understanding that any code that uses this API will almost 
 * certainly be broken (repeatedly) as the API evolves.</i></p>
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IProjectFacetVersion
{
    /**
     * Returns the project facet descriptor.
     * 
     * @return the project facet descriptor
     */
    
    IProjectFacet getProjectFacet();
    
    /**
     * Returns the version string.
     * 
     * @return the version string
     */
    
    String getVersionString();
    
    /**
     * Returns the id of the plugin that defines this project facet version. 
     * This method will return <code>null</code> if this version is not defined. 
     * 
     * @return the id of the plugin that defines this project facet version, or
     *   <code>null</code>
     */
    
    String getPluginId();
    
    /**
     * Returns the constraint that has to be satisfied prior to installing this
     * project facet.
     * 
     * @return the constraint that has to be satisfied prior to installing this 
     *   project facet
     */
    
    IConstraint getConstraint();
    
    /**
     * Determines whether this project facet version supports a particular 
     * action type. For instance, some project facets may not be uninstallable, 
     * in which case they will not support <code>Action.Type.UNINSTALL</code>.
     * 
     * @param base the set of facets currently installed in the project that
     *   the desired action type would be executed against (element type:
     *   {@see IProjectFacetVersion})
     * @param type action type
     * @return <code>true</code> if and only if this project facet supports the 
     *   provided action type
     */
    
    boolean supports( Set base,
                      Action.Type type );
    
    /**
     * Determines whether this project facet version supports a particular 
     * action type. For instance, some project facets may not be uninstallable, 
     * in which case they will not support <code>Action.Type.UNINSTALL</code>.
     * 
     * @param type action type
     * @return <code>true</code> if this project facet supports the provided 
     *   action type, <code>false</code> otherwise
     * @deprecated use {@link supports( Set, Action.Type)} instead
     */
    
    boolean supports( Action.Type type );
    
    /**
     * Returns all of the action definitions for this project facet version.
     * 
     * @return all of the action definitions for this project facet version
     *   (element type: {@link IActionDefinition})
     */
    
    Set getActionDefinitions();
    
    /**
     * Returns the action definitions corresponding to a particular action type
     * over this project facet version. For <code>INSTALL</code> and
     * <code>UNINSTALL</code> action types, this method will return a set of
     * length 0 or 1. For <code>VERSION_CHANGE</code> action type, the returned
     * set may contain more than one item as there may exist multiple action
     * definitions for converting from various versions.
     * 
     * @param type action type
     * @return a set containing action definitions corresponding to a particular
     *   action type over this project facet version 
     *   (element type: {@link IActionDefinition})
     */
    
    Set getActionDefinitions( Action.Type type );
    
    /**
     * Returns the action definition corresponding to a particular action type
     * over this project facet version. The {@link supports( Set, Action.Type )} 
     * method can be used to check whether the action is supported prior to
     * calling this method.
     * 
     * @param base the set of facets currently installed in the project that
     *   the desired action type would be executed against (element type:
     *   {@see IProjectFacetVersion})
     * @param type action type
     * @return the action definition corresponding to a particular action type
     *   over this project facet version
     * @throws CoreException if this project facet version does not support the
     *   provided action type
     */
    
    IActionDefinition getActionDefinition( Set base,
                                           Action.Type type )
    
        throws CoreException;
    
    /**
     * Creates a new instance of the config object associated with the specified
     * action on this facet. Will return <code>null</code> if the action 
     * requires no config.
     * 
     * @param type the type of the action.
     * @param pjname the name of the project that this action will be executed
     *   on
     * @return the action config object, or <code>null</code>
     * @throws CoreException if this project facet version does not support the
     *   specified action type or if failed while creating the action config
     *   object
     * @deprecated this method will not behave correctly in presence of multiple
     * action definitions of the same type as can be the case with VERSION_CHANGE
     * actions; instead use IActionDefinition.createConfigObject()
     */
    
    Object createActionConfig( Action.Type type,
                               String pjname )
    
        throws CoreException;
    
    /**
     * @deprecated this method will not behave correctly in presence of multiple
     * action definitions of the same type as can be the case with VERSION_CHANGE
     * actions; instead compare appropriate IActionDefinition objects directly 
     */
    
    boolean isSameActionConfig( Action.Type type,
                                IProjectFacetVersion fv )
    
        throws CoreException;
    
    /**
     * Determines whether this facet version is valid for projects that have
     * the provided set of fixed facets. The determination is done by checking 
     * to see whether this facet or any of its dependencies are in conflict with
     * any of the fixed facets.
     * 
     * @param fixed the set of fixed facets (element type: {@see IProjectFacet})
     * @return <code>true</code> if this facet version is valid for the projects
     *   that have the provided set of fixed facets
     */
    
    boolean isValidFor( Set fixed );
    
    boolean conflictsWith( IProjectFacetVersion fv );
    
}
