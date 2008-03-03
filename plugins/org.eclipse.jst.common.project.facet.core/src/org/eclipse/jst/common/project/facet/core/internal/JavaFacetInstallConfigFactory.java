package org.eclipse.jst.common.project.facet.core.internal;

import org.eclipse.jst.common.project.facet.core.JavaFacetInstallConfig;
import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;

public final class JavaFacetInstallConfigFactory

    implements IActionConfigFactory
    
{
    public Object create() 
    {
        return new JavaFacetInstallConfig();
    }

}
