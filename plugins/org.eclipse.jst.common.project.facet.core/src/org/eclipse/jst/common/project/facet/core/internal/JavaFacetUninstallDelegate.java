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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class JavaFacetUninstallDelegate 

    extends JavaFacetDelegate 
    
{
    public void execute( final IProject project, 
                         final IProjectFacetVersion fv, 
                         final Object cfg, 
                         final IProgressMonitor monitor ) 
    
        throws CoreException 
        
    {
        final RelevantFiles files = new RelevantFiles( project );

        validateEdit( files );
        
        // Find output directories. They will be removed later.
        
        final List<IPath> outputFolders = new ArrayList<IPath>();
        
        try
        {
            final IJavaProject jproj = JavaCore.create( project );
            
            outputFolders.add( jproj.getOutputLocation() );
            
            for( IClasspathEntry cpe : jproj.getRawClasspath() )
            {
                outputFolders.add( cpe.getOutputLocation() );
            }
        }
        catch( Exception e )
        {
            // Ignore the exception since we tearing down and the user might be doing this
            // because the project is corrupted.
        }
        
        // Remove java nature. This will automatically remove the builder.

        final IProjectDescription desc = project.getDescription();
        final List<String> natures = new ArrayList<String>();
        
        for( String nature : desc.getNatureIds() )
        {
            if( ! nature.equals( JavaCore.NATURE_ID ) )
            {
                natures.add( nature );
            }
        }
        
        desc.setNatureIds( natures.toArray( new String[ natures.size() ] ) );
        project.setDescription( desc, null );

        // Delete various metadata files.
        
        files.dotClasspathFile.delete( true, null );
        files.jdtCorePrefsFile.delete( true, null );
        files.jstFacetCorePrefsFile.delete( true, null );
        
        // Delete all the output folders and their contents.
        
        final IWorkspace ws = ResourcesPlugin.getWorkspace();
        
        for( IPath path : outputFolders )
        {
            if( path != null )
            {
                delete( ws.getRoot().getFolder( path ) );
            }
        }
    }
    
    private static final void delete( final IFolder folder )
    
        throws CoreException
        
    {
        final IContainer parent = folder.getParent();
        
        if( parent instanceof IFolder &&
            parent.members( IContainer.INCLUDE_HIDDEN | IContainer.INCLUDE_PHANTOMS | 
                            IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS ).length == 1 )
        {
            delete( (IFolder) parent );
        }
        else
        {
            if( folder.exists() )
            {
                folder.delete( true, null );
            }
        }
    }

}
