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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeBridge;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class BridgedRuntime

    extends AbstractRuntime
    
{
    private final String bridgeId;
    private final String nativeRuntimeId;
    private final IRuntimeBridge.IStub stub;
    private Set<IProjectFacetVersion> supported;
    private List<IRuntimeComponent> composition;
    
    BridgedRuntime( final String bridgeId,
                    final String nativeRuntimeId,
                    final IRuntimeBridge.IStub stub )
    {
        this.bridgeId = bridgeId;
        this.nativeRuntimeId = nativeRuntimeId;
        this.stub = stub;
    }
    
    String getBridgeId()
    {
        return this.bridgeId;
    }
    
    String getNativeRuntimeId()
    {
        return this.nativeRuntimeId;
    }
    
    public List<IRuntimeComponent> getRuntimeComponents()
    {
        final List<IRuntimeComponent> components = this.stub.getRuntimeComponents();
        
        for( IRuntimeComponent rc : components )
        {
            ( (RuntimeComponent) rc ).setRuntime( this );
        }
        
        return Collections.unmodifiableList( components );
    }
    
    public Map<String,String> getProperties()
    {
        return Collections.unmodifiableMap( this.stub.getProperties() );
    }

    public synchronized boolean supports( final IProjectFacetVersion fv )
    {
        if( fv.getPluginId() == null )
        {
            return true;
        }
        
        final List<IRuntimeComponent> comp = getRuntimeComponents();
        
        if( this.supported == null || ! this.composition.equals( comp ) )
        {
            this.supported = RuntimeManagerImpl.getSupportedFacets( comp );
            this.composition = comp;
        }
        
        return this.supported.contains( fv );
    }

    @Override
    public IStatus validate( final IProgressMonitor monitor )
    {
        if( this.stub instanceof IRuntimeBridge.Stub )
        {
            return ( (IRuntimeBridge.Stub) this.stub ).validate( monitor );
        }
        else
        {
            return super.validate( monitor );
        }
    }

}
