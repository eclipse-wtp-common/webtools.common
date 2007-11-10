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

package org.eclipse.wst.common.project.facet.ui;

import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;

/**
 * The interface exposed to the facet action wizard pages that allows them
 * to gather information about the wizard state.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IWizardContext 
{
    /**
     * Returns the name of the project that the wizard is operating on. If the
     * wizard is in the project creation mode, the project will not yet exist
     * in the workspace.
     * 
     * @return the name of the project that the wizard is operating on
     */
    
    String getProjectName();
    
    /**
     * Returns the set of facets currently selected in the wizard. If the wizard
     * is in the add/remove facets mode (vs. project creation), this method will 
     * return the set of facets currently installed in a project after being 
     * modified by the current set of actions. 
     * 
     * @return the set of facets currently selected in the wizard (element type:
     *   {@see IProjectFacetVersion})
     */
    
    Set getSelectedProjectFacets();
    
    /**
     * Determines whether the specified facet is currently selected in the
     * wizard. See {@see getSelectedProjectFacets()} for more information.
     * 
     * @param fv the project facet version object
     * @return <code>true</code> if an only if the provided project facet is
     *   currently selected in the wizard
     */
    
    boolean isProjectFacetSelected( IProjectFacetVersion fv );
    
    /**
     * Returns the set of actions currently specified by the user.
     * 
     * @return the set of actions currently specified by the user
     */
    
    Set getActions();
    
    /**
     * Finds the action of specified type that applies to the specified facet,
     * if such action exists. If the wizard is in the add/remove facets mode
     * (vs. project creation), you cannot depend on finding the install action
     * for a required facet as that facet may have already been installed.
     * 
     * @param type the action type
     * @param fv the project facet version object
     * @return the action object or <code>null</code>
     */
    
    Action getAction( Action.Type type,
                      IProjectFacetVersion fv );
    
}
