/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.events.internal;

import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public class FacetedProjectEvent

    implements IFacetedProjectEvent
    
{
    private final IFacetedProject project;
    private final Type eventType;
    
    public FacetedProjectEvent( final IFacetedProject project,
                                final Type eventType )
    {
        this.project = project;
        this.eventType = eventType;
    }
    
    public final IFacetedProject getProject()
    {
        return this.project;
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
