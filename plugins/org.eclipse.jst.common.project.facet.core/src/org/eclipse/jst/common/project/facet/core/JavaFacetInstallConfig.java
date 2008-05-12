/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.util.EventListenerRegistry;
import org.eclipse.wst.common.project.facet.core.util.IEventListener;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public class JavaFacetInstallConfig
{
//    private static final String PROD_PROP_SOURCE_FOLDER = "defaultJavaSourceFolder"; //$NON-NLS-1$
//    private static final String PROD_PROP_SOURCE_FOLDER_LEGACY = "defaultSource"; //$NON-NLS-1$
//    private static final String DEFAULT_SOURCE_FOLDER = "src"; //$NON-NLS-1$

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
    
}
