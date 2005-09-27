package org.eclipse.wst.common.project.facet.core.runtime;

import java.util.Comparator;
import java.util.Set;

public interface IRuntimeComponentType
{
    String getId();
    String getPluginId();
    Set getVersions();
    boolean hasVersion( String version );
    IRuntimeComponentVersion getVersion( String version );
    IRuntimeComponentVersion getLatestVersion();
    Comparator getVersionComparator();
    String getIconPath();
}
