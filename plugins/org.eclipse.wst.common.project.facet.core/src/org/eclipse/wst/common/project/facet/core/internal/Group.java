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

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IGroup;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * The implementation of the {@see IGroup} interface.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class Group 

    implements IGroup 
    
{
    private String id = null;
    
    private final Set members = new HashSet();
    
    private final Set membersReadOnly 
        = Collections.unmodifiableSet( this.members );
    
    public String getId() 
    {
        return this.id;
    }
    
    void setId( final String id )
    {
        this.id = id;
    }
    
    public Set getMembers()
    {
        return this.membersReadOnly;
    }
    
    void addMember( final IProjectFacetVersion fv )
    {
        this.members.add( fv );
    }
    
    public String toString()
    {
        return this.id;
    }

}
