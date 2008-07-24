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

package org.eclipse.wst.common.project.facet.ui.internal.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.wst.common.project.facet.core.internal.ProjectFacetRef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class MultiFacetConstraintOperator

    extends ConstraintOperator
    
{
    private final List<ProjectFacetRef> facetRefs;
    private final List<ProjectFacetRef> facetRefsReadOnly;
    
    public MultiFacetConstraintOperator( final Type type )
    {
        super( type );
        
        this.facetRefs = new ArrayList<ProjectFacetRef>();
        this.facetRefsReadOnly = Collections.unmodifiableList( this.facetRefs );
    }
    
    public List<ProjectFacetRef> getProjectFacetRefs()
    {
        return this.facetRefsReadOnly;
    }
    
    public void addProjectFacetRef( final ProjectFacetRef facetRef )
    {
        this.facetRefs.add( facetRef );
    }
    
    public void addProjectFacetRefs( final Collection<ProjectFacetRef> facetRefs )
    {
        this.facetRefs.addAll( facetRefs );
    }
    
    public void removeProjectFacetRef( final ProjectFacetRef facetRef )
    {
        this.facetRefs.remove( facetRef );
    }
    
    public void removeProjectFacetRefs( final Collection<ProjectFacetRef> facetRefs )
    {
        this.facetRefs.removeAll( facetRefs );
    }
    
}
