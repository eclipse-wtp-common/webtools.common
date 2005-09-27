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

import java.util.Set;

/**
 * A preset is a user convenience mechanism for quickly selecting a predefined 
 * set of project facets. Presets can be defined by plugin writers through the
 * supplied extension point as well as by end users. When a user preset is
 * created, the metadata describing it is stored in the workspace.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IPreset
{
    /**
     * Returns the id of the preset.
     * 
     * @return the id of the preset
     */
    
    String getId();

    /**
     * Returns the preset label. The label should be used when presenting the
     * preset to the user.
     * 
     * @return the preset label
     */
    
    String getLabel();
    
    /**
     * Returns the project facets that are part of this preset.
     * 
     * @return project facets that are part of this preset (element type: 
     *   {@link IProjectFacetVersion})
     */

    Set getProjectFacets();
    
    /**
     * Indicates whether the preset is user defined. A user-defined preset is
     * stored in the workspace and can be deleted.
     * 
     * @return <code>true</code> if the preset is user-defined,
     *   <code>false</code> otherwise
     */
    
    boolean isUserDefined();
    
}
