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

package org.eclipse.wst.common.project.facet.core.runtime.internal;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.core.util.internal.Versionable;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class RuntimeComponentType

    extends Versionable<IRuntimeComponentVersion>
    implements IRuntimeComponentType
    
{
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
        this.versions.addItemWithKey( ver.getVersionString(), ver );
    }
    
    @SuppressWarnings( "rawtypes" )
    
    public Object getAdapter( final Class type )
    {
        return Platform.getAdapterManager().loadAdapter( this, type.getName() );
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
