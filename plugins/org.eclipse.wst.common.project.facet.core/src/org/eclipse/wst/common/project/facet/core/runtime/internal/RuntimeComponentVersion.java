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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;
import org.eclipse.wst.common.project.facet.core.internal.IVersion;
import org.eclipse.wst.common.project.facet.core.internal.Versionable;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class RuntimeComponentVersion

    implements IRuntimeComponentVersion, IVersion
    
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
    
    public Versionable getVersionable()
    {
        return (Versionable) this.type;
    }

    public IAdapterFactory getAdapterFactory( final Class type )
    
        throws CoreException
        
    {
        synchronized( this.adapterFactories )
        {
            Object factory = this.adapterFactories.get( type.getName() );
            
            if( factory == null )
            {
                return null;
            }
            
            if( factory instanceof PluginAndClass )
            {
                final PluginAndClass ref = (PluginAndClass) factory;
                
                factory = FacetCorePlugin.instantiate( ref.plugin, ref.clname,
                                                       IAdapterFactory.class );

                this.adapterFactories.put( type.getName(), factory );
            }
            
            return (IAdapterFactory) factory;
        }
    }
    
    void addAdapterFactory( final String type,
                            final String plugin,
                            final String factory )
    {
        synchronized( this.adapterFactories )
        {
            this.adapterFactories.put( type, new PluginAndClass( plugin, factory ) );
        }
    }
    
    private static final class PluginAndClass
    {
        public final String plugin;
        public final String clname;
        
        public PluginAndClass( final String plugin,
                               final String clname )
        {
            this.plugin = plugin;
            this.clname = clname;
        }
    }
    
}
