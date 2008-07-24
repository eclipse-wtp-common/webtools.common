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

package org.eclipse.jst.common.project.facet.core.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.common.project.facet.core.StandardJreRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeBridge;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class JavaRuntimeBridge

    implements IRuntimeBridge
    
{
    public Set<String> getExportedRuntimeNames() throws CoreException
    {
        final Set<String> result = new HashSet<String>();
        
        for( IVMInstallType vmInstallType : JavaRuntime.getVMInstallTypes() )
        {
            for( IVMInstall vmInstall : vmInstallType.getVMInstalls() )
            {
                result.add( vmInstall.getName() );
            }
        }

        return result;
    }

    
    public IStub bridge( final String name ) throws CoreException
    {
        IVMInstall vmInstall = null;
        
        for( IVMInstallType vmInstallType : JavaRuntime.getVMInstallTypes() )
        {
            vmInstall = vmInstallType.findVMInstallByName( name );
            
            if( vmInstall != null )
            {
                break;
            }
        }
        
        return new Stub( vmInstall );
    }

    private static class Stub
    
        extends IRuntimeBridge.Stub
        
    {
        private final IVMInstall vmInstall;

        public Stub( final IVMInstall vmInstall )
        {
            this.vmInstall = vmInstall;
        }

        public List<IRuntimeComponent> getRuntimeComponents()
        {
            final IRuntimeComponent rc = StandardJreRuntimeComponent.create( this.vmInstall );
            return Collections.singletonList( rc );
        }

        public Map<String,String> getProperties()
        {
            return Collections.emptyMap();
        }
    }
    
}