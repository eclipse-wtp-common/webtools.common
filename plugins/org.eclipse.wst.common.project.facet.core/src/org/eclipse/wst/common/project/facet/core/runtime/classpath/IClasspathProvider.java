package org.eclipse.wst.common.project.facet.core.runtime.classpath;

import java.util.List;

import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

public interface IClasspathProvider
{
    List getClasspathEntries( IProjectFacetVersion fv );
}
