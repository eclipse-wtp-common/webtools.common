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

import java.util.Collections;
import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.VersionFormatException;
import org.eclipse.wst.common.project.facet.core.internal.Versionable;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class RuntimeComponentType

    extends Versionable
    implements IRuntimeComponentType
    
{
    private static final IVersionAdapter VERSION_ADAPTER = new IVersionAdapter()
    {
        public String adapt( final Object obj )
        {
            return ( (IRuntimeComponentVersion) obj ).getVersionString();
        }
    };

    private String id;
    private String plugin;
    
    public String getId()
    {
        return this.id;
    }
    
    void setId( final String id )
    {
        this.id = id;
    }
    
    public String getPluginId()
    {
        return this.plugin;
    }
    
    void setPluginId( final String plugin )
    {
        this.plugin = plugin;
    }
    
    void addVersion( final IRuntimeComponentVersion ver )
    {
        this.versions.add( ver.getVersionString(), ver );
    }

    public IRuntimeComponentVersion getVersion( final String version )
    {
        final IRuntimeComponentVersion rcv
            = (IRuntimeComponentVersion) this.versions.get( version );
        
        if( rcv == null )
        {
            final String msg
                = NLS.bind( RuntimeManagerImpl.Resources.runtimeComponentVersionNotDefined,
                            this.id, version );
            
            throw new IllegalArgumentException( msg );
        }
        
        return rcv;
    }

    public IRuntimeComponentVersion getLatestVersion()
    
        throws VersionFormatException, CoreException
        
    {
        if( this.versions.size() > 0 )
        {
            final Comparator comp = getVersionComparator( true, VERSION_ADAPTER );
            final Object max = Collections.max( this.versions, comp );
            
            return (IRuntimeComponentVersion) max;
        }
        else
        {
            return null;
        }
    }
    
    public Object getAdapter( final Class type )
    {
        return Platform.getAdapterManager().loadAdapter( this, type.getName() );
    }

    protected IVersionAdapter getVersionAdapter()
    {
        return VERSION_ADAPTER;
    }
    
    public String createVersionNotFoundErrMsg( final String verstr )
    {
        return NLS.bind( RuntimeManagerImpl.Resources.runtimeComponentVersionNotDefined,
                         this.id, verstr );
    }
    
    public String toString()
    {
        return this.id;
    }
    
}
