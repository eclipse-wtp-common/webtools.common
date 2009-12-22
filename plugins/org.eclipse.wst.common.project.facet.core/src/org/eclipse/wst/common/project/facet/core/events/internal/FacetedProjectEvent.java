/******************************************************************************
 * Copyright (c) 2010 Oracle
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
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class FacetedProjectEvent

    implements IFacetedProjectEvent
    
{
    private final IFacetedProject project;
    private final IFacetedProjectWorkingCopy workingCopy;
    private final Type eventType;
    
    public FacetedProjectEvent( final IFacetedProject project,
                                final Type eventType )
    {
        this.project = project;
        this.workingCopy = null;
        this.eventType = eventType;
    }

    public FacetedProjectEvent( final IFacetedProjectWorkingCopy workingCopy,
                                final Type eventType )
    {
        this.project = null;
        this.workingCopy = workingCopy;
        this.eventType = eventType;
    }
    
    public final IFacetedProject getProject()
    {
        return this.project;
    }
    
    public final IFacetedProjectWorkingCopy getWorkingCopy()
    {
        return this.workingCopy;
    }

    public final Type getType()
    {
        return this.eventType;
    }
    
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        
        buf.append( "<event project=\"" ); //$NON-NLS-1$
        buf.append( this.project.getProject().getName() );
        buf.append( "\" type=\"" ); //$NON-NLS-1$
        buf.append( this.eventType.name() );
        buf.append( "\">\n" ); //$NON-NLS-1$
        
        toStringInternal( buf );
        
        buf.append( "</event>" ); //$NON-NLS-1$
        
        return buf.toString();
    }
    
    protected void toStringInternal( final StringBuilder buf )
    {
    }

}
