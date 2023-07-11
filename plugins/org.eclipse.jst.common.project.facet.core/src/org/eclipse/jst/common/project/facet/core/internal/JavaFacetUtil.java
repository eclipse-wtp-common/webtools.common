/******************************************************************************
 * Copyright (c) 2010, 2023 Oracle and others
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
 *    Nitin Dahyabhai - Java 12, 16, 17, 18, 19 support
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core.internal;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
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
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_1_8, "JavaSE-1.8" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_9, "JavaSE-9" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_10, "JavaSE-10" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_11, "JavaSE-11" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_12, "JavaSE-12" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_13, "JavaSE-13" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_14, "JavaSE-14" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_15, "JavaSE-15" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_16, "JavaSE-16" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_17, "JavaSE-17" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_18, "JavaSE-18" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_19, "JavaSE-19" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_20, "JavaSE-20" ); //$NON-NLS-1$
        FACET_VER_TO_EXEC_ENV.put( JavaFacet.VERSION_21, "JavaSE-21" ); //$NON-NLS-1$
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
        final Map<String, String> options = jproj.getOptions( false );
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
		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] rawClasspath = javaProject.getRawClasspath();
		/*
		 * We're only handling the "java" facet. Avoid needlessly reordering
		 * the build path and change the container directly.
		 */
		boolean changed = false;
		boolean hadJRE = false;
		for (int i = 0; i < rawClasspath.length; i++) {
			if (rawClasspath[i].getEntryKind() == IClasspathEntry.CPE_CONTAINER && JavaRuntime.JRE_CONTAINER.equals(rawClasspath[i].getPath().segment(0))) {
				hadJRE = true;
				IClasspathEntry oldEntry = rawClasspath[i];
				IPath path = CPE_PREFIX_FOR_EXEC_ENV.append(getCorrespondingExecutionEnvironment(newver));
				IClasspathEntry newEntry = JavaCore.newContainerEntry(path, oldEntry.getAccessRules(), oldEntry.getExtraAttributes(), oldEntry.isExported());
				rawClasspath[i] = newEntry;
				changed = true;
			}
		}
		if (!hadJRE) {
			boolean insertedJRE = false;
			List<IClasspathEntry> entries = new ArrayList<>(Arrays.asList(rawClasspath));
			for (int i = rawClasspath.length - 1; i >= 0; i++) {
				if (rawClasspath[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					entries.add(i, JavaCore.newContainerEntry(CPE_PREFIX_FOR_EXEC_ENV.append(getCorrespondingExecutionEnvironment(newver))));
					insertedJRE = true;
					break;
				}
			}
			if (!insertedJRE) {
				entries.add(0, JavaCore.newContainerEntry(CPE_PREFIX_FOR_EXEC_ENV.append(getCorrespondingExecutionEnvironment(newver))));
			}
			rawClasspath = entries.toArray(new IClasspathEntry[entries.size()]);
		}
		if (changed || !hadJRE) {
			javaProject.setRawClasspath(rawClasspath, new NullProgressMonitor());
		}
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
