package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jst.server.core.ClasspathRuntimeTargetHandler;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeTargetHandler;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;

public final class RuntimeBridge
{
    private static Map mappings = new HashMap();
    
    static
    {
        mappings.put( "org.eclipse.jst.server.tomcat.runtime.55",
                      RuntimeManager.get().getRuntimeComponentType( "tomcat" ).getVersion( "5.5" ) );
        
        mappings.put( "org.eclipse.jst.server.tomcat.runtime.41",
                      RuntimeManager.get().getRuntimeComponentType( "tomcat" ).getVersion( "4.1" ) );
    }
    
    public static void port()
    {
        final IRuntime[] runtimes = ServerCore.getRuntimes();
        
        for( int i = 0; i < runtimes.length; i++ )
        {
            final IRuntime runtime = runtimes[ i ];
            final String name = runtime.getName();
            
            if( ! RuntimeManager.get().isRuntimeDefined( name ) )
            {
                final String type = runtime.getRuntimeType().getId();
                
                final IRuntimeComponentVersion mapped
                    = (IRuntimeComponentVersion) mappings.get( type );
                
                if( mapped != null )
                {
                    final List components = new ArrayList();
                    
                    Map properties;
                    
                    properties = new HashMap();
                    properties.put( "location", runtime.getLocation().toPortableString() );
                    properties.put( "name", name );
                    
                    components.add( RuntimeManager.get().createRuntimeComponent( mapped, properties ) );
                    
                    final ClasspathRuntimeTargetHandler cphandler
                        = getClasspathHandler( runtime );
                    
                    final IPath jrecontainer 
                        = findJreContainer( cphandler.getDelegateClasspathEntries( runtime, null ) );
                    
                    final IVMInstallType vminstalltype
                        = JavaRuntime.getVMInstallType( jrecontainer.segment( 1 ) );
                    
                    final IVMInstall2 vminstall
                        = (IVMInstall2) vminstalltype.findVMInstallByName( jrecontainer.segment( 2 ) );
                    
                    final String jvmver = vminstall.getJavaVersion();
                    final IRuntimeComponentVersion rcv;
                    
                    if( jvmver.startsWith( "1.4" ) )
                    {
                        rcv = RuntimeManager.get().getRuntimeComponentType( "standard.jre" ).getVersion( "1.4" );
                    }
                    else if( jvmver.startsWith( "1.5" ) )
                    {
                        rcv = RuntimeManager.get().getRuntimeComponentType( "standard.jre" ).getVersion( "5.0" );
                    }
                    else
                    {
                        continue;
                    }

                    properties = new HashMap();
                    properties.put( "name", jrecontainer.segment( 2 ) );
                    
                    components.add( RuntimeManager.get().createRuntimeComponent( rcv, properties ) );
                    
                    RuntimeManager.get().defineRuntime( name, components, null );
                }
            }
        }
    }
    
    private static ClasspathRuntimeTargetHandler getClasspathHandler( final IRuntime r )
    {
        final IRuntimeTargetHandler[] handlers 
            = ServerCore.getRuntimeTargetHandlers();
        
        for( int j = 0; j < handlers.length; j++ )
        {
            final IRuntimeTargetHandler handler = handlers[ j ];
            
            if( handler.supportsRuntimeType( r.getRuntimeType() ) ) 
            {
                return (ClasspathRuntimeTargetHandler) handler.getAdapter( ClasspathRuntimeTargetHandler.class );
            }
        }
        
        throw new IllegalStateException();
    }
    
    private static IPath findJreContainer( final IClasspathEntry[] cpentries )
    {
        for( int i = 0; i < cpentries.length; i++ )
        {
            final IPath path = cpentries[ i ].getPath();
            
            if( path.segment( 0 ).equals( JavaRuntime.JRE_CONTAINER ) )
            {
                return path;
            }
        }
        
        throw new IllegalStateException();
    }
    
}
