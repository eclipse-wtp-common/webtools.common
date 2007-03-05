/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.runtime;

import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;

/**
 * Represents a configured instance of a runtime component type and version. A
 * runtime insance is composed of multiple runtime components.
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
     * Returns the runtime that this component belongs to or <code>null</code> if this component
     * has not yet been associated with a runtime.
     * 
     * @return the runtime that that this component belongs to
     */
    
    IRuntime getRuntime();
    
    /**
     * Returns the properties associated with this runtime component. The
     * contents will vary dependending on the component type/version, but 
     * usually this will at least contain the path to the location on disk where 
     * the runtime is installed.
     * 
     * @return the properties associated with this runtime component
     */
    
    Map<String,String> getProperties();
    
    /**
     * Returns the value of the specified property.
     * 
     * @param name the property name
     * @return the property value, or <code>null</code>
     */
    
    String getProperty( String name );
    
}
