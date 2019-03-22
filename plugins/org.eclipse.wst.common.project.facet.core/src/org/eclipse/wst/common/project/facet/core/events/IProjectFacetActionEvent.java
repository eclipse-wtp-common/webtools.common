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

import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * This interface is implemented by the event object that is used for the PRE_INSTALL, POST_INSTALL, 
 * PRE_UNINSTALL, POST_UNINSTALL, PRE_VERSION_CHANGE, and POST_VERSION_CHANGE events.
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface IProjectFacetActionEvent

    extends IFacetedProjectEvent
    
{
    /**
     * The project facet that this event is about.
     * 
     * @return the project facet that this event is about.
     */
    
    IProjectFacet getProjectFacet();
    
    /**
     * The version of the project facet that this event is about. In the case of the
     * PRE_VERSION_CHANGE and POST_VERSION_CHANGE events, this will be the new version.
     * 
     * @return the version of the project facet that this event is about
     */
    
    IProjectFacetVersion getProjectFacetVersion();
    
    /**
     * The config object that will be (or was) used for configuring the facet action.
     * 
     * @return the config object of the facet action
     */
    
    Object getActionConfig();
    
}
