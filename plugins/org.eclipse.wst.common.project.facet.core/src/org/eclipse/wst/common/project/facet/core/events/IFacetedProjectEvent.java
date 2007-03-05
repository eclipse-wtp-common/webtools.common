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

package org.eclipse.wst.common.project.facet.core.events;

import org.eclipse.wst.common.project.facet.core.IFacetedProject;

/**
 * The root interface of all faceted project events. 
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IFacetedProjectEvent
{
    enum Type
    {
        /**
         * Type of the event that gets triggered when faceted project metadata is modified in
         * any way. No detailed information about the change is provided. Note that while listening
         * for PROJECT_MODIFIED events is guaranteed to catch all other events, the inverse is not
         * true. Listing on all the other events is not guaranteed to catch all PROJECT_MODIFIED
         * events. This is because there are circumstances when the system does not have the details
         * about the type of the change (such as when the faceted project metadata file is modified
         * on disk).
         */
        
        PROJECT_MODIFIED,
        
        /**
         * Type of the event that gets triggered right before a facet is installed. Events of this 
         * type can be cast to {@see IProjectFacetActionEvent} interface to get additional details 
         * about the change. 
         */
        
        PRE_INSTALL,
        
        /**
         * Type of the event that gets triggered right after a facet is installed. Events of this 
         * type can be cast to {@see IProjectFacetActionEvent} interface to get additional details 
         * about the change. 
         */
        
        POST_INSTALL,
        
        /**
         * Type of the event that gets triggered right before a facet is uninstalled. Events of this 
         * type can be cast to {@see IProjectFacetActionEvent} interface to get additional details 
         * about the change. 
         */
        
        PRE_UNINSTALL,
        
        /**
         * Type of the event that gets triggered right after a facet is uninstalled. Events of this 
         * type can be cast to {@see IProjectFacetActionEvent} interface to get additional details 
         * about the change. 
         */
        
        POST_UNINSTALL,
        
        /**
         * Type of the event that gets triggered right before a facet version is changed. Events of 
         * this type can be cast to {@see IProjectFacetActionEvent} interface to get additional 
         * details about the change. 
         */
        
        PRE_VERSION_CHANGE,
        
        /**
         * Type of the event that gets triggered right after a facet version is changed. Events of 
         * this type can be cast to {@see IProjectFacetActionEvent} interface to get additional 
         * details about the change. 
         */
        
        POST_VERSION_CHANGE,
        
        /**
         * Type of the event that gets triggered when project's fixed facets are changed. Events
         * of this type can be cast to {@see IFixedFacetsChangedEvent} interface to get additional
         * details about the change.
         */
        
        FIXED_FACETS_CHANGED,
        
        /**
         * Type of the event that gets triggered when the set of runtimes that the project targets
         * is changed. Events of this type can be cast to {@see ITargetedRuntimesChangedEvent} 
         * interface to get additional details about the change.
         */
        
        TARGETED_RUNTIMES_CHANGED,
        
        /**
         * Type of the event that gets triggered when the primary targeted runtime of the project
         * is changed. Events of this type can be cast to {@see IPrimaryRuntimeChangedEvent}
         * interface to get additional details about the change. 
         */
        
        PRIMARY_RUNTIME_CHANGED
    }
    
    /**
     * Returns the type of this event.
     * 
     * @return the type of this event
     */
    
    Type getType();
    
    /**
     * The affected faceted project.
     * 
     * @return the affected faceted project
     */
    
    IFacetedProject getProject();

}
