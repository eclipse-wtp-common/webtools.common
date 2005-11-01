package org.eclipse.wst.common.project.facet.core;

import org.eclipse.core.runtime.CoreException;

public interface IActionConfigFactory
{
    Object create()
    
        throws CoreException;
    
}
