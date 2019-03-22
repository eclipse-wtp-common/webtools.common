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

package org.eclipse.wst.common.project.facet.core.runtime.events;

import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * The root interface of all runtime lifecycle events. 
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface IRuntimeLifecycleEvent
{
    enum Type
    {
        VALIDATION_STATUS_CHANGED
    }
    
    /**
     * Returns the type of this event.
     * 
     * @return the type of this event
     */
    
    Type getType();
    
    /**
     * The affected runtime.
     * 
     * @return the affected runtime
     */
    
    IRuntime getRuntime();

}
