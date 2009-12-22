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

package org.eclipse.wst.common.project.facet.core.internal;

import static org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin.PLUGIN_ID;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findExtensions;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findRequiredAttribute;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.getTopLevelElements;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.instantiate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.project.facet.core.ProjectFacetDetector;
import org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.InvalidExtensionException;

/**
 * Contains the logic for processing the <code>detectors</code> extension point. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetDetectorsExtensionPoint
{
    public static final String EXTENSION_POINT_ID = "detectors"; //$NON-NLS-1$
    
    private static final String EL_DETECTOR = "detector"; //$NON-NLS-1$
    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

    private static List<DetectorExtension> extensions = null;
    
    public static List<ProjectFacetDetector> getDetectors()
    {
        readExtensions();
        
        final List<ProjectFacetDetector> detectors = new ArrayList<ProjectFacetDetector>();
        
        for( DetectorExtension extension : extensions )
        {
            detectors.add( extension.getDetector() );
        }
        
        return Collections.unmodifiableList( detectors );
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
                    readExtension( element );
                }
                catch( InvalidExtensionException e )
                {
                    // Continue. The problem has been reported to the user via the log.
                }
            }
        }
    }

    private static void readExtension( final IConfigurationElement config )
    
        throws InvalidExtensionException
        
    {
        final String pluginId = config.getContributor().getName();
        final String className = findRequiredAttribute( config, ATTR_CLASS );
        
        if( className == null )
        {
            throw new InvalidExtensionException();
        }
        
        final DetectorExtension extension = new DetectorExtension( pluginId, className );
        
        extensions.add( extension );
    }

    private static final class DetectorExtension
    {
        private final String pluginId;
        private final String className;
        private ProjectFacetDetector detector;
        
        public DetectorExtension( final String pluginId,
                                  final String className )
        
            throws InvalidExtensionException
            
        {
            this.pluginId = pluginId;
            this.className = className;
            this.detector = instantiate( this.pluginId, this.className, ProjectFacetDetector.class );
            
            if( this.detector == null )
            {
                throw new InvalidExtensionException();
            }
        }
        
        public ProjectFacetDetector getDetector()
        {
            return this.detector;
        }
    }
    
}
