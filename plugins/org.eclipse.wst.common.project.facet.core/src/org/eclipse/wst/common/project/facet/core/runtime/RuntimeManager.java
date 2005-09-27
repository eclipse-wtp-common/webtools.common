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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.wst.common.project.facet.core.runtime.internal.RuntimeManagerImpl;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public abstract class RuntimeManager 
{
    private static RuntimeManager instance = new RuntimeManagerImpl();
    
    /**
     * Returns the singleton instance of the <code>RuntimeManager</code>
     * 
     * @return the singleton instance of the <code>RuntimeManager</code>
     */
    
    public static RuntimeManager get()
    {
        return instance;
    }
    
    public abstract Set getRuntimeComponentTypes();
    public abstract boolean isRuntimeComponentTypeDefined( String id );
    public abstract IRuntimeComponentType getRuntimeComponentType( String id );
    
    public abstract Set getRuntimes();
    
    public abstract boolean isRuntimeDefined( String name );
    
    public abstract IRuntime getRuntime( String name );
    
    public abstract IRuntime defineRuntime( String name,
                                            List components,
                                            Map properties );
    
    public abstract void deleteRuntime( IRuntime runtime );
    
    public abstract IRuntimeComponent createRuntimeComponent( IRuntimeComponentVersion rcv,
                                                              Map properties );
    
}
