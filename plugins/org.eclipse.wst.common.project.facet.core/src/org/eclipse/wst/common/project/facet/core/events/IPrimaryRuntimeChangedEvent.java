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

package org.eclipse.wst.common.project.facet.core.events;

import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * This interface is implemented by the event object that is used for the PRIMARY_RUNTIME_CHANGED 
 * event. 
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface IPrimaryRuntimeChangedEvent

    extends IFacetedProjectEvent
    
{
    /**
     * Returns the old primary runtime for the project.
     * 
     * @return the old primary runtime for the project, or <code>null</code>
     */
    
    IRuntime getOldPrimaryRuntime();
    
    /**
     * Returns the new primary runtime for the project.
     * 
     * @return the new primary runtime for the project, or <code>null</code>
     */
    
    IRuntime getNewPrimaryRuntime();

}
