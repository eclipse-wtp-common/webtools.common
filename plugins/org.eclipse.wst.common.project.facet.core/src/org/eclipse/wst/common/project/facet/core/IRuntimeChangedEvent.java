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

package org.eclipse.wst.common.project.facet.core;

import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * Describes the runtime changed event to the RUNTIME_CHANGED event handlers.
 *
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @deprecated use the IFacetedProjectListener class from the facet.core.events package instead
 *   of the event handler extension point
 */

public interface IRuntimeChangedEvent
{
    /**
     * Returns the runtime previously associated with the project.
     * 
     * @return the runtime previously associated with the project, or
     *   <code>null</code>
     */
    
    IRuntime getOldRuntime();
    
    /**
     * Returns the runtime that's now associated with the project.
     * 
     * @return the runtime that's now associated with the project, or
     *   <code>null</code>
     */
    
    IRuntime getNewRuntime();
    
}
