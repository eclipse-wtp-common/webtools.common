/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.runtime;

/**
 * Represents a version of a runtime component. A runtime instance is composed 
 * of multiple runtime components, each of which has a type and a version.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IRuntimeComponentVersion
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
