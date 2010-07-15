/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    shuff@us.ibm.com - several revisions of source paths validation logic
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core;

import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.PLUGIN_ID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.common.project.facet.core.internal.FacetCorePlugin;
import org.eclipse.jst.common.project.facet.core.internal.JavaFacetUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.ActionConfig;
import org.eclipse.wst.common.project.facet.core.util.EventListenerRegistry;
import org.eclipse.wst.common.project.facet.core.util.IEventListener;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class JavaFacetInstallConfig

    extends ActionConfig
    
{
    private static final String PROD_PROP_OUTPUT_FOLDER = "defaultJavaOutputFolder"; //$NON-NLS-1$
    private static final String PROD_PROP_OUTPUT_FOLDER_LEGACY = "outputFolder"; //$NON-NLS-1$
    private static final String DEFAULT_OUTPUT_FOLDER = "build/classes"; //$NON-NLS-1$
    
    public static class ChangeEvent
    {
        public enum Type
        {
            SOURCE_FOLDERS_CHANGED,
            DEFAULT_OUTPUT_FOLDER_CHANGED
        }
        
        private final Type type;
        private final JavaFacetInstallConfig installConfig;
        
        ChangeEvent( final Type type,
                     final JavaFacetInstallConfig installConfig )
        {
            this.type = type;
            this.installConfig = installConfig;
        }
        
        public final Type getType()
        {
            return this.type;
        }

        public final JavaFacetInstallConfig getJavaFacetInstallConfig()
        {
            return this.installConfig;
        }
    }

    /**
     * Specifies if the local file system is case sensitive. Used by validation. This is a non-final
     * instance field to allow unit tests to override the default behavior. Unit tests will access
     * this field via reflection. Do not rename or otherwise modify without changing unit tests too.
     */
    
    private boolean caseSensitiveFs = EFS.getLocalFileSystem().isCaseSensitive();

    private EventListenerRegistry<ChangeEvent.Type,ChangeEvent> listeners;
    private List<IPath> sourceFolders;
    private List<IPath> sourceFoldersReadOnly;
    private IPath defaultOutputFolder;
        
    public JavaFacetInstallConfig()
    {
        this.listeners = new EventListenerRegistry<ChangeEvent.Type,ChangeEvent>( ChangeEvent.Type.class );
        
        this.sourceFolders = new CopyOnWriteArrayList<IPath>();
        this.sourceFoldersReadOnly = Collections.unmodifiableList( this.sourceFolders );
        this.defaultOutputFolder = null;
        
        String sourceFolder = FacetCorePlugin.getJavaSrcFolder();
        
        this.sourceFolders.add( new Path( sourceFolder ) );
        
        String outputFolder = getProductProperty( PROD_PROP_OUTPUT_FOLDER );
        
        if( outputFolder == null )
        {
            outputFolder = getProductProperty( PROD_PROP_OUTPUT_FOLDER_LEGACY );
        }
        
        if( outputFolder == null )
        {
            outputFolder = DEFAULT_OUTPUT_FOLDER;
        }
        
        this.defaultOutputFolder = new Path( outputFolder );
    }
    
    @Override
    public Set<IFile> getValidateEditFiles()
    {
        final Set<IFile> files = super.getValidateEditFiles();
        final IProject project = getFacetedProjectWorkingCopy().getProject();
        
        if( project != null )
        {
            files.add( project.getFile( IProjectDescription.DESCRIPTION_FILE_NAME ) );
            files.add( project.getFile( JavaFacetUtil.FILE_CLASSPATH ) );
            files.add( project.getFile( JavaFacetUtil.FILE_JDT_CORE_PREFS ) );
            files.add( project.getFile( ClasspathHelper.LEGACY_METADATA_FILE_NAME ) );
        }
        
        return files;
    }
    
    @Override
    public IStatus validate()
    {
        IStatus status = Status.OK_STATUS;
        
        final List<IPath> localFolderList = new ArrayList<IPath>( this.sourceFolders );
        
        while( ! localFolderList.isEmpty() )
        {
            final IPath folder = localFolderList.remove( 0 );
            status = validateSourceFolder( localFolderList, folder );
            
            if( ! status.isOK() )
            {
                break;
            }
        }
        
        return status;
    }

    /**
     * Validates the provided source folder against the existing configuration. Can be used to check
     * if adding a given source folder to this configuration will introduce validation problems.
     *  
     * @param candidateSourceFolder the source folder to validate
     * @return status detailing the validity of the candidate source folder
     * @throw {@link IllegalArgumentException} if candidateSourceFolder is null
     */
    
    public IStatus validateSourceFolder( final String candidateSourceFolder )
    {
        if( candidateSourceFolder == null )
        {
            throw new IllegalArgumentException();
        }
        
        return validateSourceFolder( this.sourceFolders, new Path( candidateSourceFolder.trim() ) );
    }
    
    private IStatus validateSourceFolder( final List<IPath> existingSourceFolders,
                                          final IPath sourceFolder )
    {
        IStatus status = Status.OK_STATUS;
        
        if( sourceFolder.segmentCount() == 0 )
        {
            status = new Status( IStatus.ERROR, PLUGIN_ID, Resources.mustSpecifySourceFolderMessage );
        }
        else
        {
            final String pjname = getFacetedProjectWorkingCopy().getProjectName();
            final String fullPath = "/" + pjname + "/" + sourceFolder; //$NON-NLS-1$ //$NON-NLS-2$
            
            status = ResourcesPlugin.getWorkspace().validatePath( fullPath, IResource.FOLDER );
            
            if( status.isOK() )
            {
                for( IPath existingSourceFolder : existingSourceFolders )
                {
                    status = validateSourceFolder( existingSourceFolder, sourceFolder );
                    
                    if( ! status.isOK() )
                    {
                        break;
                    }
                }
            }
        }
        
        return status;
    }

    private IStatus validateSourceFolder( final IPath existingPath,
                                          final IPath newPath )
    {
        final int existingPathLen = existingPath.segmentCount();
        final int newPathLen = newPath.segmentCount();
        final int minPathLen = Math.min( existingPathLen, newPathLen );
        
        if( minPathLen == 0 )
        {
            // The check that handles this case better is done elsewhere.
            
            return Status.OK_STATUS;
        }
        
        for( int i = 0; i < minPathLen; i++ )
        {
            if( comparePathSegments( existingPath.segment( i ), newPath.segment( i ) ) == false )
            {
                return Status.OK_STATUS;
            }
        }
        
        final String message;
        
        if( existingPathLen == newPathLen )
        {
            message = NLS.bind( Resources.nonUniqueSourceFolderMessage, newPath );
        }
        else if( existingPathLen > newPathLen )
        {
            message = NLS.bind( Resources.cannotNestSourceFoldersMessage, newPath, existingPath );
        }
        else
        {
            message = NLS.bind( Resources.cannotNestSourceFoldersMessage, existingPath, newPath );
        }
        
        return new Status( IStatus.ERROR, PLUGIN_ID, message );
    }

    private boolean comparePathSegments( final String a,
                                         final String b )
    {
        return ( this.caseSensitiveFs && a.equals( b ) ) || ( ! this.caseSensitiveFs && a.equalsIgnoreCase( b ) );
    }

    public List<IPath> getSourceFolders()
    {
        return this.sourceFoldersReadOnly;
    }
    
    public void setSourceFolders( final List<IPath> paths )
    {
        if( ! this.sourceFolders.equals( paths ) )
        {
            this.sourceFolders.clear();
            this.sourceFolders.addAll( paths );

            final ChangeEvent event = new ChangeEvent( ChangeEvent.Type.SOURCE_FOLDERS_CHANGED, this );
            this.listeners.notifyListeners( ChangeEvent.Type.SOURCE_FOLDERS_CHANGED, event );
        }
    }
    
    public void setSourceFolder( final IPath path )
    {
        final List<IPath> newSourceFolders;
        
        if( path == null )
        {
            newSourceFolders = Collections.emptyList();
        }
        else
        {
            newSourceFolders = Collections.singletonList( path );
        }
        
        setSourceFolders( newSourceFolders );
    }
    
    public void addSourceFolder( final IPath path )
    {
        if( path == null )
        {
            throw new IllegalArgumentException();
        }
        
        final List<IPath> newSourceFolders = new ArrayList<IPath>( getSourceFolders() );
        newSourceFolders.add( path );
        setSourceFolders( newSourceFolders );
    }
    
    public void removeSourceFolder( final IPath path )
    {
        final List<IPath> newSourceFolders = new ArrayList<IPath>( getSourceFolders() );
        newSourceFolders.remove( path );
        setSourceFolders( newSourceFolders );
    }
    
    public IPath getDefaultOutputFolder()
    {
        return this.defaultOutputFolder;
    }
    
    public void setDefaultOutputFolder( final IPath defaultOutputFolder )
    {
        if( ! equal( this.defaultOutputFolder, defaultOutputFolder ) )
        {
            this.defaultOutputFolder = defaultOutputFolder;
            
            final ChangeEvent event = new ChangeEvent( ChangeEvent.Type.DEFAULT_OUTPUT_FOLDER_CHANGED, this );
            this.listeners.notifyListeners( ChangeEvent.Type.DEFAULT_OUTPUT_FOLDER_CHANGED, event );
        }
    }
    
    public void addListener( final IEventListener<ChangeEvent> listener,
                             final ChangeEvent.Type... types )
    {
        this.listeners.addListener( listener, types );
    }
    
    public void removeListener( final IEventListener<ChangeEvent> listener )
    {
        this.listeners.removeListener( listener );
    }

    private static boolean equal( final Object obj1,
                                  final Object obj2 )
    {
        if( obj1 == null || obj2 == null )
        {
            return false;
        }
        else
        {
            return obj1.equals( obj2 );
        }
    }
    
    private static String getProductProperty( final String propName )
    {
        String value = null;
        
        if( Platform.getProduct() != null )
        {
            value = Platform.getProduct().getProperty( propName );
        }
        
        return value;
    }

    private static final class Resources 

        extends NLS 

    {
        public static String nonUniqueSourceFolderMessage;
        public static String cannotNestSourceFoldersMessage;
        public static String mustSpecifySourceFolderMessage;

        static 
        {
            initializeMessages( JavaFacetInstallConfig.class.getName(), Resources.class );
        }
    }
    
}
