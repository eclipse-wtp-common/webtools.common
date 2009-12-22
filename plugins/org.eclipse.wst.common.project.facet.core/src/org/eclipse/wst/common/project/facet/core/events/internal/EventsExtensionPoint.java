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

package org.eclipse.wst.common.project.facet.core.events.internal;

import static org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin.PLUGIN_ID;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findExtensions;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findRequiredAttribute;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.getTopLevelElements;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectFrameworkImpl;
import org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.InvalidExtensionException;

/**
 * Contains the logic for processing the <code>listeners</code> extension point.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class EventsExtensionPoint
{
    public static final String EXTENSION_POINT_ID = "listeners"; //$NON-NLS-1$
    
    private static final String EL_LISTENER = "listener"; //$NON-NLS-1$
    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
    private static final String ATTR_EVENT_TYPES = "eventTypes"; //$NON-NLS-1$
    
    public static void processExtensions( final FacetedProjectFrameworkImpl framework )
    {
        for( IConfigurationElement element 
             : getTopLevelElements( findExtensions( PLUGIN_ID, EXTENSION_POINT_ID ) ) )
        {
            final String pluginId = element.getContributor().getName();
            
            if( element.getName().equals( EL_LISTENER ) )
            {
                try
                {
                    final String className = findRequiredAttribute( element, ATTR_CLASS );
                    final String eventTypes = element.getAttribute( ATTR_EVENT_TYPES );
                    
                    final List<IFacetedProjectEvent.Type> types 
                        = new ArrayList<IFacetedProjectEvent.Type>();
                    
                    if( eventTypes != null )
                    {
                        for( String segment : eventTypes.split( "," ) ) //$NON-NLS-1$
                        {
                            segment = segment.trim();
                            
                            try
                            {
                                final String uppercased = segment.toUpperCase();
                                types.add( IFacetedProjectEvent.Type.valueOf( uppercased ) );
                            }
                            catch( IllegalArgumentException e )
                            {
                                final String msg 
                                    = Resources.bind( Resources.invalidEventType, segment, 
                                                      pluginId );
                                
                                FacetCorePlugin.log( msg );
                            }
                        }
                    }
                    
                    final IFacetedProjectEvent.Type[] typesArray 
                        = new IFacetedProjectEvent.Type[ types.size() ];
                    
                    types.toArray( typesArray );
                    
                    final IFacetedProjectListener listener 
                        = new DelayedClassLoadingListener( pluginId, className );
                    
                    framework.addListener( listener, typesArray );
                }
                catch( InvalidExtensionException e )
                {
                    // Continue. The problem has been reported to the user via the log.
                }
            }
        }
    }

    public static final class Resources
    
        extends NLS
        
    {
        public static String invalidEventType;
        
        static
        {
            initializeMessages( EventsExtensionPoint.class.getName(), Resources.class );
        }
    }
    
}
