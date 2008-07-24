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

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class FacetedProjectTemplate

    implements IFacetedProjectTemplate
    
{
    private String id;
    private String label;
    private final Set<IProjectFacet> fixed;
    private final Set<IProjectFacet> fixedReadOnly;
    private IPreset preset;
    
    FacetedProjectTemplate() 
    {
        this.fixed = new HashSet<IProjectFacet>();
        this.fixedReadOnly = Collections.unmodifiableSet( this.fixed );
    }
    
    public String getId()
    {
        return this.id;
    }
    
    void setId( final String id )
    {
        this.id = id;
    }

    public String getLabel()
    {
        return this.label;
    }
    
    void setLabel( final String label )
    {
        this.label = label;
    }

    public Set<IProjectFacet> getFixedProjectFacets()
    {
        return this.fixedReadOnly;
    }
    
    void addFixedProjectFacet( final IProjectFacet facet )
    {
        this.fixed.add( facet );
    }

    public IPreset getInitialPreset()
    {
        return this.preset;
    }
    
    void setInitialPreset( final IPreset preset )
    {
        this.preset = preset;
    }

}
