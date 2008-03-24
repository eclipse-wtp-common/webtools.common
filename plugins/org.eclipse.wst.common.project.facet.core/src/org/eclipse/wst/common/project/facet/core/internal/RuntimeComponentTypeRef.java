/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.reportMissingAttribute;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.project.facet.core.IVersionExpr;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.common.project.facet.core.runtime.internal.RuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.core.util.internal.VersionExpr;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class RuntimeComponentTypeRef
{
    private static final String ATTR_ID = "id"; //$NON-NLS-1$
    private static final String ATTR_VERSION = "version"; //$NON-NLS-1$
    
    private final IRuntimeComponentType rct;
    private final VersionExpr<IRuntimeComponentVersion> vexpr;
    
    @SuppressWarnings( "unchecked" )
    public RuntimeComponentTypeRef( final IRuntimeComponentType rct,
                                    final IVersionExpr vexpr )
    {
        this( rct, (VersionExpr<IRuntimeComponentVersion>) vexpr );
    }

    public RuntimeComponentTypeRef( final IRuntimeComponentType rct,
                                    final VersionExpr<IRuntimeComponentVersion> vexpr )
    {
        this.rct = rct;
        this.vexpr = vexpr;
    }
    
    public boolean check( final Set<IRuntimeComponentVersion> rcvs )
    {
        for( IRuntimeComponentVersion rcv : rcvs )
        {
            if( this.rct == rcv.getRuntimeComponentType() )
            {
                if( this.vexpr != null )
                {
                    return this.vexpr.check( rcv );
                }
                else
                {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static RuntimeComponentTypeRef read( final IConfigurationElement config )
    {
        final String pluginId = config.getContributor().getName();
        final String id = config.getAttribute( ATTR_ID );

        if( id == null )
        {
            reportMissingAttribute( config, ATTR_ID );
            return null;
        }
        
        if( ! RuntimeManager.isRuntimeComponentTypeDefined( id ) )
        {
            FacetedProjectFrameworkImpl.reportMissingRuntimeComponentType( id, pluginId );
            return null;
        }
        
        final IRuntimeComponentType rct = RuntimeManager.getRuntimeComponentType( id );
        
        final String v = config.getAttribute( ATTR_VERSION );
        VersionExpr<RuntimeComponentVersion> vexpr = null;
        
        if( v != null )
        {
            try
            {
                vexpr = new VersionExpr<RuntimeComponentVersion>( rct, v, pluginId );
            }
            catch( CoreException e )
            {
                FacetCorePlugin.log( e.getStatus() );
                return null;
            }
        }
        
        return new RuntimeComponentTypeRef( rct, vexpr );
    }
    
}
