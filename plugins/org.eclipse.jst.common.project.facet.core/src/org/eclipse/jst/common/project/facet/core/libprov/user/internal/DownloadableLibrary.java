/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core.libprov.user.internal;

import static org.eclipse.jdt.core.IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME;
import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.PLUGIN_ID;
import static org.eclipse.wst.common.project.facet.core.util.internal.ZipUtil.unzip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.UserLibraryManager;
import org.eclipse.osgi.util.NLS;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@SuppressWarnings( "restriction" )

public final class DownloadableLibrary
{
    private String name;
    private String downloadProvider;
    private String url;
    private String licenseUrl;
    
    private Map<IPath,DownloadableLibraryComponentAttributes> componentAttributesMap
        = new HashMap<IPath,DownloadableLibraryComponentAttributes>();
    
    private final List<String> includePatterns = new ArrayList<String>();
    private final List<String> includePatternsReadOnly = Collections.unmodifiableList( this.includePatterns );
    private final List<String> excludePatterns = new ArrayList<String>();
    private final List<String> excludePatternsReadOnly = Collections.unmodifiableList( this.excludePatterns );
    
    public String getName()
    {
        return this.name;
    }
    
    public void setName( final String name )
    {
        this.name = name;
    }
    
    public String getDownloadProvider()
    {
        return this.downloadProvider;
    }
    
    public void setDownloadProvider( final String downloadProvider )
    {
        this.downloadProvider = downloadProvider;
    }
    
    public String getUrl()
    {
        return this.url;
    }
    
    public void setUrl( final String url )
    {
        this.url = url;
    }
    
    public String getLicenseUrl()
    {
        return this.licenseUrl;
    }
    
    public void setLicenseUrl( final String licenseUrl )
    {
        this.licenseUrl = licenseUrl;
    }
    
    public DownloadableLibraryComponentAttributes getComponentAttributes( final IPath path )
    {
        return getComponentAttributes( path, false );
    }
    
    public DownloadableLibraryComponentAttributes getComponentAttributes( final IPath path,
                                                                          final boolean createIfNecessary )
    {
        DownloadableLibraryComponentAttributes attachment = this.componentAttributesMap.get( path );
        
        if( attachment == null && createIfNecessary )
        {
            attachment = new DownloadableLibraryComponentAttributes( path.toPortableString() );
            this.componentAttributesMap.put( path, attachment );
        }
        
        return attachment;
    }
    
    public Collection<String> getIncludePatterns()
    {
        return this.includePatternsReadOnly;
    }
    
    public void addIncludePattern( final String includePattern )
    {
        if( includePattern == null )
        {
            return;
        }
        
        for( String segment : includePattern.split( "," ) ) //$NON-NLS-1$
        {
            segment = segment.trim();
            
            if( segment.length() > 0 )
            {
                this.includePatterns.add( segment );
            }
        }
    }
        
    public Collection<String> getExcludePatterns()
    {
        return this.excludePatternsReadOnly;
    }
    
    public void addExcludePattern( final String excludePattern )
    {
        if( excludePattern == null )
        {
            return;
        }
        
        for( String segment : excludePattern.split( "," ) ) //$NON-NLS-1$
        {
            segment = segment.trim();
            
            if( segment.length() > 0 )
            {
                this.excludePatterns.add( segment );
            }
        }
    }
        
    public void download( final File destFolder,
                          final String localLibraryName,
                          final IProgressMonitor monitor )
    
        throws CoreException, InterruptedException
        
