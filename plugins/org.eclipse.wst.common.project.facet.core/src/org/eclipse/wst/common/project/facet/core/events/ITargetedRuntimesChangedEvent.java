/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.events;

import java.util.Set;

import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * This interface is implemented by the event object that is used for the TARGETED_RUNTIMES_CHANGED 
 * event. 
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface ITargetedRuntimesChangedEvent

    extends IFacetedProjectEvent
    
{
    /**
     * Returns the set runtimes that the project previously targeted.
     * 
     * @return the set runtimes that the project previously targeted
     */
    
    Set<IRuntime> getOldTargetedRuntimes();

    /**
     * Returns the set runtimes that the project now targets.
     * 
     * @return the set runtimes that the project now targets
     */
    
    Set<IRuntime> getNewTargetedRuntimes();
    
}
