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

package org.eclipse.wst.common.project.facet.core.events.internal;

import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetActionEvent;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetActionEvent

    extends FacetedProjectEvent
    implements IProjectFacetActionEvent
    
{
    private final IProjectFacetVersion fv;
    private final Object config;
    
    public ProjectFacetActionEvent( final IFacetedProject project,
                                    final Type eventType,
                                    final IProjectFacetVersion fv,
                                    final Object config )
    {
        super( project, eventType );
        
        this.fv = fv;
        this.config = config;
    }
    
    public IProjectFacet getProjectFacet()
    {
        return this.fv.getProjectFacet();
    }

    public IProjectFacetVersion getProjectFacetVersion()
    {
        return this.fv;
    }

    public Object getActionConfig()
    {
        return this.config;
    }
    
    @Override
    protected void toStringInternal( final StringBuilder buf )
    {
        buf.append( "  <facet id=\"" ); //$NON-NLS-1$
        buf.append( this.fv.getProjectFacet().getId() );
        buf.append( "\" version=\"" ); //$NON-NLS-1$
        buf.append( this.fv.getVersionString() );
        buf.append( "\"/>\n" ); //$NON-NLS-1$
    }

}