    {
        try 
        {
            // Create the directory where the downloaded archive will be written to
            // and where the exploded library will reside.
            
            destFolder.mkdirs();
            
            // Make sure that destination folder is empty and clear it out if necessary.
            
            for( File f : destFolder.listFiles() )
            {
                delete( f );
            }

            // Define the temporary download file.
            
            final File destFile = new File( destFolder, "download.zip" ); //$NON-NLS-1$
            
            // Perform the download.
                
            download( new URL( this.url ), destFile, monitor );
            
            // Unzip the downloaded file.

            unzip( destFile, destFolder, monitor );
            
            // Delete the original downloaded file.
            
            destFile.delete();
            
            // Configure the user library.
            
            final List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
            final IPath destFolderPath = new Path( destFolder.getPath() );
            
            for( File jarFile : findAllJarFiles( destFolder ) )
            {
                final IPath jarPath = new Path( jarFile.getPath() );
                final IPath relativeJarPath = jarPath.makeRelativeTo( destFolderPath );
                
                if( ! shouldInclude( relativeJarPath ) )
                {
                    continue;
                }
                
                IPath sourceArchivePath = null;
                String javadocArchivePath = null;

                final DownloadableLibraryComponentAttributes attachment = getComponentAttributes( relativeJarPath );
                
                if( attachment != null )
                {
                    final String sourceArchivePathString = attachment.getSourceArchivePath();
                    
                    if( sourceArchivePathString != null )
                    {
                        sourceArchivePath = destFolderPath.append( sourceArchivePathString );
                    }
                    
                    javadocArchivePath = attachment.getJavadocArchivePath();
                    
                    if( javadocArchivePath != null )
                    {
                        final int separator = javadocArchivePath.indexOf( '!' );
                        final IPath pathInArchive;
                        
                        if( separator == -1 )
                        {
                            pathInArchive = null;
                        }
                        else
                        {
                            pathInArchive = new Path( javadocArchivePath.substring( separator + 1 ) );
                            javadocArchivePath = javadocArchivePath.substring( 0, separator );
                        }
                        
                        final IPath p = destFolderPath.append( javadocArchivePath );
                        
                        if( pathInArchive != null || p.toFile().isFile() )
                        {
                            javadocArchivePath = "jar:file:/" + p.toPortableString() + "!/"; //$NON-NLS-1$ //$NON-NLS-2$
                            
                            if( javadocArchivePath != null )
                            {
                                javadocArchivePath = javadocArchivePath + pathInArchive.toPortableString() + "/"; //$NON-NLS-1$
                            }
                        }
                        else
                        {
                            javadocArchivePath = "file:/" + p.toPortableString() + "/"; //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }
                }
                
                final IClasspathEntry cpe = newLibraryEntry( jarPath, sourceArchivePath, javadocArchivePath );
                entries.add( cpe );
            }
            
            final IClasspathEntry[] entriesArray 
                = entries.toArray( new IClasspathEntry[ entries.size() ] );
            
            final UserLibraryManager userLibraryManager = JavaModelManager.getUserLibraryManager();
            userLibraryManager.setUserLibrary( localLibraryName, entriesArray, false );
        }
        catch( IOException e )
        {
            final IStatus st = new Status( IStatus.ERROR, PLUGIN_ID, Resources.failedWhileDownloading, e );
            throw new CoreException( st );
        }
    }
    
    private void download( final URL url,
                           final File destFile,
                           final IProgressMonitor monitor )
    
        throws IOException, InterruptedException
        
    {
        monitor.setTaskName( Resources.progressConnecting );
        
        final URLConnection urlConnection = url.openConnection();
        final int size = urlConnection.getContentLength();
        
        final String totalSizeString;
        
        if( size == -1 )
        {
            totalSizeString = null;
            monitor.beginTask( Resources.progressTransferStarted, IProgressMonitor.UNKNOWN );
        }
        else
        {
            totalSizeString = formatByteCount( size );
            monitor.beginTask( Resources.progressTransferStarted, size );
        }
        
        InputStream in = null;
        FileOutputStream out = null;
        
        try
        {
            in = urlConnection.getInputStream();
            out = new FileOutputStream( destFile );
            
            final byte[] buffer = new byte[ 16 * 1024 ];
            
            int count = 0;
            int bytesTransfered = 0;
            
            while( ( count = in.read( buffer ) ) != -1 )
            {
                if( monitor.isCanceled() )
                {
                    throw new InterruptedException();
                }
                
                out.write( buffer, 0, count );
                bytesTransfered += count;
                
                monitor.worked( count );
                monitor.setTaskName( formatDownloadProgressMessage( bytesTransfered, totalSizeString ) );
            }
        }
        finally
        {
            if( in != null )
            {
                try
                {
                    in.close();
                }
                catch( IOException e ) {}
            }
            
            if( out != null )
            {
                try
                {
                    out.close();
                }
                catch( IOException e ) {}
            }
            
            monitor.done();
        }
    }
    
    private boolean shouldInclude( final IPath path )
    {
        if( ! this.includePatterns.isEmpty() )
        {
            boolean included = false;
            
            for( String pattern : this.includePatterns )
            {
                if( path.equals( new Path( pattern ) ) )
                {
                    included = true;
                    break;
                }
            }
            
            if( ! included )
            {
                return false;
            }
        }
        
        for( String pattern : this.excludePatterns )
        {
            if( path.equals( new Path( pattern ) ) )
            {
                return false;
            }
        }
        
        return true;
    }
    
    private String formatByteCount( final int byteCount )
    {
        if( byteCount < 1024 * 1024 )
        {
            return String.format( "%3.0f KB", ( (float) byteCount ) / 1024 ); //$NON-NLS-1$
        }
        else
        {
            return String.format( "%.1f MB" , ( (float) byteCount ) / ( 1024 * 1024 ) ); //$NON-NLS-1$
        }
    }
    
    private String formatDownloadProgressMessage( final int bytesTransfered,
                                                  final String totalSizeString )
    {
        final String bytesTransferedString = formatByteCount( bytesTransfered );
        
        if( totalSizeString != null )
        {
            return NLS.bind( Resources.progressTransferred, bytesTransferedString, totalSizeString );
        }
        else
        {
            return NLS.bind( Resources.progressTransferredNoTotalSize, bytesTransferedString );
        }
    }
    
    private static void delete( final File f )

        throws IOException
    
    {
        if( f.isDirectory() )
        {
            for( File child : f.listFiles() )
            {
                delete( child );
            }
        }
    
        if( ! f.delete() )
        {
            final String msg = NLS.bind( Resources.errorCouldNotDelete, f.getPath() );
            throw new IOException( msg );
        }
    }
    
    private static List<File> findAllJarFiles( final File directory )
    {
        final List<File> result = new ArrayList<File>();
        findAllJarFiles( directory, result );
        return result;
    }
    
    private static void findAllJarFiles( final File directory,
                                         final List<File> result )
    {
        for( File f : directory.listFiles() )
        {
            if( f.isDirectory() )
            {
                findAllJarFiles( f, result );
            }
            else
            {
                final String fname = f.getName().toLowerCase();
                
                if( fname.endsWith( ".jar" ) ) //$NON-NLS-1$
                {
                    result.add( f );
                }
            }
        }
    }
    
    private static IClasspathEntry newLibraryEntry( final IPath library,
                                                    final IPath src,
                                                    final String javadoc )
    {
        final IAccessRule[] access = {};
        
        final IClasspathAttribute[] attrs;
        
        if( javadoc == null )
        {
            attrs = new IClasspathAttribute[ 0 ];
        }
        else
        {
            attrs = new IClasspathAttribute[]
            { 
               JavaCore.newClasspathAttribute( JAVADOC_LOCATION_ATTRIBUTE_NAME, javadoc )
            };
        }
        
        return JavaCore.newLibraryEntry( library, src, null, access, attrs, false );
    }
    
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String failedWhileDownloading;
        public static String progressConnecting;
        public static String progressTransferStarted;
        public static String progressTransferred;
        public static String progressTransferredNoTotalSize;
        public static String errorCouldNotDelete;
    
        static
        {
            initializeMessages( DownloadableLibrary.class.getName(), 
                                Resources.class );
        }
    }
    
}
