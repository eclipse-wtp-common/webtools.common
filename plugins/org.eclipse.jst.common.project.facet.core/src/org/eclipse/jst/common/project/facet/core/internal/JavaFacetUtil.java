/******************************************************************************
 * Copyright (c) 2010 Oracle
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.common.project.facet.core.ClasspathHelper;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class JavaFacetUtil
{
    public static final String FILE_CLASSPATH = ".classpath";  //$NON-NLS-1$
    public static final String FILE_JDT_CORE_PREFS = ".settings/org.eclipse.jdt.core.prefs"; //$NON-NLS-1$
    
    private static final IPath CPE_PREFIX_FOR_EXEC_ENV 
        = new Path( "org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType" ); //$NON-NLS-1$
    
    private static final Map<IProjectFacetVersion,String> FACET_VER_TO_EXEC_ENV = new HashMap<IProjectFacetVersion,String>();
    
    static
    {
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_1_3, "J2SE-1.3" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_1_4, "J2SE-1.4" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_1_5, "J2SE-1.5" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_1_6, "JavaSE-1.6" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_1_7, "JavaSE-1.7" ); //$NON-NLS-1$
    }
    
    public static String getCompilerLevel()
    {
        String level = JavaCore.getOption( JavaCore.COMPILER_COMPLIANCE );
        
        if( level == null )
        {
            final Hashtable<?,?> defaults = JavaCore.getDefaultOptions();
            level = (String) defaults.get( JavaCore.COMPILER_COMPLIANCE );
        }
        
        return level;
    }

    public static String getCompilerLevel( final IProject project )
    {
        final IJavaProject jproj = JavaCore.create( project );
        String level = jproj.getOption( JavaCore.COMPILER_COMPLIANCE, false );
        
        if( level == null )
        {
            level = getCompilerLevel();
        }
        
        return level;
    }
    
    public static void setCompilerLevel( final IProject project,
                                         final IProjectFacetVersion fv )
    
        throws CoreException
        
    {
        setCompilerLevel( project, fv.getVersionString() );
    }

    public static void setCompilerLevel( final IProject project,
                                         final String level )
    
        throws CoreException
        
    {
        final IJavaProject jproj = JavaCore.create( project );
        final Map<?,?> options = jproj.getOptions( false );
        JavaCore.setComplianceOptions( level, options );
        jproj.setOptions( options );
    }
    
    public static void scheduleFullBuild( final IProject project )
    {
        // This code is modeled after the code in 
        // org.eclipse.jdt.internal.ui.util.CoreUtility.getBuildJob() method.
        
        final IWorkspace ws = ResourcesPlugin.getWorkspace();
        
        final String msg 
            = NLS.bind( Resources.buildingMsg, project.getName() );
        
        final Job buildJob = new Job( msg ) 
        {
            public IStatus run( final IProgressMonitor monitor ) 
            {
                monitor.beginTask( msg, 2 );
                
                try
                {
                    project.build( IncrementalProjectBuilder.FULL_BUILD,
                                   new SubProgressMonitor( monitor, 1 ) );
                    
                    ws.build( IncrementalProjectBuilder.INCREMENTAL_BUILD, 
                              new SubProgressMonitor( monitor, 1 ) );
                    
                }
                catch( CoreException e )
                {
                    return e.getStatus();
                }
                finally
                {
                    monitor.done();
                }
                
                return Status.OK_STATUS;
            }
            
            public boolean belongsTo( final Object family ) 
            {
                return family == ResourcesPlugin.FAMILY_MANUAL_BUILD;
            }
        };
         
        buildJob.setRule( ws.getRuleFactory().buildRule() );
        buildJob.schedule();
    }
    
    public static void resetClasspath( final IProject project,
                                       final IProjectFacetVersion oldver,
                                       final IProjectFacetVersion newver )
    
        throws CoreException
        
    {
        if( oldver != null )
        {
            ClasspathHelper.removeClasspathEntries( project, oldver );
        }
        
        // If this was a java project before it became a faceted project or
        // the JRE container has been added manually, the above method will not
        // delete the old JRE container. Do it manually.
        
        removeJreContainer( project );
        
        if( ! ClasspathHelper.addClasspathEntries( project, newver ) ) 
        {
            final IVMInstall vm = JavaRuntime.getDefaultVMInstall();
            
            if( vm != null )
            {
                final IPath path = CPE_PREFIX_FOR_EXEC_ENV.append( getCorrespondingExecutionEnvironment( newver ) );
                final IClasspathEntry cpe = JavaCore.newContainerEntry( path );
                final List<IClasspathEntry> entries = Collections.singletonList( cpe );
                
                ClasspathHelper.addClasspathEntries( project, newver, entries );
            }
        }
    }
    
    private static void removeJreContainer( final IProject proj ) 
    
        throws CoreException
        
    {
        final IJavaProject jproj = JavaCore.create( proj );
        final IClasspathEntry[] cp = jproj.getRawClasspath();
        
        int pos = -1;
        
        for( int i = 0; i < cp.length; i++ )
        {
            final IClasspathEntry cpe = cp[ i ];
            
            if( cpe.getEntryKind() == IClasspathEntry.CPE_CONTAINER &&
                cpe.getPath().segment( 0 ).equals( JavaRuntime.JRE_CONTAINER ) )
            {
                pos = i;
                break;
            }
        }
            
        if( pos == -1 )
        {
            return;
        }
        
        final IClasspathEntry[] newcp 
            = new IClasspathEntry[ cp.length - 1 ];
        
        System.arraycopy( cp, 0, newcp, 0, pos );
        System.arraycopy( cp, pos + 1, newcp, pos, newcp.length - pos );
        
        jproj.setRawClasspath( newcp, null );
    }
    
    public static String getCorrespondingExecutionEnvironment( final IProjectFacetVersion fv )
    {
        final String res = FACET_VER_TO_EXEC_ENV.get( fv );
        
        if( res == null )
        {
            throw new IllegalArgumentException( fv.toString() );
        }
        
        return res;
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String buildingMsg;
        
        static
        {
            initializeMessages( JavaFacetUtil.class.getName(), 
                                Resources.class );
        }
    }
    
}
