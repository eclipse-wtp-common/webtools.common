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

import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface IProjectFacetsChangedEvent

    extends IFacetedProjectEvent
    
{
    Set<IProjectFacetVersion> getAddedFacets();
    Set<IProjectFacetVersion> getRemovedFacets();
    Set<IProjectFacetVersion> getFacetsWithChangedVersions();
    Set<IProjectFacetVersion> getAllAffectedFacets();
}
