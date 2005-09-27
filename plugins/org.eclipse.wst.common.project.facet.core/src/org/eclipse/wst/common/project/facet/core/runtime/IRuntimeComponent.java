package org.eclipse.wst.common.project.facet.core.runtime;

import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;

public interface IRuntimeComponent

    extends IAdaptable
    
{
    IRuntimeComponentType getRuntimeComponentType();
    IRuntimeComponentVersion getRuntimeComponentVersion();
    Map getProperties();
    String getProperty( String key );
}
