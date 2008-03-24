/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * The implementation of the {@see IPreset} interface that's used for user-defined presets.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class UserPreset

    implements IPreset
    
{
    private final String id;
    private final String label;
    private final String description;
    private final Set<IProjectFacetVersion> facets;
    
    public UserPreset( final String id,
                       final String label,
                       final String description,
                       final Set<IProjectFacetVersion> facets ) 
    {
        this.id = id;
        this.label = label;
        this.description = description;
        this.facets = Collections.unmodifiableSet( new HashSet<IProjectFacetVersion>( facets ) );
    }
    
    public String getId()
    {
        return this.id;
    }
    
    public Type getType()
    {
        return Type.USER_DEFINED;
    }

    public String getLabel()
    {
        return this.label;
    }
    
    public String getDescription()
    {
        return this.description;
    }
    
    public Set<IProjectFacetVersion> getProjectFacets()
    {
        return this.facets;
    }
    
    public boolean isUserDefined()
    {
        return false;
    }
    
    public String toString()
    {
        return this.label;
    }
    
}
