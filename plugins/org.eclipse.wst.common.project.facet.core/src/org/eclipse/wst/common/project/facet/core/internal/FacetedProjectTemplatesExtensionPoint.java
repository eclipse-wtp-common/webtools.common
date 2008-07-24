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

package org.eclipse.wst.common.project.facet.core.internal;

import static org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin.PLUGIN_ID;
import static org.eclipse.wst.common.project.facet.core.internal.FacetedProjectFrameworkImpl.reportMissingFacet;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findExtensions;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findOptionalElement;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findRequiredAttribute;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.getElementValue;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.getTopLevelElements;

import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.util.internal.IndexedSet;
import org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.InvalidExtensionException;

/**
 * Contains the logic for processing the <code>template</code> element of the <code>facets</code> 
 * extension point.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class FacetedProjectTemplatesExtensionPoint
{
    public static final String EXTENSION_POINT_ID = "facets"; //$NON-NLS-1$
    
    private static final String ATTR_FACET = "facet"; //$NON-NLS-1$
    private static final String ATTR_ID = "id"; //$NON-NLS-1$
    private static final String EL_FIXED = "fixed"; //$NON-NLS-1$
    private static final String EL_LABEL = "label"; //$NON-NLS-1$
    private static final String EL_PRESET = "preset"; //$NON-NLS-1$
    private static final String EL_TEMPLATE = "template"; //$NON-NLS-1$
    
    private static IndexedSet<String,IFacetedProjectTemplate> templates = null;
    
    public static Set<IFacetedProjectTemplate> getTemplates()
    {
        readExtensions();
        return templates.getUnmodifiable();
    }
    
    public static IFacetedProjectTemplate getTemplate( final String id )
    {
        readExtensions();
        return templates.get( id );
    }
    
    private static synchronized void readExtensions()
    {
        if( templates != null )
        {
            return;
        }
        
        templates = new IndexedSet<String,IFacetedProjectTemplate>();        
        
        for( IConfigurationElement element 
             : getTopLevelElements( findExtensions( PLUGIN_ID, EXTENSION_POINT_ID ) ) )
        {
            if( element.getName().equals( EL_TEMPLATE ) )
            {
                try
                {
                    readTemplate( element );
                }
                catch( InvalidExtensionException e )
                {
                    // Continue. The problem has been reported to the user via the log.
                }
            }
        }
    }

    private static void readTemplate( final IConfigurationElement config )
    
        throws InvalidExtensionException
        
    {
        final String pluginId = config.getContributor().getName();
        
        final FacetedProjectTemplate template = new FacetedProjectTemplate();
        template.setId( findRequiredAttribute( config, ATTR_ID ) );
        
        final IConfigurationElement elLabel = findOptionalElement( config, EL_LABEL );
        template.setLabel( getElementValue( elLabel, template.getId() ) );
    
        final IConfigurationElement[] children = config.getChildren();
        
        for( int i = 0; i < children.length; i++ )
        {
            final IConfigurationElement child = children[ i ];
            final String childName = child.getName();
            
            if( childName.equals( EL_FIXED ) )
            {
                final String fid = findRequiredAttribute( child, ATTR_FACET );
                
                if( ! ProjectFacetsManager.isProjectFacetDefined( fid ) )
                {
                    reportMissingFacet( fid, pluginId );
                    return;
                }
                
                template.addFixedProjectFacet( ProjectFacetsManager.getProjectFacet( fid ) );
            }
            else if( childName.equals( EL_PRESET ) )
            {
                final String pid = findRequiredAttribute( child, ATTR_ID );
                
                if( ! ProjectFacetsManager.isPresetDefined( pid ) )
                {
                    final String msg
                        = Resources.bind( Resources.presetNotDefined, pid, pluginId );
                    
                    FacetCorePlugin.log( msg );
                    
                    return;
                }
                
                template.setInitialPreset( ProjectFacetsManager.getPreset( pid ) );
            }
        }
        
        templates.add( template.getId(), template );
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String presetNotDefined;
        
        static
        {
            initializeMessages( FacetedProjectTemplatesExtensionPoint.class.getName(), 
                                Resources.class );
        }
    }
    
}
