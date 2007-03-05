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
import org.eclipse.wst.common.project.facet.core.events.IPrimaryRuntimeChangedEvent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class PrimaryRuntimeChangedEvent

    extends FacetedProjectEvent
    implements IPrimaryRuntimeChangedEvent
    
{
    private final IRuntime oldPrimaryRuntime;
    private final IRuntime newPrimaryRuntime;
    
    public PrimaryRuntimeChangedEvent( final IFacetedProject project,
                                       final IRuntime oldPrimaryRuntime,
                                       final IRuntime newPrimaryRuntime )
    {
        super( project, Type.PRIMARY_RUNTIME_CHANGED );
        
        this.oldPrimaryRuntime = oldPrimaryRuntime;
        this.newPrimaryRuntime = newPrimaryRuntime;
    }
    
    public IRuntime getOldPrimaryRuntime()
    {
        return this.oldPrimaryRuntime;
    }

    public IRuntime getNewPrimaryRuntime()
    {
        return this.newPrimaryRuntime;
    }
    
    @Override
    protected void toStringInternal( final StringBuilder buf )
    {
        buf.append( "  <old-primary-runtime name=\"" ); //$NON-NLS-1$
        buf.append( this.oldPrimaryRuntime == null ? "<null>" : this.oldPrimaryRuntime.getName() ); //$NON-NLS-1$
        buf.append( "\"/>\n" ); //$NON-NLS-1$

        buf.append( "  <new-primary-runtime name=\"" ); //$NON-NLS-1$
        buf.append( this.newPrimaryRuntime == null ? "<null>" : this.newPrimaryRuntime.getName() ); //$NON-NLS-1$
        buf.append( "\"/>\n" ); //$NON-NLS-1$
    }

}
