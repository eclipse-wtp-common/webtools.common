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

package org.eclipse.jst.common.project.facet.core.libprov.internal;

import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.PLUGIN_ID;
import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.log;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findExtensions;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findRequiredAttribute;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.getTopLevelElements;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.instantiate;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.loadClass;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jst.common.project.facet.core.libprov.ILibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.LegacyLibraryProviderDetector;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.InvalidExtensionException;

/**
 * Contains the logic for processing the <code>legacyLibraryProviderDetectors</code> extension point. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class LegacyLibraryProviderDetectorsExtensionPoint
{
    public static final String EXTENSION_POINT_ID = "legacyLibraryProviderDetectors"; //$NON-NLS-1$
    
    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
    private static final String EL_DETECTOR = "detector"; //$NON-NLS-1$

    private static List<DetectorExtension> extensions = null;
    
    public static synchronized ILibraryProvider detect( final IProject project,
                                                        final IProjectFacet facet )
    {
        readExtensions();
        
        for( Iterator<DetectorExtension> itr = extensions.iterator(); itr.hasNext(); )
        {
            final DetectorExtension extension = itr.next();
            final LegacyLibraryProviderDetector detector;
            
            try
            {
                detector = extension.createDetector();
            }
            catch( InvalidExtensionException e )
            {
                // Problem already reported to the user in the log. Removing the extension from
                // the list so that we don't keep tripping over it in the future.
                
                itr.remove();
                continue;
            }
            
            try
            {
                final ILibraryProvider provider = detector.detect( project, facet );
                
                if( provider != null )
                {
                    return provider;
                }
            }
            catch( Exception e )
            {
                log( e );
            }
        }
        
        return null;
    }
    
    private static synchronized void readExtensions()
    {
        if( extensions != null )
        {
            return;
        }
        
        extensions = new ArrayList<DetectorExtension>();        
        
        for( IConfigurationElement element 
             : getTopLevelElements( findExtensions( PLUGIN_ID, EXTENSION_POINT_ID ) ) )
        {
            if( element.getName().equals( EL_DETECTOR ) )
            {
                try
                {
                    final String pluginId = element.getContributor().getName();
                    final String className = findRequiredAttribute( element, ATTR_CLASS );
                    extensions.add( new DetectorExtension( pluginId, className ) );
                }
                catch( InvalidExtensionException e )
                {
                    // Continue. The problem has been reported to the user via the log.
                }
            }
        }
    }

    private static final class DetectorExtension
    {
        private final String pluginId;
        private final String className;
        private WeakReference<Class<? extends LegacyLibraryProviderDetector>> classWeakReference;
        
        public DetectorExtension( final String pluginId,
                                  final String className )
        {
            this.pluginId = pluginId;
            this.className = className;
        }
        
        public synchronized LegacyLibraryProviderDetector createDetector()
        
            throws InvalidExtensionException
            
        {
            Class<? extends LegacyLibraryProviderDetector> detectorClass 
                = this.classWeakReference == null ? null : this.classWeakReference.get();
            
            if( detectorClass == null )
            {
                detectorClass = loadClass( this.pluginId, this.className, LegacyLibraryProviderDetector.class );
                
                if( detectorClass == null )
                {
                    throw new InvalidExtensionException();
                }
                
                this.classWeakReference = new WeakReference<Class<? extends LegacyLibraryProviderDetector>>( detectorClass );
            }
            
            final LegacyLibraryProviderDetector detector = instantiate( this.pluginId, detectorClass );
            
            if( detector == null )
            {
                throw new InvalidExtensionException();
            }
            
            return detector;
        }
    }
    
}
