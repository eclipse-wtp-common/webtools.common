package org.eclipse.wst.common.project.facet.core.events;

import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

public interface IProjectFacetsChangedEvent

    extends IFacetedProjectEvent
    
{
    Set<IProjectFacetVersion> getAddedFacets();
    Set<IProjectFacetVersion> getRemovedFacets();
    Set<IProjectFacetVersion> getFacetsWithChangedVersions();
    Set<IProjectFacetVersion> getAllAffectedFacets();
}
