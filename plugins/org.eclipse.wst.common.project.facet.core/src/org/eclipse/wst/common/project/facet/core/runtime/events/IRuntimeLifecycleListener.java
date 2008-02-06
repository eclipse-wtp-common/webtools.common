/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.runtime.events;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IRuntimeLifecycleListener
{
    /**
     * This method is called by the framework to notify the listener that an event has occurred.
     * 
     * <p>It is highly advised that any listener that performs non-critical or long-running tasks 
     * that can be performed in the background (such as updating UI) do so asynchronously by
     * spinning off a thread, scheduling a job, etc. This allows the original modification to
     * complete faster resulting in better perceived responsiveness.</p>
     * 
     * @param event the object describing the event
     */
    
    void handleEvent( IRuntimeLifecycleEvent event );
    
}
