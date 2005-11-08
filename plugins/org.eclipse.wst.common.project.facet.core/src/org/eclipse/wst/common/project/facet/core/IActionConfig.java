package org.eclipse.wst.common.project.facet.core;

import org.eclipse.core.runtime.IStatus;

public interface IActionConfig
{
    void setVersion( IProjectFacetVersion fv );
    void setProjectName( String pjname );
    IStatus validate();
}
