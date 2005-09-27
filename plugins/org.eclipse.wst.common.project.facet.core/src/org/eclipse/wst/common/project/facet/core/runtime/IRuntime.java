package org.eclipse.wst.common.project.facet.core.runtime;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

public interface IRuntime

    extends IAdaptable
    
{
    String getName();
    List getRuntimeComponents();
    Map getProperties();
    String getProperty( String key );
    boolean supports( IProjectFacetVersion fv );
}
