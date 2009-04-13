/******************************************************************************
 * Copyright (c) 2008 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.runtime.internal;

import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.instantiate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.core.util.internal.UnknownVersion;
import org.eclipse.wst.common.project.facet.core.util.internal.Versionable;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class RuntimeComponentVersion

    implements IRuntimeComponentVersion
    
{
    private String plugin;
    private IRuntimeComponentType type;
    private String version;
    private final Map<String,Object> adapterFactories = new HashMap<String,Object>();
    private Map<IRuntimeComponentVersion,Integer> compTable = null;
    
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

    public IAdapterFactory getAdapterFactory( final Class<?> type )
    
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
                factory = instantiate( ref.plugin, ref.clname, IAdapterFactory.class );
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
    
    void setComparisonTable( final Map<IRuntimeComponentVersion,Integer> compTable )
    {
        this.compTable = compTable;
    }

    public int compareTo( final Object obj )
    {
        if( obj == this )
        {
            return 0;
        }
        else if( obj instanceof IRuntimeComponentVersion )
        {
            final IRuntimeComponentVersion rcv = (IRuntimeComponentVersion) obj;
            
            if( rcv.getRuntimeComponentType() != this.type )
            {
                final String msg
                    = Resources.bind( Resources.cannotCompareVersionsOfDifferentTypes,
                                      this.type.getId(), this.version,
                                      rcv.getRuntimeComponentType().getId(), 
                                      rcv.getVersionString() );
                
                throw new RuntimeException( msg );
            }
            
            return this.compTable.get( rcv ).intValue();
        }
        else if( obj instanceof UnknownVersion )
        {
            try
            {
                final Comparator<String> comp = this.type.getVersionComparator();
                return comp.compare( this.version, ( (IVersion) obj ).getVersionString() );
            }
            catch( CoreException e )
            {
                throw new RuntimeException( e );
            }
        }
        else
        {
            throw new IllegalArgumentException();
        }
        
    }
    
    @SuppressWarnings( "unchecked" )
    public Object getAdapter( final Class type )
    {
        return Platform.getAdapterManager().loadAdapter( this, type.getName() );
    }

    public String toString()
    {
        return this.type.getId() + " " + this.version; //$NON-NLS-1$
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

    private static final class Resources
    
        extends NLS
        
    {
        public static String cannotCompareVersionsOfDifferentTypes;
        
        static
        {
            initializeMessages( RuntimeComponentVersion.class.getName(), Resources.class );
        }
        
        public static String bind( final String template,
                                   final Object arg1,
                                   final Object arg2,
                                   final Object arg3,
                                   final Object arg4 )
        {
            return NLS.bind( template, new Object[] { arg1, arg2, arg3, arg4 } );
        }
    }
    
}
