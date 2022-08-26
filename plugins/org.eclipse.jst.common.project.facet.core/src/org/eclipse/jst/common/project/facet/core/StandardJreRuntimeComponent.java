/******************************************************************************
 * Copyright (c) 2010, 2022 Oracle and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation
 *    Carl Anderson - Java 9 support
 *    John Collier - Java 10-11, 13-15 support
 *    Leon Keuroglian - Java 12 support
 *    Nitin Dahyabhai - Java 12, 16, 17 support
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
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StandardJreRuntimeComponent 
{
    public static final String TYPE_ID = "standard.jre"; //$NON-NLS-1$
    public static final IRuntimeComponentType TYPE = RuntimeManager.getRuntimeComponentType( TYPE_ID );
    public static final IRuntimeComponentVersion VERSION_1_3 = TYPE.getVersion( "1.3" ); //$NON-NLS-1$
    public static final IRuntimeComponentVersion VERSION_1_4 = TYPE.getVersion( "1.4" ); //$NON-NLS-1$
    public static final IRuntimeComponentVersion VERSION_1_5 = TYPE.getVersion( "1.5" ); //$NON-NLS-1$
    public static final IRuntimeComponentVersion VERSION_1_6 = TYPE.getVersion( "1.6" ); //$NON-NLS-1$
    public static final IRuntimeComponentVersion VERSION_1_7 = TYPE.getVersion( "1.7" ); //$NON-NLS-1$
    public static final IRuntimeComponentVersion VERSION_1_8 = TYPE.getVersion( "1.8" ); //$NON-NLS-1$
    public static final IRuntimeComponentVersion VERSION_9 = TYPE.getVersion( "9" ); //$NON-NLS-1$
    public static final IRuntimeComponentVersion VERSION_10 = TYPE.getVersion( "10" ); //$NON-NLS-1$
    public static final IRuntimeComponentVersion VERSION_11 = TYPE.getVersion( "11" ); //$NON-NLS-1$
    public static final IRuntimeComponentVersion VERSION_12 = TYPE.getVersion( "12" ); //$NON-NLS-1$
    public static final IRuntimeComponentVersion VERSION_13 = TYPE.getVersion( "13" ); //$NON-NLS-1$
    public static final IRuntimeComponentVersion VERSION_14 = TYPE.getVersion( "14" ); //$NON-NLS-1$
    public static final IRuntimeComponentVersion VERSION_15 = TYPE.getVersion( "15" ); //$NON-NLS-1$
    public static final IRuntimeComponentVersion VERSION_16 = TYPE.getVersion( "16" ); //$NON-NLS-1$
    public static final IRuntimeComponentVersion VERSION_17 = TYPE.getVersion( "17" ); //$NON-NLS-1$
    public static final IRuntimeComponentVersion VERSION_18 = TYPE.getVersion( "18" ); //$NON-NLS-1$
    public static final IRuntimeComponentVersion VERSION_19 = TYPE.getVersion( "19" ); //$NON-NLS-1$

    @Deprecated
    public static final IRuntimeComponentVersion VERSION_5_0 = VERSION_1_5;
    
    @Deprecated( )
    public static final IRuntimeComponentVersion VERSION_6_0 = VERSION_1_6;

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
        

        /* Handle null, then LTS versions, then remaining versions backwards */
        if( jvmver == null ) 
        {
            rcv = StandardJreRuntimeComponent.VERSION_17;
        }
        else if( jvmver.startsWith( "17" ) ) //$NON-NLS-1$
        {
            rcv = StandardJreRuntimeComponent.VERSION_17;
        }
        else if( jvmver.startsWith( "11" ) ) //$NON-NLS-1$
        {
            rcv = StandardJreRuntimeComponent.VERSION_11;
        }
        else if( jvmver.startsWith( "1.8" ) ) //$NON-NLS-1$
        {
        	rcv = StandardJreRuntimeComponent.VERSION_1_8;
        }
        else if( jvmver.startsWith( "19" ) ) //$NON-NLS-1$
        {
            rcv = StandardJreRuntimeComponent.VERSION_19;
        }
        else if( jvmver.startsWith( "18" ) ) //$NON-NLS-1$
        {
            rcv = StandardJreRuntimeComponent.VERSION_18;
        }
        else if( jvmver.startsWith( "16" ) ) //$NON-NLS-1$
        {
            rcv = StandardJreRuntimeComponent.VERSION_16;
        }
        else if( jvmver.startsWith( "15" ) ) //$NON-NLS-1$
        {
            rcv = StandardJreRuntimeComponent.VERSION_15;
        }
        else if( jvmver.startsWith( "14" ) ) //$NON-NLS-1$
        {
            rcv = StandardJreRuntimeComponent.VERSION_14;
        }
        else if( jvmver.startsWith( "13" ) ) //$NON-NLS-1$
        {
            rcv = StandardJreRuntimeComponent.VERSION_13;
        }
        else if( jvmver.startsWith( "12" ) ) //$NON-NLS-1$
        {
            rcv = StandardJreRuntimeComponent.VERSION_12;
        }
        else if( jvmver.startsWith( "10" ) ) //$NON-NLS-1$
        {
            rcv = StandardJreRuntimeComponent.VERSION_10;
        }
        else if( jvmver.startsWith( "9" ) ) //$NON-NLS-1$
        {
            rcv = StandardJreRuntimeComponent.VERSION_9;
        }
        else if( jvmver.startsWith( "1.7" ) ) //$NON-NLS-1$
        {
        	rcv = StandardJreRuntimeComponent.VERSION_1_7;
        }
        else if( jvmver.startsWith( "1.6" ) ) //$NON-NLS-1$
        {
        	rcv = StandardJreRuntimeComponent.VERSION_1_6;
        }
        else if( jvmver.startsWith( "1.5" ) ) //$NON-NLS-1$
        {
        	rcv = StandardJreRuntimeComponent.VERSION_1_5;
        }
        else if( jvmver.startsWith( "1.4" ) ) //$NON-NLS-1$
        {
        	rcv = StandardJreRuntimeComponent.VERSION_1_4;
        }
        else if( jvmver.startsWith( "1.3" ) ) //$NON-NLS-1$
        {
        	rcv = StandardJreRuntimeComponent.VERSION_1_3;
        }
        else
        { // Unrecognizable, so use the Eclipse Platform minimum
            rcv = StandardJreRuntimeComponent.VERSION_11;
        }
        
        final Map<String,String> properties = new HashMap<String,String>();
        
        if( vmInstall != null )
        {
            properties.put( StandardJreRuntimeComponent.PROP_VM_INSTALL_TYPE, 
                            vmInstall.getVMInstallType().getId() );
            
            properties.put( StandardJreRuntimeComponent.PROP_VM_INSTALL_ID, 
                            vmInstall.getId() );
        }
        
        return RuntimeManager.createRuntimeComponent( rcv, properties );
    }
    
}
