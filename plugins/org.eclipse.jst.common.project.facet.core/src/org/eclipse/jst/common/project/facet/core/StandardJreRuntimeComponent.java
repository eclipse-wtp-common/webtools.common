/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class StandardJreRuntimeComponent 
{
    public static final String TYPE_ID = "standard.jre"; //$NON-NLS-1$
    
    public static final IRuntimeComponentType TYPE 
        = RuntimeManager.getRuntimeComponentType( TYPE_ID );
    
    public static final IRuntimeComponentVersion VERSION_1_3
        = TYPE.getVersion( "1.3" ); //$NON-NLS-1$
    
    public static final IRuntimeComponentVersion VERSION_1_4
        = TYPE.getVersion( "1.4" ); //$NON-NLS-1$

    public static final IRuntimeComponentVersion VERSION_5_0
        = TYPE.getVersion( "5.0" ); //$NON-NLS-1$
    
    public static final IRuntimeComponentVersion VERSION_6_0
        = TYPE.getVersion( "6.0" ); //$NON-NLS-1$
    
    public static final String PROP_VM_INSTALL_TYPE = "vm-install-type"; //$NON-NLS-1$
    public static final String PROP_VM_INSTALL_ID = "vm-install-id"; //$NON-NLS-1$
    
    public static IRuntimeComponent create( final IVMInstall vmInstall )
    {
        String jvmver = null;
        
        if( vmInstall instanceof IVMInstall2 )
        {
            final IVMInstall2 vmInstall2 = (IVMInstall2) vmInstall;
            jvmver = vmInstall2.getJavaVersion();
        }
        
        final IRuntimeComponentVersion rcv;
        
        if( jvmver == null ) 
        {
            rcv = StandardJreRuntimeComponent.VERSION_6_0;
        } 
        else if( jvmver.startsWith( "1.3" ) )
        {
            rcv = StandardJreRuntimeComponent.VERSION_1_3;
        }
        else if( jvmver.startsWith( "1.4" ) )
        {
            rcv = StandardJreRuntimeComponent.VERSION_1_4;
        }
        else if( jvmver.startsWith( "1.5" ) || jvmver.startsWith( "5.0" ) )
        {
            rcv = StandardJreRuntimeComponent.VERSION_5_0;
        }
        else if( jvmver.startsWith( "1.6" ) || jvmver.startsWith( "6.0" ) )
        {
            rcv = StandardJreRuntimeComponent.VERSION_6_0;
        }
        else 
        {
            rcv = StandardJreRuntimeComponent.VERSION_6_0;
        }
        
        final Map<String,String> properties = new HashMap<String,String>();
        
        properties.put( StandardJreRuntimeComponent.PROP_VM_INSTALL_TYPE, 
                        vmInstall.getVMInstallType().getId() );
        
        properties.put( StandardJreRuntimeComponent.PROP_VM_INSTALL_ID, 
                        vmInstall.getId() );
        
        return RuntimeManager.createRuntimeComponent( rcv, properties );
    }
    
}
