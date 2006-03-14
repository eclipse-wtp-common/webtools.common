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

import org.eclipse.wst.common.project.facet.core.IRuntimeChangedEvent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * The implementation of the <code>IRuntimeChangedEvent</code> interface.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class RuntimeChangedEvent

    implements IRuntimeChangedEvent
    
{
    private final IRuntime oldRuntime;
    private final IRuntime newRuntime;
    
    public RuntimeChangedEvent( final IRuntime oldRuntime,
                                final IRuntime newRuntime )
    {
        this.oldRuntime = oldRuntime;
        this.newRuntime = newRuntime;
    }
    
    public IRuntime getOldRuntime()
    {
        return this.oldRuntime;
    }

    public IRuntime getNewRuntime()
    {
        return this.newRuntime;
    }

}
