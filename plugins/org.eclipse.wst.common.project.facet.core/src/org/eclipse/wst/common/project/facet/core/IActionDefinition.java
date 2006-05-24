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

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;

/**
 * An action is an operation on a single facet within a faceted project to
 * install, uninstall, or change the version of the facet. An action definition
 * represents the information supplied by the facet author regarding the
 * implementation of an action. A single action definition can apply to multiple
 * facet versions. For instance, the facet author may choose to supply one
 * action definition for all versions of his facet. 
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IActionDefinition
{
    /**
     * The name of the property that's used to constraint the starting version
     * of the VERSION_CHANGE action definition.
     */
    
    static final String PROP_FROM_VERSIONS = "from.versions"; //$NON-NLS-1$
    
    /**
     * Returns the id of the action definition. If not explicitly specified, a
     * default action id is generated that takes the following form:
     * [facet-id]#[version-expression]#[action-type](#[prop.name]=[prop.value])*.
     * 
     * @return the id of the action definition
     */
    
    String getId();
    
    /**
     * Returns the project facet that this action definition is associated with.
     * 
     * @return the project facet that this action definition is associated with
     */
    
    IProjectFacet getProjectFacet();
    
    /**
     * Returns the version expression that controls which facet versions this
     * action definition applies to.
     * 
     * @return the version expression that controls which facet versions this
     *   action definition applies to
     */
    
    IVersionExpr getVersionExpr();
    
    /**
     * Returns the action type, such as <code>INSTALL</code>, <code>UNINSTALL</code>,
     * or <code>VERSION_CHANGE</code>.
     * 
     * @return the action type
     */
    
    Action.Type getActionType();
    
    /**
     * Returns the properties that further specify action behavior and 
     * applicability.
     * 
     * @return the properties of this action definition (key type: {@see String}, 
     *   value type {@see Object})
     */
    
    Map getProperties();
    
    /**
     * Returns the property value corresponding to the provided name.
     * 
     * @param name the name of the property
     * @return the value of the property
     */
    
    Object getProperty( String name );
    
    /**
     * Creates a new config object that can be used for parameterizing the
     * execution of this action. If this action definition does not specify a
     * config object factory, this method will return <code>null</code>.
     * 
     * @param fv the actual facet version that this config object will be used
     *   with; should be one of the versions matched by the version expression
     *   specified for this action definition
     * @param pjname the project name
     * @return the new config object
     * @throws CoreException if failed while instantiating the config object
     *   factory or creating the config object
     */
    
    Object createConfigObject( IProjectFacetVersion fv,
                               String pjname )
    
        throws CoreException;
    
}