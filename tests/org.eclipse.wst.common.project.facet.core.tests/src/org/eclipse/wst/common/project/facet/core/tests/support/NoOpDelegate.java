package org.eclipse.wst.common.project.facet.core.tests.support;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

public final class NoOpDelegate

    implements IDelegate
    
{
    public void execute( final IProject project, 
                         final IProjectFacetVersion fv, 
                         final Object config, 
                         final IProgressMonitor monitor ) 
    {
        // do nothing
    }
    
}
