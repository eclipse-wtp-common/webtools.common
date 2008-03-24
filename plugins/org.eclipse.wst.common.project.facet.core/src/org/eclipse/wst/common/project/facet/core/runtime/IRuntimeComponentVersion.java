/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.runtime;

import org.eclipse.wst.common.project.facet.core.IVersion;

/**
 * Represents a version of a runtime component. A runtime instance is composed of multiple runtime 
 * components, each of which has a type and a version.
 * 
 * <p>This interface is not intended to be implemented outside of this framework. Runtime component
 * versions are declared using <code>org.eclipse.wst.common.project.facet.core.runtimes</code>
 * extension point. Once declared, client code can get access to 
 * <code>IRuntimeComponentVersion</code> objects by using methods on the 
 * {@see IRuntimeComponentType} class.</p>  
 *
 * @see IRuntimeComponentType.getVersions()
 * @see IRuntimeComponentType.getVersions(String)
 * @see IRuntimeComponentType.getVersion(String)
 * @see IRuntimeComponentType.hasVersion(String)
 * @see IRuntimeComponentType.getLatestVersion()
 * @see IRuntimeComponentType.getSortedVersions(boolean)
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IRuntimeComponentVersion

    extends Comparable, IVersion
    
{
    /**
     * Returns the runtime component type that this is a version of.
     * 
     * @return returns the runtime component type that this is a version of
     */
    
    IRuntimeComponentType getRuntimeComponentType();
    
    /**
     * Returns the version string.
     * 
     * @return the version string
     */
    
    String getVersionString();
    
}
