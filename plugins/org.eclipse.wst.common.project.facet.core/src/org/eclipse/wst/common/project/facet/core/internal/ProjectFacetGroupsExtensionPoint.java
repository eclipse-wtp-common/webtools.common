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

package org.eclipse.wst.common.project.facet.core.internal;

import static org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin.PLUGIN_ID;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findExtensions;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findOptionalElement;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findRequiredAttribute;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findRequiredElement;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.getElementValue;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.getTopLevelElements;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.InvalidExtensionException;

/**
 * Contains the logic for processing the <code>groups</code> extension point, along with the
 * <code>group</code> and <code>group-member</code> elements of the <code>facets</code> extension 
 * point. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetGroupsExtensionPoint
{
    private static final String GROUPS_EXTENSION_POINT_ID = "groups"; //$NON-NLS-1$
    private static final String FACETS_EXTENSION_POINT_ID = "facets"; //$NON-NLS-1$
    
    private static final String EL_GROUP = "group"; //$NON-NLS-1$
    private static final String EL_LABEL = "label"; //$NON-NLS-1$
    private static final String EL_DESCRIPTION = "description"; //$NON-NLS-1$
    private static final String EL_INCLUDE = "include"; //$NON-NLS-1$
    private static final String EL_MEMBERS = "members"; //$NON-NLS-1$
    private static final String EL_PROJECT_FACET_VERSION = "project-facet-version"; //$NON-NLS-1$
    private static final String EL_GROUP_MEMBER = "group-member"; //$NON-NLS-1$

    private static final String ATTR_ID = "id"; //$NON-NLS-1$
    private static final String ATTR_GROUP = "group"; //$NON-NLS-1$
    private static final String ATTR_FACET = "facet"; //$NON-NLS-1$
    private static final String ATTR_VERSION = "version"; //$NON-NLS-1$
    private static final String ATTR_VERSIONS = "versions"; //$NON-NLS-1$
    
    public static void processExtensions( final FacetedProjectFrameworkImpl framework )
    {
        // Process group definitions and stand-alone group enlistments.
        
        for( IConfigurationElement element 
             : getTopLevelElements( findExtensions( PLUGIN_ID, GROUPS_EXTENSION_POINT_ID ) ) )
        {
            try
            {
                final String elname = element.getName();
                
                if( elname.equals( EL_GROUP ) )
                {
                    final String id = findRequiredAttribute( element, ATTR_ID );
                    final String label = getElementValue( findRequiredElement( element, EL_LABEL ), null );
                    final String description = getElementValue( findOptionalElement( element, EL_DESCRIPTION ), "" ); //$NON-NLS-1$
                    
                    final Group group = findOrCreateGroup( framework, id );
                    
                    group.setLabel( label );
                    group.setDescription( description );
                    
                    processIncludeDirectives( framework, group, element );
                }
                else if( elname.equals( EL_MEMBERS ) )
                {
                    final String gid = findRequiredAttribute( element, ATTR_GROUP );
                    final Group group = findOrCreateGroup( framework, gid );
                    
                    processIncludeDirectives( framework, group, element );
                }
            }
            catch( InvalidExtensionException e )
            {
                // Continue. The problem has been reported to the user via the log.
            }
        }
        
        // Process group enlistments as part of facet declaration.
        
        for( IConfigurationElement element 
             : getTopLevelElements( findExtensions( PLUGIN_ID, FACETS_EXTENSION_POINT_ID ) ) )
        {
            if( element.getName().equals( EL_PROJECT_FACET_VERSION ) )
            {
                final IConfigurationElement[] groupMemberElements = element.getChildren( EL_GROUP_MEMBER );
                
                if( groupMemberElements.length > 0 )
                {
                    final String fid = element.getAttribute( ATTR_FACET );
                    
                    if( fid != null && framework.isProjectFacetDefined( fid ) )
                    {
                        final IProjectFacet f = framework.getProjectFacet( fid );
                        final String version = element.getAttribute( ATTR_VERSION );
                        
                        if( version != null && f.hasVersion( version ) )
                        {
                            final IProjectFacetVersion fv = f.getVersion( version );
                            
                            for( IConfigurationElement groupMemberElement : groupMemberElements )
                            {
                                try
                                {
                                    final String gid = findRequiredAttribute( groupMemberElement, ATTR_ID );
                                    findOrCreateGroup( framework, gid ).addMember( fv );
                                }
                                catch( InvalidExtensionException e )
                                {
                                    // Continue. The problem has been reported to the user via the log.
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Process the deprecated declaration of groups within facets extension point.

        for( IConfigurationElement element 
             : getTopLevelElements( findExtensions( PLUGIN_ID, FACETS_EXTENSION_POINT_ID ) ) )
        {
            if( element.getName().equals( EL_GROUP ) )
            {
                try
                {
                    final String id = findRequiredAttribute( element, ATTR_ID );
                    final String label = getElementValue( findRequiredElement( element, EL_LABEL ), null );
                    final String description = getElementValue( findOptionalElement( element, EL_DESCRIPTION ), "" ); //$NON-NLS-1$
                    
                    final Group group = findOrCreateGroup( framework, id );
                    
                    group.setLabel( label );
                    group.setDescription( description );
                }
                catch( InvalidExtensionException e )
                {
                    // Continue. The problem has been reported to the user via the log.
                }
            }
        }
    }
    
    private static void processIncludeDirectives( final FacetedProjectFrameworkImpl framework,
                                                  final Group group,
                                                  final IConfigurationElement context )
    {
        final String bundleId = context.getContributor().getName();
        
        for( IConfigurationElement element : context.getChildren( EL_INCLUDE ) )
        {
            try
            {
                final String fid = findRequiredAttribute( element, ATTR_FACET );
                final String versions = element.getAttribute( ATTR_VERSIONS );
                
                if( framework.isProjectFacetDefined( fid ) )
                {
                    final IProjectFacet f = framework.getProjectFacet( fid );
                    group.addMembers( versions == null ? f.getVersions() : f.getVersions( versions ) );
                }
                else
                {
                    ProblemLog.reportMissingFacet( fid, bundleId );
                }
            }
            catch( InvalidExtensionException e )
            {
                // Continue. The problem has been reported to the user via the log.
            }
            catch( CoreException e )
            {
                FacetCorePlugin.log( e );
            }
        }
    }
    
    private static Group findOrCreateGroup( final FacetedProjectFrameworkImpl framework,
                                            final String id )
    {
        final Group group;
        
        if( framework.isGroupDefined( id ) )
        {
            group = (Group) framework.getGroup( id );
        }
        else
        {
            group = new Group();
            group.setId( id );
            framework.addGroup( group );
        }
        
        return group;
    }

    public static final class Resources
    
        extends NLS
        
    {
        public static String invalidEventType;
        
        static
        {
            initializeMessages( ProjectFacetGroupsExtensionPoint.class.getName(), Resources.class );
        }
    }
    
}
