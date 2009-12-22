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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.common.project.facet.core.JavaFacetInstallConfig;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class JavaFacetInstallDelegate 

    extends JavaFacetDelegate 
    
{
    public void execute( final IProject project, 
                         final IProjectFacetVersion fv, 
                         final Object cfg, 
                         final IProgressMonitor monitor ) 
    
        throws CoreException 
        
    {
        validateEdit( project );
        
        final JavaFacetInstallConfig config = castToConfig( cfg );

        // Create the source and the output directories.

        IJavaProject jproject = null;
        
        if( project.exists() )
        {
            jproject = JavaCore.create( project );
        }
        
        if( ! jproject.exists() )
        {
            final List<IClasspathEntry> cp = new ArrayList<IClasspathEntry>();
            
            for( IPath srcFolderPath : config.getSourceFolders() )
            {
                final IFolder folder = project.getFolder( srcFolderPath );
                mkdirs( folder, false );
                
                cp.add( JavaCore.newSourceEntry( folder.getFullPath() ) );
            }
            
            final IPath defOutputPath = config.getDefaultOutputFolder();
            IFolder defOutputFolder = null;
            
            if( defOutputPath != null )
            {
                defOutputFolder = project.getFolder( config.getDefaultOutputFolder() );
                mkdirs( defOutputFolder, true );
            }

            // Add the java nature. This will automatically add the builder.

            final IProjectDescription desc = project.getDescription();
            final String[] current = desc.getNatureIds();
            final String[] replacement = new String[ current.length + 1 ];
            System.arraycopy( current, 0, replacement, 0, current.length );
            replacement[ current.length ] = JavaCore.NATURE_ID;
            desc.setNatureIds( replacement );
            project.setDescription( desc, null );

            // Setup the classpath.
            
            if( defOutputFolder == null )
            {
                jproject.setRawClasspath( cp.toArray( new IClasspathEntry[ cp.size() ] ), null ); 
            }
            else
            {
                jproject.setRawClasspath( cp.toArray( new IClasspathEntry[ cp.size() ] ), 
                                          defOutputFolder.getFullPath(), null );
            }

            JavaFacetUtil.resetClasspath( project, null, fv );
            JavaFacetUtil.setCompilerLevel( project, fv );
        }
        else
        {
            // Set the compiler compliance level for the project. Ignore whether
            // this might already be set so at the workspace level in case
            // workspace settings change later or the project is included in a
            // different workspace.
            
            String oldCompilerLevel = JavaFacetUtil.getCompilerLevel( project );
            JavaFacetUtil.setCompilerLevel( project, fv );
            
            String newCompilerLevel = JavaFacetUtil.getCompilerLevel( project );
            
            // Schedule a full build of the project if the compiler level changed
            // because we want classes in the project to be recompiled.
            
            if( newCompilerLevel != null && ! newCompilerLevel.equals( oldCompilerLevel ) )
            {
                JavaFacetUtil.scheduleFullBuild( project );
            }
        }
    }
    
    private static JavaFacetInstallConfig castToConfig( final Object cfg )
    {
        if( cfg instanceof JavaFacetInstallConfig )
        {
            return (JavaFacetInstallConfig) cfg;
        }
        else
        {
            final IAdapterManager manager = Platform.getAdapterManager();
            return (JavaFacetInstallConfig) manager.getAdapter( cfg, JavaFacetInstallConfig.class );
        }
    }
    
    private static void mkdirs( final IFolder folder,
                                final boolean isDerived )
    
        throws CoreException
        
    {
        if( ! folder.exists() )
        {
            final IContainer parent = folder.getParent();
            
            if( parent instanceof IFolder )
            {
                mkdirs( (IFolder) parent, isDerived );
            }
            
            folder.create( true, true, null );
            folder.setDerived( isDerived );
        }
    }

}
