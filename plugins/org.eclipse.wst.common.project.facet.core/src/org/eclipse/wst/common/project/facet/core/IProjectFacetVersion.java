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

import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;

/**
 * Contains metadata that describes a specific version of a project facet. This 
 * interface is not intended to be implemented by by clients.
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
     * @param type action type
     * @return <code>true</code> if this project facet supports the provided 
     *   action type, <code>false</code> otherwise
     */
    
    boolean supports( Action.Type action );
    
}
