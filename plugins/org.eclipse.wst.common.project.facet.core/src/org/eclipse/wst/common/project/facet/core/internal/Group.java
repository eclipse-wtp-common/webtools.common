/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IGroup;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * The implementation of the {@link IGroup} interface.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class Group 

    implements IGroup 
    
{
    private String id;
    private String label;
    private String description;
    private final Set<IProjectFacetVersion> members;
    private final Set<IProjectFacetVersion> membersReadOnly;
    
    Group() 
    {
        this.description = "";  //$NON-NLS-1$
        this.members = new HashSet<IProjectFacetVersion>();
        this.membersReadOnly = Collections.unmodifiableSet( this.members );
    }
    
    public String getId() 
    {
        return this.id;
    }
    
    void setId( final String id )
    {
        this.id = id;
        
        if( this.label == null )
        {
            this.label = id;
        }
    }
    
    public String getLabel()
    {
        return this.label;
    }
    
    void setLabel( final String label )
    {
        this.label = label;
    }
    
    public String getDescription()
    {
        return this.description;
    }
    
    void setDescription( final String description )
    {
        this.description = description;
    }
    
    public Set<IProjectFacetVersion> getMembers()
    {
        return this.membersReadOnly;
    }
    
    void addMember( final IProjectFacetVersion fv )
    {
        this.members.add( fv );
    }
    
    void addMembers( final Collection<IProjectFacetVersion> facets )
    {
        this.members.addAll( facets );
    }
    
    public String toString()
    {
        return getLabel();
    }

}
