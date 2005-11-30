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

import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;

/**
 * Represents a configured instance of a runtime component type and version. A
 * runtime insance is composed of multiple runtime components.
 * 
 * <p><i>This class is part of an interim API that is still under development 
 * and expected to change significantly before reaching stability. It is being 
 * made available at this early stage to solicit feedback from pioneering 
 * adopters on the understanding that any code that uses this API will almost 
 * certainly be broken (repeatedly) as the API evolves.</i></p>
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IRuntimeComponent

    extends IAdaptable
    
{
    /**
     * Returns the runtime component type that this is an instance of.
     * 
     * @return the runtime component type that this is an instance of
     */
    
    IRuntimeComponentType getRuntimeComponentType();
    
    /**
     * Returns the runtime component version that this is an instance of.
     * 
     * @return the runtime component version that this is an instance of
     */
    
    IRuntimeComponentVersion getRuntimeComponentVersion();
    
    /**
     * Returns the properties associated with this runtime component. The
     * contents will vary dependending on the component type/version, but 
     * usually this will at least contain the path to the location on disk where 
     * the runtime is installed.
     * 
     * @return the properties associated with this runtime component (key
     *   type: {@see String}, value type: {@see String})
     */
    
    Map getProperties();
    
    /**
     * Returns the value of the specified property.
     * 
     * @param name the property name
     * @return the property value, or <code>null</code>
     */
    
    String getProperty( String name );
    
}
