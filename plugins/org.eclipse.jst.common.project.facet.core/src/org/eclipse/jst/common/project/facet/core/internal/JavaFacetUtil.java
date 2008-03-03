/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core.internal;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.common.project.facet.core.ClasspathHelper;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class JavaFacetUtil
{
    public static String getCompilerLevel()
    {
        final IScopeContext context = new InstanceScope();
        final IEclipsePreferences prefs = context.getNode( JavaCore.PLUGIN_ID );
        String level = prefs.get( JavaCore.COMPILER_COMPLIANCE, null );
        
        if( level == null )
        {
            final Hashtable defaults = JavaCore.getDefaultOptions();
            level = (String) defaults.get( JavaCore.COMPILER_COMPLIANCE );
        }
        
        return level;
    }

    public static String getCompilerLevel( final IProject project )
    {
        final IScopeContext context = new ProjectScope( project );
        final IEclipsePreferences prefs = context.getNode( JavaCore.PLUGIN_ID );
        String level = prefs.get( JavaCore.COMPILER_COMPLIANCE, null );
        
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
        setCompilerLevel( project, facetToCompilerLevel( fv ) );
    }

    public static void setCompilerLevel( final IProject project,
                                         final String level )
    
        throws CoreException
        
    {
        final IScopeContext context = new ProjectScope( project );
        
        final IEclipsePreferences prefs 
            = context.getNode( JavaCore.PLUGIN_ID );
        
        if( level.equals( JavaCore.VERSION_1_3 ) )
        {
            prefs.put( JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_3 );
            prefs.put( JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_1 );
            prefs.put( JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_3 );
            prefs.put( JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.IGNORE );
            prefs.put( JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.IGNORE );
        }
        else if( level.equals( JavaCore.VERSION_1_4 ) )
        {
            prefs.put( JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_4 );
            prefs.put( JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_2 );
            prefs.put( JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_3 );
            prefs.put( JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.WARNING );
            prefs.put( JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.WARNING );
        }
        else if( level.equals( JavaCore.VERSION_1_5 ) )
        {
            prefs.put( JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5 );
            prefs.put( JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5 );
            prefs.put( JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5 );
            prefs.put( JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.ERROR );
            prefs.put( JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.ERROR );
        }
        else if( level.equals( JavaCore.VERSION_1_6 ) )
        {
            prefs.put( JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_6 );
            prefs.put( JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_6 );
            prefs.put( JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6 );
            prefs.put( JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.ERROR );
            prefs.put( JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.ERROR );
        }
        else
        {
            throw new IllegalStateException();
        }

        try
        {
            prefs.flush();
        }
        catch( BackingStoreException e )
        {
            // TODO: Handle this.
        }
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
                IPath path = new Path( JavaRuntime.JRE_CONTAINER );
                path = path.append( vm.getVMInstallType().getId() );
                path = path.append( vm.getName() );
                
                final IClasspathEntry cpe 
                    = JavaCore.newContainerEntry( path );
                
                final List entries = Collections.singletonList( cpe );
                
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
    
    public static IProjectFacetVersion compilerLevelToFacet( final String ver )
    {
        if( ver.equals( "1.5" ) ) //$NON-NLS-1$
        {
            return JavaFacet.JAVA_50;
        }
        else if( ver.equals(  "1.6" ) ) //$NON-NLS-1$
        {
            return JavaFacet.JAVA_60;
        }
        else
        {
            return JavaFacet.FACET.getVersion( ver );
        }
    }

    public static String facetToCompilerLevel( final IProjectFacetVersion fv )
    {
        if( fv == JavaFacet.JAVA_50 )
        {
            return JavaCore.VERSION_1_5;
        }
        else if( fv == JavaFacet.JAVA_60 )
        {
            return JavaCore.VERSION_1_6;
        }
        else
        {
            return fv.getVersionString();
        }
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
