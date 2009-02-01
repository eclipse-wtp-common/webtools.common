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

package org.eclipse.wst.common.project.facet.core;

import java.util.Set;

/**
 * A preset is a user convenience mechanism for quickly selecting a predefined set of project 
 * facets. Presets can be defined by plugin writers through the supplied <code>presets</code> 
 * extension point as well as by end users. User-defined presets are stored in the workspace.
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface IPreset
{
    /**
     * The enumberation of preset types.
     * 
     * @since 2.0
     */
    
    enum Type
    {
        /**
         * Static presets are created using the <code>presets</code> extension point and are fully
         * specified in the extension point. 
         */
        
        STATIC,
        
        /**
         * Dynamic presets are created by registering a factory in the <code>presets</code>
         * extension point and are not fully specified until they are resolved within the context
         * of use. To resolve a dynamic preset, cast the preset object to {@link IDynamicPreset} and
         * then use the {@link IDynamicPreset#resolve(java.util.Map)} method. 
         */
        
        DYNAMIC,
        
        /**
         * User presets are created using the <code>ProjectFacetsManager.definePreset()</code>
         * methods and are stored in the workspace.
         */
        
        USER_DEFINED
    }
    
    /**
     * Returns the id of the preset.
     * 
     * @return the id of the preset
     */
    
    String getId();
    
    /**
     * Returns the type of the preset. If the preset type is {@link Type#DYNAMIC}, then the preset
     * object can be cast to {@link IDynamicPreset}.
     * 
     * @return the type of the preset
     * @since 2.0
     */
    
    Type getType();

    /**
     * Returns the label that should be used when presenting the preset to the user.
     * 
     * @return the preset label
     */
    
    String getLabel();
    
    /**
     * Returns the description of the preset.
     * 
     * @return the description of the preset
     */
    
    String getDescription();
    
    /**
     * Returns the project facets that are part of this preset.
     * 
     * @return project facets that are part of this preset
     */

    Set<IProjectFacetVersion> getProjectFacets();
    
    /**
     * Indicates whether the preset is user defined. A user-defined preset is
     * stored in the workspace and can be deleted.
     * 
     * @return <code>true</code> if the preset is user-defined,
     *   <code>false</code> otherwise
     * @deprecated use the {@link #getType()} method instead
     */
    
    boolean isUserDefined();
    
}
