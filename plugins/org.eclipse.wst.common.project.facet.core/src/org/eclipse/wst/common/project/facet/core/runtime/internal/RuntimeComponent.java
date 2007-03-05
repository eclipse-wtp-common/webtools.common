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

package org.eclipse.wst.common.project.facet.core.runtime.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class RuntimeComponent

    implements IRuntimeComponent
    
{
    private RuntimeComponentVersion rcv;
    private IRuntime runtime;
    private final Map<String,String> properties;
    private final Map<String,String> propertiesReadOnly;
    
    /**
     * This class should not be instantiated outside this package.
     */

    RuntimeComponent() 
    {
        this.rcv = null;
        this.runtime = null;
        this.properties = new HashMap<String,String>();
        this.propertiesReadOnly = Collections.unmodifiableMap( this.properties );
    }

    public IRuntimeComponentType getRuntimeComponentType()
    {
        return this.rcv.getRuntimeComponentType();
    }

    public IRuntimeComponentVersion getRuntimeComponentVersion()
    {
        return this.rcv;
    }
    
    void setRuntimeComponentVersion( final IRuntimeComponentVersion rcv )
    {
        this.rcv = (RuntimeComponentVersion) rcv;
    }
    
    public IRuntime getRuntime()
    {
        return this.runtime;
    }
    
    void setRuntime( final IRuntime runtime )
    {
        if( runtime == null )
        {
            throw new IllegalArgumentException();
        }
        
        if( this.runtime != null && ! this.runtime.equals( runtime ) )
        {
            throw new IllegalStateException( Resources.runtimeAlreadySet );
        }
        
        this.runtime = runtime;
    }

    public Map<String,String> getProperties()
    {
        return this.propertiesReadOnly;
    }

    public String getProperty( final String key )
    {
        return this.properties.get( key );
    }
    
    void setProperty( final String key,
                      final String value )
    {
        this.properties.put( key, value );
    }

    public Object getAdapter( final Class type )
    {
        final IAdapterManager manager = Platform.getAdapterManager();
        Object res = manager.loadAdapter( this, type.getName() );
        
        if( res == null )
        {
            IAdapterFactory factory = null;
            
            try
            {
                factory = this.rcv.getAdapterFactory( type );
            }
            catch( CoreException e )
            {
                FacetCorePlugin.log( e );
            }
            
            if( factory != null )
            {
                res = factory.getAdapter( this, type );
            }
        }
        
        return res;
    }

    public boolean equals( final Object obj )
    {
        if( obj instanceof RuntimeComponent )
        {
            final RuntimeComponent rc = (RuntimeComponent) obj;
            return this.rcv == rc.rcv && this.properties.equals( rc.properties );
        }
        
        return false;
    }
    
    public int hashCode()
    {
        return this.rcv.hashCode();
    }
    
    public String toString()
    {
        return this.rcv.toString();
    }

    private static final class Resources
    
        extends NLS
        
    {
        public static String runtimeAlreadySet;
        
        static
        {
            initializeMessages( RuntimeComponent.class.getName(), Resources.class );
        }
    }
    
}
