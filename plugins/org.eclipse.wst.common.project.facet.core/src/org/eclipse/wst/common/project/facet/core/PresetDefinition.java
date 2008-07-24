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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The preset definition is used in several contexts as a means of describing a preset. It is
 * different from {@link IPreset} in that the preset definition is not an actual preset that's
 * registered with the system. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 2.0
 */

public final class PresetDefinition
{
    private final String label;
    private final String description;
    private final Set<IProjectFacetVersion> facets;
    
    /**
     * Creates a new preset definition.
     * 
     * @param label the label that should be used when presenting the preset to the user
     * @param description the description of the preset
     * @param facets the facets that comprise the preset
     */
    
    public PresetDefinition( final String label,
                             final String description,
                             final Set<IProjectFacetVersion> facets )
    {
        this.label = label;
        this.description = description;
        this.facets = Collections.unmodifiableSet( new HashSet<IProjectFacetVersion>( facets ) );
        
        for( IProjectFacetVersion fv : facets )
        {
            if( fv == null )
            {
                throw new IllegalArgumentException();
            }
        }
    }
    
    /**
     * Returns the label that should be used when presenting the preset to the user.
     * 
     * @return the preset label
     */
    
    public String getLabel()
    {
        return this.label;
    }

    /**
     * Returns the description of the preset.
     * 
     * @return the description of the preset
     */
    
    public String getDescription()
    {
        return this.description;
    }
    
    /**
     * Returns the project facets that are part of this preset.
     * 
     * @return project facets that are part of this preset
     */

    public Set<IProjectFacetVersion> getProjectFacets()
    {
        return this.facets;
    }
    
}
