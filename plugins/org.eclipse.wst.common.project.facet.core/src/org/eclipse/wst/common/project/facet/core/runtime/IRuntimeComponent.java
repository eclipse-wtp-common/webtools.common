/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.runtime;

import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;

/**
 * Represents a configured instance of a runtime component type and version. A runtime instance is 
 * composed of multiple runtime components.
 * 
 * <p>This interface is not intended to be implemented outside of this framework. Client code can 
 * get access to <code>IRuntimeComponent</code> objects by using methods on the 
 * {@link IRuntime} and {@link RuntimeManager} classes.</p>  
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @see IRuntime#getRuntimeComponents()
 * @see RuntimeManager#createRuntimeComponent(IRuntimeComponentVersion,Map) 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
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
