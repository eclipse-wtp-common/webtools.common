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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeBridge;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class BridgedRuntime

    extends AbstractRuntime
    
{
    private final String bridgeId;
    private final String nativeRuntimeId;
    private final IRuntimeBridge.IStub stub;
    private Set supported;
    private List composition;
    
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
    
    public List getRuntimeComponents()
    {
        return Collections.unmodifiableList( this.stub.getRuntimeComponents() );
    }
    
    public Map getProperties()
    {
        return Collections.unmodifiableMap( this.stub.getProperties() );
    }

    public synchronized boolean supports( final IProjectFacetVersion fv )
    {
        if( fv.getPluginId() == null )
        {
            return true;
        }
        
        final List comp = this.stub.getRuntimeComponents();
        
        if( this.supported == null || ! this.composition.equals( comp ) )
        {
            this.supported = RuntimeManagerImpl.getSupportedFacets( comp );
            this.composition = comp;
        }
        
        return this.supported.contains( fv );
    }

}
