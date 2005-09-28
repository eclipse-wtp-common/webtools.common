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

package org.eclipse.wst.common.project.facet.core.runtime.internal;

import java.util.HashMap;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class RuntimeComponentVersion

    implements IRuntimeComponentVersion
    
{
    private String plugin;
    private IRuntimeComponentType type;
    private String version;
    private final HashMap adapterFactories = new HashMap();
    
    public String getPluginId()
    {
        return this.plugin;
    }
    
    void setPluginId( final String plugin )
    {
        this.plugin = plugin;
    }

    public IRuntimeComponentType getRuntimeComponentType()
    {
        return this.type;
    }
    
    void setRuntimeComponentType( final IRuntimeComponentType type )
    {
        this.type = type;
    }

    public String getVersionString()
    {
        return this.version;
    }
    
    void setVersionString( final String version )
    {
        this.version = version;
    }
    
    IAdapterFactory getAdapterFactory( final Class type )
    {
        synchronized( this.adapterFactories )
        {
            Object factory = this.adapterFactories.get( type.getName() );
            
            if( factory == null )
            {
                return null;
            }
            
            if( factory instanceof String )
            {
                final Bundle bundle = Platform.getBundle( this.plugin );
                
                try
                {
                    final Class cl = bundle.loadClass( (String) factory );
                    factory = cl.newInstance();
                    this.adapterFactories.put( type.getName(), factory );
                }
                catch( Exception e )
                {
                    // TODO: handle this better
                    throw new RuntimeException( e );
                }
            }
            
            return (IAdapterFactory) factory;
        }
    }
    
    void addAdapterFactory( final String type,
                            final String factory )
    {
        synchronized( this.adapterFactories )
        {
            this.adapterFactories.put( type, factory );
        }
    }
    
}
