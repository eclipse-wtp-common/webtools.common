/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;
import org.osgi.framework.Bundle;

/**
 * Contains utility functions for dealing with files.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FileUtil
{
    public static final String FILE_DOT_PROJECT = ".project"; //$NON-NLS-1$
    
    private FileUtil() {}
    
    public static void validateEdit( final IFile... files )
    
        throws CoreException
        
    {
        final IWorkspace ws = ResourcesPlugin.getWorkspace();
        final IStatus st = ws.validateEdit( files, IWorkspace.VALIDATE_PROMPT );
        
        if( st.getSeverity() == IStatus.ERROR )
        {
            throw new CoreException( st );
        }
    }
    
    /**
     * Copies a resource from a plugin to a destination in the workspace.
     *
     * @param pluginId the plugin id
     * @param pathInPlugin the path of the resource within the plugin
     * @param destination the destination path within the workspace
     */
    
    public static void copyFromPlugin( final String pluginId,
                                       final IPath pathInPlugin,
                                       final IFile destination )

        throws CoreException

    {
        copyFromPlugin( pluginId, pathInPlugin, destination.getLocation().toFile() );
    }
    
    /**
     * Copies a resource from a plugin to a destination in the workspace.
     *
     * @param bundle the plugin bundle
     * @param pathInPlugin the path of the resource within the plugin
     * @param destination the destination path within the workspace
     */

    public static void copyFromPlugin( final Bundle bundle,
                                       final IPath pathInPlugin,
                                       final IFile destination )

        throws CoreException

    {
        copyFromPlugin( bundle, pathInPlugin, destination.getLocation().toFile() );
    }
    
    /**
     * Copies a resource from a plugin to a destination on the file system.
     *
     * @param pluginId the plugin id
     * @param pathInPlugin the path of the resource within the plugin
     * @param destination the destination path within the workspace
     */
    
    public static void copyFromPlugin( final String pluginId,
                                       final IPath pathInPlugin,
                                       final File destination )
    
        throws CoreException
        
    {
        final Bundle bundle = Platform.getBundle( pluginId );
        copyFromPlugin( bundle, pathInPlugin, destination );
    }
    
    /**
     * Copies a resource from a plugin to a destination on the file system.
     *
     * @param bundle the plugin bundle
     * @param pathInPlugin the path of the resource within the plugin
     * @param destination the destination path within the workspace
     */

    public static void copyFromPlugin( final Bundle bundle,
                                       final IPath pathInPlugin,
                                       final File destination )
    
        throws CoreException
        
    {
        final InputStream contents = readFileFromPlugin( bundle, pathInPlugin );
        
        try
        {
            writeFile( destination, contents );
        }
        finally
        {
            try
            {
                contents.close();
            }
            catch( Exception e ) {}
        }
    }
    
    public static InputStream readFileFromPlugin( final Bundle bundle,
                                                  final IPath pathInPlugin )
    
        throws CoreException
        
    {
        try
        {
            return FileLocator.openStream( bundle, pathInPlugin, false );
        }
        catch( IOException e )
        {
            throw new CoreException( FacetCorePlugin.createErrorStatus( e.getMessage(), e ) );
        }
    }
    
    public static IFile getWorkspaceFile( final File f )
    {
        final IWorkspace ws = ResourcesPlugin.getWorkspace();
        final IWorkspaceRoot wsroot = ws.getRoot();
        final IPath path = new Path( f.getAbsolutePath() );
        
        final IFile[] wsFiles = wsroot.findFilesForLocation( path );
        
        if( wsFiles.length > 0 )
        {
            return wsFiles[ 0 ];
        }
        
        return null;
    }
    
    public static IContainer getWorkspaceContainer( final File f )
    {
        final IWorkspace ws = ResourcesPlugin.getWorkspace();
        final IWorkspaceRoot wsroot = ws.getRoot();
        final IPath path = new Path( f.getAbsolutePath() );
        
        final IContainer[] wsContainers = wsroot.findContainersForLocation( path );
        
        if( wsContainers.length > 0 )
        {
            return wsContainers[ 0 ];
        }
        
        return null;
    }
    
    public static void mkdirs( final File f )
    
        throws CoreException
        
    {
        if( f.exists() )
        {
            if( f.isFile() )
            {
                final String msg
                    = NLS.bind( Resources.locationIsFile, 
                                f.getAbsolutePath() );
                
                throw new CoreException( FacetCorePlugin.createErrorStatus( msg ) );
            }
        }
        else
        {
            mkdirs( f.getParentFile() );
            
            final IContainer wsContainer = getWorkspaceContainer( f );
            
            if( wsContainer != null )
            {
                // Should be a folder...
                
                final IFolder iFolder = (IFolder) wsContainer;
                iFolder.create( true, true, null );
            }
            else
            {
                final boolean isSuccessful = f.mkdir();
                
                if( ! isSuccessful )
                {
                    final String msg
                        = NLS.bind( Resources.failedToCreateDirectory, 
                                    f.getAbsolutePath() );
                    
                    throw new CoreException( FacetCorePlugin.createErrorStatus( msg ) );
                }
            }
        }
    }
    
    public static void writeFile( final File f,
                                  final InputStream contents )
    
        throws CoreException
        
    {
        if( f.exists() )
        {
            if( f.isDirectory() )
            {
                final String msg
                    = NLS.bind( Resources.locationIsDirectory, 
                                f.getAbsolutePath() );
                
                throw new CoreException( FacetCorePlugin.createErrorStatus( msg ) );
            }
        }
        else
        {
            mkdirs( f.getParentFile() );
        }
        
        final IFile wsfile = getWorkspaceFile( f );

        if( wsfile != null )
        {
            validateEdit( new IFile[] { wsfile } );
            
            if( wsfile.exists() )
            {
                wsfile.setContents( contents, true, false, null );
            }
            else
            {
                wsfile.create( contents, true, null );
            }
        }
        else
        {
            if( f.exists() && ! f.canWrite())
            {
                final String msg
                    = NLS.bind( Resources.cannotWriteFile, 
                                f.getAbsolutePath() );
                
                throw new CoreException( FacetCorePlugin.createErrorStatus( msg ) );
            }
            
            final byte[] buffer = new byte[ 1024 ];
            FileOutputStream out = null;
            
            try
            {
                out = new FileOutputStream( f );
                
                for( int count; ( count = contents.read( buffer ) ) != -1; )
                {
                    out.write( buffer, 0, count );
                }
                
                out.flush();
            }
            catch( IOException e )
            {
                final String msg 
                    = NLS.bind( Resources.failedWhileWriting, f.getAbsolutePath() );
                
                throw new CoreException( FacetCorePlugin.createErrorStatus( msg, e ) );
            }
            finally
            {
                if( out != null )
                {
                    try
                    {
                        out.close();
                    }
                    catch( IOException e ) {}
                }
            }
        }
    }

    private static final class Resources
    
        extends NLS
        
    {
        public static String failedWhileWriting;
        public static String cannotWriteFile;
        public static String failedToCreateDirectory;
        public static String locationIsDirectory;
        public static String locationIsFile;
        
        static
        {
            initializeMessages( FileUtil.class.getName(), 
                                Resources.class );
        }
    }
    
}
