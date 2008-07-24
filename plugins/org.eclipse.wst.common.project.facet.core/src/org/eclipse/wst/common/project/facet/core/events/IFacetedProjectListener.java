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

package org.eclipse.wst.common.project.facet.core.events;

import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;

/**
 * This interface is implemented by those who wish to receive events when a faceted project is
 * changed. There are three ways to register a listener: (a) using methods on {@link 
 * IFacetedProject}, (b) using methods on {@link FacetedProjectFramework}, or (c) using the provided
 * extension point. Method (a) catches only events for a specific faceted project while methods
 * (b) and (c) catch events for all faceted projects in the workspace.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface IFacetedProjectListener
{
    /**
     * This method is called by the framework to notify the listener that an event has occurred.
     * 
     * <p>All events except for <code>PROJECT_MODIFIED</code> of this type are triggered in the 
     * lock context of the operation that caused the original change. This guarantees that the 
     * project has not changed further from the state it was in after the change was complete, 
     * however the listener is unable to call any modifier methods on the {@link IFacetedProject} 
     * interface. To do so, the listener must spin off a thread.</p>
     * 
     * <p>It is highly advised that any listener that performs non-critical or long-running tasks 
     * that can be performed in the background (such as updating UI) do so asynchronously by
     * spinning off a thread, scheduling a job, etc. This allows the original modification to
     * complete faster resulting in better perceived responsiveness.</p>
     * 
     * @param event the object describing the event
     */
    
    void handleEvent( IFacetedProjectEvent event );
    
}
